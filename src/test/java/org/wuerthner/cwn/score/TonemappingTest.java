package org.wuerthner.cwn.score;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class TonemappingTest {

    @Test
    public void testToneMapping() {
        for (int pitch = 24; pitch<128; pitch++) {
            for (int es = -2; es < 3; es++) {
                String cPitch = Score.getCPitch(pitch, es);
                int value = Score.getPitch(cPitch);
                int enhShift = Score.getEnharmonicShift(cPitch);
                assertTrue(value == pitch);
                assertTrue(Math.abs(es-enhShift) <= 1);
            }
        }
    }
}
