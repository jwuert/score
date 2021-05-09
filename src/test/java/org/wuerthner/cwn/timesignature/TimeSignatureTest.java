package org.wuerthner.cwn.timesignature;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class TimeSignatureTest {
	
	@Test
	public void testGroups() {
		// String sig = " 3 / 4 - 2 + 8 / 8 ";
		// String sig = "3/4+2-8/8";
		// String sig = "3/4+2-8/8-1/12";
		// String sig = " 3 / 4 ";
		// String sig = " 3 + 2 + 3 / 8 ";
		// String sig = "3+2+3/8";
		String sig = "3*2*3/16 + 1/8";
		// assertTrue(MetricTools.isValidMetric(sig));
		// MetricTools ts = new MetricTools(sig);
	}
	
	@Test
	public void testSignature() {
		
		SimpleTimeSignature ts = new SimpleTimeSignature("2*3/8");
		assertTrue(ts.toString().equals("6/8"));
		
		ts = new SimpleTimeSignature("3+2/4+3/8");
		assertTrue(ts.toString().equals("5/4 + 3/8"));
	}
}
