package org.wuerthner.cwn.timesignature;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wuerthner.cwn.metric.Ratio;
import org.wuerthner.cwn.metric.Ratio.Signum;

public class RatioTest {
	@Test
	public void testRatio() {
		Ratio r = new Ratio(3, 4);
		assertTrue(r.getSignum() == Signum.PLUS);
		assertTrue(r.getNumerator().length == 1);
		assertTrue(r.getSingleNumerator() == 3);
		assertTrue(r.getDenominator() == 4);
	}
	
	@Test
	public void testRatioAsString() {
		Ratio r = new Ratio("2+3/8");
		assertTrue(r.getSignum() == Signum.PLUS);
		assertTrue(r.getNumerator().length == 2);
		assertTrue(r.getNumerator()[0] == 2);
		assertTrue(r.getNumerator()[1] == 3);
		assertTrue(r.getDenominator() == 8);
	}
}
