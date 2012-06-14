package org.rhq.pocket;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Async task doing the communication with the RHQ server
 * @author Heiko W. Rupp
 */
public class TalkToServerTask extends AsyncTask<Object,Void,JsonNode> {

    public static final String CNAME ="TalkToServerTask";

    private Context ctx;
    private FinishCallback callback;
    private String subUrl;
    Dialog dialog;
    private String encodedCredentials;
    private String mode = "GET";
    private Refreshable refreshable;
    private Exception storedException; // Created in background, fetched later in UI thread for further processing

    public TalkToServerTask(Context ctx, FinishCallback callback, String subUrl) {

        this.ctx = ctx;
        this.callback = callback;
        this.subUrl = subUrl;

        if (ctx instanceof Refreshable)
            refreshable = (Refreshable) ctx;

        String username = RHQPocket.getInstance().username;
        String password = RHQPocket.getInstance().password;

        String s = username + ":" + password;
        this.encodedCredentials = Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);
    }

    public TalkToServerTask(Context ctx, FinishCallback callback, String subUrl, String mode) {
        this(ctx,callback,subUrl);
        this.mode = mode;
    }

    @SuppressWarnings("unchecked")
    public AsyncTask<Object,Void,JsonNode> execute() {

        return super.execute();

    }


    protected JsonNode doInBackground(Object... objects) {

        if (mode.equals("DELETE") && objects!=null && objects.length>0)
            throw new IllegalArgumentException("DELETE supports not attached objects");

        InputStream inputStream;
        BufferedReader br=null;
        long t1 = System.currentTimeMillis();
        try {
            // Example remote url
            //   http://localhost:7080/rest/1/resource/10001
            String urlString = getHostPort() + "/rest/1"; // TODO put into preferences
            urlString =urlString + subUrl;
            URL url = new URL(urlString);
            System.out.println("Going for " +mode + " " + urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(mode);

            conn.setDoInput(true);
            conn.setRequestProperty ("Authorization", "Basic " + encodedCredentials);
            conn.setRequestProperty("Accept","application/json");

            ObjectMapper mapper = new ObjectMapper();
            // set into lenient mode
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // pretty print
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,true);

            conn.setRequestProperty("Content-Type","application/json");

            if (objects!=null && objects.length>0) {
                conn.setDoOutput(true);
            }
            conn.connect();

            if (objects!=null && objects.length>0) {

                OutputStream out = conn.getOutputStream();

/*
                if (true) {
                    String result = mapper.writeValueAsString(objects[0]);
                    System.out.println("Json to send: \n" + result);
                    System.out.flush();
                }
*/
                mapper.writeValue(out, objects[0]);

                out.flush();
                out.close();
            }

            int responseCode = conn.getResponseCode();
            Log.d(CNAME,"response code was "+ responseCode);
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Fake a response for further processing
                inputStream = new ByteArrayInputStream("{\"value\":\"ok\"}".getBytes());
            }
            else if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            if (inputStream!=null) {

                br = new BufferedReader(new InputStreamReader(
                    inputStream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }

                String outcome;
                Log.d(CNAME,"Response: " + builder.toString());
                JsonNode operationResult=null;
                if (builder !=null) {
                    outcome= builder.toString();
                    operationResult = mapper.readTree(outcome);
                    return operationResult;
                }
                return operationResult;
            }
        } catch (Exception e) {
            storedException = e;
        }
        finally {
            long t2 = System.currentTimeMillis();
            Log.d(CNAME,"Request took " + (t2-t1) + "ms");
            if (br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                    Log.w("TTST, closing of stream", e.getMessage());
                }
        }
        return null;
    }


    protected void onPreExecute() {

        if (refreshable!=null) {
            refreshable.showProgress();
        } else {
            dialog = new Dialog(ctx);
            dialog.setTitle("Please wait");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    protected void onPostExecute(JsonNode jsonNode) {

        if (refreshable!=null) {
            refreshable.hideProgress();
        } else {
            if (dialog!=null) {
                dialog.cancel();
                dialog.hide();
            }
        }

        if (storedException!=null) {
            Log.w(CNAME,storedException);
            if (storedException instanceof ConnectException) {
                Toast.makeText(ctx,ctx.getString(R.string.can_not_connect_to_server),Toast.LENGTH_LONG).show();
            } else {
                callback.onFailure(storedException);
            }
        }

        if (jsonNode!=null) {
            callback.onSuccess(jsonNode);
        } else {
            callback.onFailure(new IllegalArgumentException("Got no result "));
        }
    }


    String getHostPort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String host = prefs.getString("host","172.31.7.7");
        String port = prefs.getString("port","7080");

        return "http://"+host+":"+port; // TODO make https the default
    }
}
