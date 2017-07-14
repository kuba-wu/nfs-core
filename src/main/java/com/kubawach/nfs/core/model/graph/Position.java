package com.kubawach.nfs.core.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {

    private String nodeId;
    
    private Long x;
    private Long y;
}
