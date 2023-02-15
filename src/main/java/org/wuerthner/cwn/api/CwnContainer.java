package org.wuerthner.cwn.api;

import java.util.List;

public interface CwnContainer {
    public List<CwnTrack> getTrackList();
    public boolean isEmpty();
    public long findLastPosition();
    public int getBarOffset();
    public long getLastPosition();
    public int getNumberOfBeats();
}
