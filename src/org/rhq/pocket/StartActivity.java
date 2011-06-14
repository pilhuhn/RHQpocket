package org.rhq.pocket;


import java.io.IOException;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TabHost;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.ResourceWithType;

public class StartActivity extends TabActivity
{

    SharedPreferences preferences ;
    TabHost tabHost;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tabHost = getTabHost();

        Intent intent = new Intent().setClass(this,OverviewActivity.class);
        TabHost.TabSpec homeSpec;

        homeSpec = tabHost.newTabSpec("overview")
                .setIndicator("Overview") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("favorites")
                .setIndicator("Favorites") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("resources")
                .setIndicator("Resources") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

        homeSpec = tabHost.newTabSpec("groups")
                .setIndicator("Groups") // TODO add icon
                .setContent(intent);
        tabHost.addTab(homeSpec);

    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final TextView foo = (TextView) findViewById(R.id.foo_text);
        foo.setText("---- unset -----");

        TalkToServerTask tst = new TalkToServerTask(this, new FinishCallback() {
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    ResourceWithType rwt = objectMapper.readValue(result,ResourceWithType.class);
                    foo.setText("Resource: " + rwt.getResourceName() + ", type= " + rwt.getTypeName());
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
            }

            public void onFailure(Exception e) {
                // TODO: Customise this generated block
            }
        }, "/resource/r/10001");
        tst.execute();

    }
}
