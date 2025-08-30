package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.api.CwnSelection.SelectionType;
import org.wuerthner.cwn.position.PositionTools;

public class ScorePresenter {
	public final static double RADCONV = Math.PI / 180.0;
	public final static double unconnectedBeamWidth = Score.NOTE_HEAD_WIDTH + 2;
	private final ScoreBuilder scoreBuilder;
	private final ScoreCanvas canvas;
	private final ScoreLayout layout;
	private final CwnSelection<CwnEvent> selection;
	private boolean debug;
	private int pianoStaffNo;

	private long firstPositionPartiallyOutOfDisplay;
	private int firstBarCompletelyOutOfDisplay;
	private int totalHeight;
	private int secondSystemBarNumber;
	
	public ScorePresenter(ScoreBuilder scoreBuilder, ScoreCanvas scoreCanvas, ScoreLayout layout, CwnSelection<CwnEvent> selection) {
		this.scoreBuilder = scoreBuilder;
		this.canvas = scoreCanvas;
		this.layout = layout;
		this.selection = selection;
		this.debug = false;
	}

	public void debug() {
		this.debug = true;
	}
	
	public void present(String title, String subtitle, String composer, int barOffset) {
		canvas.open();
		canvas.drawString(title, "title", (int) (layout.getWidth() * 0.5), layout.getTitleHeight() - 30, "center");
		canvas.drawString(subtitle, "subtitle", (int) (layout.getWidth() * 0.5), layout.getTitleHeight() + 0, "center");
		canvas.drawString(composer, "track", (int) (layout.getWidth() - 30), layout.getTitleHeight() + 10, "right");
		if (scoreBuilder.getScoreParameter().getFilename() != null) {
			canvas.drawString(scoreBuilder.getScoreParameter().getFilename(), "barNumber", (int) (layout.getWidth() - 30), layout.getTitleHeight() + 20, "right");
		}
		if (scoreBuilder.getScoreParameter().markup.contains(Markup.Type.NOTE_ATTRIBUTES)) {
			if (selection.hasSingleSelection()) {
				CwnEvent event = selection.getSingleSelection();
				CwnTrack track = scoreBuilder.getTrackList().get(0);
				Trias trias = PositionTools.getTrias(track, event.getPosition());
				String position = trias.toFormattedString();
				String duration = ""+event.getDuration();
				int attrX = (int) (layout.getWidth() - 72);
				canvas.drawString("position:", "barNumber", attrX, 20, "right");
				canvas.drawString("duration:", "barNumber", attrX, 32, "right");
				canvas.drawString(position, "barNumber", attrX+6, 20, "left");
				canvas.drawString(duration, "barNumber", attrX+6, 32, "left");
				if (event instanceof CwnNoteEvent) {
					String pitch = Score.getCPitch(((CwnNoteEvent) event).getPitch(), ((CwnNoteEvent) event).getEnharmonicShift());
					String voice = "" + (((CwnNoteEvent) event).getVoice()+1);
					String velocity = "" + ((CwnNoteEvent) event).getVelocity();
					canvas.drawString("pitch:", "barNumber", attrX, 44, "right");
					canvas.drawString("voice:", "barNumber", attrX, 56, "right");
					canvas.drawString("velocity:", "barNumber", attrX, 68, "right");
					canvas.drawString(pitch, "barNumber", attrX+6, 44, "left");
					canvas.drawString(voice, "barNumber", attrX+6, 56, "left");
					canvas.drawString(velocity, "barNumber", attrX+6, 68, "left");
				} else if (event instanceof CwnSymbolEvent) {
					String type = ((CwnSymbolEvent) event).getSymbolName();
					String parameter = ""+((CwnSymbolEvent) event).getParameter();
					String offset = ""+((CwnSymbolEvent) event).getVerticalOffset();
					canvas.drawString("type:", "barNumber", attrX, 44, "right");
					canvas.drawString("parameter:", "barNumber", attrX, 56, "right");
					canvas.drawString("offset:", "barNumber", attrX, 68, "right");
					canvas.drawString(type, "barNumber", attrX+6, 44, "left");
					canvas.drawString(parameter, "barNumber", attrX+6, 56, "left");
					canvas.drawString(offset, "barNumber", attrX+6, 68, "left");
				} else if (event instanceof CwnBarEvent) {
					String type = ((CwnBarEvent)event).getTypeString();
					canvas.drawString("type:", "barNumber", attrX, 44, "right");
					canvas.drawString(type, "barNumber", attrX+6, 44, "left");
				} else if (event instanceof CwnTempoEvent) {
					String label = ((CwnTempoEvent) event).getLabel();
					String tempo = ""+((CwnTempoEvent) event).getTempo();
					canvas.drawString("label:", "barNumber", attrX, 44, "right");
					canvas.drawString("tempo:", "barNumber", attrX, 56, "right");
					canvas.drawString(label, "barNumber", attrX+6, 44, "left");
					canvas.drawString(tempo, "barNumber", attrX+6, 56, "left");
				} else if (event instanceof CwnKeyEvent) {
					String key = Score.KEYS[((CwnKeyEvent)event).getKey()+7];
					String genus = Score.GENUS[((CwnKeyEvent)event).getGenus()];
					canvas.drawString("key:", "barNumber", attrX, 44, "right");
					canvas.drawString("genus:", "barNumber", attrX, 56, "right");
					canvas.drawString(key, "barNumber", attrX+6, 44, "left");
					canvas.drawString(genus, "barNumber", attrX+6, 56, "left");
				} else if (event instanceof CwnClefEvent) {
					String clef = Score.CLEFS[((CwnClefEvent)event).getClef()];
					canvas.drawString("clef:", "barNumber", attrX, 44, "right");
					canvas.drawString(clef, "barNumber", attrX+6, 44, "left");
				} else if (event instanceof CwnTimeSignatureEvent) {
					String timeSignature = ((CwnTimeSignatureEvent)event).getTimeSignature().toString();
					canvas.drawString("signature:", "barNumber", attrX, 44, "right");
					canvas.drawString(timeSignature, "barNumber", attrX+6, 44, "left");
				} else if (event instanceof CwnInfoEvent) {
					String name = ((CwnInfoEvent)event).getName();
					String info = ((CwnInfoEvent)event).getInfo(0) + " : " + ((CwnInfoEvent)event).getInfo(1);
					canvas.drawString("name:", "barNumber", attrX, 44, "right");
					canvas.drawString("info:", "barNumber", attrX, 56, "right");
					canvas.drawString(name, "barNumber", attrX+6, 44, "left");
					canvas.drawString(info, "barNumber", attrX+6, 56, "left");
				}
			}
		}
		pianoStaffNo = 0;
		int barIndex = barOffset;
		firstPositionPartiallyOutOfDisplay = Long.MAX_VALUE;
		firstBarCompletelyOutOfDisplay = 0;
		secondSystemBarNumber = 0;
		int systemIndex = 0;
		for (ScoreSystem system : scoreBuilder) {
			boolean indent = (systemIndex == 0 && barOffset == 0);
			int staffIndex = 0;
			double xBarPosition = layout.getBorder() + (indent ? layout.getSystemIndent() : 0);
			int yTop = layout.getBorder() + layout.getTitleHeight() + layout.getSystemSpace() + (staffIndex + systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight();
			int yBottom = yTop + (system.size() - 1) * layout.getStaffHeight() + layout.getLineHeight() * 4;
			canvas.drawLine((int) xBarPosition - 1, yTop, (int) xBarPosition - 1, yBottom);
			canvas.drawLine((int) xBarPosition, yTop, (int) xBarPosition, yBottom);
			boolean lastInStaff;
			for (ScoreStaff staff : system) {
				drawStaff(systemIndex, indent, staffIndex, staff);
				xBarPosition = layout.getBorder() + (indent ? layout.getSystemIndent() : 0);
				int barNumberPerStaff = 0;
				for (ScoreBar bar : staff) {
					lastInStaff = staff.size() == barNumberPerStaff+2;
					double xWidth = bar.getStretchedDurationAsPixel(layout.getPixelPerTick());
					drawBar(systemIndex, staffIndex, barIndex, bar, (int)xBarPosition, (int) xWidth, (barNumberPerStaff == 0), barIndex == 0, lastInStaff, yTop, yBottom, staff.getTrack());
					xBarPosition += bar.getOffset(layout.getPixelPerTick(), (barNumberPerStaff == 0), barIndex == 0);
					int voiceIndex = 0;
					for (ScoreVoice voice : bar) {
						drawVoice(systemIndex, staffIndex, barIndex, voiceIndex, voice, bar.getTimeSignature().getMetric(), xBarPosition, xWidth, bar.isMultiVoiceBar());
						voiceIndex++;
					}
					xBarPosition = xBarPosition + xWidth;
					barIndex += (staffIndex == 0 ? 1 : 0);
					if (canvas.outsideDrawArea()) {
						firstPositionPartiallyOutOfDisplay = Math.min(firstPositionPartiallyOutOfDisplay, bar.getStartPosition());
					}
					barNumberPerStaff++;
				}
				staffIndex++;
			}
			
			systemIndex++;
			if (canvas.outsideDrawArea()) {
				firstBarCompletelyOutOfDisplay = barIndex + 1;
				break;
			}
		}
		canvas.close();
		totalHeight = layout.getBorder() + layout.getTitleHeight() + layout.getSystemSpace() + (systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight();
	}
	
	public int getHeight() {
		return totalHeight;
	}
	
	public int getSecondSystemBarOffset() {
		return secondSystemBarNumber;
	}
	
	public int getFirstBarCompletelyOutOfDisplay() {
		return firstBarCompletelyOutOfDisplay;
	}
	
	public long getFirstPositionPartiallyOutOfDisplay() {
		return firstPositionPartiallyOutOfDisplay;
	}
	
	private void drawStaff(int systemIndex, boolean indent, int staffIndex, ScoreStaff staff) {
		int x1 = layout.getBorder() + (indent ? layout.getSystemIndent() : 0);
		int x2 = layout.getWidth() - layout.getBorder();
		int yTop = layout.getBorder() + layout.getTitleHeight() + layout.getSystemSpace() + (staffIndex + systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight();
		// System.out.println("!! " + scoreBuilder.getTrackList().size() + " ? " + staffIndex);
		boolean mute = scoreBuilder.getTrackList().get(staffIndex).getMute();

		if (staff.getTrack().getPiano()) {
			// PIANO STAFF
			pianoStaffNo++;
			if (pianoStaffNo == 1) {
				int y1 = yTop;
				canvas.drawImage("pianostaff1", x1 - 12, y1, false);
			} else if (pianoStaffNo == 2){
				int y1 = yTop - layout.getSystemSpace();
				canvas.drawImage("pianostaff2", x1 - 12, y1, false);
				pianoStaffNo = 0;
			}
		}

		if (staff != null && staff.getTrack() != null && indent) {
			if (staff.getTrack().getPiano()) {
				// PIANO
				if (pianoStaffNo == 1) {
					canvas.drawString(staff.getTrack().getName(), (mute ? "trackMuted" : "track"),
							x1 - 15,
							(int) (yTop + layout.getStaffHeight() - 3 * layout.getLineHeight()),
							"right");
				}
			} else {
				// NORMAL STAFF
				canvas.drawString(staff.getTrack().getName() , (mute ? "trackMuted" : "track"), x1 - 10, (int) (yTop + layout.getLineHeight() * 3), "right");
			}

			if (scoreBuilder.getScoreParameter().markup.contains(Markup.Type.ATTRIBUTES)) {
				int channel = scoreBuilder.getTrackList().get(staffIndex).getChannel();
				int instrument = scoreBuilder.getTrackList().get(staffIndex).getInstrument();
				int volume = scoreBuilder.getTrackList().get(staffIndex).getVolume();
				if (volume<0 || volume>10) volume = 10;
				String channelSelection = CwnTrack.CHANNELS[channel];
				String instrumentSelection = CwnTrack.MIDI_INSTRUMENTS[instrument];
				String volumeSelection = CwnTrack.VOLUMES[volume];
				canvas.drawString("Channel " + channelSelection + " - Volume " + volumeSelection + " ",
						"nole", x1-10, yTop + layout.getLineHeight()*3 + 12, "right");
				canvas.drawString(instrumentSelection + " ",
						"nole", x1-10, yTop + layout.getLineHeight()*3 - 20, "right");
			}
			if (scoreBuilder.getScoreParameter().markup.contains(Markup.Type.AMBITUS)) {
				ScoreBar bar = staff.getBar(0);
				QuantizedPosition qp = new QuantizedPosition(bar, 0, 1);
				QuantizedDuration qd = new QuantizedDuration(scoreBuilder.getScoreParameter(), 0);
				CwnNoteEvent highest = scoreBuilder.getTrackList().get(staffIndex).getHighestNote();
				CwnNoteEvent lowest = scoreBuilder.getTrackList().get(staffIndex).getLowestNote();
				if (highest!=null && lowest != null) {
					ScoreNote hNote = new ScoreNote(bar, highest, qp, qd, 0, false);
					ScoreNote lNote = new ScoreNote(bar, lowest, qp, qd, 0, false);
					int yAmbitusLow = yTop + lNote.getY() * 3 - 40;
					int yAmbitusHigh = yTop + hNote.getY() * 3 - 40;
					canvas.drawLine(x1 +3, yAmbitusLow, x1 +3, yAmbitusHigh+2, "blue");
					canvas.drawDot(x1 +2, yAmbitusHigh);
					canvas.drawDot(x1 +2, yAmbitusLow);
				}
			}
		}
		for (int i = 0; i < 5; i++) {
			int y = yTop + i * layout.getLineHeight();
			if (mute) {
				canvas.drawLine(x1, y, x2, y, "grey");
			} else {
				canvas.drawLine(x1, y, x2, y);
			}
		}
		// canvas.drawLine(x1, yTop, x1, yTop + 4 * layout.getLineHeight());
		canvas.drawLine(x2, yTop, x2, yTop + 4 * layout.getLineHeight());
		//
		// track selected?
		//
		if (this.selection.hasStaffSelected(staffIndex)) {
			canvas.drawLine(x1 - 5, yTop + 2, x1 - 5, yTop + 4 * layout.getLineHeight() - 2, "red");
			canvas.drawLine(x1 - 3, yTop + 1, x1 - 3, yTop + 4 * layout.getLineHeight() - 1, "red");
		}
	}
	
	private void drawBar(int systemIndex, int staffIndex, int barIndex, ScoreBar bar, int xBarPosition, int xWidth, boolean firstBarInStaff, boolean firstBarInTotal, boolean lastInStaff, int ySystemTop, int ySystemBottom, CwnTrack track) {
		int yTop = layout.getBorder() + layout.getTitleHeight() + layout.getSystemSpace() + (staffIndex + systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight();

		if (staffIndex == 0 && firstBarInStaff) {
			canvas.drawString("" + (barIndex + 1), "barNumber", xBarPosition, yTop - 4, "left");
			if (systemIndex == 1) {
				secondSystemBarNumber = barIndex + 1;
			}
		}

		//
		// handle clef
		//
		int offset = 0;
		if (bar.hasExplicitClef() || firstBarInStaff) {
			if (debug) {
				canvas.drawLine(xBarPosition, yTop - layout.getSystemSpace(), xBarPosition + Score.CLEF_WIDTH - 2, yTop - layout.getSystemSpace(), "green");
			}
			boolean alternative = selection.contains(bar.getClefEvent());
			canvas.drawImage("clef" + bar.getClef(), xBarPosition, yTop - 8, alternative);
			offset += Score.CLEF_WIDTH;
		}
		//
		// handle key
		//
		if (bar.hasExplicitKey() || firstBarInStaff) {
			int previousKey = bar.getPreviousKey();
			// System.out.println("* " + PositionTools.getTrias(track, bar.getStartPosition()) + ": " + previousKey + " -> " + bar.getKey());
			int clef = bar.getClef();
			int key = bar.getKey();
			int count = 0;
			boolean alternative = selection.contains(bar.getKeyEvent());
			if (bar.hasExplicitKey()) {
				int signsToBeRemoved = bar.getSignsToBeRemoved(); // <0 means "b", >0 means "#" have to be removed!
				if (signsToBeRemoved > 0) { // remove sharps
					for (int i = 0; i < Math.abs(previousKey); i++) {
						if (i >= key) {
							int x = (int) ((count++ * 6));
							int y = ((Score.sharpTab[i] + Score.signShift[clef]) * 3 - 12);
							canvas.drawImage("nat", xBarPosition + offset + x, yTop + y, alternative);
						}
					}
				} else if (signsToBeRemoved < 0) { // remove flats
					for (int i = 0; i < Math.abs(previousKey); i++) {
						if (-i <= key) {
							int x = (int) ((count++ * 6));
							int y = ((Score.flatTab[i] + Score.signShift[clef]) * 3 - 12);
							canvas.drawImage("nat", xBarPosition + offset + x, yTop + y, alternative);
						}
					}
				}
				offset += Math.abs(signsToBeRemoved) * Score.KEY_WIDTH + 2;
			}
			if (debug) {
				canvas.drawLine(xBarPosition + offset, yTop - layout.getSystemSpace(), xBarPosition + offset + Math.abs(bar.getKey()) * Score.KEY_WIDTH, yTop - layout.getSystemSpace(), "green");
			}
			for (int i = 0; i < Math.abs(key); i++) {
				int x = (int) ((i * 6));
				if (key > 0) { // sharps
					int y = ((Score.sharpTab[i] + Score.signShift[clef]) * 3 - 12);
					canvas.drawImage("sharp", xBarPosition + offset + x, yTop + y, alternative);
				} else { // flats
					int y = ((Score.flatTab[i] + Score.signShift[clef]) * 3 - 12);
					canvas.drawImage("flat", xBarPosition + offset + x, yTop + y, alternative);
				}
			}
			offset += Math.abs(bar.getKey()) * Score.KEY_WIDTH + 2;
		}
		//
		// handle time signature
		//
		if (bar.hasExplicitTimeSignature() || firstBarInTotal) {
			if (debug) {
				canvas.drawLine(xBarPosition + offset, yTop - layout.getSystemSpace(), xBarPosition + offset + Score.TIMESIGNATURE_WIDTH - 2, yTop - layout.getSystemSpace(), "green");
			}
			TimeSignature timeSignature = bar.getTimeSignature();
			boolean alternative = selection.contains(bar.getTimeSignatureEvent());
			canvas.drawString(timeSignature.getNumerator(), "timeSignature", xBarPosition + 3 + offset + (int) (0.5 * Score.TIMESIGNATURE_WIDTH) - 6, yTop + 13, "left", alternative);
			canvas.drawString(timeSignature.getDenominator(), "timeSignature", xBarPosition + 3 + offset + (int) (0.5 * Score.TIMESIGNATURE_WIDTH) - 6, yTop + 13 + layout.getLineHeight() * 2, "left", alternative);
			// offset += Score.TIMESIGNATURE_WIDTH;
		}
		// SPACING:
		// offset += 12;
		// reset offset:
		offset = bar.getOffset(layout.getPixelPerTick(), firstBarInStaff, barIndex==0);

		// Separator between CONFIG and SCORE region:
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop + 0.4 * layout.getLineHeight()));
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop + 1.4 * layout.getLineHeight()));
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop + 2.4 * layout.getLineHeight()));
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop + 3.4 * layout.getLineHeight()));
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop));
//		canvas.drawDot(xBarPosition + offset - 3, (int) (yTop + 4*layout.getLineHeight() - 2));
		//
		// right border
		//
		int xRight = layout.getWidth() - layout.getBorder();
		// canvas.drawString("x: " + xRight, "Arial", xRight, yTop, "left");
		// canvas.drawLine(xBarPosition + offset + xWidth, yTop, xBarPosition + offset + xWidth, yTop + 4 * layout.getLineHeight());
		//
		// handle barline
		//
		CwnBarEvent barline = bar.getBarEvent();
		//if (barline == null || barline.getTypeString().equals(CwnBarEvent.STANDARD))
		if (!firstBarInStaff) {
			int x = xBarPosition;
			//if (xBarPosition + offset + xWidth < xRight - 12) { // 34
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight());
		}
		if (barline!=null && !barline.getTypeString().equals(CwnBarEvent.STANDARD)) {
			boolean alternative = selection.contains(barline);
			String type = barline.getTypeString();
			int x = xBarPosition;
			// int x = xBarPosition + offset + xWidth + 2;
			
			if (type.equals(CwnBarEvent.DOUBLE)) {
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 2, yTop, x + 2, yTop + 4 * layout.getLineHeight(), alternative);
			} else if (type.equals(CwnBarEvent.BEGIN_REPEAT)) {
				canvas.drawLine(x - 3, yTop, x - 3, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x - 2, yTop, x - 2, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawDot(x + 1, (int) (yTop + 1.4 * layout.getLineHeight()));
				canvas.drawDot(x + 1, (int) (yTop + 2.4 * layout.getLineHeight()));
			} else if (type.equals(CwnBarEvent.END_REPEAT)) {
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 2, yTop, x + 2, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 3, yTop, x + 3, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawDot(x - 4, (int) (yTop + 1.4 * layout.getLineHeight()));
				canvas.drawDot(x - 4, (int) (yTop + 2.4 * layout.getLineHeight()));
			} else if (type.equals(CwnBarEvent.BEGIN_AND_END_REPEAT)) {
				canvas.drawLine(x - 3, yTop, x - 3, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x - 1, yTop, x - 1, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 2, yTop, x + 2, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawDot(x - 7, (int) (yTop + 1.4 * layout.getLineHeight()));
				canvas.drawDot(x - 7, (int) (yTop + 2.4 * layout.getLineHeight()));
				canvas.drawDot(x + 3, (int) (yTop + 1.4 * layout.getLineHeight()));
				canvas.drawDot(x + 3, (int) (yTop + 2.4 * layout.getLineHeight()));
			} else if (type.equals(CwnBarEvent.END)) {
				canvas.drawLine(x - 2, yTop, x - 2, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 0, yTop, x + 0, yTop + 4 * layout.getLineHeight(), alternative);
				canvas.drawLine(x + 1, yTop, x + 1, yTop + 4 * layout.getLineHeight(), alternative);
			}
		}
		
		//
		// handle tempi
		//
		for (CwnTempoEvent event : bar.getTempi()) {
			if (event.getLabel() != null) {
				// int x = xBarPosition;
				int xStart = (int) ((event.getPosition() - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
				int x = xBarPosition + (xStart == 0 ? 0 : offset + xStart);
				int y = yTop - layout.getSystemSpace();
				boolean alternative = selection.contains(event);
				canvas.drawString(event.getLabel(), "lyrics", x, y, "left", alternative);
			}
		}

		//
		// draw pointer
		//
		CwnPointer pointer = selection.getPointer();
		final long pointerPosition = pointer.getPosition();
		// System.out.println("### region: " + pointer.getRegion() + ", pp: " + pointerPosition + ", bStart: " + bar.getStartPosition() + ", offset:" + offset + ", " + xBarPosition);
		if (pointer.getRegion() == CwnPointer.Region.SCORE
				&& (bar.getStartPosition()) <= pointerPosition
				&& pointerPosition < bar.getEndPosition()
				&& staffIndex == pointer.getStaffIndex()) {
			// NOTE
			int relPosition = (int) ((pointerPosition - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration()) + 2;
			int yBase = layout.getBorder() + layout.getTitleHeight()
					+ (staffIndex + systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight() + layout.getSystemSpace() + 1;
			int yPos = yBase + pointer.getY(bar.getClef()) * 3 - 42;
			canvas.drawImage("head1", xBarPosition + offset + relPosition, yPos, false);
			// Position
			Trias trias = PositionTools.getTrias(track, pointerPosition);
			String pos = trias.toString();
			canvas.drawString(pos, "barNumber", xBarPosition + offset + relPosition, yTop - layout.getSystemSpace(), "left");
		} else if (pointer.getRegion() == CwnPointer.Region.CONFIG && (bar.getStartPosition()) <= pointerPosition && pointerPosition < bar.getEndPosition()) {
			// CONFIG
			canvas.drawRect(xBarPosition + 4, yTop + 2, xBarPosition + offset - 2, yTop + 4 * layout.getLineHeight() - 2 );
		}

		//
		// draw cursor
		//
		if (selection.hasCursor()) {
			long absCursor = selection.getCursorPosition();
			if (bar.getStartPosition() <= absCursor && absCursor < bar.getEndPosition()) {
				int relCursor = (int) ((absCursor - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
				// system cursor
				// canvas.drawLine(xBarPosition + offset + relCursor, ySystemTop, xBarPosition + offset + relCursor, ySystemBottom);
				// staff cursor
				canvas.drawLine(xBarPosition + offset + relCursor, yTop - 4,
						xBarPosition + offset + relCursor, yTop + 4 * layout.getLineHeight() + 4);
			}
		}
		//
		// draw caret (if not first bar)
		//
		long absCaret = scoreBuilder.getScoreParameter().getCaret();
		if (!firstBarInTotal) {
			if (bar.getStartPosition() <= absCaret && absCaret < bar.getEndPosition()) {
				int relCursor = (int) ((absCaret - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
				// canvas.drawString("* " + firstBarInStaff + ", " + firstBarInTotal + ", " + barIndex, "lyrics", xBarPosition + offset + relCursor, yTop-50, "left");
				// caret
				int x = xBarPosition + offset + relCursor - 6;
				int y1 = ySystemTop - 12;
				int y2 = ySystemBottom + 12;
				if (staffIndex == 0) {
					canvas.drawLine(x, y1, x - 2, y1 - 2);
					canvas.drawLine(x, y1 - 1, x - 2, y1 - 2);
					canvas.drawLine(x, y1, x + 2, y1 - 2);
					canvas.drawLine(x, y1 - 1, x + 2, y1 - 2);
					canvas.drawLine(x, y1, x, y2);
					canvas.drawLine(x, y2, x - 2, y2 + 2);
					canvas.drawLine(x, y2 + 1, x - 2, y2 + 2);
					canvas.drawLine(x, y2, x + 2, y2 + 2);
					canvas.drawLine(x, y2 + 1, x + 2, y2 + 2);
				}
			}
		}

		//
		// draw grid
		//
		if (layout.showGrid()) {
			int subdivision = bar.getTimeSignature().getNumeratorSum();
			for (Trias trias = PositionTools.getTrias(bar.getTrack(), bar.getStartPosition()); trias.beat < subdivision; trias = trias.nextBeat()) {
				long pos = PositionTools.getPosition(bar.getTrack(), trias);
				int relPosition = (int) ((pos - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
				canvas.drawLine(xBarPosition + offset + relPosition, yTop+3, xBarPosition + offset + relPosition, yTop + 4 * layout.getLineHeight() -3, "grey");
				// System.out.println("=> xBarPosition: " + xBarPosition + ", offset: " + offset + ", relPos: " + relPosition + ", x: " + (xBarPosition + offset + relPosition)) ;
				// canvas.drawString("+"+trias.beat,"Arial",xBarPosition + offset + relPosition, yTop-5,"red");
			}
		}

		//
		// draw mouse frame
		//
		if (selection.hasMouseDown()) {
			int mouseStaff = selection.getMouseStaff();
			long mouseLeftPosition = selection.getMouseLeftPosition();
			long mouseRightPosition = selection.getMouseRightPosition();
			int relMouseLeftPosition = (int) ((mouseLeftPosition - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
			int relMouseRightPosition = (int) ((mouseRightPosition - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
			if (selection.getSelectionType() == SelectionType.NOTE) {
				// note selection - draws left and right borders
				// TODO: CHANGE SELECTION XXXXX
				if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition()) {
					// left:
					if (mouseStaff < 0) {
						// all staffs
						// canvas.drawLine(xBarPosition + offset + relMouseLeftPosition, ySystemTop, xBarPosition + offset + relMouseLeftPosition, ySystemBottom, "red");
						drawLeftMouseRangeIndicator(xBarPosition + offset + relMouseLeftPosition, ySystemTop, xBarPosition + offset + relMouseLeftPosition, ySystemBottom);
					} else if (mouseStaff == staffIndex) {
						// mouseStaff
						// canvas.drawLine(xBarPosition + offset + relMouseLeftPosition, yTop, xBarPosition + offset + relMouseLeftPosition, yTop + 4 * layout.getLineHeight(), "red");
						drawLeftMouseRangeIndicator(xBarPosition + offset + relMouseLeftPosition, yTop, xBarPosition + offset + relMouseLeftPosition, yTop + 4 * layout.getLineHeight());
					}
				}
				if (bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
					// right:
					if (mouseStaff < 0) {
						// all staffs
						// canvas.drawLine(xBarPosition + offset + relMouseRightPosition, ySystemTop, xBarPosition + offset + relMouseRightPosition, ySystemBottom, "red");
						drawRightMouseRangeIndicator(xBarPosition + offset + relMouseRightPosition, ySystemTop, xBarPosition + offset + relMouseRightPosition, ySystemBottom);
					} else if (mouseStaff == staffIndex) {
						// mouseStaff
						// canvas.drawLine(xBarPosition + offset + relMouseRightPosition, yTop, xBarPosition + offset + relMouseRightPosition, yTop + 4 * layout.getLineHeight(), "red");
						drawRightMouseRangeIndicator(xBarPosition + offset + relMouseRightPosition, yTop, xBarPosition + offset + relMouseRightPosition, yTop + 4 * layout.getLineHeight());
					}
				}
			} else if (selection.getSelectionType() == SelectionType.POSITION) {
				// Arrow for Symbol Events
				if (mouseStaff == staffIndex) {
					if (selection.getSelectionSubType() == CwnSelection.SelectionSubType.WITHIN_STAFF) {
						// BOW UP, BOW DOWN
						int y = yTop - layout.getSystemSpace() + selection.getPointer().getRelativeY() + 3;
						int mouseDeltaY = selection.getPointer().getDeltaY();

						// position selection - draws just a line at one y-position
						if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition() && bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// if all happens within one bar
							drawMouseRangePositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y + mouseDeltaY, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition()) {
							// left:
							drawMouseRangeLeftPositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y + (int) (mouseDeltaY * 0.5), xBarPosition + offset + xWidth);
						} else if (bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// right:
							drawMouseRangeRightPositionIndicator(xBarPosition, y + (int) (mouseDeltaY * 0.5), y + mouseDeltaY, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() > mouseLeftPosition && bar.getEndPosition() <= mouseRightPosition) {
							// complete bar:
							drawMouseRangeCenterPositionIndicator(xBarPosition, y + (int) (mouseDeltaY * 0.5), xBarPosition + offset + xWidth);
						}
					} else if (selection.getSelectionSubType() == CwnSelection.SelectionSubType.ABOVE_STAFF) {
						// ABOVE STAFF
						int y = yTop-10;
						if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition() && bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// if all happens within one bar
							drawMouseRangePositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition()) {
							// left:
							drawMouseRangeLeftPositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y, xBarPosition + offset + xWidth);
						} else if (bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// right:
							drawMouseRangeRightPositionIndicator(xBarPosition, y, y, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() > mouseLeftPosition && bar.getEndPosition() <= mouseRightPosition) {
							// complete bar:
							drawMouseRangeCenterPositionIndicator(xBarPosition, y, xBarPosition + offset + xWidth);
						}
					} else if (selection.getSelectionSubType() == CwnSelection.SelectionSubType.BELOW_STAFF_RANGE) {
						// BELOW STAFF
						int y = yTop+4*layout.getLineHeight()+10;
						if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition() && bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// if all happens within one bar
							drawMouseRangePositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition()) {
							// left:
							drawMouseRangeLeftPositionIndicator(xBarPosition + offset + relMouseLeftPosition, y, y, xBarPosition + offset + xWidth);
						} else if (bar.getStartPosition() <= mouseRightPosition && mouseRightPosition < bar.getEndPosition()) {
							// right:
							drawMouseRangeRightPositionIndicator(xBarPosition, y, y, xBarPosition + offset + relMouseRightPosition);
						} else if (bar.getStartPosition() > mouseLeftPosition && bar.getEndPosition() <= mouseRightPosition) {
							// complete bar:
							drawMouseRangeCenterPositionIndicator(xBarPosition, y, xBarPosition + offset + xWidth);
						}
					} else if (selection.getSelectionSubType() == CwnSelection.SelectionSubType.BELOW_STAFF_POINT) {
						// BELOW STAFF POINT
						int y = yTop+4*layout.getLineHeight()+10;
						if (bar.getStartPosition() <= mouseLeftPosition && mouseLeftPosition < bar.getEndPosition()) {
							int x = xBarPosition + offset + relMouseLeftPosition;
							canvas.drawLine(x-2, y-2, x+2, y+2, "red");
							canvas.drawLine(x-2, y+2, x+2, y-2, "red");
						}
					}
				}
			}
		}
		
		//
		// draw symbols
		//
		for (CwnSymbolEvent event : bar.getSymbols()) {
			int xStart = (int) ((event.getPosition() - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
			int xEnd = (int) ((event.getPosition() + event.getDuration() - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration());
			int y = yTop - layout.getSystemSpace() ;//+ event.getVerticalOffset();
			boolean alternative = selection.contains(event);
			int eventParameter = event.getParameter();
			int eventVerticalOffset = event.getVerticalOffset();
			if (event.isCrescendo()) {
				//
				// Crescendo
				//
				y += layout.getStaffHeight() - 4 + eventVerticalOffset;
				if (alternative) {
					canvas.drawLine(xBarPosition + offset + xStart, y, xBarPosition + offset + xEnd, y - 4, "red");
					canvas.drawLine(xBarPosition + offset + xStart, y, xBarPosition + offset + xEnd, y + 4, "red");
					canvas.drawLine(xBarPosition + offset + xStart + 2, y, xBarPosition + offset + xEnd + 2, y - 4, "red");
					canvas.drawLine(xBarPosition + offset + xStart + 2, y, xBarPosition + offset + xEnd + 2, y + 4, "red");
				} else {
					canvas.drawLine(xBarPosition + offset + xStart, y, xBarPosition + offset + xEnd, y - 4);
					canvas.drawLine(xBarPosition + offset + xStart, y, xBarPosition + offset + xEnd, y + 4);
					canvas.drawLine(xBarPosition + offset + xStart + 2, y, xBarPosition + offset + xEnd + 2, y - 4);
					canvas.drawLine(xBarPosition + offset + xStart + 2, y, xBarPosition + offset + xEnd + 2, y + 4);
				}
			} else if (event.isDecrescendo()) {
				//
				// Decrescendo
				//
				y += layout.getStaffHeight() - 4 + eventVerticalOffset;
				if (alternative) {
					canvas.drawLine(xBarPosition + offset + xStart, y - 4, xBarPosition + offset + xEnd, y, "red");
					canvas.drawLine(xBarPosition + offset + xStart, y + 4, xBarPosition + offset + xEnd, y, "red");
					canvas.drawLine(xBarPosition + offset + xStart, y - 4, xBarPosition + offset + xEnd + 2, y, "red");
					canvas.drawLine(xBarPosition + offset + xStart, y + 4, xBarPosition + offset + xEnd + 2, y, "red");
				} else {
					canvas.drawLine(xBarPosition + offset + xStart, y - 4, xBarPosition + offset + xEnd, y);
					canvas.drawLine(xBarPosition + offset + xStart, y + 4, xBarPosition + offset + xEnd, y);
					canvas.drawLine(xBarPosition + offset + xStart + 2, y - 4, xBarPosition + offset + xEnd + 2, y);
					canvas.drawLine(xBarPosition + offset + xStart + 2, y + 4, xBarPosition + offset + xEnd + 2, y);
				}
			} else if (event.isBowUp()) {
				//
				// Bowup
				//
				int xoffset = 4;
				int yoffset = 10;
				int yAdd = 0;
				// System.out.println("=> " + vOffset + ", " + delta);
				// canvas.drawArc(xBarPosition + offset + xStart + xoffset, y+yAdd, xBarPosition + offset + xEnd + xoffset, y +yAdd - delta, 1, yoffset, alternative);
				canvas.drawArc(xBarPosition + offset + xStart + xoffset,
						y+yAdd+eventVerticalOffset,
						xBarPosition + offset + xEnd + xoffset,
						y +yAdd + eventVerticalOffset + eventParameter,
						1, yoffset, alternative);
			} else if (event.isBowDown()) {
				//
				// Bowdown
				//
				int xoffset = 4;
				int yoffset = +10;
				int yAdd = 0;//-30 - layout.getLineHeight()*5;
				// canvas.drawArc(xBarPosition + offset + xStart + xoffset, y-yAdd, xBarPosition + offset + xEnd + xoffset, y -yAdd - delta, -1, yoffset, alternative);
				canvas.drawArc(xBarPosition + offset + xStart + xoffset,
						y+yAdd+eventVerticalOffset,
						xBarPosition + offset + xEnd + xoffset,
						y +yAdd + eventVerticalOffset + eventParameter,
						-1, yoffset, alternative);
			} else if (event.isOctave()) {
				if (event.getSymbolName().equals("o"+CwnSymbolEvent.SYMBOL_8VA)) canvas.drawString("8va", "nole", xBarPosition + offset + xStart -2, y+6, "left", alternative);
				if (event.getSymbolName().equals("o"+CwnSymbolEvent.SYMBOL_15VA)) canvas.drawString("15va", "nole", xBarPosition + offset + xStart -2, y+6, "left", alternative);
				canvas.drawLine(xBarPosition + offset + xStart + 16, y, xBarPosition + offset + xEnd, y, alternative);
				canvas.drawLine(xBarPosition + offset + xEnd, y, xBarPosition + offset + xEnd, y+4, alternative);
			} else if (event.isCase()) {
				if (event.getSymbolName().equals(CwnSymbolEvent.SYMBOL_CASE1)) canvas.drawString("1.", "nole", xBarPosition + offset + xStart -2, y+16, "left", alternative);
				if (event.getSymbolName().equals(CwnSymbolEvent.SYMBOL_CASE2)) canvas.drawString("2.", "nole", xBarPosition + offset + xStart -2, y+16, "left", alternative);
				// canvas.drawLine(xBarPosition + offset + xStart - 4, y, xBarPosition + offset + xWidth, y, alternative);
				// canvas.drawLine(xBarPosition + offset + xStart - 4, y, xBarPosition + offset + xStart - 4, y + 16, alternative);
				canvas.drawLine(xBarPosition + offset - 4, y, xBarPosition + offset + xEnd, y, alternative);
				canvas.drawLine(xBarPosition + offset - 4, y, xBarPosition + offset - 4, y + 16, alternative);
			} else {
				y += layout.getStaffHeight() - 10 + eventVerticalOffset;
				canvas.drawImage(event.getSymbolName(), xBarPosition + offset + xStart + eventParameter, y, alternative);
			}
		}
		//
		// Intervals, Crossings, Parallels, Riemann, etc...
		//
		Map<Long, List<String>> intervalMap = scoreBuilder.getScoreParameter().markupMap;
		boolean requiresOnBeat = scoreBuilder.getScoreParameter().markup.contains(Markup.Type.HARMONY) || scoreBuilder.getScoreParameter().markup.contains(Markup.Type.RIEMANN);
		if (!intervalMap.isEmpty() && staffIndex==0) {
			for (long pos = bar.getStartPosition(); pos < bar.getEndPosition(); pos = pos + scoreBuilder.getScoreParameter().resolutionInTicks) {
				boolean isOnBeat = pos%scoreBuilder.getScoreParameter().ppq==0;
				if (!requiresOnBeat || isOnBeat) {
					List<String> markList = intervalMap.get(pos);
					if (markList != null) {
						int relPosition = (int) ((pos - bar.getStartPosition()) * xWidth * 1.0 / bar.getDuration()) + 2;
						int len = markList.size();
						for (int i = 0; i < len; i++) {
							String markup = markList.get(i);
							boolean markupBold = markup.indexOf("!") > 0;
							if (markupBold) {
								markup = markup.replace("!", "");
							}
							String markupPattern = "^([^[_^]]+)(\\_([^^]+))?(\\^([^!]+))?$";
							String markupBase = markup.replaceAll(markupPattern, "$1");
							String markupSub = markup.replaceAll(markupPattern, "$3");
							String markupSuper = markup.replaceAll(markupPattern, "$5");
							int markupBaseOffset = markupBase.length() - 1;
							int markX = xBarPosition + offset + relPosition;
							// int markY = ySystemBottom + (int) (0.4 * layout.getStaffHeight() - i*12) + 6;
							int markY = ySystemBottom + (int) (0.4 * layout.getStaffHeight() - i * 8);
							canvas.drawString(markupBase, markupBold ? "markupBold" : "markup", markX, markY, "left");
							if (!markupSub.equals("")) {
								canvas.drawString(markupSub, "nole", markX + (4 * markupBaseOffset) + 7, markY + 3, "left");
							}
							if (!markupSuper.equals("")) {
								canvas.drawString(markupSuper, "nole", markX + (4 * markupBaseOffset) + 9, markY - 3, "left");
							}
						}
					}
				}
			}
		}
	}
	
	private void drawMouseRangePositionIndicator(int xLeft, int yTop, int yBottom, int xRight) {
		canvas.drawLine(xLeft, yTop, xRight, yBottom, "red"); // line
		canvas.drawLine(xLeft - 2, yTop - 2, xLeft - 2, yTop - 2 + 5); // start box
		canvas.drawLine(xRight, yBottom, xRight - 3, yBottom - 3); // end (arrow)
		canvas.drawLine(xRight, yBottom, xRight - 3, yBottom + 3); // end (arrow)
	}
	
	private void drawMouseRangeLeftPositionIndicator(int xLeft, int yTop, int yBottom, int xRight) {
		canvas.drawLine(xLeft, yTop, xRight + 1, yBottom, "red"); // line
		canvas.drawLine(xLeft - 2, yTop - 2, xLeft - 1, yTop - 2 + 5); // start box
	}
	
	private void drawMouseRangeRightPositionIndicator(int xLeft, int yTop, int yBottom, int xRight) {
		canvas.drawLine(xLeft, yTop, xRight, yBottom, "red"); // line
		canvas.drawLine(xRight, yBottom, xRight - 3, yBottom - 3); // end (arrow)
		canvas.drawLine(xRight, yBottom, xRight - 3, yBottom + 3); // end (arrow)
	}
	
	private void drawMouseRangeCenterPositionIndicator(int xLeft, int yTop, int xRight) {
		canvas.drawLine(xLeft, yTop, xRight + 1, yTop, "red"); // line
	}
	
	private void drawLeftMouseRangeIndicator(int xLeft, int yTop, int xRight, int yBottom) {
		canvas.drawLine(xLeft, yTop - 10, xRight, yBottom + 10, "red");
		canvas.drawLine(xLeft, yTop - 10, xRight + 5, yTop - 11, "red");
		canvas.drawLine(xLeft, yBottom + 10, xRight + 5, yBottom + 11, "red");
	}
	
	private void drawRightMouseRangeIndicator(int xLeft, int yTop, int xRight, int yBottom) {
		canvas.drawLine(xLeft, yTop - 10, xRight, yBottom + 10, "red");
		canvas.drawLine(xLeft, yTop - 10, xRight - 5, yTop - 11, "red");
		canvas.drawLine(xLeft, yBottom + 10, xRight - 5, yBottom + 11, "red");
	}
	
	private void drawVoice(int systemIndex, int staffIndex, int barIndex, int voiceIndex, ScoreVoice voice, Metric metric, double xBarPosition, double xWidth, boolean multiVoiceBar) {
		int yTop = layout.getBorder() + layout.getTitleHeight() + (staffIndex + systemIndex * scoreBuilder.getNumberOfTracks()) * layout.getStaffHeight();
		int yBase = yTop + layout.getSystemSpace() + 1;
		for (CharacterGroup characterGroup : voice.getCharacterGroups()) {
			//
			// duration character group "characterGroup"
			//
			double characterGroupRelativePosition = round(characterGroup.getRelativePosition() );/// metric.duration());
			double characterGroupRelativeDuration = round(characterGroup.getRelativeDuration() );/// metric.duration());

			int characterGroupStartPosition = (int) (xBarPosition + characterGroupRelativePosition * xWidth);
			if (debug) {
				int characterGroupEndPosition = (int) (characterGroupStartPosition + characterGroupRelativeDuration * xWidth - 3);
				canvas.drawLine(characterGroupStartPosition, yTop - 2, characterGroupEndPosition, yTop - 2, "green");
				canvas.drawLine(characterGroupStartPosition, yTop - 2, characterGroupStartPosition, yTop - 4);
				canvas.drawLine(characterGroupEndPosition, yTop - 2, characterGroupEndPosition, yTop - 4);
			}
			
			final double characterGroupRelativeEndPosition = round(characterGroupRelativePosition + characterGroupRelativeDuration);
			TreeSet<ScoreObject> scoreObjectSet = (TreeSet<ScoreObject>) voice.getScoreObjectSet().stream()
					.filter(so -> (round(so.getRelativePosition()) >= characterGroupRelativePosition) && (round(so.getRelativePosition()) < characterGroupRelativeEndPosition))
					.collect(Collectors.toCollection(TreeSet::new));
			if (!scoreObjectSet.isEmpty()) {
				ScoreObject firstObject = scoreObjectSet.first();
				ScoreObject lastObject = scoreObjectSet.last();
				ScoreObject firstNoteOrChord = scoreObjectSet.stream().filter(so -> !so.isRest()).findFirst().orElse(null);
				if (firstNoteOrChord == null) break;
				int stemDirection = voice.getStemDirection() != 0 ? voice.getStemDirection() : firstNoteOrChord.getStemDirection();
				int beamDirection = getBeamDirection(scoreObjectSet);
				int xPositionFirst = getXPosition(firstObject, xBarPosition, xWidth, stemDirection, 0);
				int xPositionLast = getXPosition(lastObject, xBarPosition, xWidth, stemDirection, 0);
				int xPositionCenter = (int) (0.5 * (xPositionFirst + xPositionLast));
				int centerPitch = (int) (scoreObjectSet.stream().filter(so -> !so.isRest()).mapToDouble(so -> so.getAveragePitch()).average().orElse(0));
				if (centerPitch > 0) {
					int beamDeltaY = (int) (Score.STEM_SLOPE * (xPositionLast - xPositionFirst) * beamDirection);
					int yPositionCenter = yBase + firstNoteOrChord.getY(centerPitch, 0, firstNoteOrChord.getMinimumNote().getClef()) * 3 - 42 + (stemDirection > 0 ? 0 : 2) - stemDirection * (Score.STEM_LENGTH + 7)
							+ beamDeltaY;
					int yPositionFirst = (int) (yPositionCenter - (Score.STEM_SLOPE * 2 * beamDirection * (xPositionFirst - xPositionCenter)));
					int yPositionLast = (int) (yPositionCenter - (Score.STEM_SLOPE * 2 * beamDirection * (xPositionLast - xPositionCenter)));
					int xPositionCenterLeft = xPositionCenter - (layout.hasFullTupletPresentation() ? 8 : 6);
					int xPositionCenterRight = xPositionCenter + (layout.hasFullTupletPresentation() ? 8 : 6);
					int yPositionCenterLeft = (int) (yPositionCenter - (Score.STEM_SLOPE * 2 * beamDirection * (xPositionCenterLeft - xPositionCenter)));
					int yPositionCenterRight = (int) (yPositionCenter - (Score.STEM_SLOPE * 2 * beamDirection * (xPositionCenterRight - xPositionCenter)));
					canvas.drawLine(xPositionFirst, yPositionFirst, xPositionCenterLeft, yPositionCenterLeft);
					canvas.drawLine(xPositionCenterRight, yPositionCenterRight, xPositionLast, yPositionLast);
					canvas.drawLine(xPositionFirst, yPositionFirst, xPositionFirst, yPositionFirst + stemDirection * 2);
					canvas.drawLine(xPositionLast, yPositionLast, xPositionLast, yPositionLast + stemDirection * 2);
					canvas.drawString("" + (layout.hasFullTupletPresentation() ? characterGroup.getFullCharacter() : characterGroup.getCharacter()),
							"nole",
							xPositionCenter,
							yPositionCenter + 4 /*- stemDirection * 4*/,
							"center");
				}
			}
		}
		for (ScoreGroup masterGroup : voice.getGroups()) {
			//
			// 1st level metric groups "masterGroup"
			//
			int masterGroupStartPosition = (int) (xBarPosition + masterGroup.getRelativeStartPosition() * xWidth);
			if (debug) {
				int masterGroupEndPosition = (int) (masterGroupStartPosition + masterGroup.getRelativeDuration() * xWidth - 3);
				canvas.drawLine(masterGroupStartPosition, yTop, masterGroupEndPosition, yTop, "blue");
				canvas.drawLine(masterGroupStartPosition, yTop, masterGroupStartPosition, yTop + 6, "blue");
				canvas.drawLine(masterGroupEndPosition, yTop, masterGroupEndPosition, yTop + 6, "blue");
			}
			for (ScoreGroup beamGroup : masterGroup.getSubGroups()) {
				//
				// groupable groups "beamGroup"
				//
				int beamGroupSize = beamGroup.size();
				int beamGroupStartPosition = (int) (xBarPosition + beamGroup.getRelativeStartPosition() * xWidth);
				if (debug) {
					int beamGroupEndPosition = (int) (beamGroupStartPosition + beamGroup.getRelativeDuration() * xWidth - 3);
					canvas.drawLine(beamGroupStartPosition + 2, yTop + 2, beamGroupEndPosition - 3, yTop + 2, "brown");
					canvas.drawLine(beamGroupStartPosition + 2, yTop + 3, beamGroupStartPosition + 2, yTop + 3, "brown");
					canvas.drawLine(beamGroupEndPosition - 3, yTop + 3, beamGroupEndPosition - 3, yTop + 3, "brown");
				}
				for (ScoreGroup splitGroup : beamGroup.getSubGroups()) {
					//
					// 2nd level metric groups "splitGroup"
					//
					int splitGroupStartPosition = (int) (xBarPosition + splitGroup.getRelativeStartPosition() * xWidth);
					if (debug) {
						int splitGroupEndPosition = (int) (splitGroupStartPosition + splitGroup.getRelativeDuration() * xWidth - 3);
						canvas.drawLine(splitGroupStartPosition + 2, yTop + 5, splitGroupEndPosition - 3, yTop + 5, "red");
						canvas.drawLine(splitGroupStartPosition + 2, yTop + 6, splitGroupStartPosition + 2, yTop + 6, "red");
						canvas.drawLine(splitGroupEndPosition - 3, yTop + 6, splitGroupEndPosition - 3, yTop + 6, "red");
					}
					for (ScoreObject scoreObject : splitGroup.getObjectSet()) {
						// int xPosition = (int) (xBarPosition + scoreObject.getRelativePosition() * xWidth);
						int xPosition = getXPosition(scoreObject, xBarPosition, xWidth, 0, 0) - 2;
						if (scoreObject.isRest()) {
							drawRest((ScoreRest) scoreObject, xPosition, yBase - voice.getVoiceLocation() * 6, xWidth);
						} else if (scoreObject.isChord()) {
							drawChord((ScoreChord) scoreObject, xPosition, yBase, beamGroupSize, xWidth, multiVoiceBar);
							drawMarks(xPosition, yTop, (ScoreChord) scoreObject);
						} else {
							throw new RuntimeException("unexpected scoreObject type: " + scoreObject);
						}
					}
				}
				if (beamGroupSize > 1) {
					//
					// first beam
					//
					int stemDirection = voice.getStemDirection() != 0 ? voice.getStemDirection() : beamGroup.getStemDirection();
					int beamDirection = getBeamDirection(beamGroup.getObjectSet());// beamGroup.getBeamDirection();
					ScoreNote firstBeamNote = (stemDirection > 0 ? beamGroup.first().getMaximumNote() : beamGroup.first().getMinimumNote());
					ScoreNote lastBeamNote = (stemDirection > 0 ? beamGroup.last().getMaximumNote() : beamGroup.last().getMinimumNote());
					// int xPositionFirst = (int) (xBarPosition + firstBeamNote.getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : 0));
					// int xPositionLast = (int) (xBarPosition + lastBeamNote.getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : 0));
					int xShiftFirst = ((beamGroup.first() instanceof ScoreChord) && ((ScoreChord) beamGroup.first()).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0);
					int xShiftLast = ((beamGroup.last() instanceof ScoreChord) && ((ScoreChord) beamGroup.last()).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0);
					int xPositionFirst = getXPosition(firstBeamNote, xBarPosition, xWidth, stemDirection, xShiftFirst);
					int xPositionLast = getXPosition(lastBeamNote, xBarPosition, xWidth, stemDirection, xShiftLast);
					int centerPitch = (int) (0.5 * (firstBeamNote.getPitch() + lastBeamNote.getPitch()));
					int beamDeltaY = (int) (Score.STEM_SLOPE * (xPositionLast - xPositionFirst) * beamDirection);
					int yPositionFirst = yBase + firstBeamNote.getY(centerPitch, firstBeamNote.getEnharmonicShift(), firstBeamNote.getClef()) * 3 - 42 + (stemDirection > 0 ? 0 : 2) - stemDirection * Score.STEM_LENGTH
							+ beamDeltaY;
					// int yPositionLast = yBase + lastBeamNote.getY(centerPitch, lastBeamNote.getEnharmonicShift(), lastBeamNote.getClef()) * 3 - 42 + (stemDirection > 0 ? 0 : 2) - stemDirection * Score.STEM_LENGTH- beamDeltaY;
					int yBeamStart = 0;
					int yBeamEnd = 0;
					// stems:
					for (ScoreObject scoreObject : beamGroup.getObjectSet()) {
						int xShift = ((scoreObject instanceof ScoreChord) && ((ScoreChord) scoreObject).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0);
						// int xPosition = (int) (xBarPosition + scoreObject.getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : xShift));
						int xPosition = getXPosition(scoreObject, xBarPosition, xWidth, stemDirection, xShift);
						int yPositionHead = yBase + (stemDirection > 0 ? scoreObject.getMinimumNote() : scoreObject.getMaximumNote()).getY() * 3 - 42 + (stemDirection > 0 ? 2 : 2);
						int yPositionBeam = (int) (yPositionFirst - (Score.STEM_SLOPE * 2 * beamDirection * (xPosition - xPositionFirst))) + 1;
						if (yBeamStart == 0) {
							yBeamStart = yPositionBeam;
						} else {
							yBeamEnd = yPositionBeam;
						}
						canvas.drawLine(xPosition, yPositionHead, xPosition, yPositionBeam);
					}
					
					// beam of 1st level
					// canvas.drawLine(xPositionFirst, yPositionFirst, xPositionLast, yPositionLast, 2);
					canvas.drawLine(xPositionFirst, yBeamStart, xPositionLast, yBeamEnd, 2);

					// beams of higher level (16th, 32nd, etc)
					for (ScoreGroup splitGroup : beamGroup.getSubGroups()) {
						ScoreObject firstObject = splitGroup.first();
						ScoreObject lastObject = splitGroup.last();
						ScoreObject[] scoreObjectArray = splitGroup.getObjectSet().toArray(new ScoreObject[] {});
						ScoreObject scoreObject;
						
						int len = scoreObjectArray.length;
						int xStart;
						int xEnd;
						int yStart;
						int yEnd;
						for (int a = 0; a < len; a++) {
							// System.out.println(" a: " + a + ", len: " + len);
							scoreObject = scoreObjectArray[a];
							// flags
							int numberOfFlags = scoreObject.getNumberOfFlags();

							int previousNumberOfFlags = (a > 0 ? scoreObjectArray[a - 1].getNumberOfFlags() : 0);
							int nextNumberOfFlags = (a < len - 1 ? scoreObjectArray[a + 1].getNumberOfFlags() : 0);
							// x
							// double xPosition = xBarPosition + scoreObject.getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : 0);
							// double xPreviousPosition = (a > 0 ? xBarPosition + scoreObjectArray[a - 1].getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : 0) : 0);
							// double xNextPosition = (a < len - 1 ? xBarPosition + scoreObjectArray[a + 1].getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : 0) : 0);
							int xShift = ((scoreObject instanceof ScoreChord) && ((ScoreChord) scoreObject).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0);
							int xShiftPrev = (a > 0 ? ((scoreObjectArray[a - 1] instanceof ScoreChord) && ((ScoreChord) scoreObjectArray[a - 1]).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0) : 0);
							int xShiftNext = (a < len - 1 ? ((scoreObjectArray[a + 1] instanceof ScoreChord) && ((ScoreChord) scoreObjectArray[a + 1]).hasShiftedNotes() ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0) : 0);
							int xPosition = getXPosition(scoreObject, xBarPosition, xWidth, stemDirection, xShift);
							int xPreviousPosition = (a > 0 ? getXPosition(scoreObjectArray[a - 1], xBarPosition, xWidth, stemDirection, xShiftPrev) : 0);
							int xNextPosition = (a < len - 1 ? getXPosition(scoreObjectArray[a + 1], xBarPosition, xWidth, stemDirection, xShiftNext) : 0);
							// y
							double yPositionBeam = yPositionFirst - (Score.STEM_SLOPE * 2 * beamDirection * (xPosition - xPositionFirst));
							double yPreviousPositionBeam = yPositionFirst - (Score.STEM_SLOPE * 2 * beamDirection * (xPreviousPosition - xPositionFirst));
							double yNextPositionBeam = yPositionFirst - (Score.STEM_SLOPE * 2 * beamDirection * (xNextPosition - xPositionFirst));
							//
							double slope = (yPositionBeam - yPreviousPositionBeam) / (xPosition - xPreviousPosition);
							for (int n = 1; n < numberOfFlags; n++) {
								boolean drawLeft = (scoreObject != firstObject && (previousNumberOfFlags > n || scoreObject == lastObject));
								boolean drawRight = (scoreObject != lastObject && (nextNumberOfFlags > n || scoreObject == firstObject || previousNumberOfFlags < n));
								boolean connectLeft = (previousNumberOfFlags > n);
								boolean connectRight = (nextNumberOfFlags > n);
								
								double factorLeft = previousNumberOfFlags > n ? 0.5 : 0.3;
								double factorRight = nextNumberOfFlags > n ? 0.5 : 0.3;
								
								int flagDelta = stemDirection * n * 4;
								if (!drawLeft && !drawRight) {
									// default - this may be not always applicable?
									drawRight = true;
								}
								// xStart = (int) ((drawLeft ? 0.5 * (xPreviousPosition + xPosition) : xPosition));
								// yStart = (int) ((drawLeft ? 0.5 * (yPreviousPositionBeam + yPositionBeam) : yPositionBeam));
								// xEnd = (int) ((drawRight ? 0.5 * (xPosition + xNextPosition) : xPosition));
								// yEnd = (int) ((drawRight ? 0.5 * (yPositionBeam + yNextPositionBeam) : yPositionBeam));
								
								// xStart = (int) ((drawLeft ? factorLeft * xPreviousPosition + (1 - factorLeft) * xPosition : xPosition));
								// yStart = (int) ((drawLeft ? factorLeft * yPreviousPositionBeam + (1 - factorLeft) * yPositionBeam : yPositionBeam));
								// xEnd = (int) ((drawRight ? (1 - factorRight) * xPosition + factorRight * xNextPosition : xPosition));
								// yEnd = (int) ((drawRight ? (1 - factorRight) * yPositionBeam + factorRight * yNextPositionBeam : yPositionBeam));
								
								// LEFT
								if (drawLeft) {
									if (connectLeft) {
										xStart = (int) (0.5 * xPreviousPosition + 0.5 * xPosition);
										yStart = (int) (0.5 * yPreviousPositionBeam + 0.5 * yPositionBeam);
									} else {
										xStart = (int) (xPosition - unconnectedBeamWidth);
										yStart = (int) (yPositionBeam - unconnectedBeamWidth * slope);
									}
								} else {
									xStart = xPosition;
									yStart = (int) yPositionBeam;
								}
								// RIGHT
								if (drawRight) {
									if (connectRight) {
										xEnd = (int) (0.5 * xPosition + 0.5 * xNextPosition);
										yEnd = (int) (0.5 * yPositionBeam + 0.5 * yNextPositionBeam);
									} else {
										xEnd = (int) (xPosition + unconnectedBeamWidth);
										yEnd = (int) (yPositionBeam + unconnectedBeamWidth * slope);
									}
								} else {
									xEnd = xPosition;
									yEnd = (int) yPositionBeam;
								}
								
								canvas.drawLine(xStart, yStart + flagDelta, xEnd, yEnd + flagDelta, 2);
								// System.out.println(" -> " + xStart + "-" + xEnd);
								// System.out.println(" " + (1 - factorRight) + "*" + xPosition + "+" + factorRight + "*" + xNextPosition);
							}
						}
					}
				}
			}
		}
	}
	
	private void drawRest(ScoreRest rest, int xPosition, int yBase, double xWidth) {
		int base = rest.getDurationBase();
		int dots = rest.getNumberOfDots();
		int centerRest = 0;
		if (base <= 4) {
			// for whole, half and quarter rest:
			centerRest = (int) (0.27 * rest.getRelativeDuration()*xWidth);
		}
		canvas.drawImage("rest" + base, xPosition + 1 + centerRest, yBase, false);
		for (int i = 0; i < dots; i++) {
			canvas.drawDot(xPosition + centerRest + 13 + i * 4, yBase + 8);
		}
	}
	
	private void drawChord(ScoreChord chord, int xPosition, int yBase, int beamGroupSize, double xWidth, boolean multivoice) {
		String lyrics = "";
		int velocity = 0;
		boolean hasEndTie = false;
		int accentPosition = (chord.getStemDirection() > 0 ? Integer.MAX_VALUE : 0);
		List<CwnAccent> accentList = new ArrayList<>();
		boolean alternative = false;
		if (chord.size() > 1) {
			int yNoteBottom = yBase + chord.getMinimumNote().getY() * 3 - 42;
			int yNoteTop = yBase + chord.getMaximumNote().getY() * 3 - 42;
			int base = chord.getDurationBase();
			int dots = chord.getNumberOfDots();
			for (ScoreNote note : chord.getObjectSet()) {
				alternative = selection.contains(note.getCwnNoteEvent());
				hasEndTie = note.hasEndTie();
				lyrics += note.getLyrics();
				velocity = note.getCwnNoteEvent().getVelocity();
				int yNote = yBase + note.getY() * 3 - 42;
				int xShift = note.getHorizontalShift();
				// draw
				drawHead(xPosition + xShift, yNote, note, base, dots);
				drawHelpLines(xPosition + xShift, yBase, yNote, note);
				// tie?
				if (note.hasStartTie()) {
					drawArc(note, xPosition, yNote, yNote,xWidth - 14, alternative, multivoice);
				}
				if (note.getCwnNoteEvent().hasAccents()) {
					accentList.addAll(note.getCwnNoteEvent().getAccentList());
					if (chord.getStemDirection() > 0) {
						accentPosition = Math.min(accentPosition, yNote + 10);
					} else {
						accentPosition = Math.max(accentPosition, yNote - 10);
					}
				}
			}
			if (beamGroupSize == 1) {
				int xShift = (chord.hasShiftedNotes() && chord.getStemDirection() < 0? (int) Score.NOTE_HEAD_WIDTH : 0);
				drawStem(xPosition + xShift, yNoteBottom, yNoteTop, chord);
			}
		} else {
			ScoreNote note = chord.getObjectSet().first();
			alternative = selection.contains(note.getCwnNoteEvent());
			hasEndTie = note.hasEndTie();
			lyrics += note.getLyrics();
			velocity = note.getCwnNoteEvent().getVelocity();
			int yNote = yBase + note.getY() * 3 - 42;
			int base = note.getDurationBase();
			int dots = note.getNumberOfDots();
			drawHead(xPosition, yNote, note, base, dots);
			drawHelpLines(xPosition, yBase, yNote, note);
			// tie?
			if (note.hasStartTie()) {
				drawArc(note, xPosition, yNote, yNote, xWidth - 14, alternative, multivoice);
			}
			if (beamGroupSize == 1) {
				drawStem(xPosition, yNote, yNote, note);
			}
			if (note.getCwnNoteEvent().hasAccents()) {
				accentList.addAll(note.getCwnNoteEvent().getAccentList());
				if (chord.getStemDirection() > 0) {
					accentPosition = yNote + 10;
				} else {
					accentPosition = yNote - 10;
				}
			}
		}
		int vCenter = (int) ((layout.getLineHeight() * 4) + (layout.getSystemSpace()));
		if (!hasEndTie) {
			if (scoreBuilder.getScoreParameter().markup.contains(Markup.Type.LYRICS) && !"".equals(lyrics)) {
				canvas.drawString(lyrics, "lyrics", xPosition, yBase + vCenter + 4, "center", alternative);
			}
			if (layout.showVelocity()) {
				canvas.drawLine(xPosition + 3, yBase + vCenter + 4, xPosition + 3, yBase + vCenter + 4 - (int) (velocity * (15.0 / 127.0)));
				canvas.drawLine(xPosition + 4, yBase + vCenter + 4, xPosition + 4, yBase + vCenter + 4 - (int) (velocity * (15.0 / 127.0)));
			}
			//}
			if (accentPosition > 0 && accentPosition < Integer.MAX_VALUE) {
				for (CwnAccent accent : accentList) {
					if (accent.getName().equals(CwnAccent.ACCENT_FERMATA) || accent.getName().equals(CwnAccent.ACCENT_SHORTFERMATA) || accent.getName().equals(CwnAccent.ACCENT_LONGFERMATA)) {
						accentPosition = Math.min(yBase - 14, accentPosition);
					}
					canvas.drawImage(accent.getName(), xPosition - 2, accentPosition - 2, false);
				}
			}
			// canvas.drawLine(xPosition, yBase + vCenter, xPosition + 20, yBase + vCenter);
		}
	}

	private void drawMarks(int xPosition, int yBase, ScoreChord chord) {
		int count = 0;
		for (String mark : chord.getMarkList()) {
			canvas.drawString(mark, "lyrics", xPosition, yBase + count++ * 12, "left");
		}
	}

	private void drawArc(ScoreNote note, int xPosition, int yNote1, int yNote2, double xWidth, boolean alternative, boolean multivoice) {
		// int x = xPosition + 7;
		// int y = yNote + 3 + note.getStemDirection() * 6;
		// int dur = (int) (note.getRelativeDuration() * xWidth);
		// canvas.drawLine(x, y, x + dur, y);
		int x = xPosition + 7;
		int y1 = yNote1 + 3 - note.getStemDirection() * 6;
		int y2 = yNote2 + 3 - note.getStemDirection() * 6;
		int dur = (int) (note.getRelativeDuration() * xWidth) + 6;
		// canvas.drawArc(x, y, x + dur, note.getStemDirection(), alternative);
		int direction;
		if (multivoice) {
			direction = note.getVoice()==1 ? -1 : +1;
			y2 = y2-5;
			y1 = y1-5;
			x = x + 2;
		} else {
			direction = -note.getStemDirection();
			if (direction>0) {
				y1 -= 6;
				y2 -= 6;
			}
		}
		canvas.drawArc(x, y1, x + dur, y2, direction, 0, alternative);
	}
	
	private void drawHelpLines(int xPosition, int yBase, int yPosition, ScoreNote note) {
		//
		// draw helplines
		//
		int xStart = xPosition - 1;
		int xEnd = (int) (xPosition + Score.NOTE_HEAD_WIDTH + 5);
		if (yPosition < yBase) {
			for (int y = yBase; y - 1 > yPosition; y -= layout.getLineHeight()) {
				canvas.drawLine(xStart, y - 1, xEnd, y - 1);
			}
		} else if (yPosition > yBase + layout.getLineHeight() * 4) {
			for (int y = yBase + layout.getLineHeight() * 5; y < yPosition + layout.getLineHeight(); y += layout.getLineHeight()) {
				canvas.drawLine(xStart, y - 1, xEnd, y - 1);
			}
		}
	}
	
	private void drawHead(int xPosition, int yPosition, ScoreNote note, int base, int dots) {
		int sign = note.getSign();
		String name = "head" + base;
		boolean alternative = selection.contains(note.getCwnNoteEvent());
		canvas.drawImage(name, xPosition + 2, yPosition, alternative);
		if (scoreBuilder.getScoreParameter().markup.contains(Markup.Type.COLOR_VOICES)) {
			canvas.drawDot(xPosition + 2, yPosition, note.getVoice());
		}
		if (sign != 0) {
			canvas.drawImage("sign" + sign, xPosition - 8, yPosition - 9, false);
		}
		for (int i = 0; i < dots; i++) {
			canvas.drawDot(xPosition + 12 + i * 4, yPosition);
		}
	}
	
	private void drawStem(int xPosition, int yPositionBottom, int yPositionTop, ScoreObject scoreObject) {
		if (scoreObject.getDurationBase() > 1) {
			int stemDirection = scoreObject.getStemDirection();
			if (stemDirection > 0) {
				xPosition += Score.NOTE_HEAD_WIDTH;
				int yTemp = yPositionTop;
				yPositionTop = yPositionBottom;
				yPositionBottom = yTemp;
			}
			xPosition += 2;
			yPositionTop += 2;
			yPositionBottom += 2;

			canvas.drawLine(xPosition, yPositionTop, xPosition, yPositionBottom + -1 * Score.STEM_LENGTH * stemDirection);
			// flags
			int numberOfFlags = scoreObject.getNumberOfFlags();
			for (int i = 0; i < numberOfFlags; i++) {
				canvas.drawImage("flag" + stemDirection, xPosition, yPositionBottom + -1 * stemDirection * (Score.STEM_LENGTH - 4 - 6 * i) - 6, false);
			}
		}
	}
	
	private int getBeamDirection(TreeSet<ScoreObject> objectSet) {
		int direction = 0;
		double previousPitch = 0;
		int upCount = 0;
		int downCount = 0;
		for (ScoreObject scoreObject : objectSet) {
			double pitch = scoreObject.getAveragePitch();
			if (previousPitch > 0) {
				upCount = upCount + (pitch > previousPitch ? 1 : 0);
				downCount = downCount + (pitch < previousPitch ? 1 : 0);
			}
			previousPitch = pitch;
		}
		if (upCount == 0 || downCount == 0) {
			direction = (upCount > 0 ? 1 : downCount > 0 ? -1 : 0);
		}
		return direction;
	}
	
	private double round(double d) {
		return Math.round(1000 * d) * 0.001;
	}
	
	private int getXPosition(ScoreObject scoreObject, double xBarPosition, double xWidth, int stemDirection, int xShift) {
		int xPosition = (int) (xBarPosition + scoreObject.getRelativePosition() * xWidth + 2 + (stemDirection > 0 ? Score.NOTE_HEAD_WIDTH : xShift));
		return xPosition;
	}
}
