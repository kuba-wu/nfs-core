package com.kubawach.nfs.core.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="SERIALIZED_SYSTEM")
public class SerializedSystem {

    @Id @GeneratedValue(generator="system-uuid") @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    @Column
    private String parentId;
    @Lob @Column
    private String value;
    
    public System toSystem(ObjectMapper objectMapper) {
        try {
            System system = objectMapper.readValue(getValue(), System.class);
            system.setId(getId());
            system.setParentId(getParentId());
            return system;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
