package org.rhq.pocket;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
public abstract class RHQActivity extends Activity implements Refreshable {

    FrameLayout progressLayout;

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

}
