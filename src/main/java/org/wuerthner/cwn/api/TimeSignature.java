package org.wuerthner.cwn.api;

//@formatter:off
/*
 *       z     z1 + ... + zi    z1         zi
 * ts = --- or ------------- or -- + ... + --
 * 	     n           n          n1         ni
 * 
 * where:
 *  
 *  zi    2^ai * 3^bi
 * ---- = -----------
 *  ni       2^ki
 * 
 * Simple Pattern:   Foreground:
 *   3/4               3/4
 *   9/16              9/16
 * 
 * Additive Pattern: Foreground:
 *   3+2+3/8           8/8
 *     8+5/12         13/12
 * 
 * Complex Pattern:  Foreground:
 *   1/2 + 3/6
 *   1/2 + 3+1/8
 *   
 *   
 * --------------------------
 * 
 * TimeSignature (foreground)
 * 
 * SimplePattern: 3/4, 8/8, 13/12, 5/4
 * 
 * AdditivePattern: 3+3+4/16
 * 
 * MixedPattern: 4/4 + 6/8
 * - composed of SimplePatterns
 * 
 * Metric
 * 
 * TS:		Metric:		Group:
 * 3/4		3/4			[0.25, 0.25, 0.25]
 * 4/4		2*2/4		[[0.25, 0.25], [0.25, 0.25]]
 * 6/8		2*3/8		[[0.125,0.125,0.125], [0.125.0.125.0.125]]
 * 
 * 3+3+4/16 3+3+4/16	[[0.0625,0.0625,0.0625], [0.0625,0.0625,0.0625], [0.0625,0.0625,0.0625,0.625]]
 * 
 * 5/4		3+2/4		[[0.25,0.25,0.25], [0.25,0.25]]
 * 5/4+		2+3/4		[[0.25,0.25], [0.25,0.25,0.25]]
 * 9/8		3*3/8		[[0.125,0.125,0.125], [0.125,0.125,0.125], [0.125,0.125,0.125]]
 * 9/8+		3/8+3/4		[[0.125,0.125,0.125], [0.25,0.25,0.25]]
 * 18/16	2*3*3/16	[[[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625]], [[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625]]]
 * 18/16+	3*2*3/16	[[[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625]], [[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625]], [[0.0625,0.0625,0.0625],[0.0625,0.0625,0.0625]]]
 * 
 * Construct from String:
 * 13/12 : 3/4+2/6
 * 18/16 : 3*2*3/16
 * 20/16 : 3*2*3/16+1/8
 * 18/16+1/8 : 3*2*3/16+1/8
 */
//@formatter:on
public interface TimeSignature {
	
	Metric getMetric();
	
	String getNumerator();
	
	String getDenominator();

	int getDenominatorInt();
	
	int getNumeratorSum();
	
	// int getNumberOfComponents();
	
	// int getNumberOfLevels();
	
	// int getLength();
	
	// Group getTopLevelGroup();
}
