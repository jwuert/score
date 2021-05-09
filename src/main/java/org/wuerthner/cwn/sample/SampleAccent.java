package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnAccent;

public class SampleAccent implements CwnAccent {
	private final String name;
	
	public SampleAccent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
