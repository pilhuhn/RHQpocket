package org.rhq.pocket.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.resource.ResourceDetailFragment;

/**
 * List the favorites
 * @author Heiko W. Rupp
 */
public class FavoritesListFragment extends ListFragment {

    private List<ResourceWithType> favoriteList;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.generic_list_fragment, container, false);
        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavorites();

    }

    protected void loadFavorites() {

        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    favoriteList = objectMapper.readValue(result,new TypeReference<List<ResourceWithType>>() {});
                    List<String> resourceNames = new ArrayList<String>(favoriteList.size());
                    for (ResourceWithType resource : favoriteList) {
                        resourceNames.add(resource.getResourceName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, resourceNames);
                    setListAdapter(adapter);

                    ProgressBar pb = (ProgressBar) layout.findViewById(R.id.list_progress);
                    if (pb!=null)
                        pb.setVisibility(View.GONE);

                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }

            }

            @Override
            public void onFailure(Exception e) {
            }
        },"/user/favorites/resource").execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResourceWithType favorite = favoriteList.get(position);

        FragmentManager fm = getFragmentManager();
        ResourceDetailFragment fragment = (ResourceDetailFragment) fm.findFragmentById(R.id.detail_container);
        if (fragment==null) {
            fragment = new ResourceDetailFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.detail_container,fragment);
            ft.commit();
        }

        fragment.setResource(favorite);
    }
}
