package com.kubawach.nfs.core.dao.graph;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="SERIALIZED_SYSTEM_POSITION")
class SerializedSystemPosition {

    @Id
    private String id;
    @Lob @Column
    private String value;
}

