package org.rhq.pocket.operation;

import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.OperationHistoryRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.TalkToServerTask;

/**
 * Show the details of a scheduled resource
 * @author Heiko W. Rupp
 */
public class OperationHistoryDetailFragment extends Fragment {

    private View layout;
    private OperationHistoryRest history;
    private String historyId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.operation_history_fragment, container, false);
        return layout;
    }

    public void setHistoryId(String id) {
        this.historyId = id;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (history==null)
            getCurrentStatus();
        else
            fillFields();

    }

    public void getCurrentStatus() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    history = objectMapper.readValue(result,new TypeReference<OperationHistoryRest>() {});
                    fillFields();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        },"/operation/history/" + historyId).execute();
    }

    private void fillFields() {

        TextView tv = (TextView) layout.findViewById(R.id.statusView);
        tv.setText(history.getStatus());
        tv = (TextView) layout.findViewById(R.id.errorView);
        tv.setText(history.getErrorMessage());
        tv = (TextView) layout.findViewById(R.id.operation_name);
        tv.setText(history.getOperationName() + " (" + history.getResourceName() +")");

        tv = (TextView) layout.findViewById(R.id.resultView);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,Object> entry : history.getResult().entrySet()) {
            sb.append("<b>");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append("</b>");
            sb.append(entry.getValue().toString());
            sb.append("<br/>");
        }
        ((RHQActivity)getActivity()).enableMenuItem(R.id.trash_this,true);

        tv.setText(Html.fromHtml(sb.toString()));
    }

    public void setHistory(OperationHistoryRest history) {
        this.history = history;
        historyId = history.getJobId();
    }

    public void deleteCurrent() {
        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                RHQActivity activity = (RHQActivity) getActivity();
                Toast.makeText(activity,getString(R.string.SuccessfullyDeleted),Toast.LENGTH_SHORT).show();
                activity.refresh(null);
                activity.enableMenuItem(R.id.trash_this,false);

                // remove the details fragment
                FragmentManager fm = getFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.detail_container);
                if (fragment!=null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.remove(fragment);
                    ft.commit();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(),getString(R.string.DeleteFailed) + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        },"/operation/history/" + historyId,"DELETE").execute();
    }
}
