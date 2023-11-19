package org.wuerthner.cwn.util;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnInfoEvent;

public class Harmony {
    public enum Fix { ALL, HARMONY, CHORD, NONE };
    private int    _base_key; // base key: The Tonika, e.g. A - the base stays the same until a modulation is done! (given by constructor)
    private int    _base_gen; // the base key's genus, e.g. Major                                                   (given by constructor)
    private int    _key;      // chord key, e.g. E (quint to A)
    private int    _genus;    // chord genus, e.g Major
    private int    _bass;     // base step of the chord, e.g. 7 for sept
    private int    _bass_abs; // base step (Halbton), absolute (from "c" on), e.g. 7 for "g"                     (given by Chord instance)
    private String _addons;   // additional intervals
    private Fix    _fix;     // if this is set to true, this Harmony is not calculated, but set manually
    private int    _mult;
    private CwnEvent _event;

    private Chord  _chord;
    private int[]  _maj = new int[RiemannTools.KEY];
    private int[]  _min = new int[RiemannTools.KEY];
    private int[]  _dim = new int[RiemannTools.KEY];

    private static StringBuffer addons;
    private static String addon;

    public Harmony() {_fix=Fix.NONE;}

    /**
     * Fix = ALL
     **/
    public Harmony(int base_key, int base_genus) {
        _fix      = Fix.ALL;
        _event    = null;
        _base_key = base_key;
        _base_gen = base_genus;
        _chord    = null;
        _mult     = 0;
        _key      = base_key;
        _genus    = base_genus;
        _bass     = 0;
        _bass_abs = RiemannTools.step_of_key[base_key];
        _addons   = "";
        System.out.println("# base_key: " + base_key + ", base_genus: " + base_genus);
    }

    /**
     * Text has to be either "FIS" or "fis" for fis major or minor.
     * The parameter determines key and genus.
     * If fix==ALL, the chord as well as the harmony are set to these values.
     * If fix==CHORD, only the chord is set, while the harmony is taken from the prev_harmony value
     * If fix==HARMONY, only the base (harmony) is set, while the chord is determined
     * if fix==NONE, harmony is taken from prev_harmony and chord is determined.
     * otherwise, key and genus are calculated from the Chord.
     **/
    public Harmony(CwnInfoEvent ie, Harmony prev_harmony, Chord c) {
        String text = ie.getInfo(0);
        String parm = ie.getInfo(1);
        _event      = ie;
        if      (parm.equals(Fix.CHORD.name()))   _fix = Fix.CHORD;
        else if (parm.equals(""))                 _fix = Fix.CHORD;
        else if (parm.equals(Fix.HARMONY.name())) _fix = Fix.HARMONY;
        else if (parm.equals(Fix.ALL.name()))     _fix = Fix.ALL;
        else                                      _fix = Fix.NONE;
        _chord    = c;
        _mult     = 0;
        int fixed_key = RiemannTools.getKey(text);
        int fixed_gen = RiemannTools.getGenus(text);

        if (_fix == Fix.HARMONY || _fix == Fix.ALL) {
            _base_key = fixed_key;
            _base_gen = fixed_gen;
        } else {
            _base_key = prev_harmony.getBaseKey();
            _base_gen = prev_harmony.getBaseGenus();
        }
        if (_fix == Fix.CHORD || _fix == Fix.ALL) {
            _key      = fixed_key;
            _genus    = fixed_gen;
            _bass     = RiemannTools.toneOfStep(_key, _chord.bass);
            _bass_abs = RiemannTools.step_of_key[_base_key];
            _addons   = constructAddons();
        } else {
            _key      = RiemannTools.UNDEFKEY;
            _genus    = RiemannTools.UNDEFGEN;
            _bass     = -1;
            _bass_abs = c.bass;
            analyse(); // sets _key, _genus, _bass
            _addons   = constructAddons();
        }
        System.out.println(ie.getPosition() + ": " + _key + ", " + _genus);
    }

    public Harmony(Harmony prev_harmony, Chord c) {
        _fix      = Fix.NONE;
        _event    = null;
        _base_key = prev_harmony.getBaseKey();
        _base_gen = prev_harmony.getBaseGenus();
        _mult     = 0;
        _chord    = c;
        _key      = RiemannTools.UNDEFKEY;
        _genus    = RiemannTools.UNDEFGEN;
        _bass     = -1;
        _bass_abs = c.bass;
        analyse(); // sets _key, _genus, _bass
        _addons   = constructAddons();
        System.out.println("    : " + _key + ", " + _genus);
    }

    public boolean hasMoreThan(int n) {
        return (fixedChord() || _chord.count >= n);
    }

    public boolean fixedHarmony() { return _fix == Fix.HARMONY || _fix == Fix.ALL; }

    public boolean fixedChord() { return _fix == Fix.CHORD || _fix == Fix.ALL; }

    public int getBaseKey() { return _base_key; }

    public int getBaseGenus() { return _base_gen; }

    public int getKey() { return _key; }

    public int getGenus() { return _genus; }

    public int getBass() { return _bass; }

    public CwnEvent getEvent() { return _event; }

    public String toString2() {
        return (_fix==Fix.HARMONY || _fix==Fix.ALL ? "* " : "  ") + getSymbol() + (_bass == 0 ? "" : "_" + _bass) + " ["+_bass_abs+"] "+
                (_addons!=null && _addons.equals("") ? "" : "^"+_addons.trim() ) +
                " [ " + getBaseKey() + ", " + getBaseGenus() + "] " + getFunction();
    }

