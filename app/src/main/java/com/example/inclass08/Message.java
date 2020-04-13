package com.example.inclass08;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    String sender_fname;
    String sender_lname;
    int id;
    String message;
    String subject;
    Date created_at;

    public Message(String sender_fname, String sender_lname, int id, String message, String subject, Date created_at) {
        this.sender_fname = sender_fname;
        this.sender_lname = sender_lname;
        this.id = id;
        this.message = message;
        this.subject = subject;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender_fname + sender_lname + '\'' +
                ", id=" + id +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
