package com.lsfv.myliteraturesharing.AsyncTasks;



public interface AsyncResponse {
    void onCallback(String response);
    void onFailure(String message);
}
