package org.wuerthner.cwn.score;

import org.wuerthner.cwn.position.PositionTools;

public class Location {
	public final ScoreBar scoreBar;
	public final long position;
	public final int pitch;
	public final int staffIndex;
	public final int systemIndex;
	public final boolean barConfig;
	public final int x;
	public final int y;
	public final int yRelative;

	public Location(ScoreBar scoreBar, long position, int pitch, int staffIndex, int systemIndex, boolean barConfig, int x, int y, int yRelative) {
		this.scoreBar = scoreBar;
		this.position = position;
		this.pitch = pitch;
		this.staffIndex = staffIndex;
		this.systemIndex = systemIndex;
		this.barConfig = barConfig;
		this.x = x;
		this.y = y;
		this.yRelative = yRelative;
	}
	
	public String getFormattedPosition() {
		
		if (barConfig) {
			return "";
		} else {
			return "" + PositionTools.getTrias(scoreBar.getTrack(), position);
		}
	}
	
	public String toString() {
		return "Location: " + (scoreBar==null?"-":PositionTools.getTrias(scoreBar.getTrack(), position)) + ", "
				+ pitch + (pitch<0?"-":"(" + Score.cpitch[pitch] + ")") + " [" + systemIndex + ":" + staffIndex + "]"
				+ (barConfig ? " CONFIG" : "");
	}
}
