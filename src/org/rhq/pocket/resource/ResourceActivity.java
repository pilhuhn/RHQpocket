package org.rhq.pocket.resource;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.ResourcePickerFragment;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.alert.AlertActivity;
import org.rhq.pocket.metric.MetricChartActivity;
import org.rhq.pocket.operation.OperationActivity;
import org.rhq.pocket.operation.OperationHistoryActivity;

/**
 * Show resources
 * @author Heiko W. Rupp
 */
public class ResourceActivity extends RHQActivity {
    private int resourceId=-1;
    private ResourceWithType resource;
    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_only_layout);
        getActionBar().setTitle(R.string.Resources);

        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentById(R.id.detail_container);
        if (f==null) {
            f = new ResourceDetailFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(R.id.detail_container,f);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // If the user has previously picked a resource, re-use it
        // If we got a resource in (e.g. from favs), don't use the one from prevs
        if (resourceId==-1 )
            resourceId = preferences.getInt("currentResourceId", -1);
        if (resourceId !=-1) {
            String resourceName = preferences.getString("currentResourceName", "");
            if (!resourceName.equals("")) {
                getActionBar().setSubtitle(resourceName);
            }

        }

        if (resourceId==-1) {
            pickResource();
        } else {
            refresh(null);
        }
    }

    private void pickResource() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new ResourcePickerFragment();
        newFragment.setCancelable(true);
        newFragment.show(ft, "dialog");
        // No need to ft.commit()
    }

    @Override
    public void refresh(View v) {

        if (resourceId==-1)
            resourceId = preferences.getInt("currentResourceId",-1);
        if (resourceId !=-1) {
            String resourceName = preferences.getString("currentResourceName", "");
            if (!resourceName.equals("")) {
                getActionBar().setSubtitle(resourceName);
            }
        }

        new TalkToServerTask(this, new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    resource = objectMapper.readValue(result,new TypeReference<ResourceWithType>() {});
                    FragmentManager fm = getFragmentManager();
                    Fragment f = fm.findFragmentById(R.id.detail_container);
                    if (f==null) {
                        f = new ResourceDetailFragment();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.add(R.id.detail_container,f);
                        ft.commit();
                    }

                    ((ResourceDetailFragment)f).setResource(resource);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
                e.printStackTrace();
            }
        },"/resource/" + resourceId).execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.resources_menu,menu);
        this.menu = menu;
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
        case R.id.list_alerts:
            intent = new Intent(this, AlertActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            intent.putExtra("resourceName", resource.getResourceName());
            startActivity(intent);
            return true;

        case R.id.show_metrics:
            intent = new Intent(this, MetricChartActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            intent.putExtra("resourceName", resource.getResourceName());
            startActivity(intent);
            return true;
        case R.id.schedule_ops:
            intent = new Intent(this, OperationActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            intent.putExtra("resourceName", resource.getResourceName());
            startActivity(intent);
            return true;
        case R.id.operation_history:
            intent = new Intent(this, OperationHistoryActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            intent.putExtra("resourceName", resource.getResourceName());
            startActivity(intent);
            return true;
        case R.id.add_to_favorites:
            addToFavorites(resource.getResourceId());
            return true;
        case R.id.pick_resource:
            pickResource();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addToFavorites(int resourceId) {
        if (resourceId==-1) {
            Toast.makeText(this, "Select a resource first", Toast.LENGTH_SHORT).show();
            return;
        }

        new TalkToServerTask(this,new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                Toast.makeText(ResourceActivity.this,"Added as favorite",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ResourceActivity.this,"Adding as favorite failed: " + e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        },"/user/favorites/resource/"+resourceId,"PUT").execute();
    }

}