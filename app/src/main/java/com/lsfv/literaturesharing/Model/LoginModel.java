package com.lsfv.literaturesharing.Model;


public class LoginModel {


    /**
     * status : 1
     * userDetails : {"user_id":58,"mobile_num":"8905982801","birthdate":"12345668","user_type":"user","firstname":"jp","lastname":"jp","email":"jp@gmail.com","certy_image":"http://www.lsfv.in/admin/certyimage/a0b7d39b77bf5bb8290d025598be21e205043d96.png"}
     * message : You are Successfully Logged in
     */

    private int status;
    private UserDetailsBean userDetails;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserDetailsBean getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsBean userDetails) {
        this.userDetails = userDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class UserDetailsBean {
        /**
         * user_id : 58
         * mobile_num : 8905982801
         * birthdate : 12345668
         * user_type : user
         * firstname : jp
         * lastname : jp
         * email : jp@gmail.com
         * certy_image : http://www.lsfv.in/admin/certyimage/a0b7d39b77bf5bb8290d025598be21e205043d96.png
         */

        private int user_id;
        private String mobile_num;
        private String birthdate;
        private String user_type;
        private String firstname;
        private String lastname;
        private String email;
        private String certy_image;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getMobile_num() {
            return mobile_num;
        }

        public void setMobile_num(String mobile_num) {
            this.mobile_num = mobile_num;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCerty_image() {
            return certy_image;
        }

        public void setCerty_image(String certy_image) {
            this.certy_image = certy_image;
        }
    }
}