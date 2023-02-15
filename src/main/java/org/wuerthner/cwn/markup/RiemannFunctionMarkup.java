package org.wuerthner.cwn.markup;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.util.Chord;
import org.wuerthner.cwn.util.Harmony;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RiemannFunctionMarkup implements Markup {
    public Type getType() { return Type.RIEMANN; }

    @Override
    public void mark(List<Long> positions, List<int[]> pitchList, int bassIndex, CwnKeyEvent key, ScoreParameter scoreParameter, Optional<CwnTrack> infoTrackOpt) {
        int basegen = key.getGenus();
        int basekey = ( basegen == 1 ? (key.getKey()+3)%15: key.getKey() )+ 9;

        Chord chord = null;
        Harmony harmony = new Harmony(basekey,basegen);
        Harmony prevHarmony = harmony;
        int length = positions.size();
        for (int posIndex = 0; posIndex < length; posIndex++) {
            final int i = posIndex;
            Long position = positions.get(posIndex);
            Optional<CwnInfoEvent> infoOptional = getInfo(infoTrackOpt, position);

            int[] array = pitchList.stream().mapToInt(arr -> arr[i]).toArray();
            chord = new Chord(array);

            if (infoOptional.isPresent()) {
                harmony = new Harmony(infoOptional.get(), prevHarmony, chord);
            } else {
                harmony = new Harmony(prevHarmony, chord);
            }
            String markup = harmony.toStringFunction();
            if (markup != null && !markup.equals("")) {
                List<String> list = scoreParameter.markupMap.computeIfAbsent(position, k -> new ArrayList<>());
                list.add(markup);
            }
            prevHarmony = harmony;
        }
    }

    private Optional<CwnInfoEvent> getInfo(Optional<CwnTrack> infoTrackOpt, Long position) {
        if (infoTrackOpt.isPresent()) {
            Optional<CwnInfoEvent> event = infoTrackOpt.get().findEventAtPosition(position, CwnInfoEvent.class);
            return event;
        }
        return Optional.empty();
    }
}
