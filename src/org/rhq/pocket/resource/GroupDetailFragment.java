package org.rhq.pocket.resource;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.rhq.core.domain.rest.GroupRest;
import org.rhq.pocket.R;

/**
 * Show details of a group
 * @author Heiko W. Rupp
 */
public class GroupDetailFragment extends Fragment {

    private View layout;
    private GroupRest group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.group_detail_fragment,container,false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFields();
    }

    public void setGroup(GroupRest group) {
        this.group = group;
    }

    public void fillFields() {
        if (group==null)
            return;

        TextView tv = (TextView) layout.findViewById(R.id.name_view);
        tv.setText(group.getName());
        tv = (TextView) layout.findViewById(R.id.category_view);
        tv.setText(group.getCategory().toString());
        CheckBox cb = (CheckBox) layout.findViewById(R.id.recursive_mark);
        cb.setChecked(group.isRecursive());
    }
}
