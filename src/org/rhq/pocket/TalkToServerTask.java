package org.rhq.pocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Async task doing the communication with the RHQ server
 * @author Heiko W. Rupp
 */
public class TalkToServerTask extends AsyncTask<Object,Void,JsonNode> {

    private Context ctx;
    private FinishCallback callback;
    private String subUrl;
    private boolean isList;
    Dialog dialog;
    private String encodedCredentials;
    private String mode = "GET";

    public TalkToServerTask(Context ctx, FinishCallback callback, String subUrl, boolean isList) {

        this.ctx = ctx;
        this.callback = callback;
        this.subUrl = subUrl;
        this.isList = isList;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String username = preferences.getString("username", "-notset-");
        String password = preferences.getString("password","-notset-");

        String s = username + ":" + password;
        this.encodedCredentials = Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);
    }

    public TalkToServerTask(Context ctx, FinishCallback callback, String subUrl, String mode, boolean isList) {
        this(ctx,callback,subUrl,isList);
        this.mode = mode;
    }


    protected JsonNode doInBackground(Object... objects) {


        InputStream inputStream = null;
        BufferedReader br=null;
        long t1 = System.currentTimeMillis();
        try {
            // Example remote url
            //   http://localhost:7080/rest/1/resource/10001
            String urlString = getHostPort() + "/rest/1"; // TODO put into preferences
            urlString =urlString + subUrl;
            URL url = new URL(urlString);
            System.out.println("Going for " + urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty ("Authorization", "Basic " + encodedCredentials);
            conn.setRequestProperty("Accept","application/json");

            conn.setRequestMethod(mode);
            ObjectMapper mapper = new ObjectMapper();

            if (objects!=null && objects.length>0) {
                conn.setRequestProperty("content-type","application/json");
                OutputStream out = conn.getOutputStream();

                String result = mapper.writeValueAsString(objects[0]);
                result = "{\"metricAggregate\":" + result + "}";

                if (true) {
                    System.out.println("Json to send: " + result);
                    System.out.flush();
                }
//                out.write("{metricSchedule:".getBytes());
//                mapper.writeValue(out, objects[0]);
//                out.write("}".getBytes());


                conn.connect();
                out.flush();
                out.close();

            }
            int responseCode = conn.getResponseCode();
            System.out.println("response code was "+ responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
                System.err.println("Response: " + builder.toString());
                JsonNode operationResult=null;
                if (builder !=null) {
                    outcome= builder.toString();
                    operationResult = mapper.readTree(outcome);
                    return operationResult;
                }
                return operationResult;
            }
        } catch (Exception e) {
            Log.w(getClass().getName(),e);
            callback.onFailure(e);
        }
        finally {
            if (br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: Customise this generated block
                }
        }
        return null;
    }


    protected void onPreExecute() {
        dialog = new Dialog(ctx);
        dialog.setTitle("Please wait");
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void onPostExecute(JsonNode jsonNode) {

        dialog.cancel();
        dialog.hide();

        if (jsonNode!=null) {
            // Unwrap Json , as jettison is sending "{type:{" and we only need the inner part, but only if it is no list
            // TODO can we peek at jsonNode to find this out?
            if (isList)
                callback.onSuccess(jsonNode);
            else {
                JsonNode inner = jsonNode.getElements().next();
                callback.onSuccess(inner);
            }
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
