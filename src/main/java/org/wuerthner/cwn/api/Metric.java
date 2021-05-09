package org.wuerthner.cwn.api;

import java.util.List;

public interface Metric extends Iterable<Metric> {
	final static public String METRIC_PATTERN = "^\\s*(\\d+)(?:\\s*(\\+|\\*)\\s*(\\d+))*\\s*/\\s*(\\d+)\\s*(?:(\\+|\\-)\\s*(\\d+)(?:\\s*(\\+|\\*)\\s*(\\d+))*\\s*/\\s*(\\d+)\\s*)*$";
	
	// public static Metric createMetrics(String metricString) {
	// return MetricTools.createMetrics(metricString);
	// }
	
	int numberOfEvents();
	
	double duration();
	
	boolean isPrimitive();
	
	Metric get(int index);
	
	DurationType getDurationType();
	
	List<Double> getFlatDurationList();
	
	List<Double> getFlatDurationList(int depth);
	
	List<DurationType> getFlatDurationTypeList();
	
	List<Metric> getFlatMetricList();
	
	/**
	 * depth of the metric tree
	 * 
	 * @return 0 if simple metric, >0 if composite metric
	 */
	int depth();
	
	Metric clone();
	
	void setDurationType(DurationType durationType);
	
	List<Double> getCumulativeDurationList(int depth);
	
	List<Metric> getFlatMetricList(int depth);
}
