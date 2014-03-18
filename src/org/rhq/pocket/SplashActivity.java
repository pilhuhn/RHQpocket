package org.rhq.pocket;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

        static final long SPLASH_MIN_MILLIS = 1 * 500L;
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


            RHQPocket.getInstance().displayRangeUnits= DisplayRange.HOUR;
            RHQPocket.getInstance().displayRangeValue=8;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean runChecker = prefs.getBoolean("alert_service",false);

            if (runChecker) {
                String tmp = prefs.getString("alert_check_interval_minutes","5");
                Integer interval = Integer.valueOf(tmp);
                Intent serviceIntent = new Intent().setClass(SplashActivity.this,AlertCheckService.class);
                if (interval!=null) {
                    serviceIntent.putExtra("intervalMinutes",interval);
                }
                startService(serviceIntent);
            }

            disableSSLCertificateChecking();

            ////////////// do the initialization work here ^^^^^^^^


            long diffTime = System.currentTimeMillis() - time;

            if (diffTime < SPLASH_MIN_MILLIS) {
                try {
                    // Display slash a little longer
                    Thread.sleep(SPLASH_MIN_MILLIS - diffTime);
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
            Intent i = new Intent(context,OverviewActivity.class);
            startActivity(i);

        }
    }

    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
     * aid testing on a local box, not for use on production.
     *
     * From https://gist.github.com/aembleton/889392
     */
    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
