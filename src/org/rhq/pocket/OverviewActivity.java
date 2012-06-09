package org.rhq.pocket;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Show the system overview ("Dashboard")
 * @author Heiko W. Rupp
 */
public class OverviewActivity extends Activity {

    private TextView alertCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overview);
        alertCountView = (TextView) findViewById(R.id.overview_alert_count);
        alertCountView.setText(getString(R.string.number_of_alerts,42) );


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get number of alerts
        // TODO list by category
        getNumberOfAlerts();
    }

    private void getNumberOfAlerts() {
        new TalkToServerTask(this,new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    Integer count = objectMapper.readValue(result,new TypeReference<Integer>() {});
                    alertCountView.setText(getString(R.string.number_of_alerts,count));
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }

            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        },"/alert/count").execute();
    }

    public void listAlerts(View v) {
        Intent i = new Intent(this,AlertActivity.class);
        startActivity(i);

    }

    public void showMetrics(View v) {

        Intent i = new Intent(this,MetricChartActivity.class);
        startActivity(i);
    }
}
