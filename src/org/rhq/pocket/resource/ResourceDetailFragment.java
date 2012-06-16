package org.rhq.pocket.resource;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.AlertRest;
import org.rhq.core.domain.rest.AvailabilityRest;
import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.TalkToServerTask;

/**
 * Show the details of a resource
 * @author Heiko W. Rupp
 */
public class ResourceDetailFragment extends Fragment {

    private View layout;
    private ResourceWithType resource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.resource_detail_fragment,container,false);
        return layout;
    }

    public void setResource(ResourceWithType resource) {

        this.resource = resource;
        if (layout!=null)
            fillFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFields();
    }

    private void fillFields() {
        if (resource !=null) {
            TextView view = (TextView) layout.findViewById(R.id.resource_name);
            view.setText(resource.getResourceName());
            view = (TextView) layout.findViewById(R.id.resource_id);
                    view.setText(String.valueOf(resource.getResourceId()));
            view = (TextView) layout.findViewById(R.id.resource_ancestry);
            String ancestry = resource.getAncestry();
            ancestry = computeAncestry(ancestry);
            view.setText(ancestry);
            view = (TextView) layout.findViewById(R.id.plugin_text);
            view.setText(resource.getPluginName()) ;
            view = (TextView) layout.findViewById(R.id.resource_type_text);
            view.setText(resource.getTypeName() + " ("+ resource.getTypeId() + ")");
            view = (TextView) layout.findViewById(R.id.availability_view);
            fillAvailability(view);
        }
    }

    private void fillAvailability(final TextView view) {

        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    AvailabilityRest avail = objectMapper.readValue(result,new TypeReference<AvailabilityRest>() {});
                    view.setText(avail.getType());
                    Drawable d = getResources().getDrawable(R.drawable.availability_grey_24);
                    if (avail.getType().equals("UP")) {

                        d=getResources().getDrawable(R.drawable.availability_green_24);
                    } else if (avail.getType().equals("DOWN")) {
                        d=getResources().getDrawable(R.drawable.availability_red_24);
                    } else if (avail.getType().equals("DISABLED"))
                        d=getResources().getDrawable(R.drawable.availability_orange_24);

                    float dim = getResources().getDimension(R.dimen.avail_icon);
                    d.setBounds(0,0,(int)dim,(int)dim);
                    view.setCompoundDrawables(null,null,d,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {
                view.setText("-unknown-");
            }
        },"/resource/"+resource.getResourceId()+"/availability").execute();
    }

    private String computeAncestry(String ancestry) {
        if (ancestry==null)
            return "- no parent -";

        StringBuilder builder = new StringBuilder();
        String[] ancestors = ancestry.split("_::_");
        int i = 0, ancestorsLength = ancestors.length;
        while (i < ancestorsLength) {
            String ancestor = ancestors[i];
            String[] parts = ancestor.split("_:_");
            builder.append(parts[2]);
            if (i!=ancestorsLength-1)
                builder.append(" > ");

            i++;
        }
        return builder.toString();
    }

    public ResourceWithType getResource() {
        return resource;
    }
}
