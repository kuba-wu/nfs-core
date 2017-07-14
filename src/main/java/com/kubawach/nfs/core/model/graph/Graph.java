package com.kubawach.nfs.core.model.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;
import com.kubawach.nfs.core.model.system.System;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {

    private List<Node> nodes;
    private List<Link> links;
    private Set<String> products;
    
    private static Map<String, Position> positionMap(SystemPosition systemPosition) {
        if (systemPosition == null) {
            return Collections.emptyMap();
        }
        val result = new HashMap<String, Position>();
        for (Position position : systemPosition.getPositions()) {
            result.put(position.getNodeId(), position);
        }
        return result;
    }
    
    private static void mapNodePositions(List<Node> nodes, Map<String, Position> positions) {
        for (Node node : nodes) {
            Position position = positions.get(node.getName()); 
            if (position != null) {
                node.setFixed(true);
                node.setX(position.getX());
                node.setY(position.getY());
            }
        }
    }
    
    public static Graph fromSystem(final System system, final SystemPosition position) {
        List<Node> nodes = new ArrayList<Node>();
        List<Link> links = new ArrayList<Link>();
        
        Map<String, Long> indexMap = new HashMap<>();
        Multimap<String, Long> productMap = HashMultimap.create();
        
        long idx = -1;
        for (Component component : system.getComponents()) {
            String product = component.getEffector().getProduct();
            
            nodes.add(Node.forReceptor(component));
            indexMap.put(component.receptorId(), ++idx);
            productMap.put(product, idx);
            
            nodes.add(Node.forEffector(component));
            indexMap.put(component.effectorId(), ++idx);
            
            if (component.getEffector().getSubstrate() == null) {
                
                nodes.add(Node.forSource(component.getId()));
                indexMap.put(component.getId(), ++idx);
            }
        }
        for (ExternalReceptor receptor : system.getReceptors()) {
            nodes.add(Node.forExternalReceptor(receptor));
            indexMap.put(receptor.getId(), ++idx);
        }
        
        // map positions
        mapNodePositions(nodes, positionMap(position));
        
        // obvious internal component links first (R-2-E, E-2-R)
        for (Component component : system.getComponents()) {
            links.addAll(Link.forComponent(component, indexMap));
        }
        
        // product-to-substrate external links (R-2-E)
        for (Component component : system.getComponents()) {
            links.addAll(Link.forComponentSubstrate(component, indexMap, productMap));
        }
        
        //  receptor threshold links
        // R-2-R (signal), R-2-R (product)
        for (Component component : system.getComponents()) {
            links.addAll(Link.forComponentThresholds(component, indexMap, productMap));
        }
        for (ExternalReceptor receptor : system.getReceptors()) {
            links.addAll(Link.forExternalReceptorThresholds(receptor, indexMap, productMap));
        }
        
        return new Graph(nodes, links, system.getAllProducts());
    }
}
