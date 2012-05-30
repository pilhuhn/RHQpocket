package org.rhq.pocket;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.rhq.core.domain.rest.MetricSchedule;

public class MetricChartActivity extends Activity
{

    SharedPreferences preferences ;
    Dialog dialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_chart_layout);

        FragmentManager fm = getFragmentManager();
        ChartFragment cf = (ChartFragment) fm.findFragmentById(R.id.chart_container);
        ScheduleListFragment slf = (ScheduleListFragment) fm.findFragmentById(R.id.left_picker);

        FragmentTransaction ft = fm.beginTransaction();
        if (cf==null) {
            ft.add(R.id.chart_container, new ChartFragment());
        }

        if (slf==null) {
            ft.add(R.id.left_picker, new ScheduleListFragment());
        }
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // If the user has previously picked a resource, re-use it
        int resourceId = preferences.getInt("currentResourceId",-1);
        if (resourceId!=-1) {
            String resourceName = preferences.getString("currentResourceName","");
            if (!resourceName.equals("")) {
                getActionBar().setSubtitle(resourceName);
            }
            // User has already picked one, lets use it
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ScheduleListFragment fragment  =
                (ScheduleListFragment) getFragmentManager().findFragmentById(R.id.left_picker);

            if (fragment==null)
                return;

            fragment.setResourceId(resourceId);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        FragmentTransaction ft;
        Fragment prev;
        DialogFragment newFragment;

        // Handle item selection
        switch (item.getItemId()) {
        case R.id.preferences:
            i = new Intent(this, Preferences.class);
            startActivity(i);
            break;
        case R.id.pick_resource:

            // Taken from Android docs:
            //
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            ft = getFragmentManager().beginTransaction();
            prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            newFragment = new ResourcePickerFragement();
            newFragment.setCancelable(true);
            newFragment.show(ft, "dialog");
            // No need to ft.commit()
            break;

        case R.id.edit_schedule:

            if (RHQPocket.getInstance().getCurrentSchedule()==null) {
                Toast.makeText(this,"Please select a metric first",Toast.LENGTH_SHORT).show();
            } else {

                ft = getFragmentManager().beginTransaction();
                prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                newFragment = new ScheduleDetailFragment();

                MetricSchedule schedule = RHQPocket.getInstance().currentSchedule;
                ((ScheduleDetailFragment)newFragment).setSchedule(schedule);

                newFragment.setCancelable(true);
                newFragment.show(ft, "dialog");
                // No need to ft.commit()
            }
            break;

        case android.R.id.home:
            // check if the schedule picker is visible and toggle its state
            View l = findViewById(R.id.left_picker);
            if (l.getVisibility()==View.VISIBLE) {
                l.setVisibility(View.GONE);
            }
            else {
                l.setVisibility(View.VISIBLE);
            }
            // TODO how to trigger a repaint of the chart view -- after the size change has taken effect?
            break;

        default:
            Log.e(getClass().getName(),"Unknown menu item :"+ item.toString());
        }
        return true;
    }
}
