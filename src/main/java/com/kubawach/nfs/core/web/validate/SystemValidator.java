package com.kubawach.nfs.core.web.validate;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;
import com.kubawach.nfs.core.model.Receptor;
import com.kubawach.nfs.core.model.Threshold;
import com.kubawach.nfs.core.model.system.System;

@org.springframework.stereotype.Component
public class SystemValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {

        return (System.class.equals(clazz));
    }

    @Override
    public void validate(Object target, Errors errors) {

        System system = (System)target;
        
        Set<String> products = system.getAllProducts();
        
        // 1 - all products used by Ext Receptors have to be in products list
        for (ExternalReceptor receptor : system.getReceptors()) {
            checkProductsOfReceptor(receptor, receptor.getId(), products, errors);
        }
        // 1.5 - all products used by Receptors have to be in list
        for (Component component : system.getComponents()) {
            checkProductsOfReceptor(component.getReceptor(), component.getId(), products, errors);
        }
        // 2 - all substrates used by Effectors have to be in products list
        for (Component component : system.getComponents()) {
            String substrate = component.getEffector().getSubstrate();
            if ((substrate != null) && !products.contains(substrate)) {
                errors.reject("", "Substrate '"+substrate+"' not found for Effector with ID '"+component.getId()+"'");
            }
        }
        
        Set<String> externalIds = asIdSet(system.getReceptors());
        // 3 - all receptors have to have a default threshold
        // 4 - all non-default thresholds must be bound to existing ExternReceptor IDs and must be >= 0
        for (Component component : system.getComponents()) {
            checkDefaultThreshold(component.getReceptor(), component.getId(), component.getEffector().getProduct(), errors);
            checkThresholds(component.getReceptor(), component.getId(), externalIds, errors);
        }
        for (ExternalReceptor receptor : system.getReceptors()) {
            checkDefaultThreshold(receptor, receptor.getId(), errors);
            checkThresholds(receptor, receptor.getId(), externalIds, errors);
        }
        // 5 - all init values should match a product
        for (String product : system.getInit().keySet()) {
            if (!products.contains(product)) {
                errors.reject("", "Initial value for product '"+product+"' does not match a product of any Effector.");
            }
        }
    }
    
    private void checkProductsOfReceptor(Receptor receptor, String id, Set<String> products, Errors errors) {
        for (Threshold threshold : receptor.getThresholds()) {
            if (!products.contains(threshold.getProduct())) {
                errors.reject("", "Product '"+threshold.getProduct()+"' not found for Receptor with ID '"+id+"'");
            }
        }
    }
    
    private void checkDefaultThreshold(ExternalReceptor receptor, String id, Errors errors) {
        for (Threshold threshold : receptor.getThresholds()) {
            if (threshold.isDefault()) {
                return ;
            }
        }
        errors.reject("", "Default threshold not found for External Receptor with ID '"+id+"'");
    }
    
    private void checkDefaultThreshold(Receptor receptor, String id, String product, Errors errors) {
        Threshold defaultThreshold = null;
        for (Threshold threshold : receptor.getThresholds()) {
            if (threshold.isDefault() && product.equals(threshold.getProduct())) {
                defaultThreshold = threshold;
            }
        }
        if (defaultThreshold == null) {
            errors.reject("", "Default threshold not found for Receptor with ID '"+id+"'");
        }
    }
    
    private void checkThresholds(Receptor receptor, String id, Set<String> externalIds, Errors errors) {
        for (Threshold threshold : receptor.getThresholds()) {
            if (!threshold.isDefault() && !externalIds.contains(threshold.getSignal())) {
                errors.reject("", "Receptor ID '"+threshold.getSignal()+"' used to set threshold not found for Receptor with ID '"+id+"'");
            }
            if (threshold.getValue() < 0) {
                errors.reject("", "Threshold value must be greater or equal zero for Receptor with ID '"+id+"'");
            }
        }
    }

    private Set<String> asIdSet(List<ExternalReceptor> receptors) {
        return FluentIterable
            .from(receptors)
            .transform(new Function<ExternalReceptor, String>() {public String apply(ExternalReceptor input) {return input.getId();}})
            .toSet();
    }
}
