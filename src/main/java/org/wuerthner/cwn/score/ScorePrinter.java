package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.List;

import org.wuerthner.cwn.api.CwnAccent;
import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnSymbolEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;

public class ScorePrinter {
	
	public final static String EOL = java.lang.System.getProperty("line.separator");
	public final static String[] KEY = new String[] { "ces", "ges", "des", "aes", "es", "bes", "f", "c", "g", "d", "a", "e", "b", "fis", "cis" };
	public final static String[] CLEF = new String[] { "violin", "bass", "\"violin^8\"", "\"violin^15\"", "\"violin_8\"", "\"bass_8\"", "\"bass_15\"", "varbaritone", "subbass", "soprano", "mezzosoprano", "alto", "tenor",
			"percussion" };
	public final static String[][] NOTE = new String[][] {
			// c cis d dis e f fis g gis a ais h c
			{ "deses", "des", "eses", "feses", "fes", "geses", "ges", "ases", "as", "beses", "ceses", "ces", "deses" }, { "c", "des", "d", "es", "fes", "f", "ges", "g", "as", "a", "bes", "ces", "c" },
			{ "c", "cis", "d", "dis", "e", "f", "fis", "g", "gis", "a", "ais", "b", "c" }, { "bis", "cis", "d", "dis", "e", "eis", "fis", "g", "gis", "a", "ais", "b", "bis" },
			{ "bis", "bisis", "cisis", "dis", "disis", "eis", "eisis", "fisis", "gis", "gisis", "ais", "aisis", "bis" } };
	public final static int[][] OCT_CORRECTION = new int[][] {
			// c cis d dis e f fis g gis a ais h c
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1 },
			{ -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1 } };
	public final static String[] OCT = new String[] { ",,,,", ",,,", ",,", ",", "", "'", "''", "'''", "''''", "'''''" };
	
	// private Editor _content = null;
	private StringBuffer _lilypond_code = null;
	private StringBuffer _lyrics = null;
	private double loglen;
	private int trunclen;
	private int lplen;
	private int lpdiff;
	private String ndur;
	private String ndots;
	private int npitch;
	private int nstep;
	private int noctv;
	private int nenhs;
	private String nacct;
	private String noid;
	private String nbow;
	private long closeBowPosition1 = Long.MAX_VALUE;
	private long closeBowPosition2 = Long.MAX_VALUE;
	private long closeVolta = 0;
	private boolean ambitus = false;
	private boolean print_marks = true;
	private boolean satb = false;
	private boolean autoBeamPrint = false;
	
	private List<CwnTrack> trackList;
	private List<CwnSymbolEvent> symbols;
	private List<CwnSymbolEvent> symbolsBowToClose;
	private List<CwnSymbolEvent> symbolsXCrescendoToClose;
	
	public String print(String documentName, String subtitleName, String composerName, boolean autoBeamPrint, ScoreParameter scoreParameter, List<CwnTrack> trackList) {
		this.trackList = trackList;
		this.symbols = new ArrayList<CwnSymbolEvent>();
		this.symbolsBowToClose = new ArrayList<CwnSymbolEvent>();
		this.symbolsXCrescendoToClose = new ArrayList<CwnSymbolEvent>();
		
		// TODO:
		ambitus = false;
		print_marks = false;
		satb = false;
		this.autoBeamPrint = autoBeamPrint;
		
		String opusName = ""; // maybe later...
		
		String title = "\"" + documentName + "\"";
		String subtitle = "\"" + subtitleName + "\"";
		// String subtitle = " \\markup \\center-column { \"" + subtitleName + "\" \\vspace #1 }";
		String opus = " \\markup \\center-column { \"" + opusName + "\" \\vspace #1 }";
		String composer = "\"" + composerName + "\"";
		String tagline = "\"" + "Ambitus" + "\"";
		
		_lilypond_code = new StringBuffer();
		//
		// Lilypond Header
		//
		_lilypond_code.append("\\header {" + EOL);
		_lilypond_code.append("  title = " + title + EOL);
		_lilypond_code.append("  subtitle = " + subtitle + EOL);
		_lilypond_code.append("  composer = " + composer + EOL);
		_lilypond_code.append("  opus = " + opus + EOL);
		_lilypond_code.append("  tagline = " + tagline + EOL);
		_lilypond_code.append("" + EOL);
		_lilypond_code.append("  % mutopia headers." + EOL);
		// _lilypond_code.append(" mutopiatitle = " + title + EOL);
		// _lilypond_code.append(" mutopiacomposer = " + composer + EOL);
		// _lilypond_code.append(" mutopiainstrument = \"Piano\"" + EOL);
		// _lilypond_code.append(" mutopiaopus = " + opus + EOL);
		// _lilypond_code.append(" style = \"baroque\"" + EOL);
		_lilypond_code.append("}" + EOL);
		_lilypond_code.append("" + EOL);
		
		//
		// Version
		//
		_lilypond_code.append("\\version \"2.18.0\"" + EOL);
		_lilypond_code.append("" + EOL);
		
		//
		// Paper
		//
		_lilypond_code.append("\\layout {" + EOL);
		_lilypond_code.append("  indent = 4\\cm" + EOL);
		_lilypond_code.append("  \\context {" + EOL);
		_lilypond_code.append("    \\Staff" + EOL);
		if (ambitus)
			_lilypond_code.append("    \\consists Ambitus_engraver" + EOL);
		_lilypond_code.append("  }" + EOL);
		_lilypond_code.append("}" + EOL);
		_lilypond_code.append(EOL);
		//
		// Score
		//
		_lilypond_code.append("\\score {" + EOL);
		_lilypond_code.append("  <<" + EOL);
		closeBowPosition1 = Long.MAX_VALUE;
		closeBowPosition2 = Long.MAX_VALUE;
		
		// int num = _doc.getNumberOfMidiTracks(false, false); // no muted tracks, no master track needed for printout
		int num = trackList.size();
		int i = 0;
		
		ScoreSystem system = new ScoreSystem(trackList, scoreParameter);
		for (ScoreStaff staff : system) {
			CwnTrack track = staff.getTrack();
			// for (CwnTrack track: trackList) {
			//
			// SYSTEM
			//
			_lyrics = new StringBuffer();
			String tName = track.getName();
			String flag = tName + "-" + i;
			_lilypond_code.append("    \\new Staff {" + EOL);
			_lilypond_code.append("      << \\context Voice = \"" + flag + "\" {" + EOL);
			//
			// TRACK NAME
			//
			_lilypond_code.append("        \\set Staff.instrumentName = \"" + tName + "\"" + EOL);
			appendNotes(staff, flag, scoreParameter.ppq);
			_lilypond_code.append("      } \\new Lyrics \\lyricsto \"" + flag + "\" { " + _lyrics.toString() + " } >>" + EOL);
			_lilypond_code.append("    }" + EOL);
			i++;
		}
		if (satb) {
			ScoreStaff S = system.get(0);
			String lyr_S = ""; // Sopran
			ScoreStaff A = system.get(1);
			String lyr_A = ""; // Alt
			ScoreStaff T = system.get(2);
			String lyr_T = ""; // Tenor
			ScoreStaff B = system.get(3);
			String lyr_B = ""; // Bass
			_lilypond_code.append("    \\context StaffGroup <<" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"sopranos\" { s1 }" + EOL);
			_lilypond_code.append("      \\context Staff = \"women\" <<" + EOL);
			_lilypond_code.append("        \\context Voice = \"sopranos\" { \\voiceOne {" + EOL);
			
			// _lilypond_code.append("\\set Score.baseMoment = #(ly:make-moment 1/2)" + EOL);
			// _lilypond_code.append("\\set strictBeatBeaming = ##t" + EOL);
			// _lilypond_code.append("\\set Score.beatStructure = #'(8 8 8 8)" + EOL);
			
			if (S != null) {
				_lyrics = new StringBuffer();
				appendNotes(S, S.getTrack().getName() + "-S", scoreParameter.ppq);
				lyr_S = _lyrics.toString();
			}
			_lilypond_code.append("        } }" + EOL);
			_lilypond_code.append("        \\context Voice = \"altos\" { \\voiceTwo {" + EOL);
			if (A != null) {
				_lyrics = new StringBuffer();
				appendNotes(A, A.getTrack().getName() + "-A", scoreParameter.ppq);
				lyr_A = _lyrics.toString();
			}
			_lilypond_code.append("        } }" + EOL);
			_lilypond_code.append("      >>" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"altos\" { s1 }" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"tenors\" { s1 }" + EOL);
			_lilypond_code.append("      \\context Staff = \"men\" <<" + EOL);
			_lilypond_code.append("        \\context Voice = \"tenors\" { \\voiceOne {" + EOL);
			if (T != null) {
				_lyrics = new StringBuffer();
				appendNotes(T, T.getTrack().getName() + "-T", scoreParameter.ppq);
				lyr_T = _lyrics.toString();
			}
			_lilypond_code.append("        } }" + EOL);
			_lilypond_code.append("        \\context Voice = \"basses\" { \\voiceTwo {" + EOL);
			if (B != null) {
				_lyrics = new StringBuffer();
				appendNotes(B, B.getTrack().getName() + "-B", scoreParameter.ppq);
				lyr_B = _lyrics.toString();
			}
			_lilypond_code.append("        } }" + EOL);
			_lilypond_code.append("      >>" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"basses\" { s1 }" + EOL);
			_lilypond_code.append(EOL);
			_lilypond_code.append("      \\context Lyrics = \"sopranos\" \\lyricsto sopranos \\lyrics { " + lyr_S + " }" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"altos\"    \\lyricsto altos    \\lyrics { " + lyr_A + " }" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"tenors\"   \\lyricsto tenors   \\lyrics { " + lyr_T + " }" + EOL);
			_lilypond_code.append("      \\context Lyrics = \"basses\"   \\lyricsto basses   \\lyrics { " + lyr_B + " }" + EOL);
			_lilypond_code.append("    >>" + EOL);
		}
		
		_lilypond_code.append("  >>" + EOL);
		_lilypond_code.append("}" + EOL);
		
		return _lilypond_code.toString();
	}
	
	private void appendNotes(ScoreStaff staff, String flag, int ppq) {
		int clef = -99;
		int key = -99;
		String met0 = "-99";
		String met1 = "-99";
		for (ScoreBar bar : staff) {
			//
			// BAR
			//
			int barClef = bar.getClef();
			if (clef != barClef) {
				clef = barClef;
				_lilypond_code.append("        \\clef " + CLEF[clef] + EOL);
			}
			//
			// KEY
			//
			int barKey = bar.getKey();
			if (key != barKey) {
				key = barKey;
				_lilypond_code.append("        \\key " + KEY[key + 7] + " \\major" + EOL);
			}
			//
			// METER
			//
			String barMet0 = bar.getTimeSignature().getNumerator();
			String barMet1 = bar.getTimeSignature().getDenominator();
			
			// int barMet0 = arrangement.getTimeSignature().getBeats();
			// int barMet1 = arrangement.getTimeSignature().getValue();
			if (!met0.equals(barMet0) || !met1.equals(barMet1)) {
				met0 = barMet0;
				met1 = barMet1;
				_lilypond_code.append("        \\time " + met0 + "/" + met1 + EOL);
			}
			//
			// BAR LINE
			//
			CwnBarEvent barEvent = bar.getBarEvent();
			// @formatter:off
			String barLine = (
					barEvent == null ? "|" :
					barEvent.getTypeString().equals(CwnBarEvent.DOUBLE) ? "||" :
					barEvent.getTypeString().equals(CwnBarEvent.BEGIN_REPEAT) ? ".|:" :
					barEvent.getTypeString().equals(CwnBarEvent.END_REPEAT) ? ":|." :
					barEvent.getTypeString().equals(CwnBarEvent.BEGIN_AND_END_REPEAT) ? ":|.|:" :
					barEvent.getTypeString().equals(CwnBarEvent.END) ? "|." :
					"|");
			// @formatter:on
			//
			// SYMBOLS
			//
			// TODO!
			//
			// SCORE
			//
			_lilypond_code.append("          ");
			symbols.addAll(bar.getSymbols());
			for (ScoreVoice voice : bar) {
				if (autoBeamPrint) {
					for (ScoreObject object : voice) {
						handleScoreObject(ppq, object, false, false);
					}
				} else {
					//
					// manual beam setting
					//
					for (ScoreGroup masterGroup : voice.getGroups()) {
						for (ScoreGroup beamGroup : masterGroup.getSubGroups()) {
							for (ScoreGroup splitGroup : beamGroup.getSubGroups()) {
								// System.out.println("SG");
								// System.out.println(splitGroup);
								for (ScoreObject object : splitGroup.getObjectSet()) {
									// startBeam is true for the first note of the outer group (start of the 8th beam)
									// endBeam is true for the last note of the outer group (end of the 8th beam)
									boolean startBeam = beamGroup.first() == object;
									boolean endBeam = beamGroup.last() == object;
									if (splitGroup.getObjectSet().first() == object) {
										// first note of the inner group
										int numberOfBeams = (startBeam ? 0 : 1);
										_lilypond_code.append(EOL + "\\set stemLeftBeamCount = #" + numberOfBeams + EOL);
									}
									if (splitGroup.getObjectSet().last() == object) {
										// last note of the inner group
										int numberOfBeams = (endBeam ? 0 : 1);
										_lilypond_code.append(EOL + "\\set stemRightBeamCount = #" + numberOfBeams + EOL);
									}
									handleScoreObject(ppq, object, startBeam, endBeam);
								}
							}
						}
					}
				}
			}
			_lilypond_code.append(EOL);
			_lilypond_code.append("        \\bar \"" + barLine + "\"" + EOL);
			// _lilypond_code.append(" \\set Score.repeatCommands = #'((volta #f)) ");
			closeVolta(bar.getEndPosition());
		}
	}
	
	private void handleScoreObject(int ppq, ScoreObject object, boolean startBeam, boolean endBeam) {
		if (object.isChord()) {
			ScoreChord chord = (ScoreChord) object;
			
			//
			// VOLTA TO BE CLOSED?
			//
			closeVolta(chord.getStartPosition());
			
			//
			// VOLTA?
			//
			List<CwnSymbolEvent> removeSymbols = new ArrayList<>();
			for (CwnSymbolEvent symbol : symbols) {
				// System.out.println(symbol + " ? " + CwnSymbolEvent.SYMBOL_MF + " =? " + (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_MF)));
				if (chord.getStartPosition() >= symbol.getPosition()) {
					if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_CASE1)) {
						_lilypond_code.append(" \\set Score.repeatCommands = #'((volta \"1.\")) ");
						closeVolta = symbol.getPosition() + symbol.getDuration();
						removeSymbols.add(symbol);
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_CASE2)) {
						_lilypond_code.append(" \\set Score.repeatCommands = #'((volta \"2.\")) ");
						closeVolta = symbol.getPosition() + symbol.getDuration();
						removeSymbols.add(symbol);
					}
				}
			}
			symbols.removeAll(removeSymbols);
			
			//
			// CHORD OPEN?
			//
			if (chord.size() > 1) {
				_lilypond_code.append("<< ");
			}
			for (ScoreNote note : chord.getObjectSet()) {
				_lilypond_code.append(formatNote(note, chord, ppq) + (startBeam ? "[" : endBeam ? "]" : "") + " ");
			}
			//
			// BOW TO BE CLOSED?
			//
			removeSymbols = new ArrayList<>();
			for (CwnSymbolEvent symbol : symbolsBowToClose) {
				if (chord.getStartPosition() >= symbol.getPosition() + symbol.getDuration()) {
					_lilypond_code.append(" ) ");
					removeSymbols.add(symbol);
				}
			}
			symbolsBowToClose.removeAll(removeSymbols);
			//
			// CELLERANDI TO BE CLOSED?
			//
			removeSymbols = new ArrayList<>();
			for (CwnSymbolEvent symbol : symbolsXCrescendoToClose) {
				if (chord.getStartPosition() >= symbol.getPosition() + symbol.getDuration()) {
					_lilypond_code.append(" \\! ");
					removeSymbols.add(symbol);
				}
			}
			symbolsXCrescendoToClose.removeAll(removeSymbols);
			//
			// CHORD CLOSE?
			//
			if (chord.size() > 1) {
				_lilypond_code.append(">> ");
			}
			
			//
			// SYMBOL
			//
			removeSymbols = new ArrayList<>();
			for (CwnSymbolEvent symbol : symbols) {
				// System.out.println(symbol + " ? " + CwnSymbolEvent.SYMBOL_MF + " =? " + (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_MF)));
				if (chord.getStartPosition() >= symbol.getPosition()) {
					removeSymbols.add(symbol);
					if (symbol.isBowUp() || symbol.isBowDown()) {
						_lilypond_code.append(" ( ");
						symbolsBowToClose.add(symbol);
					} else if (symbol.isCrescendo()) {
						_lilypond_code.append(" \\<");
						symbolsXCrescendoToClose.add(symbol);
					} else if (symbol.isDecrescendo()) {
						_lilypond_code.append(" \\>");
						symbolsXCrescendoToClose.add(symbol);
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_PPP)) {
						_lilypond_code.append(" \\ppp ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_PP)) {
						_lilypond_code.append(" \\pp ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_P)) {
						_lilypond_code.append(" \\p ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_MP)) {
						_lilypond_code.append(" \\mp ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_MF)) {
						_lilypond_code.append(" \\mf ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_F)) {
						_lilypond_code.append(" \\f ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_FF)) {
						_lilypond_code.append(" \\ff ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_FFF)) {
						_lilypond_code.append(" \\fff ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_FP)) {
						_lilypond_code.append(" \\fp ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_SF)) {
						_lilypond_code.append(" \\sf ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_SFF)) {
						_lilypond_code.append(" \\sff ");
					} else if (symbol.getSymbolName().equals(CwnSymbolEvent.SYMBOL_SFZ)) {
						_lilypond_code.append(" \\sfz ");
					}
				}
			}
			symbols.removeAll(removeSymbols);
			
		} else if (object.isRest()) {
			//
			// REST
			//
			ScoreRest rest = (ScoreRest) object;
			_lilypond_code.append(formatRest(rest, ppq) + " ");
		} else if (object.isNote()) {
			throw new RuntimeException("TODO: Note Element!");
		} else {
			throw new RuntimeException("Unknown element type: " + object);
		}
	}
	
	private void closeVolta(long l) {
		if (closeVolta > 0 && l >= closeVolta) {
			_lilypond_code.append(" \\set Score.repeatCommands = #'((volta #f)) ");
		}
	}
	
	private String formatRest(ScoreRest rest, int ppq) {
		long dur = rest.getDuration();
		int numberOfDots = rest.getNumberOfDots();
		ndots = "";
		if (numberOfDots > 0) {
			ndots = new String(new char[numberOfDots - 1]).replace('\0', '.');
		}
		return "r" + lpLength(dur, ppq) + ndots;
	}
	
	private String formatNote(ScoreNote ne, ScoreChord sc, int ppq) {
		// System.out.println("--> " + ne.getStartPosition() + ": " + ne.isSplit() + ", " + ne.hasStartTie() + ", " + ne.hasEndTie());
		// Output.out(ne.getDuration() + ", " + ne.getDisplay(_content.getResolution()) + ", " + sc.duration() + ", " + sc.display());
		// ndur = lpLength((int)ne.getDuration()); // TODO: tiplets etc
		String aux_text = "";
		if (print_marks) { // TODO: print marks??? => ScorePrintDialog!
			// List<Object> list = MarkerTools.getMarks(ne);
			// for (Iterator it = list.iterator(); it.hasNext();) {
			// aux_text += it.next().toString() + " ";
			// }
			// if (aux_text.length() > 0)
			// aux_text = "^\\markup { " + aux_text + " } ";
		}
		// TODO: triplets...
		ndur = lpLength((int) sc.getDuration(), ppq);
		npitch = ne.getPitch();
		nstep = npitch % 12;
		nenhs = ne.getEnharmonicShift() + 2;
		noctv = (int) ((npitch - nstep) / 12) + OCT_CORRECTION[nenhs][nstep];
		nbow = "";
		nacct = "";
		int numberOfDots = ne.getNumberOfDots();
		ndots = "";
		if (numberOfDots > 0) {
			ndots = new String(new char[numberOfDots - 1]).replace('\0', '.');
		}
		// close bow
		if (ne.getStartPosition() <= closeBowPosition1 && closeBowPosition1 <= ne.getEndPosition()) {
			nbow += ")";
			closeBowPosition1 = Long.MAX_VALUE;
		}
		if (ne.getStartPosition() <= closeBowPosition2 && closeBowPosition2 <= ne.getEndPosition()) {
			nbow += "\\)";
			closeBowPosition2 = Long.MAX_VALUE;
		}
		
		//
		// Accents
		//
		if (ne.getCwnNoteEvent().hasAccents()) {
			List<? extends CwnAccent> accentList = ne.getCwnNoteEvent().getAccentList();
			String name;
			for (CwnAccent accent : accentList) {
				name = accent.getName();
				// @formatter:off
				nacct += name.equals(CwnAccent.ACCENT_ACCENT)      ? "\\accent" :
						name.equals(CwnAccent.ACCENT_ESPRESSIVO)   ? "\\espressivo" :
						name.equals(CwnAccent.ACCENT_MARCATO)      ? "\\marcato" :
						name.equals(CwnAccent.ACCENT_PORTATO)      ? "\\portato":
						name.equals(CwnAccent.ACCENT_STACCATO)     ? "\\staccato" :
						name.equals(CwnAccent.ACCENT_STACCATISSIMO) ? "\\staccatissimo" :
						name.equals(CwnAccent.ACCENT_TENUTO)       ? "\\tenuto" :
						name.equals(CwnAccent.ACCENT_PRALL)        ? "\\prall" :
						name.equals(CwnAccent.ACCENT_MORDENT)      ? "\\mordent" :
						name.equals(CwnAccent.ACCENT_PRALLMORDENT) ? "\\prallmordent" :
						name.equals(CwnAccent.ACCENT_UPPRALL)      ? "\\upprall" :
						name.equals(CwnAccent.ACCENT_DOWNPRALL)    ? "\\downprall" :
						name.equals(CwnAccent.ACCENT_UPMORDENT)    ? "\\upmordent" :
						name.equals(CwnAccent.ACCENT_DOWNMORDENT)  ? "\\downmordent" :
						name.equals(CwnAccent.ACCENT_LINEPRALL)    ? "\\lineprall" :
						name.equals(CwnAccent.ACCENT_PRALLPRALL)   ? "\\prallprall" :
						name.equals(CwnAccent.ACCENT_PRALLDOWN)    ? "\\pralldown" :
						name.equals(CwnAccent.ACCENT_PRALLUP)      ? "\\prallup" :
						name.equals(CwnAccent.ACCENT_TRILL)        ? "\\trill" :
						name.equals(CwnAccent.ACCENT_TURN)         ? "\\turn" :
						name.equals(CwnAccent.ACCENT_REVERSETURN)  ? "\\reverseturn" :
						name.equals(CwnAccent.ACCENT_UPBOW)        ? "\\upbow" :
						name.equals(CwnAccent.ACCENT_DOWNBOW)      ? "\\downbow" :
						name.equals(CwnAccent.ACCENT_FLAGEOLET)    ? "\\flageolet" :
						name.equals(CwnAccent.ACCENT_SNAPPIZZICATO) ? "\\snappizzicato" :
						name.equals(CwnAccent.ACCENT_OPEN)         ? "\\open" :
						name.equals(CwnAccent.ACCENT_HALFOPEN)     ? "\\halfopen" :
						name.equals(CwnAccent.ACCENT_STOPPED)      ? "\\stopped" :
//						name.equals(CwnAccent.ACCENT_BOWUP)        ? "\\bowup" :
//						name.equals(CwnAccent.ACCENT_BOWDOWN)      ? "\\bowdown" :
						name.equals(CwnAccent.ACCENT_FERMATA)      ? "\\fermata" :
						name.equals(CwnAccent.ACCENT_SHORTFERMATA) ? "\\shortfermata" :
						name.equals(CwnAccent.ACCENT_LONGFERMATA)  ? "\\longfermata" :
						"";
					// @formatter:on
			}
			
		}
		
		// Output.out("pit: " + npitch + ", step: " + nstep + ", oct: " + noctv + ", enh: " + nenhs);
		// TODO:
		_lyrics.append(ne.getLyrics() + " ");
		String splitNote = (ne.hasStartTie() ? " ~ " : "");
		return NOTE[nenhs][nstep] + OCT[noctv] + ndur + ndots + nacct + nbow + splitNote + aux_text;
	}
	
	private String lpLength(long midilen, int ppq) {
		// brahms: int cli = 10-int(1.000001*::log(bb->display()/3)/::log(2));
		//
		// midilen: midilen/3: loglen: trunclen: lpdiff: lplen:
		// = 2**(9-tunclen)
		// 1536 512 9 9 0 1 = 2**0
		// 768 256 8 8 0 2 = 2**1
		// 384 128 7 7 0 4 = 2**2
		// 336 112 6.8073549 6 8 8..
		// 288 96 6.5849625 6 5 8.
		// 192 64 6 6 0 8 = 2**3
		// 96 32 5 5 0 16 = 2**4
		loglen = Math.log(128.0 * midilen / ppq) / Math.log(2.0);
		trunclen = (int) loglen;
		lpdiff = (int) (10 * (loglen - trunclen));
		lplen = (int) Math.pow(2.0, 9.0 - trunclen);
		// Output.out("len: " + midilen + " -> loglen:" + loglen + ", trunclen:" + trunclen + ", lpdiff: " + lpdiff + ", lplen: " + lplen);
		if (lpdiff > 7)
			return lplen + "..";
		else if (lpdiff > 4)
			return lplen + ".";
		else
			return lplen + "";
	}
}
