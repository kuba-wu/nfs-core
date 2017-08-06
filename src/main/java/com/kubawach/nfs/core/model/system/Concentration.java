package com.kubawach.nfs.core.model.system;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Concentration implements Cloneable {

    public static Concentration ZERO = new Concentration(BigDecimal.ZERO, BigDecimal.ZERO);

    public static Concentration copy(final Concentration origin) {
        return new Concentration(origin.getTime(), origin.getProduct());
    }

    private BigDecimal time;
    private BigDecimal product;
}
