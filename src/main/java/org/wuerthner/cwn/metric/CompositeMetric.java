package org.wuerthner.cwn.metric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;

public class CompositeMetric implements Metric {
	private final List<Metric> components = new ArrayList<>();
	
	public CompositeMetric() {
	}
	
	public CompositeMetric(List<Metric> components) {
		this.components.addAll(components);
	}
	
	public CompositeMetric(Metric metric) {
		components.add(metric);
	}
	
	public void add(Metric metric) {
		components.add(metric);
	}
	
	public void addAll(Metric metric) {
		components.addAll(StreamSupport.stream(Spliterators.spliteratorUnknownSize(metric.iterator(), Spliterator.ORDERED), false).collect(Collectors.<Metric> toList()));
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	@Override
	public Metric get(int index) {
		return components.get(index);
	}
	
	@Override
	public int numberOfEvents() {
		return components.size();
	}
	
	@Override
	public double duration() {
		double duration = 0;
		for (Metric metric : components) {
			duration += metric.duration();
		}
		return duration;
	}
	
	@Override
	public DurationType getDurationType() {
		if (components.isEmpty()) {
			throw new RuntimeException("Empty Metric!");
		}
		return components.get(0).getDurationType();
	}
	
	@Override
	public void setDurationType(DurationType durationType) {
		throw new RuntimeException("operation CompositeMetric.setDurationType() not supported!");
	}
	
	@Override
	public Iterator<Metric> iterator() {
		return components.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		String sep = "";
		for (Metric metric : components) {
			builder.append(sep);
			builder.append(metric.toString());
			sep = ", ";
		}
		builder.append(")");
		return builder.toString();
	}
	
	@Override
	public List<Double> getFlatDurationList() {
		return getFlatDurationList(-1);
	}
	
	@Override
	public List<Double> getFlatDurationList(int depth) {
		List<Double> durationList = new ArrayList<>();
		if (depth == 0) {
			durationList.add(this.duration());
		} else {
			for (Metric subMetric : this) {
				durationList.addAll(subMetric.getFlatDurationList(depth - 1));
			}
		}
		return durationList;
	}
	
	@Override
	public List<Double> getCumulativeDurationList(int depth) {
		List<Double> cumulativeList = new ArrayList<>();
		double value = 0;
		for (Double d : getFlatDurationList(depth)) {
			cumulativeList.add(value);
			value = value + d;
		}
		return cumulativeList;
	}
	
	@Override
	public List<DurationType> getFlatDurationTypeList() {
		List<DurationType> durationTypeList = new ArrayList<>();
		for (Metric subMetric : this) {
			durationTypeList.addAll(subMetric.getFlatDurationTypeList());
		}
		return durationTypeList;
	}
	
	@Override
	public List<Metric> getFlatMetricList() {
		List<Metric> metricList = new ArrayList<>();
		for (Metric subMetric : this) {
			metricList.addAll(subMetric.getFlatMetricList());
		}
		return metricList;
	}
	
	@Override
	public List<Metric> getFlatMetricList(int depth) {
		List<Metric> metricList = new ArrayList<>();
		if (depth == 0) {
			metricList.add(this);
		} else {
			for (Metric subMetric : this) {
				metricList.addAll(subMetric.getFlatMetricList(depth - 1));
			}
		}
		return metricList;
	}
	
	@Override
	public int depth() {
		int maxDepth = 0;
		for (Metric subMetric : this) {
			int depth = subMetric.depth() + 1;
			maxDepth = Math.max(depth, maxDepth);
		}
		return maxDepth;
	}
	
	@Override
	public Metric clone() {
		Metric metric = new CompositeMetric(components.stream().map(m -> m.clone()).collect(Collectors.toList()));
		return metric;
	}
}
