package com.kubawach.nfs.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kubawach.nfs.core.dao.graph.SystemPositionDao;
import com.kubawach.nfs.core.dao.system.SerializedSystemDao;
import com.kubawach.nfs.core.model.graph.Graph;
import com.kubawach.nfs.core.model.graph.SystemPosition;
import com.kubawach.nfs.core.model.system.*;
import com.kubawach.nfs.core.model.system.System;

@Service
public class GraphService {

    @Autowired private SystemPositionDao positionDao;
    @Autowired private SerializedSystemDao systemDao;
    
    public Graph createForSystem(System system) {
        
        return Graph.fromSystem(system, getForSystem(system.getId()));
    }
    
    private SystemPosition getForSystem(String id) {
        if (id == null) {
            return null;
        }
        SystemPosition result = positionDao.findById(id);
        if (result == null) {
            SerializedSystem system = systemDao.findOne(id);
            result = getForSystem(system.getParentId());
        }
        
        return result;
    }
}
