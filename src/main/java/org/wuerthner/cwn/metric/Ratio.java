package org.wuerthner.cwn.metric;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Ratio {
	public enum Signum {
		PLUS, MINUS
	}
	
	final static public String METRIC_PATTERN = "^\\s*(\\d+)(?:\\s*(\\+)\\s*(\\d+))*\\s*/\\s*(\\d+)\\s*$";
	
	private final Signum signum;
	private final int[] numerator;
	private final int denominator;
	
	public Ratio(Signum signum, int[] numerator, int denominator) {
		this.signum = signum;
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public Ratio(int numerator, int denominator) {
		this(Signum.PLUS, new int[] { numerator }, denominator);
	}
	
	public Ratio(String ratioAsString) {
		if (!ratioAsString.matches(METRIC_PATTERN)) {
			throw new RuntimeException("Ratio does not match pattern!");
		}
		String[] parts = ratioAsString.split("/", 2);
		String[] addends = parts[0].trim().split("\\+");
		signum = Signum.PLUS;
		numerator = new int[addends.length];
		for (int i = 0; i < addends.length; i++) {
			numerator[i] = Integer.parseInt(addends[i]);
		}
		denominator = Integer.parseInt(parts[1].trim());
	}
	
	public Signum getSignum() {
		return signum;
	}
	
	public int[] getNumerator() {
		return numerator;
	}
	
	public int getSingleNumerator() {
		return numerator[0];
	}
	
	public String getNumeratorAsString() {
		return Arrays.stream(numerator).mapToObj(n -> "" + n).collect(Collectors.joining("+"));
	}
	
	public int getNumeratorSum() {
		return Arrays.stream(numerator).sum();
	}
	
	public int getDenominator() {
		return denominator;
	}
}
