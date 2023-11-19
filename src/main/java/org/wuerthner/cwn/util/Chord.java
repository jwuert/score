package org.wuerthner.cwn.util;

import org.wuerthner.cwn.api.CwnNoteEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Chord {
    private static int[] __key;
    private static int[] __mult;
    private static CwnNoteEvent ne;
    private static int p;
    private static int i;
    private static boolean done;
    private static int pi;

    public int[] key;
    public int[] mult;
    public int count;
    public int bass; // the base pitch (modulo 12)

    public Chord() {}

    public Chord(List<CwnNoteEvent> es) {
        count = 0;
        bass  = 256;
        //__key  = new int[es.size()];
        //__mult = new int[es.size()];
        init(es);

    }

    public Chord(int[] pitchArray) {
        count = 0;
        bass = 256;
        __key  = new int[pitchArray.length];
        __mult = new int[pitchArray.length];
        init(pitchArray);
    }

    private void init(int[] pitchArray) {
        for (int pi : pitchArray) {
            if (pi < bass) bass = pi;
            p = RiemannTools.keyOfStep(pi%12, 0);
            done = false;
            for (i=0; i<count; i++) {
                //if (__key!=null && __key.length>i)
               if (__key[i] == p) {
                    __mult[i]++;
                    done = true;
                    break;
                }
                    //}
            }
            if (!done) {
                // if (__key!=null && __key.length>count) {
                    __key[count] = p;
                    __mult[count] = 1;
                // }
                count++;
            }
        }
        bass = bass%12;
        key  = new int[count];
        mult = new int[count];
        for (i=0; i<count; i++) {
            //if (__key!=null && __key.length>i) {
                key[i] = __key[i];
                mult[i] = __mult[i];
            //}
        }
    }
    private void init(List<CwnNoteEvent> es) {
        __key  = new int[es.size()];
        __mult = new int[es.size()];
        for (Iterator it = es.iterator(); it.hasNext();) {
            ne = (CwnNoteEvent)it.next();
            pi = ne.getPitch();
            if (pi < bass) bass = pi;
            p = RiemannTools.keyOfStep(pi%12, ne.getEnharmonicShift());
            done = false;
            for (i=0; i<count; i++) {
                if (__key[i] == p) {
                    __mult[i]++;
                    done = true;
                    break;
                }
            }
            if (!done) {
                __key[count] = p;
                __mult[count] = 1;
                count++;
            }
        }
        bass = bass%12;
        key  = new int[count];
        mult = new int[count];
        for (i=0; i<count; i++) {
            key[i]  = __key[i];
            mult[i] = __mult[i];
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<count; i++) {
            buf.append(RiemannTools.sKey[key[i]] + " (" + mult[i] + ")");
            if (i<count-1) buf.append(", ");
        }
        return buf.toString();
    }
}
