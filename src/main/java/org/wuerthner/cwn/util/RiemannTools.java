package org.wuerthner.cwn.util;

import java.util.HashMap;
import java.util.Map;

public class RiemannTools {
    public static final int KEY      = 22;
    public static final int INTERVAL = 14;
    public static final int GENUS    = 4;
    public static final int STEP     = 12;

    public static final int UNDEFKEY = 0;
    public static final int FES = 1;
    public static final int CES = 2;
    public static final int GES = 3;
    public static final int DES = 4;
    public static final int AS = 5;
    public static final int ES = 6;
    public static final int B = 7;
    public static final int F = 8;
    public static final int C = 9;
    public static final int G = 10;
    public static final int D = 11;
    public static final int A = 12;
    public static final int E = 13;
    public static final int H = 14;
    public static final int FIS = 15;
    public static final int CIS = 16;
    public static final int GIS = 17;
    public static final int DIS = 18;
    public static final int AIS = 19;
    public static final int EIS = 20;
    public static final int HIS = 21;

    public static final int UNDEFINT = 0;
    public static final int PRIME = 1;
    public static final int MINORSECOND = 2;
    public static final int MAJORSECOND = 3;
    public static final int MINORTHIRD = 4;
    public static final int MAJORTHIRD = 5;
    public static final int FOURTH = 6;
    public static final int TRITONUS = 7;
    public static final int FIFTH = 8;
    public static final int MINORSIXTH = 9;
    public static final int MAJORSIXTH = 10;
    public static final int MINORSEVENTH = 11;
    public static final int MAJORSEVENTH = 12;
    public static final int OCTAVE = 13;

    public static final int UNDEFGEN = 0;
    public static final int MINOR = 1;
    public static final int MAJOR = 2;
    public static final int DIMINISHED = 3;

    private static Map<String,Integer> key_of_string = new HashMap<String,Integer>();
    private static Map<String,Integer> genus_of_string = new HashMap<String,Integer>();

    static {
        key_of_string.put("-", UNDEFKEY);
        key_of_string.put("FES", FES);
        key_of_string.put("CES", CES);
        key_of_string.put("GES", GES);
        key_of_string.put("DES", DES);
        key_of_string.put("AS", AS);
        key_of_string.put("ES", ES);
        key_of_string.put("B", B);
        key_of_string.put("F", F);
        key_of_string.put("C", C);
        key_of_string.put("G", G);
        key_of_string.put("D", D);
        key_of_string.put("A", A);
        key_of_string.put("E", E);
        key_of_string.put("H", H);
        key_of_string.put("FIS", FIS);
        key_of_string.put("CIS", CIS);
        key_of_string.put("GIS", GIS);
        key_of_string.put("DIS", DIS);
        key_of_string.put("AIS", AIS);
        key_of_string.put("EIS", EIS);
        key_of_string.put("HIS", HIS);

        key_of_string.put("fes", FES);
        key_of_string.put("ces", CES);
        key_of_string.put("ges", GES);
        key_of_string.put("des", DES);
        key_of_string.put("as", AS);
        key_of_string.put("es", ES);
        key_of_string.put("b", B);
        key_of_string.put("f", F);
        key_of_string.put("c", C);
        key_of_string.put("g", G);
        key_of_string.put("d", D);
        key_of_string.put("a", A);
        key_of_string.put("e", E);
        key_of_string.put("h", H);
        key_of_string.put("fis", FIS);
        key_of_string.put("cis", CIS);
        key_of_string.put("gis", GIS);
        key_of_string.put("dis", DIS);
        key_of_string.put("ais", AIS);
        key_of_string.put("eis", EIS);
        key_of_string.put("his", HIS);

        genus_of_string.put("-", UNDEFGEN);
        genus_of_string.put("FES", MAJOR);
        genus_of_string.put("CES", MAJOR);
        genus_of_string.put("GES", MAJOR);
        genus_of_string.put("DES", MAJOR);
        genus_of_string.put("AS", MAJOR);
        genus_of_string.put("ES", MAJOR);
        genus_of_string.put("B", MAJOR);
        genus_of_string.put("F", MAJOR);
        genus_of_string.put("C", MAJOR);
        genus_of_string.put("G", MAJOR);
        genus_of_string.put("D", MAJOR);
        genus_of_string.put("A", MAJOR);
        genus_of_string.put("E", MAJOR);
        genus_of_string.put("H", MAJOR);
        genus_of_string.put("FIS", MAJOR);
        genus_of_string.put("CIS", MAJOR);
        genus_of_string.put("GIS", MAJOR);
        genus_of_string.put("DIS", MAJOR);
        genus_of_string.put("AIS", MAJOR);
        genus_of_string.put("EIS", MAJOR);
        genus_of_string.put("HIS", MAJOR);

        genus_of_string.put("fes", MINOR);
        genus_of_string.put("ces", MINOR);
        genus_of_string.put("ges", MINOR);
        genus_of_string.put("des", MINOR);
        genus_of_string.put("as", MINOR);
        genus_of_string.put("es", MINOR);
        genus_of_string.put("b", MINOR);
        genus_of_string.put("f", MINOR);
        genus_of_string.put("c", MINOR);
        genus_of_string.put("g", MINOR);
        genus_of_string.put("d", MINOR);
        genus_of_string.put("a", MINOR);
        genus_of_string.put("e", MINOR);
        genus_of_string.put("h", MINOR);
        genus_of_string.put("fis", MINOR);
        genus_of_string.put("cis", MINOR);
        genus_of_string.put("gis", MINOR);
        genus_of_string.put("dis", MINOR);
        genus_of_string.put("ais", MINOR);
        genus_of_string.put("eis", MINOR);
        genus_of_string.put("his", MINOR);
    }

