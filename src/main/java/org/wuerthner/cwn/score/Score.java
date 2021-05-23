package org.wuerthner.cwn.score;

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
}
