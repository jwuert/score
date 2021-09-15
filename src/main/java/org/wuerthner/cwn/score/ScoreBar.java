package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnClefEvent;
import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnSymbolEvent;
import org.wuerthner.cwn.api.CwnTempoEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.api.TimeSignature;
import org.wuerthner.cwn.position.PositionTools;

public class ScoreBar implements Iterable<ScoreVoice> {
	private final long start;
	private final long duration;
	private final CwnTrack cwnTrack;
	private final TimeSignature timeSignature;
	private final ScoreParameter scoreParameter;
	private final List<ScoreVoice> scoreVoiceList = new ArrayList<>();
	private final List<CwnTempoEvent> tempoList = new ArrayList<>();
	private final List<CwnSymbolEvent> symbolList = new ArrayList<>();
	private final int key;
	private final int clef;
	private final int[] signs = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
	private final boolean multiVoiceBar;
	private final boolean explicitTimeSignature;
	private final boolean explicitKey;
	private final boolean explicitClef;
	private int shortestValue;
	private double stretchFactor = 1.0;
	private final CwnKeyEvent keyEvent;
	private final CwnClefEvent clefEvent;
	private final CwnTimeSignatureEvent timeSignatureEvent;
	private CwnBarEvent barEvent = null;
	
	public ScoreBar(long start, CwnTrack track, ScoreParameter scoreParameter) {
		this.start = start;
		this.duration = track.nextBar(start) - start;
		this.cwnTrack = track;
		this.scoreParameter = scoreParameter;
		//
		CwnTimeSignatureEvent timeSignatureEvent = cwnTrack.getTimeSignature(start);
		this.timeSignature = timeSignatureEvent.getTimeSignature();
		this.explicitTimeSignature = (timeSignatureEvent.getPosition() == start);
		this.timeSignatureEvent = explicitTimeSignature ? timeSignatureEvent : null;
		
		//
		CwnKeyEvent keyEvent = cwnTrack.getKey(start);
		this.key = keyEvent.getKey();
		this.explicitKey = (keyEvent.getPosition() == start);
		this.keyEvent = explicitKey ? keyEvent : null;
		
		//
		CwnClefEvent clefEvent = cwnTrack.getClef(start);
		this.clef = clefEvent.getClef();
		this.explicitClef = (clefEvent.getPosition() == start);
		this.clefEvent = explicitClef ? clefEvent : null;
		
		//
		int numberOfVoicesNeeded = Math.max(1, getNumberOfVoicesInTrack());
		this.multiVoiceBar = (numberOfVoicesNeeded > 1);
		for (int v = 0; v < numberOfVoicesNeeded; v++) {
			ScoreVoice voice = new ScoreVoice(this, v, scoreParameter);
			scoreVoiceList.add(voice);
		}
		initSigns();
		this.shortestValue = (int) duration;
	}
	
	/*
	 * Create a new Bar filled with Rest(s)
	 */
	public ScoreBar(ScoreBar previousBar) {
		this.start = previousBar.getEndPosition();
		this.duration = previousBar.duration;
		this.cwnTrack = previousBar.cwnTrack;
		this.scoreParameter = previousBar.scoreParameter;
		this.timeSignature = previousBar.timeSignature;
		this.explicitTimeSignature = false;
		this.timeSignatureEvent = null;
		this.key = previousBar.key;
		this.explicitKey = false;
		this.keyEvent = null;
		this.clef = previousBar.clef;
		this.explicitClef = false;
		this.clefEvent = null;
		this.shortestValue = (int) duration;
		this.multiVoiceBar = false;
		ScoreVoice voice = new ScoreVoice(this, 0, scoreParameter);
		scoreVoiceList.add(voice);
		initSigns();
		voice.fillWithRests();
	}
	
	private void initSigns() {
		for (int i = 0; i < 7; i++) {
			signs[i] = Score.allSigns[key + 7][i];
		}
	}
	
	public boolean isMultiVoiceBar() {
		return multiVoiceBar;
	}
	
	public void handleSign(ScoreNote note) {
		int y = Score.invPitch[note.getPitch()];
		int step = note.getPitch() % 12;
		int sgn = Score.sign[step];
		
		int enh = note.getEnharmonicShift();
		if (enh != 0) {
			y += Score.enhF[enh + 2][step];
			sgn = Score.enhS[enh + 2][step];
		}
		int sgMem = signs[y % 7];
		signs[y % 7] = sgn;
		if (sgMem == sgn) {
			sgn = 0;
		} else {
			if (sgn == 0) {
				sgn = 3;
			}
		}
		note.setSign(sgn);
	}
	
	public void addNote(CwnNoteEvent cwnNoteEvent, long noteStartPosition, long noteDuration) {
		int voiceNumber = cwnNoteEvent.getVoice();
		if (voiceNumber >= scoreVoiceList.size()) {
			// if voiceNumber is > max. voice number (which happens when a bar contains gaps in voice, e.g. voice1, voice3), set voice to top most voice:
			// (TODO: long term solution: map voice to index in scoreVoiceList!)
			voiceNumber = scoreVoiceList.size() - 1;
		}
		if (voiceNumber >= 0 && voiceNumber < scoreVoiceList.size()) {
			scoreVoiceList.get(voiceNumber).addNote(cwnNoteEvent, noteStartPosition, noteDuration);
			shortestValue = Math.min(shortestValue, (int) noteDuration);
		} else {
			throw new RuntimeException("Voice " + voiceNumber + " not in Bar (number of voices=" + scoreVoiceList.size() + ")");
		}
	}
	
