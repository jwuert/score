package org.wuerthner.cwn.api;

import org.wuerthner.cwn.api.exception.InvalidPositionException;

public class Trias {
	public final static String PATTERN = "^\\s*\\d+\\s*\\.\\s*\\d+\\s*\\.\\s*\\d+\\s*$";
	public final static String BAR_PATTERN = "^\\s*\\d+\\s*$";
	public final int bar;
	public final int beat;
	public final int tick;
	
	public Trias(int bar, int beat, int tick) {
		this.bar = bar;
		this.beat = beat;
		this.tick = tick;
	}
	
	public Trias(String position) {
		if (position.matches(PATTERN)) {
			int p1 = position.indexOf('.');
			int p2 = position.indexOf('.', p1 + 1);
			// bar = Integer.valueOf(position.substring(0, p1).trim()) - 1;
			bar = Integer.parseInt(position.substring(0, p1).trim()) - 1;
			beat = Integer.parseInt(position.substring(p1 + 1, p2).trim()) - 1;
			tick = Integer.parseInt(position.substring(p2 + 1).trim());
			if (bar < 0) {
				throw new InvalidPositionException("Invalid bar: " + bar);
			} else if (beat < 0) {
				throw new InvalidPositionException("Invalid beat: " + beat);
			}
		} else if (position.matches(BAR_PATTERN)) {
			int number = Integer.parseInt(position.trim()) - 1;
			bar = (number < 0 ? 0 : number);
			beat = 0;
			tick = 0;
		} else {
			throw new InvalidPositionException("Malformed position: " + position);
		}
	}
	
	public Trias firstBeat() {
		return new Trias(bar, 0, 0);
	}

	public Trias nextBar() {
		return new Trias(bar + 1, 0, 0);
	}

	public Trias nextBars(int n) {
		return new Trias(bar + n, 0, 0);
	}

	public Trias nextBeat() {
		return new Trias(bar, beat + 1, 0);
	}
	
	@Override
	public String toString() {
		return (bar + 1) + "." + (beat + 1) + "." + tick;
	}

	public String toFormattedString() {
		return makeString(""+(bar+1), 2) + "." + makeString(""+(beat+1), 2) + "." + makeString(""+tick, 4);
	}

	private final String makeString(String n, int len) {
		StringBuffer buf = new StringBuffer();
		int l = n.length();
		for (int i=len; i>=l; i--) buf.append(" ");
		buf.append(n);
		return buf.toString();
	}

}
