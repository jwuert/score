package org.wuerthner.cwn.metric;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wuerthner.cwn.api.Metric;
import org.wuerthner.specs.Requirement;
import org.wuerthner.specs.Testcase;

public class MetricTest {
	private final static Requirement reqMetric = new Requirement(org.wuerthner.cwn.testsuite.Spec.MODEL, "The metric element defines the metric structure of a bar.");
	
	@Test
	public void testMetric_3_3_4() {
		Testcase testcase = new Testcase(
				"Create a two-level metric '3*3/4'. The 1st level submetrixes (amount 3) are of duration 2.25. The 2nd level submetrixes (amount 3) are of duration 0.75. The 3rd level single events are of duration 0.25.",
				reqMetric);
		
		Metric metric = MetricTools.createRegularMetrics("3*3/4");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 3);
		assertTrue(testcase.toString(), metric.duration() == 2.25);
		
		for (Metric subMetric : metric) {
			assertTrue(testcase.toString(), subMetric.numberOfEvents() == 3);
			assertTrue(testcase.toString(), subMetric.duration() == 0.75);
			for (Metric subSubMetric : subMetric) {
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 1);
				assertTrue(testcase.toString(), subSubMetric.duration() == 0.25);
			}
		}
	}
	
	@Test
	public void testMetric_4_4() {
		Testcase testcase = new Testcase(
				"Create a two-level metric '4/4'. The 1st level submetrixes (amount 2) are of duration 1. The 2nd level submetrixes (amount 2) are of duration 0.5. The 3rd levelsingle events are of duration 0.25.",
				reqMetric);
		Metric metric = MetricTools.createRegularMetrics("4/4");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1);
		for (Metric subMetric : metric) {
			assertTrue(testcase.toString(), subMetric.numberOfEvents() == 2);
			assertTrue(testcase.toString(), subMetric.duration() == 0.5);
			for (Metric subSubMetric : subMetric) {
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 1);
				assertTrue(testcase.toString(), subSubMetric.duration() == 0.25);
			}
		}
	}
	
	@Test
	public void testMetric_2_2_4() {
		Testcase testcase = new Testcase(
				"Create a two-level metric '2*2/4'. The 1st level submetrixes (amount 2) are of duration 1. The 2nd level submetrixes (amount 2) are of duration 0.5. The 3rd levelsingle events are of duration 0.25.",
				reqMetric);
		Metric metric = MetricTools.createRegularMetrics("2*2/4");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1);
		for (Metric subMetric : metric) {
			assertTrue(testcase.toString(), subMetric.numberOfEvents() == 2);
			assertTrue(testcase.toString(), subMetric.duration() == 0.5);
			for (Metric subSubMetric : subMetric) {
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 1);
				assertTrue(testcase.toString(), subSubMetric.duration() == 0.25);
			}
		}
	}
	
	@Test
	public void testMetric_2_plus_2_4() {
		Testcase testcase = new Testcase(
				"Create a two-level metric '2+2/4'. The 1st level submetrixes (amount 2) are of duration 1. The 2nd level submetrixes (amount 2) are of duration 0.5. The 3rd levelsingle events are of duration 0.25.",
				reqMetric);
		Metric metric = MetricTools.createRegularMetrics("2+2/4");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1);
		for (Metric subMetric : metric) {
			assertTrue(testcase.toString(), subMetric.numberOfEvents() == 2);
			assertTrue(testcase.toString(), subMetric.duration() == 0.5);
			for (Metric subSubMetric : subMetric) {
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 1);
				assertTrue(testcase.toString(), subSubMetric.duration() == 0.25);
			}
		}
	}
	
	@Test
	public void testMetric_3_1_4() {
		Testcase testcase = new Testcase("Create a metric '3+1/4'. The 1st level submetrixes (amount 2) are of duration 1. The 2nd level are of duration 0.75 (amount 3) and 0.25 (amount 1).", reqMetric);
		Metric metric = MetricTools.createRegularMetrics("3+1/4");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1);
		
		Metric metric1 = metric.get(0);
		Metric metric2 = metric.get(1);
		assertTrue(testcase.toString(), metric1.numberOfEvents() == 3);
		assertTrue(testcase.toString(), metric1.duration() == 0.75);
		assertTrue(testcase.toString(), metric2.numberOfEvents() == 1);
		assertTrue(testcase.toString(), metric2.duration() == 0.25);
		
	}
	
	@Test
	public void testMetric_3_2_3_16_plus_1_8() {
		Testcase testcase = new Testcase(
				"Create a composite metric '3*2*3/16 + 1/8'. The 1st level submetrixes (amount 2) are of duration 1.25. The 2nd level (of 3*2*3/16) are of duration 1.125 (amount 3) The 3rd level are of duration 0.375, 4th level of duration 0.1875 and last level 0.0625. The 2nd level of the 1/8 metric is of duration 0.125.",
				reqMetric);
		Metric metric = MetricTools.createRegularMetrics(" 3*2*3/16 + 1/8");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1.25);
		Metric metric1 = metric.get(0);
		assertTrue(testcase.toString(), metric1.numberOfEvents() == 3);
		assertTrue(testcase.toString(), metric1.duration() == 1.125);
		for (Metric subMetric : metric1) {
			assertTrue(testcase.toString(), subMetric.numberOfEvents() == 2);
			assertTrue(testcase.toString(), subMetric.duration() == 0.375);
			for (Metric subSubMetric : subMetric) {
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 3);
				assertTrue(testcase.toString(), subSubMetric.duration() == 0.1875);
				for (Metric subSubSubMetric : subSubMetric) {
					assertTrue(testcase.toString(), subSubSubMetric.numberOfEvents() == 1);
					assertTrue(testcase.toString(), subSubSubMetric.duration() == 0.0625);
				}
			}
		}
		
		Metric metric2 = metric.get(1);
		assertTrue(testcase.toString(), metric2.numberOfEvents() == 1);
		assertTrue(testcase.toString(), metric2.duration() == 0.125);
	}
	
	@Test
	public void testValidMetric() {
		Testcase testcase = new Testcase("Test the validity of metrics regarding the pattern: " + Metric.METRIC_PATTERN, reqMetric);
		assertTrue(testcase.toString(), MetricTools.isValidMetric("3/4"));
		assertTrue(testcase.toString(), MetricTools.isValidMetric(" 3 / 4 "));
		assertTrue(testcase.toString(), MetricTools.isValidMetric("3+2+8/4"));
		assertTrue(testcase.toString(), MetricTools.isValidMetric(" 3 + 2 + 8 / 4 "));
		assertTrue(testcase.toString(), MetricTools.isValidMetric(" 3 / 4 - 8 / 8 "));
	}
	
	@Test
	public void testInvalidMetric() {
		Testcase testcase = new Testcase("Test invalid merics regarding the pattern given.", reqMetric);
		assertFalse(testcase.toString(), MetricTools.isValidMetric(""));
		assertFalse(testcase.toString(), MetricTools.isValidMetric("3"));
		assertFalse(testcase.toString(), MetricTools.isValidMetric("a/4"));
		assertFalse(testcase.toString(), MetricTools.isValidMetric(" +3 / +4"));
		assertFalse(testcase.toString(), MetricTools.isValidMetric(" +3 / 4"));
		assertFalse(testcase.toString(), MetricTools.isValidMetric(" 3 / +4"));
	}
	
	@Test
	public void testDepth() {
		Testcase testcase = new Testcase("Test the depth level of the submatrixes for a metric '3*2*3/16+1/8'.", reqMetric);
		Metric metric = MetricTools.createRegularMetrics(" 3*2*3/16 + 1/8");
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		int count = 0;
		int[] depths = new int[2];
		// System.out.println(metric + ": depth=" + metric.depth());
		assertTrue(testcase.toString(), metric.depth() == 4);
		for (Metric subMetric : metric) {
			// System.out.println(" " + subMetric + ": depth=" + subMetric.depth());
			depths[count++] = subMetric.depth();
			for (Metric subSubMetric : subMetric) {
				// System.out.println(" " + subSubMetric + ": depth=" + subSubMetric.depth());
				assertTrue(testcase.toString(), subSubMetric.depth() == 2);
				assertTrue(testcase.toString(), subSubMetric.numberOfEvents() == 2);
				for (Metric subSubSubMetric : subSubMetric) {
					// System.out.println(" " + subSubSubMetric + ": depth=" + subSubSubMetric.depth());
					assertTrue(testcase.toString(), subSubSubMetric.depth() == 1);
					assertTrue(testcase.toString(), subSubSubMetric.numberOfEvents() == 3);
				}
			}
		}
		assertTrue(testcase.toString(), depths[0] == 3);
		assertTrue(testcase.toString(), depths[1] == 0);
	}
	
	@Test
	public void test54() {
		Testcase testcase = new Testcase("Create a composite metric '5/4'. The 1st level submetrixes (amount 2) are of duration 1.25. The 2nd level submetrixes (amount 3 and 2) are of duration 0.75 and 0.5.", reqMetric);
		Metric metric = MetricTools.createRegularMetrics("5/4");
		assertTrue(testcase.toString(), metric.depth() == 2);
		assertTrue(testcase.toString(), metric.numberOfEvents() == 2);
		assertTrue(testcase.toString(), metric.duration() == 1.25);
		Metric subMetric1 = metric.get(0);
		Metric subMetric2 = metric.get(1);
		assertTrue(testcase.toString(), subMetric1.numberOfEvents() == 3);
		assertTrue(testcase.toString(), subMetric1.duration() == 0.75);
		assertTrue(testcase.toString(), subMetric2.numberOfEvents() == 2);
		assertTrue(testcase.toString(), subMetric2.duration() == 0.5);
	}
}
