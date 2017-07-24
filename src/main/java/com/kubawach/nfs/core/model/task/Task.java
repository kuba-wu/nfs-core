package com.kubawach.nfs.core.model.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubawach.nfs.core.model.system.SerializedSystem;
import com.kubawach.nfs.core.model.system.System;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"objectMapper", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name="TASK")
public class Task {

    @Transient @Autowired private ObjectMapper objectMapper;
    
    @Id @GeneratedValue(generator="system-uuid") @GenericGenerator(name="system-uuid", strategy = "uuid") private String id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="SYSTEM_ID") private SerializedSystem system;
    @Column(length=2048) private String name;
    @Column(length=2048) private String description;
    @Column(name="IS_PUBLIC") private boolean isPublic = false;
    
    public void setSystem(System system) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.system = system.toSerialized(objectMapper);
    }
    
    @JsonIgnore
    public void setSerializedSystem(SerializedSystem system) {
        this.system = system;
    }
    
    @JsonIgnore
    public SerializedSystem getSerializedSystem() {
        return system;
    }
    
    public System getSystem() {
        
        return system.toSystem(objectMapper);
    }
    
    @PostLoad
    public void initialize() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
}
