package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;

public class ScoreSystem implements Iterable<ScoreStaff> {
	private final List<ScoreStaff> staffList = new ArrayList<>();
	
	public ScoreSystem(ScoreParameter scoreParameter, ScoreSystem totalSystem) {
		int numberOfStaffs = totalSystem.size();
		for (int i = 0; i < numberOfStaffs; i++) {
			staffList.add(new ScoreStaff(scoreParameter, totalSystem.get(i).getTrack()));
		}
	}
	
	public ScoreSystem(List<CwnTrack> trackList, ScoreParameter scoreParameter) {
		for (CwnTrack track : trackList) {
			if (!track.getMute()) {
				ScoreStaff staff = new ScoreStaff(track, scoreParameter);
				staffList.add(staff);
			}
		}
	}

	public void update(ScoreParameter scoreParameter, ScoreUpdate update) {
		for (ScoreStaff staff : staffList) {
			if (update.contains(staff.getTrack())) {
				staff.update(scoreParameter, update);
			}
		}
	}

	@Override
	public Iterator<ScoreStaff> iterator() {
		return staffList.iterator();
	}
	
	public int size() {
		return staffList.size();
	}
	
	public void setStretchFactor(double stretchFactor) {
		for (ScoreStaff staff : staffList) {
			staff.setStretchFactor(stretchFactor);
		}
	}
	
	public void addBarToStaff(int staffIndex, ScoreBar scoreBar) {
		staffList.get(staffIndex).addBar(scoreBar);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int count = 1;
		for (ScoreStaff staff : staffList) {
			builder.append("- Staff: " + count++ + System.getProperty("line.separator"));
			builder.append(staff.toString());
		}
		return builder.toString();
	}
	
	public ScoreStaff get(int staffIndex) {
		return staffList.get(staffIndex);
	}
}
