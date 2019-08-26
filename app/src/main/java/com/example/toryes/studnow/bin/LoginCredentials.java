package com.example.toryes.studnow.bin;



import java.io.Serializable;

public class LoginCredentials implements Serializable{


    private String user_id;
    private String useridentificationNo;
    private String session_token ;
    private String first_name;
    private String last_name;
    private String email ;
    private String phone;
    private String gender ;
    private String password;
    private String image;
    private String latitude ;
    private String longitude;
    private String firebasetoken;

    public void setFirebasetoken(String firebasetoken) {
        this.firebasetoken = firebasetoken;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public void setUseridentificationNo(String useridentificationNo) {
        this.useridentificationNo = useridentificationNo;
    }

    public String getUseridentificationNo() {
        return useridentificationNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getSession_token() {
        return session_token;
    }


    public String getUser_id() {
        return user_id;
    }
//    public LoginCredentials(String email, String password,String longitude, String latitude) {
//        this.email = email;
//        this.password = password;
//        this.latitude=latitude;
//        this.longitude=longitude;
//    }
//    public LoginCredentials(String first_name, String last_name,String phone, String email,String password,String longitude, String latitude) {
//        this.email = email;
//        this.password = password;
//        this.first_name=first_name;
//        this.last_name=last_name;
//        this.phone=phone;
//        this.latitude=latitude;
//        this.longitude=longitude;
//    }
}