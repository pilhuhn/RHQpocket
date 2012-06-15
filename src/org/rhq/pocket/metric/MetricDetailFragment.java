package org.rhq.pocket.metric;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.measurement.MeasurementUnits;
import org.rhq.core.domain.rest.MetricAggregate;
import org.rhq.core.domain.rest.MetricSchedule;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQPocket;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.helper.MetricsUnitConverter;

/**
 * Fragment that shows the tabular view of one metric
 * @author Heiko W. Rupp
 */
public class MetricDetailFragment extends Fragment implements MetricDetailContainer {

    int scheduleId;
    private View view;
    private MetricSchedule schedule;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.metric_detail_fragment, container,false);

        return view;
    }

    @Override
    public void setSchedule(MetricSchedule schedule) {
        this.schedule = schedule;
        this.scheduleId = schedule.getScheduleId();
        fetch();
    }

    @Override
    public void update() {
//        fetch();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fetch();
    }

    private void fillValues(MetricAggregate agg) {
        TextView t = (TextView) view.findViewById(R.id.name_view);
        MetricSchedule currentSchedule = RHQPocket.getInstance().currentSchedule;
        t.setText(currentSchedule.getScheduleName());
        t = (TextView)view.findViewById(R.id.avg_val_view);
        MeasurementUnits mu = MeasurementUnits.getUsingDisplayUnits(currentSchedule.getUnit());
        double v = agg.getAvg();
        t.setText(String.valueOf(MetricsUnitConverter.scaleValue(v,mu)));
        v = agg.getMin();
        t = (TextView)view.findViewById(R.id.min_val_view);
        t.setText(String.valueOf(MetricsUnitConverter.scaleValue(v,mu)));
        t = (TextView)view.findViewById(R.id.max_val_view);
        v = agg.getMax();
        t.setText(String.valueOf(MetricsUnitConverter.scaleValue(v,mu)));
    }

    private void fetch() {

        MetricSchedule currentSchedule = RHQPocket.getInstance().currentSchedule;
        if (currentSchedule==null) {
            Toast.makeText(getActivity(),R.string.select_metric_first,Toast.LENGTH_SHORT).show();
            return;
        }
        scheduleId = currentSchedule.getScheduleId();

        new TalkToServerTask(getActivity(), new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                MetricAggregate metrics;
                try {
                    metrics = mapper.readValue(result,MetricAggregate.class);
                    fillValues(metrics);

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

}
