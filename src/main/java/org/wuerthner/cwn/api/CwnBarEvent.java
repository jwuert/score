package org.wuerthner.cwn.api;

public interface CwnBarEvent extends CwnEvent {
	public final static String STANDARD = "|";
	public final static String DOUBLE = "||";
	public final static String END = "|I";
	public final static String BEGIN_REPEAT = "I|:";
	public final static String END_REPEAT = " :|I";
	public final static String BEGIN_AND_END_REPEAT = ":|I|:";
	
	public final static String[] TYPES = new String[] { STANDARD, DOUBLE, END, BEGIN_REPEAT, END_REPEAT, BEGIN_AND_END_REPEAT };
	
	public String getType();
	// public Metric getMetric();
}
