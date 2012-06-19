package org.rhq.pocket.operation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;

/**
 * Activity to display operation histories and to handle them
 * @author Heiko W. Rupp
 */
public class OperationHistoryActivity extends RHQActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_one_detail_layout);
        getActionBar().setTitle(R.string.OperationHistories);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.list_container);
        if (fragment==null) {
            fragment = new OperationsHistoryListPicker(0);
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(R.id.list_container,fragment);
            ft.commit();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null && bundle.containsKey("resourceId")) {
            ((OperationsHistoryListPicker)fragment).setResourceId(bundle.getInt("resourceId"));
            String name = bundle.getString("resourceName");
            if (name!=null)
                getActionBar().setSubtitle(name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.operation_history_menu, menu);
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        this.menu=menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.trash_this) {

            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.detail_container);
            if (fragment!=null && fragment instanceof OperationHistoryDetailFragment) {
                ((OperationHistoryDetailFragment)fragment).deleteCurrent();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh(View v) {

        FragmentManager fm = getFragmentManager();
        Fragment lFragment = fm.findFragmentById(R.id.list_container);
        Fragment rFragment = fm.findFragmentById(R.id.detail_container);
        if (lFragment!=null && lFragment instanceof OperationsHistoryListPicker) {
            ((OperationsHistoryListPicker) lFragment).setupList();
        }
        if (rFragment!=null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.remove(rFragment);
            ft.commit();
        }
    }
}