package com.kubawach.nfs.core.model.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.kubawach.nfs.core.model.Product;
import com.kubawach.nfs.core.model.Signal;

import lombok.Data;

@Data
public class State {

    /**
     * Product-to-concentration map. 
     */
    private final Map<String, Product> products;
    /**
     * Signal-to-state map;
     */
    private final Map<String, Signal> signals;

    public static State copy(final State state) {
        Map<String, Product> products = new HashMap<>();
        for (Entry<String, Product> entry : state.products.entrySet()) {
            products.put(entry.getKey(), Product.copy(entry.getValue()));
        }
        Map<String, Signal> signals = new HashMap<>();
        for (Entry<String, Signal> entry : state.signals.entrySet()) {
            signals.put(entry.getKey(), Signal.copy(entry.getValue()));
        }
        return new State(products, signals);
    }

    public Product getProduct(final String product) {
        return products.get(product);
    }

    public Signal getSignal(final String receptorId) {
        return signals.get(receptorId);
    }
}
