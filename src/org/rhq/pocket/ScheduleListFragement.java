package org.rhq.pocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.MetricSchedule;

/**
 * Fragment that shows a list of metric schedules
 * @author Heiko W. Rupp
 */
public class ScheduleListFragement extends ListFragment {

    int resourceId = 0;
    List<MetricSchedule> metricSchedules = new ArrayList<MetricSchedule>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.schedule_list_fragment, container);

        System.out.println("Inflated view");



        return view;

    }

    private void setAdapterForList() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> names = new ArrayList<String>();

                try {
                    List<Map<String,MetricSchedule>> resources = objectMapper.readValue(result,new TypeReference<List<Map<String,MetricSchedule>>>() {});
                    System.out.println(resources);
                    metricSchedules.clear();

                    for (Map<String,MetricSchedule> map : resources) {

                        Set<Map.Entry<String,MetricSchedule>> set = map.entrySet();
                        for (Map.Entry entry : set) {
                            MetricSchedule schedule = (MetricSchedule) entry.getValue();
                            if (schedule.getType().equalsIgnoreCase("MEASUREMENT")) { // filter for numeric metrics
                                names.add(schedule.getDisplayName());
                                metricSchedules.add(schedule);
                            }
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
        },"/resource/" + resourceId + "/schedules", true).execute();
    }

    public void setResourceId(int scheduleId) {
        this.resourceId = scheduleId;
        ChartFragment fragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chart_fragment);
        if (fragment==null)
            return;

        fragment.setScheduleId(new MetricSchedule());


        setAdapterForList();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        ChartFragment fragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chart_fragment);
        if (fragment==null)
            return;

        fragment.setScheduleId(metricSchedules.get(position));
    }
}
