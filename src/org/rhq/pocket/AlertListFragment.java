package org.rhq.pocket;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.AlertDefinition;
import org.rhq.core.domain.rest.AlertRest;

/**
 * Show the list of alerts
 * @author Heiko W. Rupp
 */
public class AlertListFragment extends ListFragment {

    private List<AlertRest> alertList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.alert_list_fragment,container,false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAlerts();
    }

    protected void fetchAlerts() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    alertList = objectMapper.readValue(result,new TypeReference<List<AlertRest>>() {});
                    setListAdapter(new AlertListItemAdapter(getActivity(),R.layout.alert_list_item,alertList));
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
        },"/alert").execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Activity activity =getActivity();
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.one_alert_container);
        if(fragment ==null) {

            fragment = new OneAlertFragment();

            AlertRest alert = alertList.get(position);
            ((OneAlertFragment)fragment).setAlert(alert);

            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
