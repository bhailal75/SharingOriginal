package com.lsfv.literaturesharing.AsyncTasks;



public interface AsyncResponse {
    void onCallback(String response);
    void onFailure(String message);
}