	void addTestNote(ScoreNote scoreNote) {
		int voiceNumber = scoreNote.getVoice();
		if (voiceNumber >= 0 && voiceNumber < scoreVoiceList.size()) {
			scoreVoiceList.get(voiceNumber).add(scoreNote);
			shortestValue = Math.min(shortestValue, (int) scoreNote.getDuration());
		} else {
			throw new RuntimeException("Voice " + voiceNumber + " not in Bar (number of voices=" + scoreVoiceList.size() + ")");
		}
	}
	
	private int getNumberOfVoicesInTrack() {
		int numberOfVoices = (int) cwnTrack.getList(CwnNoteEvent.class).stream().filter(n -> n.getPosition() >= start && n.getPosition() < getEndPosition()).map(n -> n.getVoice()).distinct().count();
		return numberOfVoices;
	}
	
	public void group() {
		for (ScoreVoice scoreVoice : scoreVoiceList) {
			scoreVoice.group();
		}
	}
	
	public List<CwnTempoEvent> getTempi() {
		return tempoList;
	}
	
	public List<CwnSymbolEvent> getSymbols() {
		return symbolList;
	}
	
	public CwnBarEvent getBarEvent() {
		return barEvent;
	}
	
	public CwnKeyEvent getKeyEvent() {
		return keyEvent;
	}
	
	public CwnClefEvent getClefEvent() {
		return clefEvent;
	}
	
	public CwnTimeSignatureEvent getTimeSignatureEvent() {
		return timeSignatureEvent;
	}
	
	public long getStartPosition() {
		return start;
	}
	
	public long getEndPosition() {
		return start + duration;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public CwnTrack getTrack() {
		return cwnTrack;
	}
	
	public TimeSignature getTimeSignature() {
		return timeSignature;
	}
	
	public int getClef() {
		return clef;
	}
	
	public int getKey() {
		return key;
	}
	
	public boolean hasExplicitTimeSignature() {
		return explicitTimeSignature;
	}
	
	public boolean hasExplicitClef() {
		return explicitClef;
	}
	
	public boolean hasExplicitKey() {
		return explicitKey;
	}

//	public int getStretchedWidthAsPixel(double pixelPerTick, boolean firstBarInStaff, boolean firstBarInTotal) {
//		int width = (int) (getDurationAsPixel(pixelPerTick) * stretchFactor + getOffset(pixelPerTick, firstBarInStaff, firstBarInTotal));
//		return width;
//	}

	public int getStretchedDurationAsPixel(double pixelPerTick) {
		return (int) (getDurationAsPixel(pixelPerTick) * stretchFactor);
	}

	public double getDurationAsPixel(double pixelPerTick) {
		double pulsesPerTick = scoreParameter.ppq * 1.0 / 960.0;
		double userStretchFactor = Math.sqrt(scoreParameter.stretchFactor*0.25);
		double factor = userStretchFactor * 160.0 *Math.sqrt(shortestValue*pulsesPerTick+200)/ (Math.max(scoreParameter.getResolutionInTicks(), shortestValue)*pulsesPerTick);
		double width = (factor * duration * pulsesPerTick * pixelPerTick);
		return width;
	}

	public int getOffset(double pixelPerTick, boolean firstBarInStaff, boolean firstBarInTotal) {
		int offset = 0;
		if (hasExplicitClef() || firstBarInStaff) {
			offset += Score.CLEF_WIDTH;
		}
		if (hasExplicitKey() || firstBarInStaff) {
			offset += Math.abs(key) * Score.KEY_WIDTH + 2;
		}
		if (hasExplicitTimeSignature() || firstBarInTotal) {
			offset += Score.TIMESIGNATURE_WIDTH;
		}
		offset += 12;
		return offset;
	}
	
	public int getShortestValue() {
		return shortestValue;
	}
	
	public void setShortestValue(int shortestValue) {
		this.shortestValue = shortestValue;
	}
	
	public void setStretchFactor(double stretchFactor) {
		this.stretchFactor = stretchFactor;
	}
	
	public ScoreParameter getScoreParameter() {
		return scoreParameter;
	}
	
	public void fillWithRests() {
		scoreVoiceList.stream().forEach(ScoreVoice::fillWithRests);
		long shortestRest = scoreVoiceList.stream().flatMap(v -> v.getScoreObjectSet().stream()).map(so -> so.getDuration()).reduce((long) shortestValue, (a, b) -> Math.min(a, b));
		shortestValue = (int) Math.min(shortestValue, shortestRest);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String sep = "\n    ";
		for (ScoreVoice scoreVoice : scoreVoiceList) {
			builder.append(sep);
			builder.append(scoreVoice.toString());
		}
		return "ScoreBar={start='" + PositionTools.getTrias(cwnTrack, start) + "', duration=" + duration + ", shortestValue=" + shortestValue + ", stretchFactor=" + stretchFactor + "\n" + "  scoreVoiceList=["
				+ builder.toString() + "]}";
	}
	
	public String toString(CwnTrack cwnTrack) {
		return toString();
	}
	
	@Override
	public Iterator<ScoreVoice> iterator() {
		return scoreVoiceList.iterator();
	}
	
	public int size() {
		return scoreVoiceList.size();
	}
	
	public void addTempo(CwnTempoEvent event) {
		tempoList.add(event);
	}
	
	public void addSymbol(CwnSymbolEvent event) {
		symbolList.add(event);
	}
	
	public void addBar(CwnBarEvent event) {
		this.barEvent = event;
	}
}
