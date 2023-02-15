package org.wuerthner.cwn.markup;

import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.Markup;
import org.wuerthner.cwn.api.ScoreParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrossingsMarkup implements Markup {
    public Type getType() { return Type.CROSSINGS; }

    @Override
    public void mark(List<Long> positions, List<int[]> pitchList, int bassIndex, CwnKeyEvent key, ScoreParameter scoreParameter, Optional<CwnTrack> infoTrackOpt) {
        int length = positions.size();
        for (int posIndex = 0; posIndex < length; posIndex++) {
            for (int voice1 = 0; voice1 < pitchList.size(); voice1++) {
                for (int voice2 = voice1 + 1; voice2 < pitchList.size(); voice2++) {
                    if (getCrossing(pitchList.get(voice1)[posIndex], pitchList.get(voice2)[posIndex])<0) {
                        List<String> list = scoreParameter.markupMap.computeIfAbsent(positions.get(posIndex), k -> new ArrayList<>());
                        if (!list.contains("X")) {
                            list.add("X");
                        }
                    }
                }
            }
        }
    }

    private int getCrossing(int pitch1, int pitch2) {
        return (int) Math.signum(pitch1 - pitch2);
    }
}
