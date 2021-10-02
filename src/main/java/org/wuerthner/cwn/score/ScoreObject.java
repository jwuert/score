package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;

public interface ScoreObject extends Comparable<ScoreObject> {
	public long getStartPosition();
	
	public long getEndPosition();
	
	public long getDuration();
	
	public double getRelativePosition();
	
	public double getRelativeDuration();
	
	public int getNumberOfDots();
	
	public boolean groupable();
	
	public DurationType getDurationType();
	
	public String toString(CwnTrack cwnTrack);
	
	/**
	 * This is the duration as it has to be displayed: for an 8th, an 8th triplet or a dotted 8th, this will return 8
	 * 
	 * @return returns a power 2 based integer
	 */
	public int getDurationBase();
	
	public boolean isRest();
	
	public boolean isChord();
	
	public boolean isNote();

	public boolean isSplit();
	
	public int getStemDirection();
	
	public int getNumberOfFlags();
	
	ScoreNote getMinimumNote();
	
	ScoreNote getMaximumNote();
	
	double getAveragePitch();
	
	int getY(int pitch, int enharmonicShift, int clef);
	
	public int fixStemDirection();
}
