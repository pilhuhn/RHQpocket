package org.rhq.pocket;

import java.io.IOException;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.MetricAggregate;
import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Fragment to display charts
 * @author Heiko W. Rupp
 */
public class ChartFragment extends Fragment implements View.OnClickListener {

    ChartView chartView;
    private Button refreshButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_fragment, container,false);

        chartView = (ChartView) view.findViewById(R.id.chart_view);
        refreshButton = (Button) view.findViewById(R.id.title);
        refreshButton.setText("Getting data ...");
        getMetrics(10007);
        getSchedule(10007);
        refreshButton.setOnClickListener(this);
        return view;
    }

    public void getMetrics(int scheduleId) {
        new TalkToServerTask(getActivity(), new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                MetricAggregate metrics;
                try {
                    metrics = mapper.readValue(result,MetricAggregate.class);
//                    refreshButton.setText("Data obtained ...");
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

    private void getSchedule(int scheduleId) {
        new TalkToServerTask(getActivity(), new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    MetricSchedule schedule = mapper.readValue(result,MetricSchedule.class);
                    refreshButton.setText(schedule.getDisplayName());
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
            getMetrics(10007);
        }
    }
}
