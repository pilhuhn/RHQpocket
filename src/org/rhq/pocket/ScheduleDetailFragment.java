package org.rhq.pocket;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;

import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Show details about a schedule
 * @author Heiko W. Rupp
 */
public class ScheduleDetailFragment extends Fragment implements View.OnClickListener {

    TextView nameView;
    EditText collectionInterval;
    CheckBox enabledBox;
    MetricSchedule schedule;
    Button schedule_update_button;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_detail_fragment, container,false);

        nameView = (TextView) view.findViewById(R.id.schedule_name);
        collectionInterval = (EditText) view.findViewById(R.id.schedule_interval);
        enabledBox = (CheckBox) view.findViewById(R.id.schedule_enabled);
        schedule_update_button = (Button)view.findViewById(R.id.schedule_update_button);
        schedule_update_button.setOnClickListener(this);
        return view;
    }

    public void setSchedule(MetricSchedule schedule) {
        this.schedule = schedule;
        if (schedule!=null) {
            nameView.setText(schedule.getDisplayName());
            collectionInterval.setText(""+schedule.getCollectionInterval());
            enabledBox.setChecked(schedule.isEnabled());
        }
    }

    public void onClick(View view) {
        if (view.equals(enabledBox)) {

            schedule.setEnabled(enabledBox.isChecked());
            schedule.setCollectionInterval(Long.getLong(collectionInterval.getText().toString()));

            new TalkToServerTask(getActivity(),new FinishCallback() {
                public void onSuccess(JsonNode result) {
                    // TODO: Customise this generated block
                }

                public void onFailure(Exception e) {
                    // TODO: Customise this generated block
                }
            },"/metric/schedule/" + schedule.getScheduleId(),"PUT",false)
            .execute(schedule);
        }
    }
}