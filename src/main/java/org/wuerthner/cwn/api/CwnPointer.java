package org.wuerthner.cwn.api;

import org.wuerthner.cwn.score.Score;

public interface CwnPointer {
    enum Region { CONFIG, SCORE, NONE }
    public Region getRegion();
    public long getPosition();
    public void setPosition(long position);
    public int getPitch();
    public void setPitch(int pitch);
    public void setRegion(Region region);
    public int getStaffIndex();
    public void setStaffIndex(int index);
    public void setRelativeY(int y);
    public int getRelativeY();
    public void setDeltaY(int y);
    public int getDeltaY();
    default int getY(int clef) {
        int pitch = getPitch();
        int ypos = 0;
        ypos = Score.invPitch[pitch];
        ypos += Score.yClef[clef];
        if (ypos < 1) {
            ypos = 1;
        }
        return ypos;
    }
    public void clear();
}
