package org.wuerthner.cwn.score;

import java.util.HashMap;
import java.util.Map;

public class Score {
	public static final int NONE = 0;
	public static final int ALLOW_DOTTED_RESTS = 1;
	public static final int SPLIT_RESTS = 2;
	
	public static final int Y_CENTER = 17;
	public static final double STEM_SLOPE = 0.05;
	public static final double NOTE_HEAD_WIDTH = 6;
	public static final int STEM_LENGTH = 19;
	public static final int TIMESIGNATURE_WIDTH = 14;
	public static final int CLEF_WIDTH = 28;
	public static final int KEY_WIDTH = 7;

	public static final Map<String,Integer> tonenameMap = new HashMap<>();
	public static final Map<String,Integer> tonenameShiftMap = new HashMap<>();

	static final int[][] allSigns = new int[][] {
		// @formatter:off
		{-1, -1, -1, -1, -1, -1, -1 },
		{-1, -1, -1, -1, -1, -1,  0 },
		{-1, -1,  0, -1, -1, -1,  0 },
		{-1, -1,  0, -1, -1,  0,  0 },
		{-1,  0,  0, -1, -1,  0,  0 },
		{-1,  0,  0, -1,  0,  0,  0 },
		{0,   0,  0, -1,  0,  0,  0 },
		{0, 0, 0, 0, 0, 0, 0 },
		{0, 0, 0, 0, 0, 0, 1 },
		{0, 0, 1, 0, 0, 0, 1 },
		{0, 0, 1, 0, 0, 1, 1 },
		{0, 1, 1, 0, 0, 1, 1 },
		{0, 1, 1, 0, 1, 1, 1 },
		{1, 1, 1, 0, 1, 1, 1 },
		{1, 1, 1, 1, 1, 1, 1 }
		// @formatter:on
	};

	// 72=c 74=d 76=e 77=f 78=fis
	static final String[] cpitch = new String[]{
			"c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   "gis", "a",    // 0-9
			"b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   // 10-19
			"gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   // 20-29
			"fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", // 30-39
			"e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", // 40-49
			"d",   "dis", "e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   // 50-59
			"c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   "gis",  "a",  // 60-69
			"b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   "fis",  "g",  // 70-79
			"gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   // 80-89
			"fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", // 90-99
			"e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", // 100-109
			"d",   "dis", "e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   // 110-119
			"c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   "gis",  "a",  // 120-129
			"b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   "fis",  "g",  // 130-139
			"gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   // 140-149
			"fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", // 150-159
			"e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", // 160-169
			"d",   "dis", "e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   // 170-179
			"c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   "gis",  "a",  // 180-189
			"b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   "fis",  "g",  // 190-199
			"gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", "e",   "f",   // 200-209
			"fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", "d",   "dis", // 210-219
			"e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   "c",   "cis", // 220-229
			"d",   "dis", "e",   "f",   "fis", "g",   "gis", "a",   "b",   "h",   // 230-239
			"c",   "cis", "d",   "dis", "e",   "f",   "fis", "g",   "gis",  "a",  // 240-249
			"b",   "h",   "c",   "cis", "d",   "dis"                              // 250-255
	};
	
	public static final int[] invPitch = new int[] { 58, 58, 57, 57, 56, 55, 55, 54, 54, 53, 53, 52, 51, 51, 50, 50, 49, 48, 48, 47, 47, 46, 46, 45, 44, 44, 43, 43, 42, 41, 41, 40, 40, 39, 39, 38, 37, 37, 36, 36, 35, 34, 34,
			33, 33, 32, 32, 31, 30, 30, 29, 29, 28, 27, 27, 26, 26, 25, 25, 24, 23, 23, 22, 22, 21, 20, 20, 19, 19, 18, 18, 17, 16, 16, 15, 15, 14, 13, 13, 12, 12, 11, 11, 10, 9, 9, 8, 8, 7, 6, 6, 5, 5, 4, 4, 3, 2, 2, 1,
			1, 0 };
	
	static final int[] sign = new int[] { 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1,
			0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0 };
	
