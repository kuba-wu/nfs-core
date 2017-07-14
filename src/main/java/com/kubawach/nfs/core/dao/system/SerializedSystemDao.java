package com.kubawach.nfs.core.dao.system;

import org.springframework.data.repository.CrudRepository;

import com.kubawach.nfs.core.model.system.SerializedSystem;

public interface SerializedSystemDao extends CrudRepository<SerializedSystem, String> {

}
