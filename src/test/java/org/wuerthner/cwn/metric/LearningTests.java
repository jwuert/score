package org.wuerthner.cwn.metric;

import org.wuerthner.cwn.api.Metric;

public class LearningTests {
	// @Test
	public void test() {
		dump("1/4");
		dump("2/4");
		dump("3/4");
		dump("4/4");
		dump("5/4");
		dump("6/8");
		dump("9/8");
		dump("7/8");
		dump("8/8");
	}
	
	private void dump(String metricString) {
		Metric metric = MetricTools.createRegularMetrics(metricString);
		System.out.println("-----------------------");
		System.out.println("Metric:   " + metricString);
		System.out.println("Metric:   " + metric);
		System.out.println("Depth:    " + metric.depth());
		System.out.println("Duration: " + metric.duration());
		System.out.println("Flatlist: " + metric.getFlatDurationList(-1));
		System.out.println("   Order 1: " + metric.getFlatDurationList(1));
		System.out.println("   Order 2: " + metric.getFlatDurationList(2));
		System.out.println("   Order 3: " + metric.getFlatDurationList(3));
		System.out.println("Primitiv: " + metric.isPrimitive());
		System.out.println("events:   " + metric.numberOfEvents());
	}
}
