package com.kubawach.nfs.core.model.state;

import static com.kubawach.nfs.core.utils.BigDecimalUtils.div;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.Product;

@Data
@NoArgsConstructor
public class Environment {

    private int runTime = 1;
    private int timeScale = 1;
    
    public BigDecimal outflow(Component component, Product concentration) {
            
        return div(component.getEffector().getOutflow().multiply(concentration.getConcentration()), timeScale);
    }
}
