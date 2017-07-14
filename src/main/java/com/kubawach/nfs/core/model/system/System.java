package com.kubawach.nfs.core.model.system;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;
import com.kubawach.nfs.core.model.Product;
import com.kubawach.nfs.core.model.Signal;
import com.kubawach.nfs.core.model.Threshold;
import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class System {

    private String id;
    private String parentId;
    
    @Valid private List<ExternalReceptor> receptors = new ArrayList<>();
    @Valid private List<Component> components = new ArrayList<>();
    private Map<String, Long> init = new HashMap<>();

    @JsonIgnore
    public long getInital(final String product) {
        Long result = init.get(product);
        return (result == null ? 0 : result);
    }

    @JsonIgnore
    public Set<String> getAllProducts() {
        Set<String> products = new HashSet<>();
        for (Component component : components) {
            products.add(component.getEffector().getProduct());
        }
        return products;
    }
    
    public SerializedSystem toSerialized(ObjectMapper mapper) {
        StringWriter out = new StringWriter();
        try {
            mapper.writeValue(out, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return new SerializedSystem(id, parentId, out.toString());
    }
    
    public State createState(final Environment env) {
        Map<String, Product> products = new HashMap<>();
        Map<String, Signal> signals = new HashMap<>();

        Set<String> allProducts = getAllProducts();
        for (String product : allProducts) {
            products.put(product, new Product(getInital(product)));
        }
        for (Component component : getComponents()) {
            Map<String, Boolean> active = new HashMap<>();
            Map<String, Long> charge = new HashMap<>();
            Map<String, Long> maxCharge = new HashMap<>();
            for (Threshold threshold : component.getReceptor().getThresholds()) {
                active.put(threshold.getProduct(), false);
                charge.put(threshold.getProduct(), 0L);
                maxCharge.put(threshold.getProduct(), component.getReceptor().getDelay()*env.getTimeScale());
            }
            signals.put(component.getId(), new Signal(active, charge, maxCharge));
        }
        for (ExternalReceptor receptor : getReceptors()) {
            Map<String, Boolean> active = new HashMap<>();
            Map<String, Long> charge = new HashMap<>();
            Map<String, Long> maxCharge = new HashMap<>();
            for (Threshold threshold : receptor.getThresholds()) {
                active.put(threshold.getProduct(), false);
                charge.put(threshold.getProduct(), 0L);
                maxCharge.put(threshold.getProduct(), receptor.getDelay()*env.getTimeScale());
            }
            signals.put(receptor.getId(), new Signal(active, charge, maxCharge));
        }
        return new State(products, signals);
    }
}
