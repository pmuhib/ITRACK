package com.client.itrack.listener;


public interface HttpRequestCallback {
    void response(String errorMessage, String responseData);

    void onError(String errorMessage);
}
