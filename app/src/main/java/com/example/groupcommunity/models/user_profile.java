package com.example.groupcommunity.models;

public class user_profile {

    String fname;
    String lname;
    String phone;
    String email;

    public user_profile() {
    }

    public user_profile(String fname, String lname, String phone, String email) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

}
