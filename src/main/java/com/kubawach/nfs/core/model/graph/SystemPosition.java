package com.kubawach.nfs.core.model.graph;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemPosition {

    private List<Position> positions;
}
