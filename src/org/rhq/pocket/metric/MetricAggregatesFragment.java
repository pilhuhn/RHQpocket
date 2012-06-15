package org.rhq.pocket.metric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.measurement.MeasurementUnits;
import org.rhq.core.domain.rest.MetricAggregate;
import org.rhq.core.domain.rest.MetricSchedule;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQPocket;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.helper.DisplayRange;
import org.rhq.pocket.helper.MetricsUnitConverter;

/**
 * Display the metrical values for the schedules of a resource
 * @author Heiko W. Rupp
 */
public class MetricAggregatesFragment extends Fragment implements MetricDetailContainer {

    private static final String[] columnNames = {"min","avg","max"};

    TableLayout tableLayout;
    private List<MetricSchedule> metricSchedules = new ArrayList<MetricSchedule>();
    private int resourceId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tableLayout = (TableLayout) inflater.inflate(R.layout.metric_aggregates_fragment, container,false);

        return this.tableLayout;
    }

    public void onStart() {
        super.onStart();



        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    List<MetricSchedule> schedules = objectMapper.readValue(result,new TypeReference<List<MetricSchedule>>() {});
                    System.out.println(schedules);
                    metricSchedules.clear();

                     for(   MetricSchedule schedule :schedules ) {
                        if (schedule.getType().equalsIgnoreCase("MEASUREMENT")) { // filter for numeric metrics
                            metricSchedules.add(schedule);
                        }
                    }
                    setupTable(metricSchedules);

                    update();

                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }

            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        },"/resource/" + resourceId + "/schedules.json").execute();

    }

    private void setupTable(List<MetricSchedule> metricSchedules) {
        for (MetricSchedule schedule : metricSchedules) {
            TableRow row = new TableRow(getActivity());
            row.setTag(schedule.getScheduleId());
            TextView nameView = new TextView(getActivity());
            nameView.setText(schedule.getDisplayName());
            row.addView(nameView);

            for (String name : columnNames) {
                TextView tv = new TextView(getActivity());
                tv.setTag(name);
                tv.setHint("n/a");
                tv.setTextAppearance(getActivity(),R.style.table_cell);
                row.addView(tv);
            }

            tableLayout.addView(row);
        }
        tableLayout.requestLayout();
    }

    @Override
    public void setSchedule(MetricSchedule schedule) {
        ; // Nothing to do
    }

    @Override
    public void update() {

        DisplayRange range = RHQPocket.getInstance().displayRangeUnits;
        int displayRangeValue = RHQPocket.getInstance().displayRangeValue;
        if (range==null) {
            range = DisplayRange.HOUR;
            displayRangeValue = 8;
        }
        long tRange = range.getAsMillis(displayRangeValue);
        long now = System.currentTimeMillis();
        long startTime = now - tRange;

        new TalkToServerTask(getActivity(), new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    List<MetricAggregate> aggregates = objectMapper.readValue(result,new TypeReference<List<MetricAggregate>>() {});
                    for (MetricAggregate agg : aggregates) {
                        TableRow row = (TableRow) tableLayout.findViewWithTag(agg.getScheduleId());
                        if (row!=null) {
                            TextView tv = (TextView) row.findViewWithTag("min");
                            MeasurementUnits mu = findUnit(agg.getScheduleId());
                            tv.setText(MetricsUnitConverter.scaleValue(agg.getMin(),mu));
                            tv = (TextView) row.findViewWithTag("avg");
                            tv.setText(MetricsUnitConverter.scaleValue(agg.getAvg(),mu));
                            tv = (TextView) row.findViewWithTag("max");
                            tv.setText(MetricsUnitConverter.scaleValue(agg.getMax(),mu));
                        }
                    }
                    tableLayout.requestLayout();

                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }

            }

            private MeasurementUnits findUnit(Integer scheduleId) {
                for (MetricSchedule schedule : metricSchedules) {
                    if (schedule.getScheduleId()==scheduleId)
                        return MeasurementUnits.getUsingDisplayUnits(schedule.getUnit());
                }
                return MeasurementUnits.NONE;
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        },"/metric/data/resource/" + resourceId + "?startTime=" + startTime + "&endTime="+now ).execute();
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
