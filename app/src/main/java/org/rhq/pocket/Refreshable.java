package org.rhq.pocket;

import android.view.View;

/**
 * Marks Activities that provide a refresh button
 * @author Heiko W. Rupp
 */
public interface Refreshable {

    void showProgress();

    void hideProgress();

    public void refresh(View v);
}