    private static int half_tone;
    private static int distance;

    public static final String[] sKey      = { "-", "FES", "CES", "GES", "DES", "AS", "ES", "B", "F", "C", "G", "D", "A", "E", "H", "FIS", "CIS", "GIS", "DIS", "AIS", "EIS", "HIS" };
    public static final String[] sInterval = { "UNDEFINT", "PRIME", "MINORSECOND", "MAJORSECOND", "MINORTHIRD", "MAJORTHIRD", "FOURTH", "TRITONUS", "FIFTH", "MINORSIXTH", "MAJORSIXTH", "MINORSEVENTH", "MAJORSEVENTH", "OCTAVE" };
    public static final String[] sGenus    = { "-", "min", "maj", "dim" };

    public static final String[][] displayKey = {
            { "UNDEFINED", "FES", "CES", "GES", "DES", "AS", "ES", "B", "F", "C", "G", "D", "A", "E", "H", "FIS", "CIS", "GIS", "DIS", "AIS", "EIS", "HIS" },
            { "undefined", "fes", "ces", "ges", "des", "as", "es", "b", "f", "c", "g", "d", "a", "e", "h", "fis", "cis", "gis", "dis", "ais", "eis", "his" }, // Minor
            { "UNDEFINED", "FES", "CES", "GES", "DES", "AS", "ES", "B", "F", "C", "G", "D", "A", "E", "H", "FIS", "CIS", "GIS", "DIS", "AIS", "EIS", "HIS" }, // Major
            { "UNDEFINED", "FES-dim", "CES-dim", "GES-dim", "DES-dim", "AS-dim", "ES-dim", "B-dim", "F-dim", "C-dim", "G-dim", "D-dim", "A-dim", "E-dim", "H-dim", "FIS-dim", "CIS-dim", "GIS-dim", "DIS-dim", "AIS-dim", "EIS-dim", "HIS-dim" }  // dim
    };

    // displayFunction[BASE_GEN][GENUS][KEY]
    public static final String[][][] displayFunction = {
            { // UNDEFGEN
                    // UNDEFKEY, FES, CES, GES, DES,  AS,  ES,   B,   F,   C,   G,   D,   A,   E,   H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
                    {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // -
                    {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // x
                    {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // X
                    {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }  // D
            }, { // MINOR
                // UNDEFKEY, FES, CES, GES, DES,  AS,  ES,   B,   F,   C,   G,   D,   A,   E,   H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // -
                {  null,    null,null,null,null,null,null,null, "s", "t", "d","dd",null,null,null,null,null,null,null,null,null,null }, // x
                {  null,    null,null,null,null,"sP","tP","dP", "S", "T", "D","DD",null,null,null,null,null,null,null,null,null,null }, // X
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }  // D
        }, { // MAJOR
                // UNDEFKEY, FES, CES, GES, DES,  AS,  ES,   B,   F,   C,   G,   D,   A,   E,   H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // -
                {  null,    null,null,null,null,null,null,null, "s", "t", "d","Sp","Tp","Dp",null,null,null,null,null,null,null,null }, // x
                {  null,    null,null,null,null,null,null,null, "S", "T", "D","DD",null,null,null,null,null,null,null,null,null,null }, // X
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }  // D
        }, { // DIMINISHED
                // UNDEFKEY, FES, CES, GES, DES,  AS,  ES,   B,   F,   C,   G,   D,   A,   E,   H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // -
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // x
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }, // X
                {  null,    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null }  // D
        }
    };

