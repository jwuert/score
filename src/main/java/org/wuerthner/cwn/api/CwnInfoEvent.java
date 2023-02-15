package org.wuerthner.cwn.api;

import java.util.List;

// InfoEvent was mainly created for the Riemann Harmony module
public interface CwnInfoEvent extends CwnEvent {
    public String getName();
    public String getInfo(int index);
    // public List<? extends CwnEvent> getEvents();
}
