package org.rhq.pocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.metric.MetricChartActivity;
import org.rhq.pocket.metric.ScheduleListFragment;

/**
 * Fragment that allows to pick a resource
 * @author Heiko W. Rupp
 */
public class ResourcePickerFragment extends DialogFragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    List<ResourceWithType> resourcesWithTypes = new ArrayList<ResourceWithType>() ;
    Button pickButton;
    TextView selectedResourceView;
    ResourceWithType selectedResource;
    ListView listView;
    TalkToServerTask ttst;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resource_picker_fragment, container);

        pickButton = (Button) view.findViewById(R.id.pick_resource);
        selectedResourceView = (TextView) view.findViewById(R.id.selected_resource);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);

        pickButton.setOnClickListener(this);


        ttst = new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<String> names = new ArrayList<String>();

                try {
                    List<ResourceWithType> resources = objectMapper.readValue(result,new TypeReference<List<ResourceWithType>>() {});
                    System.out.println(resources);
                    resourcesWithTypes.clear();

                    for (ResourceWithType rwt: resources) {
                            names.add(rwt.getResourceName());
                            resourcesWithTypes.add(rwt);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
                    listView.setAdapter(adapter);
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }


                // TODO: Customise this generated block
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        },"/resource/platforms");
        ttst.execute();

        return view;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.pick_resource);
        return dialog;
    }

    // Get children
    public void onItemClick(AdapterView l, View v, int position, long id) {

        selectedResource = resourcesWithTypes.get(position);
        selectedResourceView.setText(selectedResource.getResourceName());
        pickButton.setEnabled(true);

        ttst = new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<String> names = new ArrayList<String>();

                try {
                    List<ResourceWithType> resources = objectMapper.readValue(result,new TypeReference<List<ResourceWithType>>() {});
                    resourcesWithTypes.clear();

                    for (ResourceWithType rwt :resources ) {
                        names.add(rwt.getResourceName());
                        resourcesWithTypes.add(rwt);
                    }

                    Activity activity = getActivity();
                    if (activity!=null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, names);
                        listView.setAdapter(adapter);
                    }
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        },"/resource/" + selectedResource.getResourceId() +"/children");
        ttst.execute();
    }

    public void onClick(View view) {
        if (view.equals(pickButton)) {
            System.err.println("Picked resource: " + selectedResource);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("currentResourceId",selectedResource.getResourceId());
            editor.putString("currentResourceName",selectedResource.getResourceName());
            editor.commit();

            if (getActivity() instanceof MetricChartActivity) {
                // TODO clean this up
                MetricChartActivity activity= (MetricChartActivity) getActivity();
                activity.getActionBar().setSubtitle(selectedResource.getResourceName());
                if (activity.dialog!=null)
                    activity.dialog.cancel();

                ScheduleListFragment fragment  =
                    (ScheduleListFragment) getFragmentManager().findFragmentById(R.id.left_picker);

                if (fragment==null) {
                    Log.d("ResPicker", "Did not find the target fragment");
                } else {
                    fragment.setResourceId(selectedResource.getResourceId());
                    FrameLayout parent = (FrameLayout) fragment.getView().getParent();
                    if (parent.getVisibility()==View.GONE) {
                        parent.setVisibility(View.VISIBLE);
                    }
                }
            } else if (getActivity() instanceof RHQActivity) {
                ((RHQActivity) getActivity()).refresh(null);
            }

        }
        if (ttst!=null)
            ttst.cancel(true);
        dismiss(); // close the dialog
    }
}
