package org.rhq.pocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Fragment that shows a list of metric schedules
 * @author Heiko W. Rupp
 */
public class ScheduleListFragment extends ListFragment {

    int resourceId = 0;
    List<MetricSchedule> metricSchedules = new ArrayList<MetricSchedule>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.schedule_list_fragment, container,false);

        return view;
    }

    private void setAdapterForList() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<String> names = new ArrayList<String>();

                try {
                    List<MetricSchedule> schedules = objectMapper.readValue(result,new TypeReference<List<MetricSchedule>>() {});
                    System.out.println(schedules);
                    metricSchedules.clear();

                     for(   MetricSchedule schedule :schedules ) {
                        if (schedule.getType().equalsIgnoreCase("MEASUREMENT")) { // filter for numeric metrics
                            names.add(schedule.getDisplayName());
                            metricSchedules.add(schedule);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
                    setListAdapter(adapter);
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

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
        MetricDetailContainer fragment = (MetricDetailContainer) getFragmentManager().findFragmentById(R.id.chart_container);
        if (fragment==null) {
            Log.d("SLF","Did not find the chart container + fragment");
            return;
        }


//        fragment.setSchedule(new MetricSchedule()); // dummy schedule as the user needs to first pick one



        setAdapterForList();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        MetricDetailContainer detailFragment = (MetricDetailContainer) getFragmentManager().findFragmentById(R.id.chart_container);
        if (detailFragment==null)
            return;

        detailFragment.setSchedule(metricSchedules.get(position));

        RHQPocket.getInstance().currentSchedule = metricSchedules.get(position);

        detailFragment.update();

/*
        ScheduleDetailFragment sdFragment = (ScheduleDetailFragment) getFragmentManager().findFragmentById(R.id.schedule_detail_fragment);
        if (sdFragment==null)
            return;

        sdFragment.setSchedule(metricSchedules.get(position));
*/

    }
}
