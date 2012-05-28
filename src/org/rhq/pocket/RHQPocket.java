package org.rhq.pocket;

import org.rhq.core.domain.rest.MetricSchedule;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
public class RHQPocket {
    private static RHQPocket ourInstance = new RHQPocket();
    MetricSchedule currentSchedule;

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
}
