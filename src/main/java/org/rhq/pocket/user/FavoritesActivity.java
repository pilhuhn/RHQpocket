package org.rhq.pocket.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;

import org.rhq.core.domain.rest.ResourceWithType;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.TalkToServerTask;
import org.rhq.pocket.alert.AlertActivity;
import org.rhq.pocket.metric.MetricChartActivity;
import org.rhq.pocket.operation.OperationActivity;
import org.rhq.pocket.operation.OperationHistoryActivity;
import org.rhq.pocket.resource.ResourceDetailFragment;

/**
 * Deal with favorites
 * @author Heiko W. Rupp
 */
public class FavoritesActivity extends RHQActivity {

    private Fragment listFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_one_detail_layout);
        getActionBar().setTitle(R.string.Favorites);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setupListFragment();

    }

    private void setupListFragment() {
        FragmentManager fm = getFragmentManager();
        listFragment = fm.findFragmentById(R.id.list_container);

        if (listFragment ==null) {
            listFragment = new FavoritesListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(R.id.list_container, listFragment);

            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites_menu,menu);
        this.menu = menu;
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home)
            return super.onOptionsItemSelected(item);

        ResourceWithType favorite=null;
        Intent intent;
        FragmentManager fm = getFragmentManager();
        ResourceDetailFragment detailFragment = (ResourceDetailFragment) fm.findFragmentById(R.id.detail_container);
        if (detailFragment!=null) {
            favorite = detailFragment.getResource();
        }

        if (favorite==null) {
            Toast.makeText(this,"No fav selected", Toast.LENGTH_SHORT).show();
            return true;
        }

        switch (item.getItemId()) {
        case R.id.remove_from_favorites:
            removeCurrentFavorite(favorite);
            return true;

        case R.id.list_alerts:
            intent = new Intent(this, AlertActivity.class);
            intent.putExtra("resourceId",favorite.getResourceId());
            intent.putExtra("resourceName", favorite.getResourceName());
            startActivity(intent);
            return true;

        case R.id.show_metrics:
            intent = new Intent(this, MetricChartActivity.class);
            intent.putExtra("resourceId", favorite.getResourceId());
            intent.putExtra("resourceName", favorite.getResourceName());
            startActivity(intent);
            return true;
        case R.id.schedule_ops:
            intent = new Intent(this, OperationActivity.class);
            intent.putExtra("resourceId", favorite.getResourceId());
            intent.putExtra("resourceName", favorite.getResourceName());
            startActivity(intent);
            return true;
        case R.id.operation_history:
            intent = new Intent(this, OperationHistoryActivity.class);
            intent.putExtra("resourceId", favorite.getResourceId());
            intent.putExtra("resourceName", favorite.getResourceName());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeCurrentFavorite(ResourceWithType resourceWithType) {
        FragmentManager fm = getFragmentManager();
        ResourceDetailFragment detailFragment = (ResourceDetailFragment) fm.findFragmentById(R.id.detail_container);
        if (detailFragment!=null) {
            ResourceWithType favorite = detailFragment.getResource();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.remove(detailFragment);

            new TalkToServerTask(this,new FinishCallback() {
                @Override
                public void onSuccess(JsonNode result) {
                    Toast.makeText(FavoritesActivity.this,"Favorite removed",Toast.LENGTH_SHORT).show();
                    refresh(null);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(FavoritesActivity.this,"Removal of favorite failed",Toast.LENGTH_LONG).show();
                }
            },"/user/favorites/resource/" + favorite.getResourceId(),"DELETE").execute();
        }

    }

    @Override
    public void refresh(View v) {
        setupListFragment();
        if (listFragment!=null)
            ((FavoritesListFragment)listFragment).loadFavorites();
    }
}