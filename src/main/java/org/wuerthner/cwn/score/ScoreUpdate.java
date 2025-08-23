package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.midi.MidiTrack;
import org.wuerthner.cwn.position.PositionTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScoreUpdate {
    public enum Type { REBUILD, RELAYOUT, REDRAW }
//    public static int UPDATE_BARS_BEFORE_CHANGE = 10;

    private List<CwnTrack> restrictedTrackList = new ArrayList<>();
    private long start = 0;
    private long end = Long.MAX_VALUE;
    private Type type;

    public ScoreUpdate() {
        this.type = Type.REDRAW;
    }

    public ScoreUpdate(Type type) {
        this.type = type;
    }

    public ScoreUpdate(CwnTrack track) {
        this.type = Type.REBUILD;
        if (track!=null) {
            restrictToTrack(track);
        }
    }

    public ScoreUpdate(CwnTrack track, CwnSelection<? extends CwnEvent> selection) {
        this.type = Type.REBUILD;
        if (track!=null) {
            restrictToTrack(track);
        }
        restrictToRange(selection);
    }

    public ScoreUpdate(List<CwnTrack> trackList, CwnSelection<? extends CwnEvent> selection) {
        this.type = Type.REBUILD;
        restrictToTracks(trackList);
        restrictToRange(selection);
    }

    public ScoreUpdate(CwnTrack track, long start, long end) {
        this.type = Type.REBUILD;
        restrictToTrack(track);
        restrictToRange(start, end);
    }

    public boolean redraw() {
        return type==Type.REDRAW;
    }

    public boolean relayout() {
        return type==Type.RELAYOUT;
    }

    public boolean rebuild() {
        return type==Type.REBUILD;
    }

    public boolean full() { return rebuild() && start == 0 && end == Long.MAX_VALUE && restrictedTrackList.isEmpty(); }

    public <Track extends CwnTrack> ScoreUpdate restrictToTrack(Track track) {
        this.restrictedTrackList = Arrays.asList(track);
        return this;
    }

    public <Track extends CwnTrack> ScoreUpdate restrictToTracks(List<Track> trackList) {
        this.restrictedTrackList = new ArrayList<>();
        this.restrictedTrackList.addAll(trackList);
        return this;
    }

    public boolean contains(CwnTrack track) {
        return (restrictedTrackList.isEmpty() || restrictedTrackList.contains(track));
    }

    public ScoreUpdate restrictToRange(CwnSelection<? extends CwnEvent> selection) {
        List<? extends CwnEvent> sel = selection.getSelection();
        int size = sel.size();
        if (size>0) {
            long start = sel.get(0).getPosition();
            long end = sel.get(size - 1).getPosition() + sel.get(size - 1).getDuration();
            restrictToRange(start, end);
        }
        return this;
    }

    public ScoreUpdate restrictToRange(long start, long end) {
        if (!restrictedTrackList.isEmpty()) {
            // this.start = PositionTools.previousBarFirstBeat(restrictedTrackList.get(0), start, UPDATE_BARS_BEFORE_CHANGE);
            this.start = PositionTools.firstBeat(restrictedTrackList.get(0), start);
            this.end = PositionTools.nextBar(restrictedTrackList.get(0), end);
        } else {
            this.start = start;
            this.end = end;
        }
        return this;
    }

    public ScoreUpdate extendRangeByOneBar() {
        if (!restrictedTrackList.isEmpty()) {
            this.start = PositionTools.previousBarFirstBeat(restrictedTrackList.get(0), start);
            this.end = PositionTools.nextBar(restrictedTrackList.get(0), end);
        }
        return this;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public List<? extends CwnTrack> getRestrictedTrackList() {
        return restrictedTrackList;
    }

    public String toString() {
        if (restrictedTrackList.isEmpty()) {
            return type.name() + " - " + (start==0 && end==Long.MAX_VALUE ? "full range" : "range: " + start + "-" + end) + ", all tracks!";
        } else {
            String tracks = restrictedTrackList.stream().map(tr -> tr.getName()).collect(Collectors.joining(", "));
            Trias startTrias = PositionTools.getTrias(restrictedTrackList.get(0), start);
            Trias endTrias = (end==Long.MAX_VALUE ? new Trias(9998,1,0) : PositionTools.getTrias(restrictedTrackList.get(0), end));
            return type.name() + " - " + (start==0 && end==Long.MAX_VALUE ? "full range" : "range [bar]: " + (startTrias.bar+1) + "-" + (endTrias.bar+1)) + ", tracks: " + tracks;
        }
    }
}
