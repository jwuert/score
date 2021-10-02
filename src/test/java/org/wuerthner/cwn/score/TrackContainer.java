package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.CwnContainer;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackContainer implements CwnContainer {

    private List<CwnTrack> trackList;
    private int barOffset;

    public TrackContainer(List<? extends CwnTrack> trackList, int barOffset) {
        this.trackList = new ArrayList<>();
        this.trackList.addAll(trackList);
        this.barOffset = barOffset;
    }

    @Override
    public List<CwnTrack> getTrackList() {
        return trackList;
    }

    @Override
    public boolean isEmpty() {
        return trackList.isEmpty();
    }

    @Override
    public long findLastPosition() {
        long lastPosition = 0;
        for (CwnTrack track : trackList) {
            List<CwnEvent> eventList = track.getList(CwnEvent.class);
            if (!eventList.isEmpty()) {
                CwnEvent lastEvent = eventList.get(eventList.size() - 1);
                long pos = lastEvent.getPosition()+lastEvent.getDuration();
                lastPosition = Math.max(lastPosition, pos);
            }
        }
        return lastPosition;
    }

    @Override
    public int getBarOffset() {
        return barOffset;
    }

    public void setBarOffset(int barOffset) {
        this.barOffset = barOffset;
    }
}
