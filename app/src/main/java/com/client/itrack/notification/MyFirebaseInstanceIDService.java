/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.client.itrack.notification;


import android.os.AsyncTask;
import android.util.Log;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    AppGlobal appGlobal =  AppGlobal.getInstance();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        final String userId  =  appGlobal.userId ;
        if(userId!=null && !userId.isEmpty())
        {
            // data.put("device_id", device_id);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        String url = Constants.BASE_URL + "get_device_id_after_login";
                        HashMap<String, String> data = new HashMap<>();
                        data.put("user_id", userId);
                        data.put("device_id", token);
                        URL uri =  new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-type", "application/json");
                        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                        wr.write(Utils.newGson().toJson(data));
                        wr.flush();
                        InputStreamReader reader  =  new InputStreamReader(connection.getInputStream());
                        BufferedReader br =  new BufferedReader(reader);
                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        // Read Server Response
                        while((line = br.readLine()) != null)
                        {
                            // Append server response in string
                            sb.append(line + "\n");
                        }

                        String text = sb.toString();
                        br.close();

                        return text ;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute(null,null,null);

//            HttpPostRequest.doPost((Activity)My, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
//                @Override
//                public void response(String errorMessage, String responseData) {
//                    try {
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                @Override
//                public void onError(String errorMessage) {
//                    Utils.hideLoadingPopup();
//                }
//            });
        }

    }
}
