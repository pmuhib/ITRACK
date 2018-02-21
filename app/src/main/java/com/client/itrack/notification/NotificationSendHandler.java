package com.client.itrack.notification;

import android.app.Activity;
import android.content.Context;

import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;

import com.client.itrack.utility.Constants;


/**
 * Created by sony on 09-07-2016.
 */
public class NotificationSendHandler {

    private final Activity context;

    public NotificationSendHandler(Activity context ) {

        this.context =  context ;
    }

    public void sendNotification(int method,String params,int scope)  // Scope 0 --> Client's Employee  1 --> Global Employee
    {
        String url;
        switch(method)
        {
            case IMethodNotification.ADD_DSR :
                if(scope==IMethodNotification.CLIENT_USER) {
                    url = Constants.BASE_URL + "notification_for_companyuser";
                }
                else
                {
                    url = Constants.BASE_URL + "notification_for_globaluser";
                }
                requestNotification(url, params);
                break ;

            case IMethodNotification.UPDATE_DSR :
                if(scope==IMethodNotification.CLIENT_USER) {
                    url = Constants.BASE_URL + "notification_for_companyuser";
                }
                else
                {
                    url = Constants.BASE_URL + "notification_for_globaluser";
                }
                requestNotification(url,params) ;
                break ;


            case IMethodNotification.NEW_EVENT_UPDATE :
                url = Constants.BASE_URL + "";
                requestNotification(url,params) ;
                break ;


            case IMethodNotification.NEW_NEWS_UPDATE :
                url = Constants.BASE_URL + "";
                requestNotification(url,params) ;
                break ;

            case IMethodNotification.ADD_QUOTE :
                if(scope==IMethodNotification.LP_USER) {
                    url = Constants.BASE_URL + "quote_notification_for_lodinguser"; // loading Point User
                }
                else
                {
                    url = Constants.BASE_URL + "quote_notification_for_globaluser"; // Selected Employee
                }
                requestNotification(url,params) ;
                break ;

            case IMethodNotification.UPDATE_QUOTE :
                if(scope==IMethodNotification.LP_USER) {
                    url = Constants.BASE_URL + "quote_notification_for_lodinguser"; // loading Point User
                }
                else
                {
                    url = Constants.BASE_URL + "quote_notification_for_globaluser"; // Selected Employee
                }
                requestNotification(url,params) ;
                break ;
            case IMethodNotification.QUOTE_RESPONSE :
                if(scope==IMethodNotification.LP_USER) {
                    url = Constants.BASE_URL + "quote_notification_for_responseemployee"; // loading Point User
                }
                else
                {
                    url = Constants.BASE_URL + "quote_notification_for_globaluser"; // Selected Employee
                }
                requestNotification(url,params) ;
                break ;


            case IMethodNotification.Quote_PROPOSAL_RESPONSE :
                if(scope==IMethodNotification.LP_USER) {
                    url = Constants.BASE_URL + "quote_notification_proposal_response_for_lodinguser"; // loading Point User
                }
                else
                {
                    url = Constants.BASE_URL + "quote_notification_proposal_response_for_globaluser"; // Selected Employee
                }
                requestNotification(url,params) ;
                break ;



        }
    }

    private void requestNotification(String url , String params)
    {
        try {
            HttpPostRequest.doPost(this.context, url, params, new HttpRequestCallback() {
                @Override
                public void response(String errorMessage, String responseData) {
                   /* try {
                        JSONObject jobj = new JSONObject(responseData);
                        Boolean status = jobj.getBoolean("status");
                        if (status) {} else {}
                    } catch (Exception e) {e.printStackTrace();}*/
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
        }
        catch(Exception ex)
        {

        }
    }


}
