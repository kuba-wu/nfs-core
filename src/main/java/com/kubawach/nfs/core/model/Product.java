package com.kubawach.nfs.core.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

    public static Product copy(final Product source) {
        return new Product(source.getConcentration());
    }

    private BigDecimal concentration;
}
