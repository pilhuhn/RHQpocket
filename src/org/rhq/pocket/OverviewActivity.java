package org.rhq.pocket;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.pocket.alert.AlertActivity;
import org.rhq.pocket.metric.MetricChartActivity;
import org.rhq.pocket.operation.OperationHistoryActivity;
import org.rhq.pocket.resource.GroupActivity;
import org.rhq.pocket.resource.ResourceActivity;
import org.rhq.pocket.user.FavoritesActivity;

/**
 * Show the system overview ("Dashboard")
 * @author Heiko W. Rupp
 */
public class OverviewActivity extends RHQActivity implements Refreshable {

    private TextView alertCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overview);
        alertCountView = (TextView) findViewById(R.id.overview_alert_count);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get number of alerts
        // TODO list by category
        getServerStatus();
        if (RHQPocket.is44()) {
            disableFeatures();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.overview_menu,menu);
        progressLayout = (FrameLayout) menu.findItem(R.id.progress_thing).getActionView();
        return true;
    }


    @Override
    public void refresh(View v) {
        getServerStatus();
    }

    private void getServerStatus() {

        new TalkToServerTask(this, new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    Map<String,Object> map = objectMapper.readValue(result,new TypeReference<Map<String,Object>>() {});
                    if (map.containsKey("values")) {
                        Map<String,String> inner = (Map<String, String>) map.get("values");
                        if (inner.containsKey("SERVER_VERSION")) {
                            RHQPocket.getInstance().serverVersion=inner.get("SERVER_VERSION");
                        }
                        if (RHQPocket.getInstance().serverVersion.startsWith("4.4")) {
                            disableFeatures();
                        }
                        Log.d("OverviewActivity", "Server version: " + RHQPocket.getInstance().serverVersion);
                        if (inner.containsKey("AlertCount")) {
                            int alertCount = Integer.valueOf(inner.get("AlertCount"));
                            alertCountView.setText(getString(R.string.number_of_alerts,alertCount));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); // TODO
                }

            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        },"/status").execute();
    }

    private void disableFeatures() {
        View v = findViewById(R.id.opsHistoryButton);
        v.setEnabled(false);
    }

    public void start(View v) {
        Intent i;
        String tag = (String) v.getTag();
        if ("alerts".equals(tag))
            i = new Intent(this,AlertActivity.class);
        else if ("resources".equals(tag)) {
            i = new Intent(this,ResourceActivity.class);
        }
        else if ("groups".equals(tag)) {
            i = new Intent(this,GroupActivity.class);
        }
        else if ("favorites".equals(tag)) {
            i = new Intent(this,FavoritesActivity.class);
        }
        else if ("metrics".equals(tag)) {
            i = new Intent(this,MetricChartActivity.class);
        } else if ("operationHistories".equals(tag)) {
            i = new Intent(this,OperationHistoryActivity.class);
        }
        else
            throw new IllegalArgumentException("Unknown tag, check overview.xml");

        startActivity(i);

    }
}
