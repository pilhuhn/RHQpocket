package org.rhq.pocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Base activity with some common functionality
 * @author Heiko W. Rupp
 */
public abstract class RHQActivity extends Activity implements Refreshable {

    protected FrameLayout progressLayout;
    protected Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this instanceof OverviewActivity)) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.preferences) {
            Intent i = new Intent(this, Preferences.class);
            startActivity(i);
            return true;
        }

        if (item.getItemId()==android.R.id.home) {
            Intent i = new Intent(this,OverviewActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableMenuItem(int id, boolean enabled) {
        if (menu==null)
            return;
        MenuItem item = menu.findItem(id);
        if (item!=null)
            item.setEnabled(enabled);
    }
}
