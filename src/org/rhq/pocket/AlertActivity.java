package org.rhq.pocket;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
public class AlertActivity extends Activity implements Refreshable{

    private FrameLayout progressLayout;

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
    public void showProgress() {
        if (progressLayout!=null) {

            ProgressBar pb = new ProgressBar(this);
            pb.setIndeterminate(true);
            pb.setVisibility(View.VISIBLE);

            progressLayout.removeAllViews();
            progressLayout.addView(pb);
        }
    }

    @Override
    public void hideProgress() {
        if (progressLayout!=null) {

            ImageButton ib = new ImageButton(this);
            ib.setImageResource(R.drawable.ic_menu_refresh);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh(view);
                }
            });
            progressLayout.removeAllViews();
            progressLayout.addView(ib);

        }
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
