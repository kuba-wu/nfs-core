package com.kubawach.nfs.core.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ExternalReceptor extends Receptor {
    
    public ExternalReceptor(long delay, Threshold[] thresholds, String id) {
        
        super(delay, thresholds);
        this.id = id;
    }
    @Getter @Setter @NotEmpty private String id;
    
    public Signal signal(final State oldState, final State newState, final Environment env) {
        return super.signal(id, oldState, newState, env);
    }
}
