package org.wuerthner.cwn.testsuite;

import org.junit.Test;
import org.wuerthner.specs.Chapter;
import org.wuerthner.specs.Specification;

public class Spec {
	public static final String PROJECT_NAME = "Score";
	public static final String PROJECT_VERSION = "1.1.0";
	public static final String AUTHOR = "Jan Würthner";
	
	public static final Chapter DEFINITION = new Chapter("Project Definition",
			"This library converts music score, given in an abstract data tree-structre (made of arrangements, tracks, voices, events), into a music sheet with a set of coordinates. Based on this output, any simple drawing tool can be used to display a full music score.");
	public static final Chapter SCOPE = new Chapter("Scope", "The scope of this document is a description of system requirements in combination " + "with the test cases and their traceability.");
	
	public static final Chapter MODEL = new Chapter("Model", "This chapter describes the data model");
	
	public static final Chapter USE_CASES = new Chapter("Use Cases", "This chapter describes integration tests and use cases");
	
	@Test
	public void init() {
		Specification.addHeader(PROJECT_NAME, PROJECT_VERSION, AUTHOR);
		Specification.add(DEFINITION);
		Specification.add(SCOPE);
		Specification.add(MODEL);
		Specification.add(USE_CASES);
	}
}
