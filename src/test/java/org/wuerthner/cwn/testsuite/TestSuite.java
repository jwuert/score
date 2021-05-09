package org.wuerthner.cwn.testsuite;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.wuerthner.specs.Specification;

// @formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({
	org.wuerthner.cwn.testsuite.TestSuite.OpenReport.class,
	org.wuerthner.cwn.testsuite.Spec.class,
	org.wuerthner.cwn.NoteTest.class,
	org.wuerthner.cwn.ScoreBuilderTest.class,
	org.wuerthner.cwn.TriasTest.class,
	org.wuerthner.cwn.print.PrintTest.class,
	org.wuerthner.cwn.metric.MetricDurationTest.class,
	org.wuerthner.cwn.metric.MetricTest.class,
	org.wuerthner.cwn.score.BarTest.class,
	org.wuerthner.cwn.score.GroupTest.class,
	org.wuerthner.cwn.score.NoteTest.class,
	org.wuerthner.cwn.score.RelativePositionAndDurationTest.class,
	org.wuerthner.cwn.score.SignsTest.class,
	org.wuerthner.cwn.score.TestAccent.class,
	org.wuerthner.cwn.score.TestScore.class,
	org.wuerthner.cwn.timesignature.RatioTest.class,
	org.wuerthner.cwn.timesignature.TimeSignatureTest.class,
	org.wuerthner.cwn.track.TrackTest.class,
	org.wuerthner.cwn.testsuite.TestSuite.CloseReport.class
})
public class TestSuite {
	public static class OpenReport {
		@Test
		public void open() {
		}
	}
	public static class CloseReport {
		@Test
		public void close() throws IOException {
			Specification.collect();
		}
	}
}
// @formatter:on