package com.kubawach.nfs.core.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Node {

    private String name;
    private boolean receptor;
    private boolean source;
    
    private Long x;
    private Long y;
    private Boolean fixed;
    
    public Node(String name, boolean receptor, boolean source) {
        this.name = name;
        this.receptor = receptor;
        this.source = source;
    }
    
    public static Node forReceptor(Component component) {
        return new Node(component.receptorId(), true, false);
    }
    
    public static Node forEffector(Component component) {
        return new Node(component.effectorId(), false, false);
    }
    
    public static Node forExternalReceptor(ExternalReceptor receptor) {
        return new Node(receptor.getId(), true, false);
    }
    
    public static Node forSource(String productId) {
        return new Node(productId, false, true);
    }
}
