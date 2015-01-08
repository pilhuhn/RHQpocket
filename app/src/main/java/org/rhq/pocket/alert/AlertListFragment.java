package org.rhq.pocket.alert;

import java.io.IOException;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.AlertDefinition;
import org.rhq.core.domain.rest.AlertRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.TalkToServerTask;

/**
 * Show the list of alerts
 * @author Heiko W. Rupp
 */
public class AlertListFragment extends ListFragment {

    private List<AlertRest> alertList;
    private int resourceId = -1;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.generic_list_fragment,container,false);

        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle!=null && bundle.containsKey("resourceId")) {
            resourceId = bundle.getInt("resourceId");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAlerts();
    }

    protected void fetchAlerts() {
        String subUrl = "/alert";
        if (resourceId > 0) {
            subUrl = subUrl + "?resourceId=" + resourceId;
        }
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    alertList = objectMapper.readValue(result,new TypeReference<List<AlertRest>>() {});
                    setListAdapter(new AlertListItemAdapter(getActivity(),R.layout.alert_list_item,alertList));

                    // Remove the progress indicator below the list now that we have returned from the server call
                    ProgressBar pb = (ProgressBar) layout.findViewById(R.id.list_progress);
                    if (pb!=null)
                        pb.setVisibility(View.GONE);
                    getListView().requestLayout();


                    getListView().requestLayout();
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        }, subUrl).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        RHQActivity activity = (RHQActivity) getActivity();
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.one_alert_container);
        if(fragment ==null) {

            fragment = new OneAlertFragment();

            AlertRest alert = alertList.get(position);
            ((OneAlertFragment)fragment).setAlert(alert);

            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(R.id.one_alert_container,fragment);
            ft.commit();

        }
        else {
            AlertRest alert = alertList.get(position);
            OneAlertFragment fragment1 = (OneAlertFragment) fragment;
            fragment1.setAlert(alert);
            fragment1.fillFields();
            fragment1.hideDetails();

        }
        activity.enableMenuItem(R.id.trash_this,true);

    }

    public void updateDefinitions(AlertDefinition definition) {
        for (AlertRest al : alertList) {
            if (al.getAlertDefinition().getId() == definition.getId()) {
                al.setAlertDefinition(definition);
                al.setDefinitionEnabled(definition.isEnabled());
            }
        }
    }
}
