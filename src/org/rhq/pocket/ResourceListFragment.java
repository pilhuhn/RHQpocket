package org.rhq.pocket;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.metric.ScheduleListFragment;

/**
 * Fragment that shows a list of resources
 * @author Heiko W. Rupp
 */
public class ResourceListFragment extends ListFragment {

    List<ResourceWithType> resourcesWithTypes = new ArrayList<ResourceWithType>() ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resource_list_fragment, container);

        System.out.println("Inflated view");

        return view;

    }

    public void onListItemClick(ListView l, View v, int position, long id) {

        ResourceWithType rwt = resourcesWithTypes.get(position);

        // TODO we need a different list item(?) to distinguish clicking for children or for other stuff
        ScheduleListFragment fragment  =
            (ScheduleListFragment) getFragmentManager().findFragmentById(R.layout.schedule_list_fragment);

        if (fragment==null)
            return;

        fragment.setResourceId(rwt.getResourceId());
    }
}
