package org.rhq.pocket.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

/**
 * Deal with favorites
 * @author Heiko W. Rupp
 */
public class FavoritesActivity extends RHQActivity {

    private Fragment listFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_one_detail_layout);

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
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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

        switch (item.getItemId()) {
        case R.id.remove_from_favorites:
            removeCurrentFavorite();
            break;

        case R.id.list_alerts:
            ;
            break;

        case R.id.show_metrics:
            ;
            break;
        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void removeCurrentFavorite() {
        FragmentManager fm = getFragmentManager();
        FavoriteDetailFragment detailFragment = (FavoriteDetailFragment) fm.findFragmentById(R.id.detail_container);
        if (detailFragment!=null) {
            ResourceWithType favorite = detailFragment.getFavorite();
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