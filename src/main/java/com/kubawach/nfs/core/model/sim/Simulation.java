package com.kubawach.nfs.core.model.sim;

import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.system.System;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Simulation {

    private Environment environment;
    private System system;
}
