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

import org.rhq.core.domain.rest.ResourceWithType;

/**
 * Fragment that shows a list of resources
 * @author Heiko W. Rupp
 */
public class ResourceListFragement extends ListFragment {

    List<ResourceWithType> resourcesWithTypes = new ArrayList<ResourceWithType>() ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resource_list_fragment, container);

        System.out.println("Inflated view");


        new TalkToServerTask(getActivity(),new FinishCallback() {
            public void onSuccess(JsonNode result) {
                System.out.println("got data :" +  result.toString());
                System.out.println("is array. " + result.isArray());
                System.out.println("is object. " + result.isObject());
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> names = new ArrayList<String>();

                try {
                    List<Map<String,ResourceWithType>> resources = objectMapper.readValue(result,new TypeReference<List<Map<String,ResourceWithType>>>() {});
                    System.out.println(resources);
                    resourcesWithTypes.clear();

                    for (Map<String,ResourceWithType> map : resources) {

                        System.out.println("is map ");
                        Set<Map.Entry<String,ResourceWithType>> set = map.entrySet();
                        for (Map.Entry entry : set) {
                            ResourceWithType rwt = (ResourceWithType) entry.getValue();
                            names.add(rwt.getResourceName());
                            resourcesWithTypes.add(rwt);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
                    setListAdapter(adapter);
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

    public void onListItemClick(ListView l, View v, int position, long id) {

        ResourceWithType rwt = resourcesWithTypes.get(position);

        // TODO we need a different list item(?) to distinguish clicking for children or for other stuff
        ScheduleListFragement fragment  =
            (ScheduleListFragement) getFragmentManager().findFragmentById(R.id.schedule_list_fragment);

        if (fragment==null)
            return;

        fragment.setResourceId(rwt.getResourceId());
    }
}
