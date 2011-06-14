package org.rhq.pocket;

import android.app.Activity;
import android.os.Bundle;

/**
 * Show the system overview ("Dashboard")
 * @author Heiko W. Rupp
 */
public class OverviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    // TODO: Customise this generated block

        String server = "http://172.31.7.9:7080/";
        String user = "rhqadmin";
        String password = "rhqadmin";


    }

    @Override
    protected void onResume() {
        super.onResume();    // TODO: Customise this generated block
    }
}
