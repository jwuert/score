package org.wuerthner.cwn.markup;

import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.Markup;
import org.wuerthner.cwn.api.ScoreParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParallelsMarkup implements Markup {
    @Override
    public Type getType() { return Type.PARALLELS; }

    @Override
    public void mark(List<Long> positions, List<int[]> pitchList, int bassIndex, CwnKeyEvent key, ScoreParameter scoreParameter, Optional<CwnTrack> infoTrackOpt) {
        int length = positions.size();
        for (int posIndex = 1; posIndex < length; posIndex++) {
            for (int voice1 = 0; voice1 < pitchList.size(); voice1++) {
                int pitch1 = pitchList.get(voice1)[posIndex];
                int prevPitch1 = pitchList.get(voice1)[posIndex-1];
                for (int voice2 = voice1 + 1; voice2 < pitchList.size(); voice2++) {
                    int pitch2 = pitchList.get(voice2)[posIndex];
                    int prevPitch2 = pitchList.get(voice2)[posIndex-1];
                    List<String> list = scoreParameter.markupMap.computeIfAbsent(positions.get(posIndex), k -> new ArrayList<>());
                    list.addAll(verifyParallels(prevPitch1, pitch1, prevPitch2, pitch2));
                }
            }
        }
    }

    private List<String> verifyParallels(int prevPitch1, int pitch1, int prevPitch2, int pitch2) {
        List<String> markList = new ArrayList<>();
        if (prevPitch1 != 0 && prevPitch2 != 0 && pitch1 != 0 && pitch2 != 0) {
            int deltaNote = Math.abs(pitch1 - pitch2);
            if (deltaNote == 0 || deltaNote == 7 || deltaNote == 12 || deltaNote == 19 || deltaNote == 24) {
                int slope1 = pitch1 - prevPitch1;
                int slope2 = pitch2 - prevPitch2;
                if (slope1 * slope2 > 0) {
                    int delta_prev = Math.abs(prevPitch1 - prevPitch2);
                    if (delta_prev == deltaNote) {
                        markList.add("P");
                    } else {
                        markList.add("(P)");
                    }
                }
            }
        }
        return markList;
    }
}
