package org.wuerthner.cwn.timesignature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.wuerthner.cwn.api.Metric;
import org.wuerthner.cwn.api.TimeSignature;
import org.wuerthner.cwn.metric.MetricTools;
import org.wuerthner.cwn.metric.Ratio;
import org.wuerthner.cwn.metric.Ratio.Signum;
import org.wuerthner.cwn.metric.SimpleMetric;

public class SimpleTimeSignature implements TimeSignature {
	private final Metric metric;
	private final List<Ratio> ratioList;
	
	public SimpleTimeSignature(Metric metric) {
		this.metric = metric;
		this.ratioList = new ArrayList<Ratio>();
		init();
	}
	
	public SimpleTimeSignature(String metricAsString) {
		this.metric = MetricTools.createRegularMetrics(metricAsString);
		this.ratioList = new ArrayList<Ratio>();
		init();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String ratioSep = "";
		for (Ratio r : ratioList) {
			builder.append(ratioSep);
			if (r.getSignum() == Signum.MINUS) {
				builder.append("-");
			}
			if (r.getNumerator().length > 1) {
				builder.append("(");
			}
			String sep = "";
			for (int n : r.getNumerator()) {
				builder.append(sep);
				builder.append(n);
				sep = "+";
			}
			if (r.getNumerator().length > 1) {
				builder.append(")");
			}
			builder.append("/");
			builder.append(r.getDenominator());
			ratioSep = " + ";
		}
		return builder.toString();
	}
	
	@Override
	public Metric getMetric() {
		return metric;
	}
	
	@Override
	public String getNumerator() {
		return ratioList.get(0).getNumeratorAsString();
	}
	
	@Override
	public int getNumeratorSum() {
		return ratioList.get(0).getNumeratorSum();
	}
	
	@Override
	public String getDenominator() {
		return "" + ratioList.get(0).getDenominator();
	}
	
	private final void init() {
		Map<Integer, Integer> divisorBeatMap = new LinkedHashMap<>();
		recurse(metric, divisorBeatMap);
		for (Integer divisor : divisorBeatMap.keySet()) {
			ratioList.add(new Ratio(divisorBeatMap.get(divisor), divisor));
		}
	}
	
	private final void recurse(Metric metric, Map<Integer, Integer> divisorBeatMap) {
		if (metric.isPrimitive()) {
			int divisor = ((SimpleMetric) metric).getDivisor();
			Integer beat = divisorBeatMap.get(divisor);
			if (beat == null) {
				beat = 0;
			}
			beat++;
			divisorBeatMap.put(divisor, beat);
		} else {
			for (Metric subMetric : metric) {
				recurse(subMetric, divisorBeatMap);
			}
		}
	}
}
