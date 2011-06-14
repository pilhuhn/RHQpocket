/*
 * RHQ Management Platform
 * Copyright (C) 2005-2011 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.pocket;

import java.io.BufferedReader;
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
import android.util.Log;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
public class TalkToServerTask extends AsyncTask<JsonNode,Void,JsonNode> {

    private Context ctx;
    private FinishCallback callback;
    private String subUrl;
    Dialog dialog;

    public TalkToServerTask(Context ctx, FinishCallback callback, String subUrl) {

        this.ctx = ctx;
        this.callback = callback;
        this.subUrl = subUrl;
    }



    protected JsonNode doInBackground(JsonNode... nodes) {

        InputStream inputStream = null;
        BufferedReader br=null;
        long t1 = System.currentTimeMillis();
        try {
//                              http://localhost:7080/rest/resource/r/10001
//            String urlString = "http://172.31.7.7:7080/rest";
            String urlString = getHostPort() + "/rest";
            urlString =urlString + subUrl;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
//            OutputStream out = conn.getOutputStream();

            ObjectMapper mapper = new ObjectMapper();
/*
            String result = mapper.writeValueAsString(operation);
            if (verbose) {
                System.out.println("Json to send: " + result);
                System.out.flush();
            }
            mapper.writeValue(out, operation);
*/

            conn.connect();
//            out.flush();
//            out.close();

            int responseCode = conn.getResponseCode();
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

                // Unwrap Json , as jettison is sending "{type:{" and we only need the inner part
                JsonNode inner = jsonNode.getElements().next(); // TODO dangerous. Does not work for all types

                callback.onSuccess(inner);
            }


    String getHostPort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String host = prefs.getString("host","172.31.7.7");
        String port = prefs.getString("port","7080");

        return "http://"+host+":"+port; // TODO make https the default
    }
        }
