package org.rhq.pocket.helper;

import java.util.EnumSet;

import org.rhq.pocket.R;

/**
 * Enum for units of a display range
 * @author Heiko W. Rupp
 */
public enum DisplayRange {

    MINUTE(0,60L, R.string.Minutes),
    HOUR(1,60*60L, R.string.Hours),
    DAY(2,24*60*60L, R.string.Days),
    WEEK(3,7*24*60*60L, R.string.Weeks);

    private final int id;
    private final long multiplier;
    private int string_code;

    DisplayRange( int id, long multiplier, int string_code) {

        this.id = id;
        this.multiplier = multiplier;
        this.string_code = string_code;
    }

    public DisplayRange getById(int id) {
        EnumSet<DisplayRange> set = EnumSet.allOf(DisplayRange.class);
        for (DisplayRange dr : set) {
            if (dr.id==id)
                return dr;
        }
        return null;
    }

    public long getAsMillis(int val) {
        return multiplier * val * 1000L;
    }

    public int getStringCode() {
        return string_code;
    }
}
