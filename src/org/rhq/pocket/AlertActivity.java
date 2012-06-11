package org.rhq.pocket;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Activity to show alerts
 * @author Heiko W. Rupp
 */
public class AlertActivity extends RHQActivity implements Refreshable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_action_layout);

        FragmentManager fm = getFragmentManager();
        AlertListFragment alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
        if (alertListFragment==null) {
            alertListFragment = new AlertListFragment();
            FragmentTransaction ft = fm.beginTransaction();

            ft.add(R.id.alert_list_container,alertListFragment);

            ft.commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.alerts_menu,menu);
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        return true;
    }



    @Override
    public void refresh(View v) {
        FragmentManager fm=getFragmentManager();
        AlertListFragment alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
        if (alertListFragment==null) {
            alertListFragment = new AlertListFragment();
            FragmentTransaction ft = fm.beginTransaction();

            ft.add(R.id.alert_list_container,alertListFragment);

            ft.commit();
        }

        else {
            alertListFragment.fetchAlerts();
        }
    }
}
