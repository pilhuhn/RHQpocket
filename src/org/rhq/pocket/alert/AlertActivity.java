package org.rhq.pocket.alert;

import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
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

import org.rhq.core.domain.rest.AlertRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.alert.OneAlertFragment;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.Refreshable;
import org.rhq.pocket.TalkToServerTask;

/**
 * Activity to show alerts
 * @author Heiko W. Rupp
 */
public class AlertActivity extends RHQActivity implements Refreshable {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_action_layout);

        FragmentManager fm = getFragmentManager();
        AlertListFragment alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
        if (alertListFragment==null) {
            alertListFragment = new AlertListFragment();
            FragmentTransaction ft = fm.beginTransaction();

            ft.add(R.id.alert_list_container,alertListFragment);

            ft.commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.alerts_menu,menu);
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final FragmentManager fm = getFragmentManager();
        final OneAlertFragment fragment = (OneAlertFragment) fm.findFragmentById(R.id.one_alert_container);

        if (fragment==null) {
            return super.onOptionsItemSelected(item);
        }


        final AlertRest[] alert = {fragment.getAlert()};
        FinishCallback fcb;

        switch (item.getItemId()) {
        case R.id.alert_ack_alert:
            fcb = new FinishCallback() {
                @Override
                public void onSuccess(JsonNode result) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    try {
                        alert[0] = objectMapper.readValue(result,new TypeReference<AlertRest>() {} );
                        fragment.setAlert(alert[0]);
                        fragment.fillFields();
                        fragment.hideDetails();
                    } catch (IOException e) {
                        e.printStackTrace();  // TODO: Customise this generated block
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AlertActivity.this, "Update failed ", Toast.LENGTH_SHORT).show();
                }
            };
            new TalkToServerTask(AlertActivity.this, fcb
                ,"/alert/" + alert[0].getId(),"PUT").execute(alert[0]);
            break;
        case R.id.alert_delete_alert:
            fcb = new FinishCallback() {
                @Override
                public void onSuccess(JsonNode result) {
                    Fragment detailFragment = fm.findFragmentById(R.id.alert_detail_container);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.remove(fragment);
                    if (detailFragment!=null)
                        ft.remove(detailFragment);
                    ft.commit();

                    AlertListFragment listFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
                    listFragment.fetchAlerts(); // We could just locally remove ,but lets check for new stuff
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AlertActivity.this, "Delete failed ", Toast.LENGTH_SHORT).show();
                }
            };
            new TalkToServerTask(AlertActivity.this, fcb
                ,"/alert/" + alert[0].getId(),"DELETE").execute();

            break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void refresh(View v) {
        FragmentManager fm=getFragmentManager();
        AlertListFragment alertListFragment = (AlertListFragment) fm.findFragmentById(R.id.alert_list_container);
        if (alertListFragment==null) {
            alertListFragment = new AlertListFragment();
            FragmentTransaction ft = fm.beginTransaction();

            ft.add(R.id.alert_list_container,alertListFragment);

            ft.commit();
        }

        else {
            alertListFragment.fetchAlerts();
        }
    }

    public void setAckMenuItemEnabled(boolean enabled) {
        MenuItem item = menu.findItem(R.id.alert_ack_alert);
        item.setEnabled(enabled);
    }


}
