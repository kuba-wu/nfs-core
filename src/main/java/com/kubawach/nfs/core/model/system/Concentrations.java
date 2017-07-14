package com.kubawach.nfs.core.model.system;

import java.util.List;

import lombok.Data;

@Data
public class Concentrations {

    private final String product;
    private final List<Concentration> values;
}
