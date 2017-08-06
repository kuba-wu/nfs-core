package com.kubawach.nfs.core.model;

import static com.kubawach.nfs.core.utils.BigDecimalUtils.div;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Effector {

    private static final Logger log = LoggerFactory.getLogger(Effector.class);
    
    @NotEmpty private String product;
    private String substrate;
    
    @NotNull @Min(0) private Long production;
    private BigDecimal outflow;

    public BigDecimal produce(final String componentId, final State oldState, final State newState, final Environment env) {
        
        Signal signal = oldState.getSignal(componentId);
        String highestCharge = signal.getHighestCharge();
        
        log.info("[component {}] concentration={}, highestCharge={}, hcTime={}, maxCharge={}, isActive={}", 
                componentId, oldState.getProduct(product).getConcentration(), highestCharge, signal.getChargeTime(highestCharge), signal.getMaxCharge(highestCharge), signal.isActiveAtAll());
        
        if (oldState.getSignal(componentId).isActiveAtAll()) {
            return BigDecimal.ZERO;
        }
        BigDecimal scaledProduction = div(production, env.getTimeScale());
        BigDecimal chargedPercent = div(signal.getChargeTime(highestCharge), signal.getMaxCharge(highestCharge)).pow(2);
        BigDecimal productionFactor = BigDecimal.ONE.subtract(chargedPercent);
        BigDecimal actualProduction = scaledProduction.multiply(productionFactor);
        if (substrate == null) {
            return actualProduction;
        }
        // has source
        Product sourceConc = oldState.getProduct(substrate);
        Product sourceNewConc = newState.getProduct(substrate);
        BigDecimal product = actualProduction.min(sourceConc.getConcentration());
        // update new - containing also what was produced
        sourceConc.setConcentration(sourceConc.getConcentration().subtract(product));
        // update old - to avoid same amount used twice 
        sourceNewConc.setConcentration(sourceNewConc.getConcentration().subtract(product));
        return product;
    }
}
