package org.rhq.pocket.operation;

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

import org.rhq.core.domain.rest.OperationDefinitionRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.TalkToServerTask;

/**
 * List fragment that shows a list of operation definitions for a resource
 * @author Heiko W. Rupp
 */
public class OperationsListPicker extends ListFragment {

    private int resourceId;
    private View layout;
    private List<OperationDefinitionRest> operationDefList;

    public OperationsListPicker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceId(int resourceId) {

        this.resourceId = resourceId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.generic_list_fragment, container, false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupList();
    }

    private void setupList() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    operationDefList = objectMapper.readValue(result,new TypeReference<List<OperationDefinitionRest>>() {});
                    List<String> resourceNames = new ArrayList<String>(operationDefList.size());
                    for (OperationDefinitionRest definitionRest : operationDefList) {
                        resourceNames.add(definitionRest.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, resourceNames);
                    setListAdapter(adapter);

                    ProgressBar pb = (ProgressBar) layout.findViewById(R.id.list_progress);
                    if (pb!=null)
                        pb.setVisibility(View.GONE);
                }
                catch (Exception e ) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        },"/operation/definitions?resourceId="+ resourceId).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        OperationDefinitionRest def = operationDefList.get(position);

        FragmentManager fm = getFragmentManager();
        Fragment fragment =  fm.findFragmentById(R.id.detail_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragment!=null) {
            ft.remove(fragment);
        }

        fragment = new ScheduleOperationDetailFragment(def, resourceId);
        ft.add(R.id.detail_container,fragment);

        ft.commit();

        // TODO hide list?

    }
}
