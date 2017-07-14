package com.kubawach.nfs.core.model.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.common.collect.Multimap;
import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;
import com.kubawach.nfs.core.model.Receptor;
import com.kubawach.nfs.core.model.Threshold;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {

    private long source;
    private long target;
    private boolean internal;
    private boolean fromReceptor;
    private String label;
    
    public static List<Link> forComponent(Component component, Map<String, Long> indexMap) {
        Link rToE = new Link(indexMap.get(component.receptorId()), indexMap.get(component.effectorId()), true, true, "");
        Link eToR = new Link(indexMap.get(component.effectorId()), indexMap.get(component.receptorId()), true, false, component.getEffector().getProduct());
        return Arrays.asList(rToE, eToR);
    }
    
    private static List<Link> createSubstrateOrProductLinks(String id, String substrateOrProduct, Map<String, Long> indexMap, Multimap<String, Long> productMap) {
        
        List<Link> links = new ArrayList<>();
        Collection<Long> producers = productMap.get(substrateOrProduct);
        for (Long producer : producers) {
            links.add(new Link(producer, indexMap.get(id), false, false, substrateOrProduct));
        }
        return links;
    }
    
    private static List<Link> createSubstrateSourceLink(String id, String sourceId, Map<String, Long> indexMap) {
        return Arrays.asList(new Link(indexMap.get(sourceId), indexMap.get(id), true, false, "substrate"));
    }
    
    public static List<Link> forComponentSubstrate(Component component, Map<String, Long> indexMap, Multimap<String, Long> productMap) {
        
        String substrate = component.getEffector().getSubstrate();
        return (substrate == null
                ? createSubstrateSourceLink(component.effectorId(), component.getId(), indexMap)
                : createSubstrateOrProductLinks(component.effectorId(), substrate, indexMap, productMap));
    }
    
    private static List<Link> createThresholdLinks(Receptor receptor, String receptorId, String internalProduct, Map<String, Long> indexMap, Multimap<String, Long> productMap) {

        List<Link> links = new ArrayList<>();
        Threshold[] thresholds = receptor.getThresholds();
        for (Threshold threshold : thresholds) {
            if (!threshold.isDefault()) {
                links.add(new Link(indexMap.get(threshold.getSignal()), indexMap.get(receptorId), false, true, ""));
            }
            if (!threshold.getProduct().equals(internalProduct)) {
                links.addAll(createSubstrateOrProductLinks(receptorId, threshold.getProduct(), indexMap, productMap));
            }
        }
        return links;
    }
    
    public static List<Link> forComponentThresholds(Component component, Map<String, Long> indexMap, Multimap<String, Long> productMap) {
        return createThresholdLinks(component.getReceptor(), component.receptorId(), component.getEffector().getProduct(), indexMap, productMap);
    }
    
    public static List<Link> forExternalReceptorThresholds(ExternalReceptor receptor, Map<String, Long> indexMap, Multimap<String, Long> productMap) {
        return createThresholdLinks(receptor, receptor.getId(), null, indexMap, productMap);
    }
}
