package com.kubawach.nfs.core.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

import lombok.Data;

@Data
public class Component {

    private static final Logger log = LoggerFactory.getLogger(Component.class);
    
    @JsonCreator
    public Component(@JsonProperty("id") final String id, @JsonProperty("receptor") final Receptor receptor, @JsonProperty("effector") final Effector effector) {
    	
        this.id = id;
        this.receptor = receptor;
        this.effector = effector;
    }

    @NotNull @Size(min=1) private final String id;
    @Valid @NotNull private final Receptor receptor;
    @Valid @NotNull private final Effector effector;

    public String receptorId() {
        return id+"-receptor";
    }
    
    public String effectorId() {
        return id+"-effector";
    }
    
    public void processProducts(final State oldState, final State newState, final Environment env) {
        Product newProduct = newState.getProduct(effector.getProduct()); 
        
        double product = effector.produce(id, oldState, newState, env);
        double result = newProduct.getConcentration()+product;
        log.info("[component {}] Produced={}, overal={}", id, product, result);
        newProduct.setConcentration(result);
    }
    
    public void processSignals(final State oldState, final State newState, final Environment env) {
        
        Signal signal = receptor.signal(id, oldState, newState, env);
        newState.getSignals().put(id, signal);
    }
}