	static final int[][] enhF = new int[][] {
			// @formatter:off
    		{ -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -2, -1 },
    		{  0, -1,  0, -1, -1,  0, -1,  0, -1,  0, -1, -1 },
    		{  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
    		{  1,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0 },
    		{  1,  1,  1,  0,  1,  1,  1,  1,  0,  1,  0,  1 }
    		// @formatter:on
	};
	
	static final int[][] enhS = new int[][] {
			// @formatter:off
    		{ -2, -1, -2, -2, -1, -2, -1, -2, -1, -2, -2, -1 },
    		{  0, -1,  0, -1, -1,  0, -1,  0, -1,  0, -1, -1 },
    		{  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
    		{  1,  1,  0,  1,  0,  1,  1,  0,  1,  0,  1,  0 },
    		{  1,  2,  2,  1,  2,  1,  2,  2,  1,  2,  1,  2 }
    		// @formatter:on
	};
	
	public static final int[] yClef = new int[] { 0, -12, 7, 14, -7, -19, -26, -10, -14, -2, -4, -6, -8 };
	
	public static final int[] sharpTab = new int[] { 0, 3, -1, 2, 5, 1, 4 };
	
	public static final int[] flatTab = new int[] { 4, 1, 5, 2, 6, 3, 0 };
	
	public static final int[] signShift = new int[] { 0, 2, 0, 0, 0, 2, 2, 4, 0, -2, 3, 1, -1, 0 };
	
	public static final int[] freqTab = new int[] { 100, 98, 96, 95, 93, 91, 89, 88, 86, 84, 83, 81, 79, 77, 76, 74, 72, 71, 69, 67, 65, 64, 62, 60, 59, 57, 55, 53, 52, 50, 48, 47, 45, 43, 41, 40, 38, 36, 35, 33, 31, 29,
			28, 26, 24 };

	public static final String[] interval = new String[] {
			//0,  1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
			"1", "2", "2", "3", "3", "4","Tr", "5", "6", "6", "7", "7", "8",
				"2", "2", "3", "3", "4","Tr", "5", "6", "6", "7", "7", "8",
				"2", "2", "3", "3", "4","Tr", "5", "6", "6", "7", "7", "8",
				"2", "2", "3", "3", "4","Tr", "5", "6", "6", "7", "7", "8",
				"2", "2", "3", "3", "4","Tr", "5", "6", "6", "7", "7", "8"
//			     "9", "9","10","10","11","Tr","12","13","13","14","14","15",
//			    "16","16","17","17","18","Tr","19","20","20","21","21","22"
	};

	public static final String[][] enharmonicShiftNames = new String[][]{
			// @formatter:off
			{"Deses", "C",    "C",   "His", "His"  }, // C
			{"Des",   "Des",  "Cis", "Cis", "Hisis"  }, // Cis
			{"Eses",  "D",    "D",   "D",   "Cisis"  }, // D
			{"Feses", "Es",   "Dis", "Dis", "Dis"  }, // Dis
			{"Fes",   "Fes",  "E",   "E",   "Disis"}, // E
			{"Geses", "F",    "F",   "Eis", "Eis"  }, // F
			{"Ges",   "Ges",  "Fis", "Fis", "Eisis"}, // Fis
			{"Ases",  "G",    "G",   "G",   "Fisis"}, // G
			{"As",    "As",   "Gis", "Gis", "Gis"  }, // Gis
			{"Heses", "A",    "A",   "A",   "Gisis"}, // A
			{"Ceses", "B",    "Ais", "Ais", "Ais"  }, // Ais
			{"Ces",   "Ces",  "H",   "H",   "Aisis"}  // H
			// @formatter:on
	};

	public enum Genus {
		UNDEFINED(0), MINOR(1), MAJOR(2), DIMINISHED(3);
		public int code;
		private Genus(int c) {
			this.code = c;
		}
	}
	static {
		tonenameMap.put("c", 0);
		tonenameMap.put("cis", 1);
		tonenameMap.put("cisis", 2);
		tonenameMap.put("ces", 11);
		tonenameMap.put("ceses", 10);

		tonenameMap.put("d", 2);
		tonenameMap.put("dis", 3);
		tonenameMap.put("disis", 4);
		tonenameMap.put("des", 1);
		tonenameMap.put("deses", 0);

		tonenameMap.put("e", 4);
		tonenameMap.put("eis", 5);
		tonenameMap.put("eisis", 6);
		tonenameMap.put("es", 3);
		tonenameMap.put("eses", 2);

		tonenameMap.put("f", 5);
		tonenameMap.put("fis", 6);
		tonenameMap.put("fisis", 7);
		tonenameMap.put("fes", 4);
		tonenameMap.put("feses", 3);

		tonenameMap.put("g", 7);
		tonenameMap.put("gis", 8);
		tonenameMap.put("gisis", 9);
		tonenameMap.put("ges", 6);
		tonenameMap.put("geses", 5);

		tonenameMap.put("a", 9);
		tonenameMap.put("ais", 10);
		tonenameMap.put("aisis", 11);
		tonenameMap.put("as", 8);
		tonenameMap.put("ases", 7);

		tonenameMap.put("h", 11);
		tonenameMap.put("his", 0);
		tonenameMap.put("hisis", 1);
		tonenameMap.put("b", 10);
		tonenameMap.put("heses", 9);

		// shift:
		tonenameShiftMap.put("c", 0);
		tonenameShiftMap.put("cis", 1);
		tonenameShiftMap.put("cisis", 2);
		tonenameShiftMap.put("ces", -1);
		tonenameShiftMap.put("ceses", -2);

		tonenameShiftMap.put("d", 0);
		tonenameShiftMap.put("dis", 1);
		tonenameShiftMap.put("disis", 2);
		tonenameShiftMap.put("des", -1);
		tonenameShiftMap.put("deses", -2);

		tonenameShiftMap.put("e", 0);
		tonenameShiftMap.put("eis", 1);
		tonenameShiftMap.put("eisis", 2);
		tonenameShiftMap.put("es", -1);
		tonenameShiftMap.put("eses", -2);

		tonenameShiftMap.put("f", 0);
		tonenameShiftMap.put("fis", 1);
		tonenameShiftMap.put("fisis", 2);
		tonenameShiftMap.put("fes", -1);
		tonenameShiftMap.put("feses", -2);

		tonenameShiftMap.put("g", 0);
		tonenameShiftMap.put("gis", 1);
		tonenameShiftMap.put("gisis", 2);
		tonenameShiftMap.put("ges", -1);
		tonenameShiftMap.put("geses", -2);

		tonenameShiftMap.put("a", 0);
		tonenameShiftMap.put("ais", 1);
		tonenameShiftMap.put("aisis", 2);
		tonenameShiftMap.put("as", -1);
		tonenameShiftMap.put("ases", -2);

		tonenameShiftMap.put("h", 0);
		tonenameShiftMap.put("his", 1);
		tonenameShiftMap.put("hisis", 2);
		tonenameShiftMap.put("b", -1);
		tonenameShiftMap.put("heses", -2);
	}

	/**
	 * Returns the pitch in a readable manner
	 **/
	public static final String getCPitch(int p, int es) {
		int n = p % 12;
		String ff = enharmonicShiftNames[n][es+2];
		return ff + " " + (p / 12 - 2);
	}

	public static final int getPitch(String name) {
		if (name.indexOf('-')>0) return 0;
		//System.out.println("=> " + name);
		String s = name.replaceAll("\\s", "").replaceFirst("(\\d)", " $1");
		String[] pair = s.split(" ");
		//System.out.println(":" + pair[0].trim() + ":");
		//System.out.println(":" + pair[1].trim() + ":");
		return tonenameMap.get(pair[0].trim().toLowerCase()) + 12*(2+Integer.parseInt(pair[1].trim()));
	}

	public static final int getEnharmonicShift(String name) {
		String s = name.replaceAll("\\s", "").replaceFirst("(\\d)", " $1");
		String[] pair = s.split(" ");
		return tonenameShiftMap.get(pair[0].trim().toLowerCase());
	}

	public static final int getPitchValueOldVersion(String note) {
		byte pitch = 0;
		if (note == null) {
			throw new RuntimeException("NoteMessage: cannot construct a pitch value on null-string");
		} else {
			note = note.trim();
			char n = note.charAt(0);
			int p = 0;
			switch (n) {
				case 'c':
					p = 0 + 24;
					break;
				case 'd':
					p = 2 + 24;
					break;
				case 'e':
					p = 4 + 24;
					break;
				case 'f':
					p = 5 + 24;
					break;
				case 'g':
					p = 7 + 24;
					break;
				case 'a':
					p = 9 + 24;
					break;
				case 'h':
					p = 11 + 24;
					break;
				case 'b':
					p = 11 + 24;
					break;
			}
			int oct = 3;
			int sg = 0;
			if (note.length() > 1) {
				char c1 = note.charAt(1);
				if (c1 == '#' || c1 == 'b') {
					if (c1 == '#')
						sg = 1;
					else
						sg = -1;
				} else if (c1 >= '0' && c1 <= '9') {
					try {
						oct = Integer.parseInt("" + c1);
					} catch (NumberFormatException nfe) {
						throw new RuntimeException(nfe.getMessage());
					}
				}
			}
			if (note.length() > 2) {
				char c2 = note.charAt(2);
				if (c2 >= '0' && c2 <= '9') {
					try {
						oct = Integer.parseInt("" + c2);
					} catch (NumberFormatException nfe) {
						throw new RuntimeException(nfe.getMessage());
					}
				}
			}
			pitch = (byte) (p + sg + 12 * oct);
		}
		return pitch;
	}

}
