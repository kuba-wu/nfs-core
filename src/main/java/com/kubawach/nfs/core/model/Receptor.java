package com.kubawach.nfs.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receptor {

    private static final Logger log = LoggerFactory.getLogger(Receptor.class);
    
    @NotNull @Min(0) private Long delay;
    @NotEmpty private Threshold[] thresholds;

    public Signal signal(final String id, final State oldState, final State newState, final Environment env) {

        Multimap<String, Threshold> thresholdMap =  mapThresholds(oldState);
        Map<String, Long> thresholds = findMaxThresholds(thresholdMap);
        
        Map<String, Boolean> activeStates = new HashMap<>();
        Map<String, Long> chargeTimes = new HashMap<>();
        for (Entry<String, Long> entry : thresholds.entrySet()) {
            
            String productId = entry.getKey();
            Long threshold = entry.getValue();
            
            double product = newState.getProduct(productId).getConcentration();
            boolean isSignalNow = (product > threshold);

            Signal previousSignal = oldState.getSignal(id);
            boolean wasSignal = previousSignal.isActive(productId);

            long time = 0;
            if (!wasSignal && isSignalNow) { 
                if (previousSignal.getChargeTime(productId)+1 <= delay*env.getTimeScale()) {
                    // signal has changed - could have been the same but not active or just switched into ON
                    // nevertheless is below delay requested so needs to be negated (not active)
                    isSignalNow = !isSignalNow;
                    time = previousSignal.getChargeTime(productId)+1;
                } else {
                    // switch time! will be ON from now, set time to the last one
                    time = previousSignal.getChargeTime(productId);
                }
            } else if (wasSignal && !isSignalNow) {
                if (previousSignal.getChargeTime(productId)-1 >= 0) {
                    // signal has changed - could have been the same but active or just switched to OFF
                    // nevertheless is above delay requested so needs to be negated (still active)
                    isSignalNow = !isSignalNow;
                    time = previousSignal.getChargeTime(productId)-1;
                } else {
                    // switch time! will be OFF from now (eg effector WILL PRODUCE)
                    // need to set "smoothing" timer
                    time = delay*env.getTimeScale();
                }
            } else if (wasSignal && isSignalNow) {
                // still ON, above given threshold & charged properly
                time = previousSignal.getChargeTime(productId);
            } else {
                // no signal earlier, no signal now
                if (previousSignal.getChargeTime(productId)-1 >= 0) {
                    time = previousSignal.getChargeTime(productId)-1;
                }
            }
            
            log.info("[receptor {}] wasSignal={}, isNow={}, shouldBeNow={}, time={}", id, wasSignal, isSignalNow, (product > threshold), time);
            
            activeStates.put(productId, isSignalNow);
            chargeTimes.put(productId, time);
        }
        
        return new Signal(activeStates, chargeTimes, oldState.getSignal(id).getMaxCharge());
    }

    private Multimap<String, Threshold> mapThresholds(final State state) {
        Multimap<String, Threshold> active = LinkedHashMultimap.create();
        for (Threshold threshold : thresholds) {
            if (threshold.isDefault() || state.getSignal(threshold.getSignal()).isActiveAtAll()) {
                active.put(threshold.getProduct(), threshold);
            }
        }
        return active;
    }
    
    private static Long findMaxThreshold(Collection<Threshold> thresholds) {
        List<Long> possible = new ArrayList<Long>();

        Long defaultThreshold = null;
        for (Threshold threshold : thresholds) {
            if (threshold.isDefault()) {
                defaultThreshold = threshold.getValue();
            } else {
                possible.add(threshold.getValue());
            }
        }
        Collections.sort(possible);
        // default if none available, highest otherwise
        return (possible.isEmpty() ? defaultThreshold : possible.get(possible.size()-1));
    }
    
    private static Map<String, Long> findMaxThresholds(final Multimap<String, Threshold> thresholds) {
        Map<String, Long> result = new HashMap<>();
        for (Entry<String, Collection<Threshold>> entry : thresholds.asMap().entrySet()) {
            result.put(entry.getKey(), findMaxThreshold(entry.getValue()));
        }
        return result;
    }
}
