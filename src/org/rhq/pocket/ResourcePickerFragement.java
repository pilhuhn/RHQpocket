package org.rhq.pocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.ResourceWithType;

/**
 * Fragment that allows to pick a resource
 * @author Heiko W. Rupp
 */
public class ResourcePickerFragement extends DialogFragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    List<ResourceWithType> resourcesWithTypes = new ArrayList<ResourceWithType>() ;
    Button pickButton;
    TextView selectedResourceView;
    ResourceWithType selectedResource;
    ListView listView;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resource_picker_fragment, container);

        pickButton = (Button) view.findViewById(R.id.pick_resource);
        selectedResourceView = (TextView) view.findViewById(R.id.selected_resource);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);

        pickButton.setOnClickListener(this);


        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                System.out.println("got data :" +  result.toString());
                System.out.println("is array. " + result.isArray());
                System.out.println("is object. " + result.isObject());
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
        },"/resource/platforms", true).execute();

        return view;

    }

    // Get children
    public void onItemClick(AdapterView l, View v, int position, long id) {

        selectedResource = resourcesWithTypes.get(position);
        selectedResourceView.setText(selectedResource.getResourceName());
        pickButton.setEnabled(true);

        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                System.out.println("got data :" +  result.toString());
                System.out.println("is array. " + result.isArray());
                System.out.println("is object. " + result.isObject());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); // TODO disable when REST API is stable
                List<String> names = new ArrayList<String>();

                try {
                    List<ResourceWithType> resources = objectMapper.readValue(result,new TypeReference<List<ResourceWithType>>() {});
                    System.out.println(resources);
                    resourcesWithTypes.clear();

                    for (ResourceWithType rwt :resources ) {
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
        },"/resource/" + selectedResource.getResourceId() +"/children", true).execute();

    }

    public void onClick(View view) {
        if (view.equals(pickButton)) {
            System.err.println("Picked resource: " + selectedResource);

            StartActivity activity= (StartActivity) getActivity();
            if (activity.dialog!=null)
                activity.dialog.cancel();

            ScheduleListFragement fragment  =
                (ScheduleListFragement) getFragmentManager().findFragmentById(R.id.schedule_list_fragment);

            if (fragment==null)
                return;

            fragment.setResourceId(selectedResource.getResourceId());

        }
        dismiss(); // close the dialog
    }
}
