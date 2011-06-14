package org.rhq.pocket;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Fragment that shows a list of resources
 * @author Heiko W. Rupp
 */
public class ResourceListFragement extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.timelines_list_fragment, container);

        List<String> timelines = new ArrayList<String>();
        timelines.add("Home");
        timelines.add("Mentions");
        timelines.add("Directs");
        timelines.add("Sent");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, timelines);
        setListAdapter(adapter);
//        getListView().setItemChecked(0,true);

        return null;

    }

}
