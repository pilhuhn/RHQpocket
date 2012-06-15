package org.rhq.pocket;

import android.app.Activity;
import android.content.Intent;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
