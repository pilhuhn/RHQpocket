package org.rhq.pocket;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.MetricAggregate;
import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Fragment to display charts
 * @author Heiko W. Rupp
 */
public class ChartFragment extends Fragment implements View.OnClickListener, MetricDetailContainer {

    ChartView chartView;
    private TextView refreshButton;
    private MetricSchedule schedule;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chart_fragment, container,false);

        chartView = (ChartView) view.findViewById(R.id.chart_view);
        refreshButton = (TextView) view.findViewById(R.id.title);
        refreshButton.setText("Select a metric ...");
        refreshButton.setOnClickListener(this);

        if (schedule!=null) {
            if (schedule.getDisplayName()!=null)
                refreshButton.setText(schedule.getDisplayName());
            if (schedule.getUnit()!=null)
                chartView.setUnit(schedule.getUnit());

            fetchMetrics(schedule.getScheduleId());
        }

        System.out.println("CF: onCreateView");
        return view;
    }

    public void fetchMetrics(int scheduleId) {
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
        }, "/metric/data/" + scheduleId).execute();

    }

    private void fetchSchedule(int scheduleId) {
        new TalkToServerTask(getActivity(), new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    MetricSchedule schedule = mapper.readValue(result,MetricSchedule.class);
                    refreshButton.setText(schedule.getDisplayName());
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
        if (view.equals(refreshButton)) {
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
            refreshButton.setText(schedule.getDisplayName());
            chartView.setUnit(schedule.getUnit());
        }
    }
}
