package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnPointer;
import org.wuerthner.cwn.api.CwnSelection;

import java.util.List;

public class TestSelection implements CwnSelection<CwnEvent> {
        public boolean contains(CwnEvent event) {				return false;			}
        public boolean hasStaffSelected(int index) {				return false;			}
        public int getSelectedStaff() {				return 0;			}
        public boolean isEmpty() {				return false;			}
        public boolean hasCursor() {				return false;			}
        public long getCursorPosition() {				return 0;			}
        public boolean hasMouseDown() {				return false;			}
        public long getMouseLeftPosition() {				return 0;			}
        public long getMouseRightPosition() {				return 0;			}
        public int getMouseStaff() {				return 0;			}
        public SelectionType getSelectionType() {				return null;			}
        public CwnPointer getPointer() {				return new CwnPointer() {
        public Region getRegion() {					return null;				}
        public long getPosition() {					return 0;				}
        public void setPosition(long position) {				}
        public int getPitch() {					return 0;				}
        public void setPitch(int pitch) {				}
        public void setRegion(Region region) {				}
        public int getStaffIndex() {					return 0;				}
        public void setStaffIndex(int index) {				}
        public void clear() {				}			};			}
        public boolean hasSingleSelection() {				return false;			}
        public CwnEvent getSingleSelection() {				return null;			}
        public List<CwnEvent> getSelection() {				return null;			}
}
