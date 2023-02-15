package org.wuerthner.cwn.util;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.midi.MidiTrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Riemann {
   // "The harmonic weight measures the correlation between the chord and its base harmony. Use values between 0 (no correlation) and 10.";

    private static int _harmonic_weight      = 8; // might try 5
    private static int _step_size            = 960; // TODO: make configurable!
    private static int _harmony_size         = 2;

    private static Harmony _harmony;
    private static Harmony _prev_harmony;

    private CwnTrack[] track;
    private CwnNoteEvent note;
    private long start;
    private CwnInfoEvent te;
    private int _width;
    private String _symbol;
    private String _functn;
//
////    public static void setParameters(Map<String,String> map) {
////        HARMONIC_WEIGHT = (String) map.get("harmonic-weight");
////        STEP_SIZE       = (String) map.get("step-size");
////        HARMONY_SIZE    = (String) map.get("harmony-size");
////        _harmonic_weight      = StringTools.getInteger(HARMONIC_WEIGHT, 5);
////        _step_size            = StringTools.getInteger(STEP_SIZE, 0);
////        _harmony_size         = StringTools.getInteger(HARMONY_SIZE, 2);
////    }
//
////    public static Map<String,String> getParameters() {
////        Map<String,String> map = new HashMap<String,String>();
////        map.put("harmonic-weight", ""+_harmonic_weight);
////        map.put("step-size", ""+_step_size);
////        map.put("harmony-size", ""+_harmony_size);
////        return map;
////    }
//
//    public void setMarks(CwnContainer arr) {
//
//        int beatlen = arr.getNumberOfBeats(); // RiemannTools.beatLength(sm.getDocumentMeter(1));
//        long lastpos = arr.getLastPosition(); // sm.getDocument().getLastPosition();
////        RiemannTrack rt = (RiemannTrack) doc.getTrack("org.cento.riemann.tracks.RiemannTrack", 0, Document.GET_ACTIVE, Document.WITHOUT_MASTER);
////        if (rt==null)
////            return;
//        int sz = arr.getTrackList().size(); // doc.getNumberOfMidiTracks(Document.GET_ACTIVE, Document.WITHOUT_MASTER);
//        CwnTrack[] track = new CwnTrack[sz];
//        for (int num = 0; num<sz; num++) {
//            track[num] = arr.getTrackList().get(num); // track[num]= doc.getMidiTrack(num, Document.GET_ACTIVE, Document.WITHOUT_MASTER);
//        }
//        List<CwnEvent> noteset;
//        List<CwnEvent> hset;
//        Chord chord;
//        _harmony = new Harmony(track[0].getKey(0).getKey() + RiemannTools.C, RiemannTools.MAJOR); // TODO: extend to use "from" instead of "0"
//        _prev_harmony = null;
//        CwnEvent ev = null;
//        CwnEvent ev1 = null;
//        //
//        // loop through positions starting at 1.1.0 in beatlen intervals
//        //
//        int step = _step_size==0 ? beatlen : _step_size;
//        for (int p = 0; p <= lastpos; p += 192) {
//            //
//            // checking the RiemannTrack for entries at the current position
//            //
//            EventSet set = rt.grabEvents(p);
//            // mark only if there is either a riemann event, or at the desired positions
//            if (!set.isEmpty() || p%step==0) {
//                noteset  = null;
//                ev       = null;
//                //
//                // collect all notes active at the current position into noteset
//                // (including those starting before this position but still "alive")
//                //
//                for (int num = 0; num<sz; num++) {
//                    if (noteset == null) {
//                        noteset = track[num].grabEventsActiveAt(p);
//                    } else {
//                        noteset.addAll(track[num].grabEventsActiveAt(p));
//                    }
//                }
//                //
//                // find a note to be marked, starting at the current position
//                //
//                for (Iterator<Event> it = noteset.iterator(); it.hasNext();) {
//                    ev1 = it.next();
//                    if (ev1.getStart() == p) {
//                        ev = ev1;
//                        break;
//                    }
//                }
//                chord = new Chord(noteset);
//                // Output.out("pos: " + new Position(p) + ", " + noteset.size() + ", " + set.size() + ", ev: " + ev + ", " + (ev==null?"":ev.isMarked()));
//                if (!set.isEmpty()) {
//                    //
//                    // set base harmony manually
//                    //
//                    te = (InfoEvent) set.get(0);
//                    _harmony = new Harmony(te, _harmony, chord);
//                } else {
//                    //
//                    // if not set manually, determine harmony from previous harmony
//                    //
//                    _harmony = new Harmony(_harmony, chord);
//                }
//                if (ev != null && !_harmony.equals(_prev_harmony)) {
//                    if (_harmony.hasMoreThan(_harmony_size)) {
//                        MarkerTools.markEvent(ev, _harmony, this);
//                        // Output.out("ev: " + new Position(ev.getStart()) + ", har: " + _harmony.toString2());
//                    }
//                    _prev_harmony = _harmony;
//                }
//            }
//        }
//        _harmony = null;
//    }
//
//    public void display(Graphics g, int x, int y, Object mark) {
//        if (mark instanceof String) {
//            g.setColor(BROWN);
//            g.setFont(FONT);
//            g.drawString((String)mark, x, 22);
//        } else if (mark instanceof Harmony) {
//            _harmony = (Harmony) mark;
//            _fm      = g.getFontMetrics(FONT);
//            _symbol  = _harmony.getSymbol();
//            _functn  = _harmony.getFunction();
//            if      (_harmony.fixedHarmony()) g.setColor(BLACK);
//            else if (_harmony.fixedChord())   g.setColor(BROWN);
//            else                              g.setColor(BLUE);
//            //
//            // draw harmony
//            //
//            g.setFont(FONT);
//            _width   = (int) _fm.getStringBounds(_symbol, g).getWidth();
//            g.drawString(_symbol, x, 22);                                  // Symbol
//            g.setFont(SMALL_FONT);
//            g.drawString(_harmony.getBassIfNotZero(), x+_width+2, 26);     // Bass
//            g.drawString(_harmony.getAddons(), x+_width+2, 14);            // Addons
//            int m = _harmony.getMultiplicity();
//            if (m > 1) // draw multiplicity
//                g.drawString("{"+m+"}", x-11, 20);
//            //
//            // draw function
//            //
//            if (_functn != null) {
//                g.setFont(ITALIC_FONT);
//                _fm      = g.getFontMetrics(ITALIC_FONT);
//                _width   = (int) _fm.getStringBounds(_functn, g).getWidth();
//                g.drawString(_functn, x, 42);                                  // Symbol
//                g.setFont(SMALL_ITALIC_FONT);
//                g.drawString(_harmony.getBassIfNotZero(), x+_width, 46);     // Bass
//                g.drawString(_harmony.getAddons(), x+_width, 34);            // Addons
//            }
//            //
//            // selection?
//            //
//            Event ev = _harmony.getEvent();
//            if (SelectionUtil.isSelected(ev)) {
//                g.setXORMode(Color.white);
//                g.fillRect(x-4, 8, 26, 38);
//                g.setPaintMode();
//            }
//        }
//    }
//
//    public String description() {
//        return _description;
//    }
//
//    public String getAuthor() { return "Jan Wuerthner"; }
//
//    public String getInfo() { return _info; }
//
//    // we don't need to force a config, since we have a good default value:
//    // no need for: public boolean requiresConfig() { return HARMONIC_WEIGHT==null; }
//
    public static int getHarmonicWeight() {
        return _harmonic_weight;
    }
//
//    public static int getStepSize() {
//        return _step_size;
//    }
//
//    public void setup() {
//        String hw = HARMONIC_WEIGHT;
//        String ss = STEP_SIZE;
//        String hs = HARMONY_SIZE;
//        if (hw == null || hw.equals("")) hw = ""+_harmonic_weight;
//        if (ss == null || ss.equals("")) ss = ""+_step_size;
//        if (hs == null || hs.equals("")) hs = ""+_harmony_size;
//        boolean ok = PluginTools.setup(this, FILENAME_PROPERTIES,
//                new String[]{"harmonic-weight", "step-size", "harmony-size"},
//                new Object[]{hw, ss, hs});
//        if (ok) {
//            prop = SystemProperties.loadProperties(FILENAME_PROPERTIES);
//            HARMONIC_WEIGHT = prop.getProperty("harmonic-weight");
//            STEP_SIZE       = prop.getProperty("step-size");
//            HARMONY_SIZE    = prop.getProperty("harmony-size");
//            _harmonic_weight = StringTools.getInteger(HARMONIC_WEIGHT, _harmonic_weight);
//            _step_size       = StringTools.getInteger(STEP_SIZE, _step_size);
//            _harmony_size    = StringTools.getInteger(HARMONY_SIZE, _harmony_size);
//        }
//    }
}