    public static final int[][] key_of_step = {
            // bb    b    0    #    x
            {   C,   C,   C, HIS, HIS }, // c
            { DES, DES, CIS, CIS, CIS }, // c#
            {   D,   D,   D,   D,   D }, // d
            {  ES,  ES, DIS, DIS, DIS }, // d#
            { FES, FES,   E,   E,   E }, // e
            {   F,   F,   F, EIS, EIS }, // f
            { GES, GES, FIS, FIS, FIS }, // f#
            {   G,   G,   G,   G,   G }, // g
            {  AS,  AS, GIS, GIS, GIS }, // g#
            {   A,   A,   A,   A,   A }, // a
            {   B,   B, AIS, AIS, AIS }, // a#
            { CES, CES,   H,   H,   H }  // h
    };



    //                                   UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
    public static final int[] tone_of_key = {   0,   3,   7,   5,   2,  6,  3,  7,  4,  1,  5,  2,  6,  3,  7,   4,   2,   6,   3,   7,   4,   1 };

    //                                   UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
    public static final int[] step_of_key = {   0,   4,  11,   6,   1,  8,  3, 10,  5,  0,  7,  2,  9,  4, 11,   6,   1,   8,   3,  10,   5,   0 };

    //                                         c  c#  d  d#  e  f  f#  g  g#  a  a#  h
    public static final int[] tone_of_step = { 0, 2,  2,  3, 3, 4,  4, 5,  6, 6,  7, 7 };


    // single_c_matrix[GENUS][KEY]
    public static final int[][] single_c_matrix = {
            // UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
            {         0,  -2,  -2,  -2,  -2,  2,  3,  2,  1,  3,  2,  1,  2, -3,  1,  -2,  -2,   1,  -2,   1,  -1,  -2 }, // quest for C MINOR
            {         0,  -2,  -2,  -2,  -2, -1, -3,  3,  1,  3,  2,  1,  2,  3,  1,  -2,  -2,  -2,  -2,  -2,  -2,  -2 }, // quest for C MAJOR
            {        -1,  -1,  -1,   2,  -1, -1,  2, -1, -1,  3, -1, -1,  2, -1, -1,   2,  -1,  -1,   2,  -1,  -1,   2 }  // quest for C DIMINISHED
    };

    // double_c_matrix[GENUS][KEY]
    public static final int[][] double_c_matrix = {
            // UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
            {         0,   0,   0,   0,   0,  0,  2,  0,  0,  4,  2,  0,  2,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // MINOR
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  4,  2,  0,  2,  2,  0,   0,   0,   0,   0,   0,   0,   0 }, // MAJOR
            {        -1,  -1,  -1,   2,  -1, -1,  2, -1, -1,  2, -1, -1,  2, -1, -1,   2,  -1,  -1,   2,  -1,  -1,   2 } // DIMINISHED
    };

