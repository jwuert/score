package org.wuerthner.cwn.score;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.wuerthner.cwn.api.CwnAccent;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.sample.SampleFactory;

public class TestAccent {
	public static final int PPQ = 384;
	
	@Test
	public void accentTest() {
		CwnFactory factory = new SampleFactory();
		CwnNoteEvent note = factory.createNoteEvent(0, PPQ, 70, 0, 87, 0);
		CwnAccent accent = factory.createAccent(CwnAccent.ACCENT_MORDENT);
		note.addAccent(accent);
		List<? extends CwnAccent> accentList = note.getAccentList();
		assertTrue(accentList.size() == 1);
		assertTrue(accentList.get(0).getName().equals(CwnAccent.ACCENT_MORDENT));
	}
}
