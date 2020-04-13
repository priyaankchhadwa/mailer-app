package com.example.inclass08;

public class User {
    int id;
    String fname;
    String lname;

    public User(int id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first name='" + fname + '\'' +
                ", last name='" + lname + '\'' +
                '}';
    }
}
