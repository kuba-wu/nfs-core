package com.kubawach.nfs.core.model.state;

import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.Product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Environment {

    private int runTime = 1;
    private int timeScale = 1;
    
    public double outflow(Component component, Product concentration) {
            
        return component.getEffector().getOutflow()*concentration.getConcentration()/timeScale;
    }
}
