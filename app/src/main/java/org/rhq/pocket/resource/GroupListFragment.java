package org.rhq.pocket.resource;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.GroupRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.TalkToServerTask;

/**
 * List resource groups
 * @author Heiko W. Rupp
 */
public class GroupListFragment extends ListFragment {

    private List<GroupRest> groupList;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.generic_list_fragment, container, false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupGroupList();

    }

    public void setupGroupList() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    groupList = objectMapper.readValue(result,new TypeReference<List<GroupRest>>() {});
                    List<String> groupNames = new ArrayList<String>(groupList.size());

                    for (GroupRest group : groupList) {
                        groupNames.add(group.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, groupNames);
                    setListAdapter(adapter);

                    // Remove the progress indicator below the list now that we have returned from the server call
                    ProgressBar pb = (ProgressBar) layout.findViewById(R.id.list_progress);
                    if (pb!=null)
                        pb.setVisibility(View.GONE);
                    getListView().requestLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        },"/group").execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        GroupRest group = groupList.get(position);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_container);


        GroupDetailFragment gdfragment = new GroupDetailFragment();
        gdfragment.setGroup(group);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragment==null)
            ft.add(R.id.detail_container,gdfragment);
        else
            ft.replace(R.id.detail_container,gdfragment);
        ft.commit();


    }
}
