package org.rhq.pocket.metric;

import java.io.IOException;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.MetricAggregate;
import org.rhq.core.domain.rest.MetricSchedule;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQPocket;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.helper.DisplayRange;

/**
 * Fragment to display charts
 * @author Heiko W. Rupp
 */
public class ChartFragment extends Fragment implements View.OnClickListener, MetricDetailContainer {

    ChartView chartView;
    private TextView chartTitleBar;
    private MetricSchedule schedule;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chart_fragment, container,false);

        chartView = (ChartView) view.findViewById(R.id.chart_view);
        chartTitleBar = (TextView) view.findViewById(R.id.title);
        chartTitleBar.setText("Select a metric ...");
        chartTitleBar.setOnClickListener(this);

        if (schedule!=null) {
            if (schedule.getDisplayName()!=null) {
                String displayName = calculateTitle();
                chartTitleBar.setText(displayName);
            }
            if (schedule.getUnit()!=null)
                chartView.setUnit(schedule.getUnit());

            fetchMetrics(schedule.getScheduleId());
        }

        System.out.println("CF: onCreateView");
        return view;
    }

    private String calculateTitle() {
        String displayName = schedule.getDisplayName();
        if (RHQPocket.getInstance().displayRangeUnits!=null) {
            String s = getString(RHQPocket.getInstance().displayRangeUnits.getStringCode());
            displayName +=  getString(R.string.last_duration,
                    RHQPocket.getInstance().displayRangeValue,
                    s);
        }
        return displayName;
    }

    public void fetchMetrics(int scheduleId) {

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
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                MetricAggregate metrics;
                try {
                    metrics = mapper.readValue(result,MetricAggregate.class);
                    chartView.setMetrics(metrics);
                    chartView.repaint();

                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                Log.e(getClass().getName(), e.getLocalizedMessage());
            }
        }, "/metric/data/" + scheduleId + "?startTime=" + startTime + "&endTime="+now).execute();

    }

    private void fetchSchedule(int scheduleId) {
        new TalkToServerTask(getActivity(), new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    MetricSchedule schedule = mapper.readValue(result,MetricSchedule.class);
                    chartTitleBar.setText(schedule.getDisplayName());
                    chartView.setUnit(schedule.getUnit());
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        },"/resource/schedule/" + scheduleId).execute();
    }


    public void onClick(View view) {
        if (view.equals(chartTitleBar)) {
            if (schedule !=null)
                fetchMetrics(schedule.getScheduleId());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void setSchedule(MetricSchedule schedule) {
        if (schedule==null)
            return;

        if (schedule.getScheduleId()==0) {
         // TODO   chartView.clear()
        }
        System.out.println("CF: setSchedule");

        this.schedule = schedule;
    }

    @Override
    public void update() {
        if (chartView!=null && schedule!=null) {
            fetchMetrics(schedule.getScheduleId());
            chartTitleBar.setText(calculateTitle());
            chartView.setUnit(schedule.getUnit());
        }
    }
}