    // harmonic_weight[GENUS][GENUS][KEY]
    public static final int[][][] harmonic_weight = {
            {
                    {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
                    {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
                    {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
                    {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 } // UNDEF
            }, {
            // UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS:  <- MINOR = base key and genus
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
            {         0,   1,   1,   1,   2,  3,  4,  5,  6,  6,  6,  5,  4,  3,  2,   1,   1,   1,   1,   1,   1,   1 }, // quest for C MINOR
            {         0,   1,   1,   1,   1,  1,  1,  2,  3,  4,  3,  6,  6,  6,  4,   3,   2,   1,   1,   1,   1,   1 }, // quest for C MAJOR
            {         0,   2,   2,   2,   2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,   2,   2,   2,   2,   2,   2,   2 }  // quest for C DIMINISHED
    }, {
            // UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS:  <- MAJOR = base key and genus
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
            {         0,   1,   1,   2,   3,  4,  6,  5,  4,  4,  6,  1,  1,  1,  1,   1,   1,   1,   1,   1,   1,   1 }, // quest for C MINOR
            // {         0,   1,   1,   1,   2,  3,  4,  5,  6,  6,  6,  5,  4,  3,  2,   1,   1,   1,   1,   1,   1,   1 }, // quest for C MAJOR
            {         0,   1,   1,   2,   3,  4,  5,  6,  7,  7,  7,  6,  5,  4,  3,   2,   1,   1,   1,   1,   1,   1 }, // quest for C MAJOR
            {         0,   2,   2,   2,   2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,   2,   2,   2,   2,   2,   2,   2 }  // quest for C DIMINISHED
    }, {
            // UNDEFKEY, FES, CES, GES, DES, AS, ES,  B,  F,  C,  G,  D,  A,  E,  H, FIS, CIS, GIS, DIS, AIS, EIS, HIS:  <- DIMINISHED = base key and genus
            {         0,   0,   0,   0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,   0,   0,   0,   0,   0,   0,   0 }, // UNDEF
            {         0,   1,   1,   1,   1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,   1,   1,   1,   1,   1,   1,   1 }, // quest for C MINOR
            {         0,   1,   1,   1,   1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,   1,   1,   1,   1,   1,   1,   1 }, // quest for C MAJOR
            {         0,   1,   1,   1,   1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,   1,   1,   1,   1,   1,   1,   1 }  // quest for C DIMINISHED
    }
    };

    public static final String[][] index_of_tone = {
            //  0    1    2    3    4    5    6    7    8    9   10   11
            //  C         D    es   E    F         G         A        H
            {  "1", "2-","2", "3-","3", "4","5-", "5", "6-","6", "7-","7"}, // UNDEF
            {  "1", "2-","2", "3", "3+","4","5-", "5", "6", "6+","7","7+"}, // MINOR
            {  "1", "2-","2", "3-","3", "4","5-", "5", "6-","6", "7-","7"}, // MAJOR
            {  "1", "2-","2", "3-","3", "4","5-", "5", "6-","6", "7-","7"} // DIMINISHED
    };

    public static final String[][] index_of_tone_without_chord = {
            //  0     1    2    3    4     5    6    7     8    9   10   11
            //  C          D    es   E     F         G          A        H
            {  null, null, null, null, null, null, null, null, null, null, null, null}, // UNDEF
            {  null, "2","2", null,null, "4", "4", null, "6", "6", "7", "7"}, // MINOR
            {  null, "2","2", null,null, "4", "4", null, "6", "6", "7", "7"}, // MAJOR
            {  null, "2","2", null,null, "4", "4", null, "6", "6", "7", "7"} // DIMINISHED
    };


    public static final int keyOfStep(int step, int enh) {
        return key_of_step[step][enh+2];
    }

    public static final int toneOfStep(int base, int step_abs) {
        // step_abs in Halftones
        return tone_of_step[ (step_abs - step_of_key[base] + 12) % 12 ];
    }

    public static final int weight(int qkey, int qgenus, int k, int mult) {
        return 10*single_c_matrix[qgenus] [ (( k-qkey + C + KEY ) % KEY) ] + (mult>1 ? 5*double_c_matrix[qgenus] [ (( k-qkey + C + KEY ) % KEY) ] : 0);
    }

    public static final int harmonic_correlation(int qkey, int qgenus, int base, int genus) {
        return harmonic_weight[genus] [qgenus] [ (( base-qkey + C + KEY) % KEY) ];
    }

    public static final int bass(int qkey, int qgenus, int b) {
        // return bass[qgenus][ (( b-qkey + C + KEY ) % KEY) ];
        return (step_of_key[qkey]==b ? 1 : 0);
    }

    public static final String getIndex(int qkey, int key, int genus, int base_key, int base_gen) {
        // return (( key-qkey + C + KEY) % KEY);
        half_tone = (step_of_key[qkey]-step_of_key[key]+12)%12;
        // distance  = ((key-base+C+KEY)%KEY); // 9: key=base (C with base C), 10: key=base+1 (G with base C)
        // Output.out("-> " + half_tone + ", " + step_of_key[qkey] + " | " + distance);
        return index_of_tone_without_chord[genus][half_tone];
    }

    public static final int getKey(String text) {
        if (text==null) return UNDEFKEY;
        return key_of_string.get(text).intValue();
    }

    public static final int getGenus(String text) {
        if (text==null) return UNDEFGEN;
        return genus_of_string.get(text).intValue();
    }



    public static int beatLength(int beat) {
        return (int) ( Math.pow(2.0, 9-(Math.log(1.0*beat)/Math.log(2.0)))*3 );
    }

}
