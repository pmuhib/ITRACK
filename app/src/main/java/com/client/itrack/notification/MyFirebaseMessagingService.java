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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.activities.DSRDetails;
import com.client.itrack.activities.EventDetail;
import com.client.itrack.activities.NewsDetail;
import com.client.itrack.activities.QuoteDetails;
import com.client.itrack.R;
import com.client.itrack.utility.DrawerConst;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("message"));

        parsingAndMappingAction(remoteMessage.getData());

    }



    private void parsingAndMappingAction(Map<String, String> data) {

        try {
            String message = data.get("message") ;
            String methodDetailString  = data.get("method");

            JSONObject methodRootJSON  = new JSONObject(methodDetailString);

            int method_name = methodRootJSON.getInt("name");
            JSONObject method_params_obj = methodRootJSON.getJSONObject("parameters");
            if(method_params_obj!=null)
            {
                String title ;
                Intent intent ;
                PendingIntent pendingIntent ;
                switch(method_name)
                {
                    case IMethodNotification.ADD_DSR :
                        String dsr_ref_num = method_params_obj.getString("dsr_ref_num");
                        int dsr_id = method_params_obj.getInt("dsr_id");
                        String createEmpType = method_params_obj.getString("createEmpType");
                        int createdEmpId = method_params_obj.getInt("createdEmpId");

                        intent = new Intent(this, DSRDetails.class);
                        intent.putExtra("dsr_id", dsr_id+"");
                        intent.putExtra("dsr_ref_no",dsr_ref_num);
                        intent.putExtra("action", "view");
                        intent.putExtra("client_comp_name", "");
                        intent.putExtra("createEmpId",createdEmpId+"");
                        intent.putExtra("createEmpType", createEmpType);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        title = "DSR Created";
                        sendNotification(title,message,pendingIntent);

                        break ;

                    case IMethodNotification.NEW_EVENT_UPDATE :

                         intent = new Intent(this, EventDetail.class);
                        int event_id = method_params_obj.getInt("id");
                            intent.putExtra("event_id", event_id+"");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "New Event";
                        sendNotification(title,message,pendingIntent);
                        break ;

                    case IMethodNotification.UPDATE_DSR : {
                        String dsr_ref_num_u = method_params_obj.getString("dsr_ref_num");
                        int dsr_id_u = method_params_obj.getInt("dsr_id");
                        String createEmpType_u = method_params_obj.getString("createEmpType");
                        int createdEmpId_u = method_params_obj.getInt("createdEmpId");

                        intent = new Intent(this, DSRDetails.class);
                        intent.putExtra("dsr_id", dsr_id_u + "");
                        intent.putExtra("dsr_ref_no", dsr_ref_num_u);
                        intent.putExtra("action", "view");
                        intent.putExtra("client_comp_name", "");
                        intent.putExtra("createEmpId", createdEmpId_u + "");
                        intent.putExtra("createEmpType", createEmpType_u);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        title = "DSR – Activity Update";
                        sendNotification(title, message, pendingIntent);
                    }
                        break ;

                    case IMethodNotification.NEW_NEWS_UPDATE :

                        intent = new Intent(this, NewsDetail.class);
                        int news_id = method_params_obj.getInt("id");
                        intent.putExtra("newsid", news_id+"");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "New News";
                        sendNotification(title,message,pendingIntent);
                        break ;

                    case IMethodNotification.ADD_QUOTE :
                        intent = new Intent(this, QuoteDetails.class);
                        int quote_id = method_params_obj.getInt("quote_id");
                        intent.putExtra("action", "view");
                        Bundle bundleAdd = new Bundle();
                        bundleAdd.putCharSequence("quote_id",quote_id+"");
                        intent.putExtra("bundle",bundleAdd);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "New Quote Request";
                        sendNotification(title,message,pendingIntent);
                        break  ;


                    case IMethodNotification.UPDATE_QUOTE :
                        intent = new Intent(this, QuoteDetails.class);
                        int quote_id_u = method_params_obj.getInt("quote_id");
                        intent.putExtra("action", "view");
                        Bundle bundleUpdate = new Bundle();
                        bundleUpdate.putCharSequence("quote_id",quote_id_u+"");
                        intent.putExtra("bundle",bundleUpdate);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "Quote - Activity Update";
                        sendNotification(title,message,pendingIntent);
                        break ;

                    case IMethodNotification.QUOTE_RESPONSE:
                        intent = new Intent(this, QuoteDetails.class);
                        int quote_id_res = method_params_obj.getInt("quote_id");
                        intent.putExtra("action", "view");
                        Bundle bundleRes = new Bundle();
                        bundleRes.putCharSequence("quote_id",quote_id_res+"");
                        intent.putExtra("bundle",bundleRes);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "Quote Proposal Request";
                        sendNotification(title,message,pendingIntent);

                        break ;
                    case IMethodNotification.MSG_LIST:
                        intent = new Intent(this, CategoryContainer.class);
                        intent.putExtra("catpos", DrawerConst.MSG_LIST);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "New Message";
                        sendNotification(title,message,pendingIntent);

                        break ;

                    case IMethodNotification.Quote_PROPOSAL_RESPONSE:
                        intent = new Intent(this, QuoteDetails.class);
                        int quote_id_res_p = method_params_obj.getInt("quote_id");
                        intent.putExtra("action", "view");
                        Bundle bundleResP = new Bundle();
                        bundleResP.putCharSequence("quote_id",quote_id_res_p+"");
                        intent.putExtra("bundle",bundleResP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        title = "Quote Proposal Response";
                        sendNotification(title,message,pendingIntent);
                        break;



                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),getClass().getName()+" : "+e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title,String messageBody,PendingIntent pendingIntent) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcnNotification =  BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.truck_small)
                .setLargeIcon(largeIcnNotification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(View.VISIBLE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int num =  0 + (int)(Math.random() * 2147483647);
        Log.d("Notification Id",num+"");
        notificationManager.notify(num, notificationBuilder.build());
    }
}
