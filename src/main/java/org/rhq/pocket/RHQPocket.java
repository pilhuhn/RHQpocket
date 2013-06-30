package org.rhq.pocket;

import org.rhq.core.domain.rest.MetricSchedule;
import org.rhq.pocket.helper.DisplayRange;

/**
 * Singleton to hold some more global app state
 * @author Heiko W. Rupp
 */
public class RHQPocket {
    private static RHQPocket ourInstance = new RHQPocket();
    public MetricSchedule currentSchedule;
    public int displayRangeValue;
    public DisplayRange displayRangeUnits;
    public String serverVersion;

    public static RHQPocket getInstance() {
        return ourInstance;
    }

    private RHQPocket() {
    }

    public MetricSchedule getCurrentSchedule() {
        return currentSchedule;
    }

    public void setCurrentSchedule(MetricSchedule currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    public static boolean is44() {
        return ourInstance.serverVersion!=null && ourInstance.serverVersion.startsWith("4.4");
    }
}
