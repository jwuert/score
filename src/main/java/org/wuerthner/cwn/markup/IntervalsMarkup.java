package org.wuerthner.cwn.markup;

import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.Markup;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.score.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IntervalsMarkup implements Markup {
    public Type getType() { return Type.INTERVALS; }

    @Override
    public void mark(List<Long> positions, List<int[]> pitchList, int bassIndex, CwnKeyEvent key, ScoreParameter scoreParameter, Optional<CwnTrack> infoTrackOpt) {
        int[] basePitch = pitchList.get(bassIndex);
        int length = positions.size();
        for (int posIndex = 0; posIndex < length; posIndex++) {
            for (int j = pitchList.size() - 1; j >= 0; j--) {
                if (j != bassIndex) {
                    String value = getInterval(basePitch[posIndex], pitchList.get(j)[posIndex]);
                    List<String> list = scoreParameter.markupMap.computeIfAbsent(positions.get(posIndex), k -> new ArrayList<>());
                    //if (!list.contains(value)) {
                        list.add(value);
                    // }
                }
            }
        }
    }

    private String getInterval(int pitch1, int pitch2) {
        int delta = Math.abs(pitch1 - pitch2);
        String interval = (delta < Score.interval.length ? Score.interval[delta] : "?");
        return interval;
    }
}
