package org.wuerthner.cwn.metric;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.Metric;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricDurationTest {
	@Test
	public void testDurationList() {
		Metric metric = MetricTools.createRegularMetrics("5/4");
		assertEquals(1.25, metric.duration(),0.0);
		assertEquals(2, metric.depth());
		assertEquals(2, metric.numberOfEvents());
		Metric metric1 = metric.get(0);
		assertEquals(3, metric1.numberOfEvents());
		assertEquals(0.75, metric1.duration(), 0.0);
		for (int i=0; i<3; i++) {
			assertTrue(metric1.get(0).isPrimitive());
			assertEquals(0.25, metric1.get(i).duration(), 0);
			assertEquals("1:4", metric1.get(i).toString());
			assertEquals(1, metric1.get(i).numberOfEvents());
		}
		Metric metric2 = metric.get(1);
		assertEquals(2, metric2.numberOfEvents());
		assertEquals(0.5, metric2.duration(), 0.0);
		for (int i=0; i<2; i++) {
			assertTrue(metric2.get(i).isPrimitive());
			assertEquals(0.25, metric2.get(i).duration(), 0);
			assertEquals("1:4", metric2.get(i).toString());
			assertEquals(1, metric2.get(i).numberOfEvents());
		}
		List<Double> flatDurationList = metric.getFlatDurationList();
		assertTrue(flatDurationList.size()==5);
		for (Double d : flatDurationList) {
			assertTrue(d==0.25);
		}
	}
}
