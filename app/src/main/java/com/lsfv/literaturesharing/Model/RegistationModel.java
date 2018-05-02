package com.lsfv.literaturesharing.model;



public class RegistationModel {

    /**
     * status : 1
     * userDetail : {"user_id":4}
     * message : You are Successfully Registered
     */

    private int status;
    private UserDetailBean userDetail;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserDetailBean getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetailBean userDetail) {
        this.userDetail = userDetail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class UserDetailBean {
        /**
         * user_id : 4
         */

        private int user_id;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}

