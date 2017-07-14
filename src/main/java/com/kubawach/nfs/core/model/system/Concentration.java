package com.kubawach.nfs.core.model.system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Concentration implements Cloneable {

    public static Concentration ZERO = new Concentration(0, 0);

    public static Concentration copy(final Concentration origin) {
        return new Concentration(origin.getTime(), origin.getProduct());
    }

    private double time;
    private double product;
}
