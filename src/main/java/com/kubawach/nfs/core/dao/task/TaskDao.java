package com.kubawach.nfs.core.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kubawach.nfs.core.model.task.Task;

public interface TaskDao extends CrudRepository<Task, String> {

    @Query("select task from Task task where task.isPublic = true")
    public List<Task> findPublic();
}
