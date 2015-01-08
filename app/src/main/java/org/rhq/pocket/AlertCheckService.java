package org.rhq.pocket;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.AlertRest;
import org.rhq.pocket.alert.AlertActivity;

/**
 * Background service that polls for new alerts
 * @author Heiko W. Rupp
 */
public class AlertCheckService extends Service {
    private boolean isRunning = false;
    private boolean shouldRun = true;
    private long since = -1;
    private int minutes;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent!=null) { // Services may get restarted
            minutes=intent.getIntExtra("intervalMinutes",5);
        }
        else {
            minutes = 5;
        }

        if (isRunning)
            return;

        isRunning=true;

        Thread t = new Thread(new Fetcher());
        t.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouldRun=false;


    }

    private class Fetcher implements Runnable {
        @Override
        public void run() {
            while (shouldRun) {

                long now = System.currentTimeMillis();
                String sub = "/alert";
                if (since>0)
                    sub = sub + "?since=" + since;

                try {
                    TalkToServerTask ttst = new TalkToServerTask(sub,getApplicationContext());
                    JsonNode node = ttst.getJsonNodes("GET",null);
                    // Skip the first round, as we are only interested in new incoming alerts
                    if (since>-1) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        try {
                            if (node!=null) {
                                List<AlertRest> alertList = objectMapper.readValue(node,new TypeReference<List<AlertRest>>() {});
                                if (alertList.size()>0) {
                                    createNotification(alertList);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }

                since=now;



                try {
                    Thread.sleep(1000 * 60 * minutes); // 1 minute TODO make configurable
                } catch (InterruptedException e) {
                    shouldRun = false;
                }
            }

            isRunning=false;
        }
    }

    private void createNotification(List<AlertRest> alertList) {
        String ns = Context.NOTIFICATION_SERVICE;
        Context context = getApplicationContext();
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
        mNotificationManager.cancelAll();
        Notification.Builder builder = new Notification.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.rhq_icon)
                .setContentTitle("New alerts")
                .setContentText("There are " + alertList.size() + " new alerts")
                .setNumber(alertList.size())
                .setLights(0xaa555, 3, 2)
                ;
        if (Build.VERSION.SDK_INT>16) {
            builder.setNumber(alertList.size());
        }

        Notification notification = builder.getNotification();


        //contentView.setImageViewResource(R.id.image, R.drawable.notification_image);

        Intent intent = new Intent(context,AlertActivity.class);
        Bundle bundle=new Bundle(3);
        intent.putExtras(bundle);
        PendingIntent pintent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        notification.setLatestEventInfo(context,
                "New Alerts",
                "There are new alerts",
                pintent);
        mNotificationManager.notify(3,notification);

    }
}
