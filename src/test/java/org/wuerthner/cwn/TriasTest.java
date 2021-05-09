package org.wuerthner.cwn;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wuerthner.cwn.api.Trias;
import org.wuerthner.cwn.api.exception.InvalidPositionException;

public class TriasTest {
	@Test
	public void testTrias() {
		Trias t = new Trias(" 7 . 9 . 47 ");
		assertTrue(t.bar == 6);
		assertTrue(t.beat == 8);
		assertTrue(t.tick == 47);
		t = new Trias("2.1.0");
		assertTrue(t.bar == 1);
		assertTrue(t.beat == 0);
		assertTrue(t.tick == 0);
	}
	
	@Test
	public void testMalformedPosition() {
		try {
			new Trias("1.3");
			assertTrue(false);
		} catch (InvalidPositionException e) {
			assertTrue(e.getMessage().equals("Malformed position: 1.3"));
		}
	}
	
	@Test
	public void testInvalidBar() {
		try {
			new Trias("0.0.0");
			assertTrue(false);
		} catch (InvalidPositionException e) {
			assertTrue(e.getMessage().equals("Invalid bar: -1"));
		}
	}
	
	@Test
	public void testInvalidBeat() {
		try {
			new Trias("1.0.0");
			assertTrue(false);
		} catch (InvalidPositionException e) {
			assertTrue(e.getMessage().equals("Invalid beat: -1"));
		}
	}
}
