package com.example.rollbookactivity;

public class Notify {
    String message, date;

    public Notify() {
    }

    public String getMessage() {
        return message;
    }

    public Notify(String message, String date) {
        this.message = message;
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
