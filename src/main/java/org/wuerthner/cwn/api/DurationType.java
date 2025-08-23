package org.wuerthner.cwn.api;

import java.util.ArrayList;
import java.util.List;

public class DurationType {
	
	public final static DurationType REGULAR = new DurationType(1, 0, 1, 1);
	public final static DurationType DUPLET = new DurationType(2, 0, 2, 3);
	public final static DurationType TRIPLET = new DurationType(3, 0, 3, 2);
	public final static DurationType QUADRUPLET = new DurationType(4, 0, 4, 3);
	public final static DurationType QUINTUPLET = new DurationType(5, 0, 5, 4);
	public final static DurationType SEXTUPLET = new DurationType(6, 0, 6, 5);
	
	public final static DurationType[] TUPLETS = { REGULAR, DUPLET, TRIPLET, QUADRUPLET, QUINTUPLET, SEXTUPLET };

	public final static DurationType DOTTED = new DurationType(1, 1, 2, 3);
	// public final static DurationType HALFDOTTED = new DurationType(1, 0, 4, 5);
	public final static DurationType BIDOTTED = new DurationType(1, 2, 4, 7);
	public final static DurationType TRIDOTTED = new DurationType(1, 3, 8, 15);
	
	public final static DurationType[] DOTS = { DOTTED, BIDOTTED, TRIDOTTED };
	
	// public final static DurationType DOTTED_TRIPLET = REGULAR; // 2/3 * 3/2 == 1!
	// public final static DurationType DODOTTED_TRIPLET = new DurationType(6, 7);
	//
	// public final static DurationType DOTTED_QUINTUPLET = new DurationType(5, 6);
	// public final static DurationType DODOTTED_QUINTUPLET = new DurationType(5, 7);
	
	private final int character;
	private final int dots;
	private final int subdivisions;
	private final int beats;
	
	public static List<DurationType> getSupportedTypesForCharacter(DurationType durationTypeCharacter) {
		List<DurationType> durationTypeList = new ArrayList<>();
		durationTypeList.add(durationTypeCharacter);
		durationTypeList.add(new DurationType(durationTypeCharacter, DOTTED));
		// durationTypeList.add(new DurationType(durationTypeCharacter, HALFDOTTED));
		durationTypeList.add(new DurationType(durationTypeCharacter, BIDOTTED));
		durationTypeList.add(new DurationType(durationTypeCharacter, TRIDOTTED));
		return durationTypeList;
	}
	
	public DurationType(int character, int dots, int subdivisions, int beats) {
		this.character = character;
		this.dots = dots;
		this.subdivisions = subdivisions;
		this.beats = beats;
	}
	
	public DurationType(DurationType characterType, DurationType extensionType) {
		if (characterType.dots > 0) {
			throw new RuntimeException("Character Type may not be dotted!");
		} else if (extensionType.character != 1) {
			throw new RuntimeException("Extension Type may not be characterized!");
		}
		int[] ratio = simplify(characterType.subdivisions * extensionType.subdivisions, characterType.beats * extensionType.beats);
		this.character = characterType.getCharacter();
		this.dots = extensionType.getDots();
		this.subdivisions = ratio[0];
		this.beats = ratio[1];
	}
	
	public int gcm(int a, int b) {
		return b == 0 ? a : gcm(b, a % b);
	}
	
	public int[] simplify(int a, int b) {
		int gcm = gcm(a, b);
		return new int[] { (a / gcm), (b / gcm) };
	}
	
	public int getCharacter() {
		return character;
	}
	
	public int getDots() {
		return dots;
	}
	
	public int getSubdivisions() {
		return subdivisions;
	}
	
	public int getBeats() {
		return beats;
	}
	
	public double getFactor() {
		return subdivisions * 1.0 / beats;
	}
	
	public String getPresentation() {
		return subdivisions + ":" + beats;
	}
	
	public static List<DurationType> getDurationTypeList(String character) {
		List<DurationType> durationTypeList = new ArrayList<>();
		durationTypeList.add(REGULAR);
		if (character.indexOf("2") >= 0) {
			durationTypeList.add(DUPLET);
		}
		if (character.indexOf("3") >= 0) {
			durationTypeList.add(TRIPLET);
		}
		if (character.indexOf("4") >= 0) {
			durationTypeList.add(QUADRUPLET);
		}
		if (character.indexOf("5") >= 0) {
			durationTypeList.add(QUINTUPLET);
		}
		if (character.indexOf("6") >= 0) {
			durationTypeList.add(SEXTUPLET);
		}
		if (character.indexOf("D") >= 0) {
			durationTypeList.add(DOTTED);
		}
		if (character.indexOf("B") >= 0) {
			durationTypeList.add(BIDOTTED);
		}
		if (character.indexOf("T") >= 0) {
			durationTypeList.add(TRIDOTTED);
		}
		return durationTypeList;
	}
	
	public String toString() {
		String description;
		
		description = (character == 1 ? "regular"
				: character == 2 ? "duplet" : character == 3 ? "triplet" : character == 4 ? "quadruplet" : character == 5 ? "quintuplet" : character == 6 ? "sextuplet" : getPresentation());
		if (dots > 0) {
			description += " (" + dots + " dot" + (dots > 1 ? "s" : "") + ")";
		}
		return description;
	}
}
