package org.rhq.pocket;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.rhq.pocket.helper.DisplayRange;

public class SplashActivity extends Activity
{


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new ForwardAction(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();




    }

    private class ForwardAction extends AsyncTask<Void,Void,Void> {

        static final long SPLASH_MIN_SECONDS = 1 * 1000L;
        Context context;
        long time;

        private ForwardAction(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            time = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ////////////// do the initialization work here vvvvvvvv

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String username = preferences.getString("username", "-notset-");
            String password = preferences.getString("password","-notset-");

            RHQPocket.getInstance().username = username;
            RHQPocket.getInstance().password = password;

            RHQPocket.getInstance().displayRangeUnits= DisplayRange.HOUR;
            RHQPocket.getInstance().displayRangeValue=8;

            ////////////// do the initialization work here ^^^^^^^^


            long diffTime = System.currentTimeMillis() - time;

            if (diffTime < SPLASH_MIN_SECONDS) {
                try {
                    // Display slash a little longer
                    Thread.sleep(SPLASH_MIN_SECONDS - diffTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            // Forward to the main activity
            Intent i = new Intent(context,MetricChartActivity.class);
            startActivity(i);

        }
    }
}
