package com.kubawach.nfs.core.model;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.kubawach.nfs.core.model.state.Environment;
import com.kubawach.nfs.core.model.state.State;

public class ReceptorTestCase {

    private  <T> Map<String, T> map(final String[] keys, final T[] values) {
        Map<String, T> result = new HashMap<>();
        for (int i=0, n=keys.length; i<n; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }
    
    @Test
    public void shouldSignalWhenPreviouslyActiveAndAboveDefaultThreshold() throws Exception {
        // given 
        Receptor receptor = new Receptor(2L, new Threshold[]{new Threshold(null, "1", 2L)});
        State state = new State(
                Collections.singletonMap("1", new Product(BigDecimal.valueOf(6L))), 
                Collections.singletonMap("signal", Signal.active("1")));
        State newState = State.copy(state);
        Environment env = new Environment();

        // when
        Signal signal = receptor.signal("signal", state, newState, env);

        // then
        assertThat(signal.isActive("1")).isTrue();
        assertThat(signal.getChargeTime("1")).isZero();
    }    

    @Test
    public void shouldSignalWhenChargedAndAboveMaxThreshold() throws Exception {
        // given 
        Receptor receptor = new Receptor(2L, new Threshold[]{new Threshold(null, "1", 5L), new Threshold("ext-signal", "1", 7L)});
        State state = new State(
                map(new String[] {"1", "2"}, new Product[] {new Product(BigDecimal.valueOf(8L)), new Product(BigDecimal.valueOf(0))}),
                map(new String[] {"1", "ext-signal"}, new Signal[] {Signal.charged("1", 3L), Signal.active("2")}));
        State newState = State.copy(state);
        Environment env = new Environment();
        
        // when
        Signal signal = receptor.signal("1", state, newState, env);

        // then
        assertThat(signal.isActive("1")).isTrue();
        assertThat(signal.getChargeTime("1")).isEqualTo(3);
    }    

    @Test
    public void shouldSignalWhenChargedAndAboveDefaultThreshold() throws Exception {
        // given 
        Receptor receptor = new Receptor(2L, new Threshold[] {new Threshold(null, "1", 5L)});
        State state = new State(
            Collections.singletonMap("1", new Product(BigDecimal.valueOf(6L))),
            Collections.singletonMap("1", Signal.charged("1", 3L)));
        State newState = State.copy(state);
        Environment env = new Environment();
        
        // when
        Signal signal = receptor.signal("1", state, newState, env);

        // then
        assertThat(signal.isActive("1")).isTrue();
        assertThat(signal.getChargeTime("1")).isEqualTo(3);
    }

    @Test
    public void shouldSignalWhenNoDelayAndAboveDefaultThreshold() throws Exception {
        // given 
        Receptor receptor = new Receptor(0L, new Threshold[]{ new Threshold(null, "1", 5L)});
        State state = new State(
            Collections.singletonMap("1", new Product(BigDecimal.valueOf(6L))),
            Collections.singletonMap("1", Signal.charged("1", 0L)));
        State newState = State.copy(state);
        Environment env = new Environment();
        
        // when
        Signal signal = receptor.signal("1", state, newState, env);

        // then
        assertThat(signal.isActive("1")).isTrue();
        assertThat(signal.getChargeTime("1")).isZero();
    }

    @Test
    public void shouldNotSignalWhenChargedAndBelowMaxThreshold() throws Exception {
        // given 
        Receptor receptor = new Receptor(2L, new Threshold[]{new Threshold(null, "1", 5L), new Threshold("ext-signal", "1", 7L)});
        State state = new State(
                map(new String[] {"1", "2"}, new Product[] {new Product(BigDecimal.valueOf(6L)), new Product(BigDecimal.valueOf(0L))}),
                map(new String[] {"1", "ext-signal"}, new Signal[] {Signal.charged("1", 3L), Signal.active("1")}));
        State newState = State.copy(state);
        Environment env = new Environment();
        
        // when
        Signal signal = receptor.signal("1", state, newState, env);

        // then
        assertThat(signal.isActive("1")).isFalse();
        assertThat(signal.getChargeTime("1")).isEqualTo(2L);
    } 
}
