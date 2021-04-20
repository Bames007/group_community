package com.example.groupcommunity.models;

public class report_holder {

    String name;
    String title;
    String address;
    String videourl;
    String description;
    String date;
    String time;

    public report_holder() {
    }

    public report_holder(String name, String title, String address, String videourl, String description, String date, String time) {
        this.name = name;
        this.title = title;
        this.address = address;
        this.videourl = videourl;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
