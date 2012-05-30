package org.rhq.pocket;

import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Interface for things on the right side of the metric_chart_layout
 * @author Heiko W. Rupp
 */
public interface MetricDetailContainer {

    void setSchedule(MetricSchedule schedule);

    void update();
}
