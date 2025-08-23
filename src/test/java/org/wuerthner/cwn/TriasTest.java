package org.wuerthner.cwn;

import org.junit.Test;
import org.wuerthner.cwn.api.Trias;
import org.wuerthner.cwn.api.exception.InvalidPositionException;

import static org.junit.Assert.*;

public class TriasTest {
	@Test
	public void testTrias() {
		Trias t = new Trias(" 7 . 9 . 47 ");
		assertEquals(6, t.bar);
		assertEquals(8, t.beat);
		assertEquals(47, t.tick);
		t = new Trias("2.1.0");
		assertEquals(1, t.bar);
		assertEquals(0, t.beat);
		assertEquals(0, t.tick);
	}
	
	@Test
	public void testMalformedPosition() {
		try {
			new Trias("1.3.4.5");
			fail();
		} catch (InvalidPositionException e) {
			assertEquals("Malformed position: 1.3.4.5", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidBar() {
		try {
			new Trias("0.0.0");
			fail();
		} catch (InvalidPositionException e) {
			assertEquals("Invalid bar: -1", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidBeat() {
		try {
			new Trias("1.0.0");
			fail();
		} catch (InvalidPositionException e) {
			assertEquals("Invalid beat: -1", e.getMessage());
		}
	}
}
