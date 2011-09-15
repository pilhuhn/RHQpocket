package org.rhq.pocket;


import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
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
import android.widget.TabHost;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.ResourceWithType;

public class StartActivity extends Activity
{

    SharedPreferences preferences ;
    TabHost tabHost;
    Dialog dialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
/*
        tabHost = getTabHost();

        Intent intent = new Intent().setClass(this,OverviewActivity.class);
        TabHost.TabSpec homeSpec;

        homeSpec = tabHost.newTabSpec("overview")
                .setIndicator("Overview") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("favorites")
                .setIndicator("Favorites") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("resources")
                .setIndicator("Resources") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("groups")
                .setIndicator("Groups") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);
*/

    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

/*
        final TextView foo = (TextView) findViewById(R.id.foo_text);
        foo.setText("---- unset -----");

        TalkToServerTask tst = new TalkToServerTask(this, new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    ResourceWithType rwt = objectMapper.readValue(result,ResourceWithType.class);
                    foo.setText("Resource: " + rwt.getResourceName() + ", type= " + rwt.getTypeName());
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                    System.out.println(result.toString());
                }
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        }, "/resource/10001", false);
        tst.execute();
*/

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
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
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = new ResourcePickerFragement();
            newFragment.show(ft, "dialog");
            break;

        default:
            Log.e(getClass().getName(),"Unknown menu item :"+ item.toString());
        }
        return true;
    }
}
