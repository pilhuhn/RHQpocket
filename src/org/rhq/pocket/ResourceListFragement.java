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
