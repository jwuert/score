package org.wuerthner.cwn.api;

import java.util.List;
import java.util.Optional;

public interface Markup {
    enum Type {
        AMBITUS, ATTRIBUTES, PARALLELS, INTERVALS, CROSSINGS, LYRICS, NOTE_ATTRIBUTES, COLOR_VOICES, HARMONY, RIEMANN
    }

    public Type getType();

    public void mark(List<Long> positions, List<int[]> pitchList, int bassIndex, CwnKeyEvent key, ScoreParameter scoreParameter, Optional<CwnTrack> infoTrackOpt);
}
