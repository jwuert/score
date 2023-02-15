package org.wuerthner.cwn.util;

import org.junit.Test;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.sample.SampleNoteEvent;

import java.util.ArrayList;
import java.util.List;

public class RiemannTest {

    @Test
    public void testHarmony() {
        Harmony harmony = new Harmony(9,2);
        System.out.println(harmony.toString2());
        System.out.println(harmony.fixedHarmony());
        System.out.println(harmony.getFunction());
    }

    @Test
    public void testChord() {
        List<CwnNoteEvent> CDur = new ArrayList<>();
        CDur.add(new SampleNoteEvent(0, 960, 72, 0, 80, 0));
        CDur.add(new SampleNoteEvent(0, 960, 76, 0, 80, 0));
        CDur.add(new SampleNoteEvent(0, 960, 79, 0, 80, 0));
        Chord C = new Chord(CDur);

        List<CwnNoteEvent> FDur = new ArrayList<>();
        FDur.add(new SampleNoteEvent(0, 960, 77, 0, 80, 0));
        FDur.add(new SampleNoteEvent(0, 960, 81, 0, 80, 0));
        FDur.add(new SampleNoteEvent(0, 960, 84, 0, 80, 0));
        Chord F = new Chord(FDur);

        List<CwnNoteEvent> GDur = new ArrayList<>();
        GDur.add(new SampleNoteEvent(0, 960, 79, 0, 80, 0));
        GDur.add(new SampleNoteEvent(0, 960, 83, 0, 80, 0));
        GDur.add(new SampleNoteEvent(0, 960, 86, 0, 80, 0));
        Chord G = new Chord(GDur);

        List<CwnNoteEvent> GDur7 = new ArrayList<>();
        GDur7.add(new SampleNoteEvent(0, 960, 83, 0, 80, 0));
        GDur7.add(new SampleNoteEvent(0, 960, 86, 0, 80, 0));
        GDur7.add(new SampleNoteEvent(0, 960, 89, 0, 80, 0));
        Chord G7 = new Chord(GDur7);

        System.out.println(C);
        System.out.println(F);
        System.out.println(G);
        System.out.println(G7);

        Harmony h0 = new Harmony(9,2); // C-Dur
        Harmony h1 = new Harmony(h0, C);
        Harmony h2 = new Harmony(h1, F);
        Harmony h3 = new Harmony(h2, G);
        Harmony h4 = new Harmony(h3, G7);
        Harmony h5 = new Harmony(h4, C);

        System.out.println(h1.toString2());
        System.out.println(h2.toString2());
        System.out.println(h3.toString2());
        System.out.println(h4.toString2());
        System.out.println(h5.toString2());
    }

    @Test
    public void testRegEx() {
        String markupPattern = "^([^[_^]]+)(\\_([^^]+))?(\\^(.+))?$";
        String m1 = "C_2";
        String m2 = "C^3";
        String m3 = "C_2^3";
        System.out.println(m1 + ": " + m1.matches(markupPattern) + " - " + m1.replaceAll(markupPattern, "$1") + " . " + m1.replaceAll(markupPattern, "$3") + " . " + m1.replaceAll(markupPattern, "$5"));
        System.out.println(m2 + ": " + m2.matches(markupPattern) + " - " + m2.replaceAll(markupPattern, "$1") + " . " + m2.replaceAll(markupPattern, "$3") + " . " + m2.replaceAll(markupPattern, "$5"));
        System.out.println(m3 + ": " + m3.matches(markupPattern) + " - " + m3.replaceAll(markupPattern, "$1") + " . " + m3.replaceAll(markupPattern, "$3") + " . " + m3.replaceAll(markupPattern, "$5"));
    }
}
