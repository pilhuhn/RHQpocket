package org.rhq.pocket.resource;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.R;

/**
 * Show the details of a resource
 * @author Heiko W. Rupp
 */
public class ResourceDetailFragment extends Fragment {

    private View layout;
    private ResourceWithType favorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.resource_detail_fragment,container,false);
        return layout;
    }

    public void setFavorite(ResourceWithType favorite) {

        this.favorite = favorite;
        if (layout!=null)
            fillFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFields();
    }

    private void fillFields() {
        if (favorite!=null) {
            TextView view = (TextView) layout.findViewById(R.id.resource_name);
            view.setText(favorite.getResourceName());
            view = (TextView) layout.findViewById(R.id.resource_id);
                    view.setText(String.valueOf(favorite.getResourceId()));
            view = (TextView) layout.findViewById(R.id.resource_ancestry);
            if (favorite.getAncestry()!=null)
                    view.setText(favorite.getAncestry());
            else
                view.setText("- no parent -");
        }
    }

    public ResourceWithType getFavorite() {
        return favorite;
    }
}