    public String toString() {
        // z.B. C^\markup { \bold C \small \raise #1.3 \column < "7" "3" > }
        // besser : d'4^\markup { \column <
        //                        { \bold   { C \combine \sub "3" { \hspace #0.1 \super "7" } } }
        //                        { \italic { T \combine \sub "3" { \hspace #0.1 \super "7" } } }
        //                      > }
        return "\\column < " +
                (_fix==Fix.HARMONY || _fix==Fix.ALL ? "{ \\bold " : "{ ") + "{ " + getSymbol() + " \\combine " +
                "\\sub \"" + (_bass == 0 ? "" : " " + _bass) + "\" " +
                "{ \\hspace #0.1 \\super \"" + (_addons!=null && _addons.equals("") ? "" : _addons.trim() ) + "\" } } } " +
                "{ \\italic { " + getFunction() + " \\combine " +
                "\\sub \"" + (_bass == 0 ? "" : " " + _bass) + "\" " +
                "{ \\hspace #0.1 \\super \"" + (_addons!=null && _addons.equals("") ? "" : _addons.trim() ) + "\" } } } " +
                ">";
    }

    public String toStringHarmony() {
        return getSymbol() + (_bass == 0 ? "" : "_" + _bass) + (_addons!=null && _addons.equals("") ? "" : "^" + _addons.trim()) + (_fix==Fix.NONE?"":"!");
    }

    public String toStringFunction() {
        return getFunction() + (_bass == 0 ? "" : "_" + _bass) + (_addons!=null && _addons.equals("") ? "" : "^" + _addons.trim()) + (_fix==Fix.NONE?"":"!");
    }

    public String getSymbol() {
        return RiemannTools.displayKey[_genus][_key];
    }

    public String getFunction() {
        int param = (RiemannTools.C + _key - _base_key + RiemannTools.KEY) % RiemannTools.KEY;
        return RiemannTools.displayFunction[_base_gen][_genus][ (RiemannTools.C + _key-_base_key+RiemannTools.KEY)%RiemannTools.KEY ];
    }

    public String getBassIfNotZero() {
        return (_bass == 0 ? "" : ""+_bass);
    }

    public String getAddons() {
        return _addons;
    }

    public int getMultiplicity() {
        return _mult;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof Harmony))
            return false;
        Harmony h = (Harmony) o;
        return
                getKey()    == h.getKey() &&
                        getGenus()  == h.getGenus() &&
                        getBass()   == h.getBass() &&
                        getAddons().equals(h.getAddons());
    }

    public final int hashCode() {
        int val = 17;
        val = 37*val + (int)(getKey());
        val = 37*val + (int)(getGenus());
        val = 37*val + (int)(getBass());
        val = 37*val + _addons.hashCode();
        return val;
    }

    private void analyse() {
        int harmonic_weight = Riemann.getHarmonicWeight();
        for (int s=RiemannTools.UNDEFKEY; s<RiemannTools.KEY; s++) {
            _maj[s] = 0;
            _min[s] = 0;
            _dim[s] = 0;
            if (_base_key == RiemannTools.UNDEFKEY) {
                for (int i=0; i<_chord.count; i++) {
                    _maj[s] +=
                            RiemannTools.weight(s, RiemannTools.MAJOR, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                    _min[s] +=
                            RiemannTools.weight(s, RiemannTools.MINOR, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                    _dim[s] +=
                            RiemannTools.weight(s, RiemannTools.DIMINISHED, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                }
            } else {
                for (int i=0; i<_chord.count; i++) {
                    _maj[s] +=
                            RiemannTools.weight(s, RiemannTools.MAJOR, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.harmonic_correlation(s, RiemannTools.MAJOR, _base_key, _base_gen) * harmonic_weight +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                    _min[s] +=
                            RiemannTools.weight(s, RiemannTools.MINOR, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.harmonic_correlation(s, RiemannTools.MINOR, _base_key, _base_gen) * harmonic_weight +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                    _dim[s] +=
                            RiemannTools.weight(s, RiemannTools.DIMINISHED, _chord.key[i], _chord.mult[i]) +
                                    RiemannTools.bass(s, RiemannTools.MAJOR, _bass_abs);
                }
            }
        }
        int max = 0;
        int mykey = RiemannTools.UNDEFKEY;
        int mygen = RiemannTools.UNDEFGEN;

        for (int k=RiemannTools.GES; k<RiemannTools.KEY; k++) {
            if (max <= _dim[k]) { max = _dim[k]; mykey = k; mygen = RiemannTools.DIMINISHED; }
        }
        for (int k=RiemannTools.GES; k<RiemannTools.KEY; k++) {
            if (max <= _min[k]) { max = _min[k]; mykey = k; mygen = RiemannTools.MINOR;  }
        }
        for (int k=RiemannTools.GES; k<RiemannTools.KEY; k++) {
            if (max <= _maj[k]) { max = _maj[k]; mykey = k; mygen = RiemannTools.MAJOR; }
        }
        for (int k=RiemannTools.GES; k<RiemannTools.KEY; k++) {
            if (max == _dim[k]) _mult++;
            if (max == _min[k]) _mult++;
            if (max == _maj[k]) _mult++;
        }

        _key   = mykey;
        _genus = mygen;
        _bass  = RiemannTools.toneOfStep(mykey, _bass_abs);
    }

    private String constructAddons() {
        addons = new StringBuffer();
        for (int i=0; i<_chord.count; i++) {
            addon = RiemannTools.getIndex(_chord.key[i], _key, _genus, _base_key, _base_gen);
            if (addon != null && !addon.equals(getBassIfNotZero())) {
                addons.append(addon + " ");
            }
        }
        return addons.toString();
    }
}
