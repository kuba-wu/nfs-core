package com.kubawach.nfs.core.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Threshold {

    private String signal;
    @NotEmpty private String product;
    @NotNull @Min(0) private Long value;
    
    @JsonIgnore
    public boolean isDefault() {
        return (signal == null);
    }
}
