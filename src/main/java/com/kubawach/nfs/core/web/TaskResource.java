package com.kubawach.nfs.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kubawach.nfs.core.dao.task.TaskDao;
import com.kubawach.nfs.core.model.task.Task;

@Controller
@RequestMapping("task")
@Transactional
public class TaskResource {

    @Autowired private TaskDao taskDao;
    
    @RequestMapping(value="/public", method=RequestMethod.GET)
    @ResponseBody
    public Iterable<Task> findPublicTasks() throws Exception {
        return taskDao.findPublic();
    }
}
