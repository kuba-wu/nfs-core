package com.kubawach.nfs.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Signal {

    public static Signal charged(String product, Long time) {
        Signal result = new Signal();
        result.active.put(product, false);
        result.chargeTime.put(product, time);
        return result;
    }
    
    public static Signal active(String product) {
        Signal result = new Signal();
        result.active.put(product, true);
        result.chargeTime.put(product, 0L);
        return result;
    }
    
    public static Signal copy(final Signal origin) {
        return new Signal(origin.getActive(), origin.getChargeTime(), origin.getMaxCharge());
    }

    public Signal(Map<String, Boolean> active, Map<String, Long> chargeTime, Map<String, Long> maxCharge) {
        this.active.putAll(active);
        this.chargeTime.putAll(chargeTime);
        this.maxCharge.putAll(maxCharge);
    }

    private final Map<String, Boolean> active = new HashMap<>();
    private final Map<String, Long> chargeTime = new HashMap<>();
    private final Map<String, Long> maxCharge = new HashMap<>();

    public boolean isActive(String product) {

        return active.get(product);
    }
    
    public boolean isActiveAtAll() {
        for (Boolean activeSignal : active.values()) {
            if (activeSignal) {
                return true;
            }
        }
        return false;
    }
    
    public Long getChargeTime(String product) {
        return chargeTime.get(product);
    }
    
    public Long getMaxCharge(String product) {
        return maxCharge.get(product);
    }
    
    public String getHighestCharge() {
        Set<String> products = chargeTime.keySet();
        String highest = null;
        long highestTime = -1;
        for (String product : products) {
            if (chargeTime.get(product) > highestTime) {
                highestTime = chargeTime.get(product);
                highest = product;
            }
        }
        return highest;
    }
}