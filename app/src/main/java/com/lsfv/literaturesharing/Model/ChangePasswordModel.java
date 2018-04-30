package com.lsfv.literaturesharing.Model;



public class ChangePasswordModel {

    /**
     * status : 1
     * message : Your password has changed Successfully
     */

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
