package org.rhq.pocket.alert;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.AlertDefinition;
import org.rhq.core.domain.rest.AlertRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.RHQPocket;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.user.UserDetailFragment;

/**
 * Fragment to show the details of one alert
 * @author Heiko W. Rupp
 */
public class OneAlertFragment extends Fragment implements View.OnClickListener {

    private AlertRest alert;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.one_alert_fragment,container,false);
        return view;
    }

    @Override
    public void onClick(View view) {


        String tag = (String) view.getTag();
        if (tag.equals("ack_userview")) {
            Activity activity =getActivity();
            FragmentManager fm = activity.getFragmentManager();
            if(fm.findFragmentById(R.id.alert_detail_container)==null) {

                UserDetailFragment fragment = new UserDetailFragment();
                fragment.setLogin(alert.getAckBy());

                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.add(R.id.alert_detail_container,fragment);
                ft.commit();

            }
        } else if (tag.equals("enable_button")) {

            AlertDefinition definition = alert.getAlertDefinition();
            definition.setEnabled(!definition.isEnabled()); // reverse state

            FinishCallback fcb = new FinishCallback() {
                @Override
                public void onSuccess(JsonNode result) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    try {
                        AlertDefinition definition1 = objectMapper.readValue(result,new TypeReference<AlertDefinition>() {} );
                        alert.setAlertDefinition(definition1);
                        alert.setDefinitionEnabled(definition1.isEnabled());
                        // update other alerts with the same definition
                        updateOtherDefinitions(definition1);
                        fillFields();
                    } catch (IOException e) {
                        e.printStackTrace();  // TODO: Customise this generated block
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(),"Update failed " ,Toast.LENGTH_SHORT).show();
                }
            };
            new TalkToServerTask(getActivity(), fcb,
                "/alert/definition/" + alert.getAlertDefinition().getId(),"PUT").execute(definition);

        }
    }

    private void updateOtherDefinitions(AlertDefinition definition) {
        FragmentManager fm = getFragmentManager();
        AlertListFragment fragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
        if (fragment!=null) {
            fragment.updateDefinitions(definition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        fillFields();

    }

    public void fillFields() {

        // Now fill in new information
        TextView name = (TextView) view.findViewById(R.id.one_alert_name);
        name.setText(alert.getName());
        TextView id = (TextView) view.findViewById(R.id.one_alert_id);
        id.setText(alert.getId());
        TextView descr = (TextView) view.findViewById(R.id.one_alert_descr);
        descr.setText(alert.getDescription());
        TextView date = (TextView) view.findViewById(R.id.one_alert_date);
        date.setText(new Date(alert.getAlertTime()).toLocaleString());
        TextView prio = (TextView) view.findViewById(R.id.one_alert_priority);
        prio.setText(alert.getAlertDefinition().getPriority());
        CheckBox enabled = (CheckBox) view.findViewById(R.id.one_alert_enabled);
        enabled.setChecked(alert.isDefinitionEnabled());
        Button enableButton = (Button) view.findViewById(R.id.one_alert_button_enable);
        if (!RHQPocket.is44()) {
            enableButton.setOnClickListener(this);
            enableButton.setTag("enable_button");
        } else {
            enableButton.setEnabled(false);
        }

        if (alert.isDefinitionEnabled()) {
            enableButton.setText(getString(R.string.Disable));
        } else {
            enableButton.setText(getString(R.string.Enable));
        }

        setAckState();

    }

    public void hideDetails() {
        // Hide details
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.alert_detail_container);
        if (fragment !=null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.remove(fragment);
            ft.commit();
        }
    }

    private void setAckState() {
        CheckBox ack = (CheckBox)view.findViewById(R.id.one_alert_ack);
        boolean acked = alert.getAckTime()>0;
        ack.setChecked(acked);
        TextView tv = (TextView) view.findViewById(R.id.one_alert_ack_user);
        if (acked) {
            tv.setText(alert.getAckBy());

            if (RHQPocket.is44()) {
                tv.setClickable(false);
                tv.setTag("ack_userview");
                tv.setPaintFlags(tv.getPaintFlags() | ~Paint.UNDERLINE_TEXT_FLAG);
            }
            else {
                tv.setClickable(true);
                tv.setOnClickListener(this);
                tv.setTag("ack_userview");
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            ((RHQActivity)getActivity()).enableMenuItem(R.id.alert_ack_alert,false);
        }
        else {
            ((RHQActivity)getActivity()).enableMenuItem(R.id.alert_ack_alert, true);
            tv.setText(""); // reset to empty
        }
    }

    public void setAlert(AlertRest alert) {

        this.alert = alert;
    }

    public AlertRest getAlert() {
        return alert;
    }
}
