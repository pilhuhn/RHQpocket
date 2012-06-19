package org.rhq.pocket.resource;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;

/**
 * Display resource groups
 * @author Heiko W. Rupp
 */
public class GroupActivity extends RHQActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(R.string.Groups);
        setContentView(R.layout.list_one_detail_layout);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.list_container);
        if (fragment==null) {
            fragment = new GroupListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(R.id.list_container,fragment);
            ft.commit();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.groups_menu,menu);
        this.menu = menu;
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        return true;
    }


    @Override
    public void refresh(View v) {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.list_container);

        if (fragment !=null && fragment instanceof GroupListFragment) {
            ((GroupListFragment) fragment).setupGroupList();
        }
    }
}