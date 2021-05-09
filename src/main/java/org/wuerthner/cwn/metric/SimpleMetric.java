package org.wuerthner.cwn.metric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;

public class SimpleMetric implements Metric {
	private final static List<Metric> emptyList = new ArrayList<>();
	private final int divisor;
	private DurationType durationType;
	
	public SimpleMetric(int divisor, DurationType durationType) {
		this.divisor = divisor;
		this.durationType = durationType;
	}
	
	@Override
	public Metric get(int index) {
		throw new RuntimeException("operation SimpleMetric.get() not supported!");
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public int numberOfEvents() {
		return 1;
	}
	
	@Override
	public double duration() {
		return 1.0 / divisor;
	}
	
	public int getDivisor() {
		return divisor;
	}
	
	@Override
	public DurationType getDurationType() {
		return durationType;
	}
	
	@Override
	public void setDurationType(DurationType durationType) {
		this.durationType = durationType;
	}
	
	@Override
	public List<DurationType> getFlatDurationTypeList() {
		List<DurationType> durationTypeList = new ArrayList<>();
		durationTypeList.add(durationType);
		return durationTypeList;
	}
	
	@Override
	public List<Double> getCumulativeDurationList(int depth) {
		List<Double> cumulativeList = new ArrayList<>();
		cumulativeList.add((double) 0);
		return cumulativeList;
	}
	
	@Override
	public List<Metric> getFlatMetricList() {
		return getFlatMetricList(0);
	}
	
	@Override
	public List<Metric> getFlatMetricList(int depth) {
		List<Metric> metricList = new ArrayList<>();
		metricList.add(this);
		return metricList;
	}
	
	@Override
	public Iterator<Metric> iterator() {
		return emptyList.iterator();
	}
	
	@Override
	public List<Double> getFlatDurationList() {
		return getFlatDurationList(0);
	}
	
	@Override
	public List<Double> getFlatDurationList(int depth) {
		List<Double> durationList = new ArrayList<>();
		durationList.add(duration());
		return durationList;
	}
	
	@Override
	public String toString() {
		return "1:" + divisor + (durationType == DurationType.REGULAR ? "" : " [" + durationType.toString() + "]");
	}
	
	@Override
	public int depth() {
		return 0;
	}
	
	@Override
	public Metric clone() {
		return new SimpleMetric(divisor, durationType);
	}
}
