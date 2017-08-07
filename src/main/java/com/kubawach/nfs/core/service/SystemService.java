package com.kubawach.nfs.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.kubawach.nfs.core.model.Component;
import com.kubawach.nfs.core.model.ExternalReceptor;
import com.kubawach.nfs.core.model.Product;
import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;
import com.kubawach.nfs.core.model.system.*;
import com.kubawach.nfs.core.model.system.System;

@Service
public class SystemService {

    public List<Concentrations> computeConcentration(final System system, final Environment env) {

        Map<String, Concentrations> result = createResult(system);
        State state = system.createState(env);

        for (int i=0; i < env.getRunTime()*env.getTimeScale(); i++) {
            updateResult(result, state, i, env);
            state = computeNewState(system, env,state);
        }
        return new ArrayList<Concentrations>(result.values());
    }

    private static void updateResult(final Map<String, Concentrations> result, final State state, final int time, final Environment env) {
        for (String id : result.keySet()) {
            Concentrations cons = result.get(id);
            cons.getValues().add(new Concentration(time/env.getTimeScale(), state.getProduct(id).getConcentration()));
        }
    }

    private static Map<String, Concentrations> createResult(final System system) {
        Map<String, Concentrations> result = new LinkedHashMap<>();
        Set<String> products = system.getAllProducts();
        for (String product : products) {
            result.put(product, new Concentrations(product, new ArrayList<Concentration>()));
        }
        return result;
    } 

    private static State computeNewState(final System system, final Environment env, final State oldState) {

        // 1st - outflow always happens, update products to calculate new later  
        for (Component component : system.getComponents()) {
            Product concentration = oldState.getProduct(component.getEffector().getProduct());
            double outflow = env.outflow(component, concentration);
            double newValue = Math.max(0, concentration.getConcentration() - outflow);
            concentration.setConcentration(newValue);
        }
        State newState = State.copy(oldState);
        // 2nd - production 
        for (Component component : system.getComponents()) {
            component.processProducts(oldState, newState, env);
        }
        // 3rd - signaling
        for (Component component : system.getComponents()) {
            component.processSignals(oldState, newState, env);
        }
        // 4th - solo receptors
        for (ExternalReceptor receptor : system.getReceptors()) {
            String receptorId = receptor.getId();
            newState.getSignals().put(receptorId, receptor.signal(oldState, newState, env));
        }

        return newState;
    }
}
