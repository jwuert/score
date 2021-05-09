package org.wuerthner.cwn.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;

public class MetricTools {
	public static final Map<String, List<String>> factorBreakDownMap;
	static {
		Map<String, List<String>> factorMap = new HashMap<>();
		factorMap.put("4", new ArrayList<String>(Arrays.asList(new String[] { "2", "2" }))); // ___________4 = 2*2: "xx xx"
		factorMap.put("5", new ArrayList<String>(Arrays.asList(new String[] { "3+2" }))); // ______________5 = 3+2: "xxx xx"
		factorMap.put("6", new ArrayList<String>(Arrays.asList(new String[] { "2", "3" }))); // ___________6 = 2*3: "xxx xxx"
		factorMap.put("7", new ArrayList<String>(Arrays.asList(new String[] { "3+2+2" }))); // ____________7 = 3+2+2: "xxx xx xx"
		factorMap.put("8", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "2" }))); // ______8 = 2*2*2: "(xx xx) (xx xx)"
		factorMap.put("9", new ArrayList<String>(Arrays.asList(new String[] { "3", "3" }))); // ___________9 = 3*3: "xxx xxx xxx"
		factorMap.put("10", new ArrayList<String>(Arrays.asList(new String[] { "2", "3+2" }))); // _______10 = 2*(3+2): "(xxx xx) (xxx xx)"
		factorMap.put("11", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+2" }))); // ________11 = 3+3+3+2: "xxx xxx xxx xx"
		factorMap.put("12", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "3" }))); // ____12 = 2*2*3: "(xxx xxx) (xxx xxx)"
		factorMap.put("13", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+2+2" }))); // ______13 = 3+3+3+2+2: "xxx xxx xxx xx xx"
		factorMap.put("14", new ArrayList<String>(Arrays.asList(new String[] { "2", "3+2+2" }))); // _____14 = 2*(3+2+2): "(xxx xx xx) (xxx xx xx)"
		factorMap.put("15", new ArrayList<String>(Arrays.asList(new String[] { "3", "3+2" }))); // _______15 = 3*(3+2): "(xxx xx) (xxx xx) (xxx xx)"
		factorMap.put("16", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "2", "2" })));
		factorMap.put("17", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+3+3+2" })));
		factorMap.put("18", new ArrayList<String>(Arrays.asList(new String[] { "2", "3", "3" })));
		factorMap.put("19", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+3+3+2+2" })));
		factorMap.put("20", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "3+2" })));
		factorMap.put("21", new ArrayList<String>(Arrays.asList(new String[] { "3", "3+2+2" })));
		factorMap.put("22", new ArrayList<String>(Arrays.asList(new String[] { "2", "3+3+3+2" })));
		factorMap.put("23", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+3+3+3+3+2" })));
		factorMap.put("24", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "2", "3" })));
		factorMap.put("25", new ArrayList<String>(Arrays.asList(new String[] { "3+2", "3+2" })));
		factorMap.put("26", new ArrayList<String>(Arrays.asList(new String[] { "2", "3+3+3+2+2" })));
		factorMap.put("27", new ArrayList<String>(Arrays.asList(new String[] { "3", "3", "3" })));
		factorMap.put("28", new ArrayList<String>(Arrays.asList(new String[] { "2", "2", "3+2+2" })));
		factorMap.put("29", new ArrayList<String>(Arrays.asList(new String[] { "3+3+3+3+3+3+3+3+3+2" })));
		factorMap.put("30", new ArrayList<String>(Arrays.asList(new String[] { "2", "3", "3+2" })));
		factorBreakDownMap = Collections.unmodifiableMap(factorMap);
	}
	
	public static final boolean isValidMetric(String metricString) {
		return metricString.matches(Metric.METRIC_PATTERN);
	}
	
	public static final Metric cloneMetrics(Metric metric, int beat, DurationType durationType) {
		Metric newMetric = metric.clone();
		List<Metric> metricList = newMetric.getFlatMetricList();
		metricList.get(beat).setDurationType(durationType);
		return newMetric;
	}
	
	public static final Metric createRegularMetrics(String metricString) {
		CompositeMetric metricList = new CompositeMetric();
		if (!metricString.matches(Metric.METRIC_PATTERN)) {
			throw new RuntimeException("Invalid pattern for metric");
		}
		//
		// 3*2*3/16 + 1/8
		//
		String[] parts = metricString.split("/");
		boolean sign = true;
		String numerator = parts[0];
		for (int i = 1; i < parts.length; i++) {
			String part = parts[i].trim();
			part = part.replaceAll("\\s+", "").replaceAll("\\+", " +").replaceAll("\\-", " -");
			String[] subparts = part.split(" ", 2);
			// subparts[0] contains the denominator
			// subparts[1] contains the following numerator
			String denominator = subparts[0];
			Metric metrics = makeRegularMetrics(sign, numerator, denominator);
			metricList.addAll(metrics);
			if (subparts.length > 1) {
				if (subparts[1].startsWith("-")) {
					sign = false;
					numerator = subparts[1].substring(1);
				} else if (subparts[1].startsWith("+")) {
					sign = true;
					numerator = subparts[1].substring(1);
				} else {
					sign = true;
					numerator = subparts[1];
				}
			} else {
				break;
			}
		}
		if (metricList.numberOfEvents() == 1) {
			return metricList.iterator().next();
		}
		return metricList;
	}
	
	private static Metric makeRegularMetrics(boolean sign, String numeratorString, String denominatorString) {
		int denominator = Integer.parseInt(denominatorString.trim());
		// array elements are either numbers or products (e.g. "3*2*3")
		numeratorString = numeratorString.replaceAll("\\s+", "");
		String[] factors = numeratorString.split("\\*");
		factors = breakDownFactors(factors);
		// factorList : e.g. [3,2,3]
		// factors : e.g. [3,2+3,3]
		Metric metrics = createRegularMetrics(factors, denominator, 0);
		return metrics;
	}
	
	private static String[] breakDownFactors(String[] factors) {
		List<String> downBrokenFactorList = new ArrayList<String>();
		for (String factor : factors) {
			List<String> downBrokenFactors = factorBreakDownMap.get(factor);
			if (downBrokenFactors == null || downBrokenFactors.isEmpty()) {
				downBrokenFactorList.add(factor);
			} else {
				downBrokenFactorList.addAll(downBrokenFactors);
			}
		}
		return downBrokenFactorList.toArray(new String[] {});
	}
	
	private static CompositeMetric createRegularMetrics(String[] factors, int denominator, int index) {
		CompositeMetric compositeMetric = new CompositeMetric();
		String factor = factors[index].trim();
		String[] addends = factor.split("\\+");
		for (int a = 0; a < addends.length; a++) {
			if (addends[a].trim().length() > 0) {
				int addend = Integer.parseInt(addends[a].trim());
				CompositeMetric metric = new CompositeMetric();
				for (int f = 0; f < addend; f++) {
					if (index + 1 < factors.length) {
						metric.addAll(createRegularMetrics(factors, denominator, index + 1));
					} else {
						metric.add(new SimpleMetric(denominator, DurationType.REGULAR));
					}
				}
				compositeMetric.add(metric);
			}
		}
		if (compositeMetric.numberOfEvents() == 1) {
			compositeMetric = (CompositeMetric) compositeMetric.iterator().next();
		}
		if (compositeMetric.numberOfEvents() > 1) {
			compositeMetric = new CompositeMetric(compositeMetric);
		}
		return compositeMetric;
	}
}
