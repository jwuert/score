package org.wuerthner.cwn.metric;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.Metric;

public class MetricDurationTest {
	@Test
	@Ignore
	public void testDurationList() {
		Metric metric = MetricTools.createRegularMetrics("5/4");
		System.out.println(metric);
		System.out.println(metric.getFlatDurationList());
	}
}
