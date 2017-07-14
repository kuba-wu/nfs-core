package com.kubawach.nfs.core.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Effector {

    private static final Logger log = LoggerFactory.getLogger(Effector.class);
    
    @NotEmpty private String product;
    private String substrate;
    
    @NotNull @Min(0) private Long production;
    private double outflow;

    public double produce(final String componentId, final State oldState, final State newState, final Environment env) {
        
        Signal signal = oldState.getSignal(componentId);
        String highestCharge = signal.getHighestCharge();
        
        log.info("[component {}] concentration={}, highestCharge={}, hcTime={}, maxCharge={}, isActive={}", 
                componentId, oldState.getProduct(product).getConcentration(), highestCharge, signal.getChargeTime(highestCharge), signal.getMaxCharge(highestCharge), signal.isActiveAtAll());
        
        if (oldState.getSignal(componentId).isActiveAtAll()) {
            return 0;
        }
        double scaledProduction = (double)production/(double)env.getTimeScale();
        double chargedPercent = Math.pow((double)signal.getChargeTime(highestCharge)/(double)signal.getMaxCharge(highestCharge), 2);
        double productionFactor = 1.0 - chargedPercent;
        double actualProduction = scaledProduction*productionFactor;
        if (substrate == null) {
            return actualProduction;
        }
        // has source
        Product sourceConc = oldState.getProduct(substrate);
        Product sourceNewConc = newState.getProduct(substrate);
        double product = Math.min(actualProduction, sourceConc.getConcentration());
        // update new - containing also what was produced
        sourceConc.setConcentration(sourceConc.getConcentration()-product);
        // update old - to avoid same amount used twice 
        sourceNewConc.setConcentration(sourceNewConc.getConcentration()-product);
        return product;
    }
}
