package org.wuerthner.cwn.api;

import java.util.Arrays;

public interface CwnSymbolEvent extends CwnEvent {
	
	public final static String SYMBOL_PPP = "PPP";
	public final static String SYMBOL_PP = "PP";
	public final static String SYMBOL_P = "P";
	public final static String SYMBOL_MP = "MP";
	public final static String SYMBOL_FP = "FP";
	public final static String SYMBOL_MF = "MF";
	public final static String SYMBOL_F = "F";
	public final static String SYMBOL_FF = "FF";
	public final static String SYMBOL_FFF = "FFF";
	public final static String SYMBOL_SFF = "SFF";
	public final static String SYMBOL_SF = "SF";
	public final static String SYMBOL_SFZ = "SFZ";
	public final static String SYMBOL_CRESCENDO = "Crescendo";
	public final static String SYMBOL_DECRESCENDO = "Decrescendo";
	public final static String SYMBOL_BOWUP = "BowUp";
	public final static String SYMBOL_BOWDOWN = "BowDown";
	public final static String SYMBOL_8VA = "8va";
	public final static String SYMBOL_15VA = "15va";
	public final static String SYMBOL_CASE1 = "Case1";
	public final static String SYMBOL_CASE2 = "Case2";
	public final static String SYMBOL_PEDAL1 = "Pedal1";
	public final static String SYMBOL_PEDAL2 = "Pedal2";
	public final static String SYMBOL_LABEL1 = "Label1";
	public final static String SYMBOL_LABEL2 = "Label2";
	public final static String SYMBOL_LABEL3 = "Label3";
	
	public final static String[] SYMBOLS = new String[] { SYMBOL_8VA, SYMBOL_15VA, SYMBOL_CASE1, SYMBOL_CASE2, SYMBOL_CRESCENDO, SYMBOL_DECRESCENDO, SYMBOL_BOWUP, SYMBOL_BOWDOWN, SYMBOL_PPP, SYMBOL_PP, SYMBOL_P,
			SYMBOL_MP, SYMBOL_FP, SYMBOL_MF, SYMBOL_F, SYMBOL_FF, SYMBOL_FFF, SYMBOL_SFF, SYMBOL_SF, SYMBOL_SFZ };
	
	public String getSymbolName();
	
	public long getDuration();
	
	public int getVerticalOffset();
	
	public int getParameter();

	public int getVoice();
	
	public boolean isCrescendo();
	
	public boolean isDecrescendo();
	
	public boolean isBowUp();
	
	public boolean isBowDown();
	
	public boolean isOctave();
	
	public boolean isCase();

	public static int indexOf(String name) {
		return Arrays.asList(SYMBOLS).indexOf(name);
	}
}
