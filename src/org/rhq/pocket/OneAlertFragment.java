package org.rhq.pocket;

import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.rhq.core.domain.rest.AlertRest;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
public class OneAlertFragment extends Fragment implements View.OnClickListener {

    private Button button;
    private AlertRest alert;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.one_alert_fragment,container,false);
        button = (Button) view.findViewById(R.id.one_alert_details_button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.equals(button)) {
            Activity activity =getActivity();
            FragmentManager fm = activity.getFragmentManager();
            if(fm.findFragmentById(R.id.alert_detail_container)==null) {

                UserDetailFragment fragment = new UserDetailFragment();
                fragment.setLogin("rhqadmin");

                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.alert_detail_container,fragment);
                ft.commit();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        fillFields();

    }

    public void fillFields() {

        // Hide details
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.alert_detail_container);
        if (fragment !=null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment);
            ft.commit();
        }

        // Now fill in new information
        TextView name = (TextView) view.findViewById(R.id.one_alert_name);
        name.setText(alert.getName());
        TextView id = (TextView) view.findViewById(R.id.one_alert_id);
        id.setText(alert.getId());
        TextView descr = (TextView) view.findViewById(R.id.one_alert_descr);
        descr.setText(alert.getDescription());
        TextView date = (TextView) view.findViewById(R.id.one_alert_date);
        date.setText(new Date(alert.getAlertTime()).toLocaleString());
    }

    public void setAlert(AlertRest alert) {

        this.alert = alert;
    }
}
