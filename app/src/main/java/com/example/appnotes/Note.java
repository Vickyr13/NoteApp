package com.example.appnotes;


import com.google.firebase.Timestamp;


public class Note {

    String title;
    String content;
    //Esta clase es de firebase
    Timestamp date;

    public Note() {


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
