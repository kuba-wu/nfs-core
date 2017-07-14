package com.kubawach.nfs.core.web;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kubawach.nfs.core.model.graph.Graph;
import com.kubawach.nfs.core.model.sim.Simulation;
import com.kubawach.nfs.core.model.system.Concentrations;
import com.kubawach.nfs.core.model.system.System;
import com.kubawach.nfs.core.service.GraphService;
import com.kubawach.nfs.core.service.SystemService;

@Controller
@RequestMapping("system")
public class SimulationController {

    public static final String SESSION_SYSTEM = "system";

    private static final Logger logger = Logger.getLogger(SimulationController.class);

    @Autowired private GraphService graphService;
    @Autowired private SystemService service;

    @RequestMapping(value="graph", method=RequestMethod.POST)
    @ResponseBody
    public Graph getGraphForSystem(@Valid @RequestBody final System system) {

        logger.info("Obtaining graph for system: "+system);
        return graphService.createForSystem(system);
    }
    
    @RequestMapping(value="simulation", method=RequestMethod.POST)
    @ResponseBody
    public List<Concentrations> computeContentrations(@RequestBody final Simulation sim) {

        logger.info("Computing for environment: "+sim.getEnvironment());
        if (sim.getSystem() == null) {
            logger.error("No system set - can't compute concentrations.");
            throw new SystemNotSetException();
        }
        return service.computeConcentration(sim.getSystem(), sim.getEnvironment());
    } 
}
