package com.kubawach.nfs.core.dao.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubawach.nfs.core.model.graph.SystemPosition;

@Repository
public class SystemPositionDao {
    
    @Autowired private SerializedSystemPositionDao serializedSystemPostionDao;
    @Autowired private ObjectMapper objectMapper;
    
    public SystemPosition findById(String id) {
        
        SerializedSystemPosition ssp = serializedSystemPostionDao.findOne(id);
        try {
            return objectMapper.readValue(ssp.getValue(), SystemPosition.class);
        } catch (Exception e) {
            return null;
        }
    }
}
