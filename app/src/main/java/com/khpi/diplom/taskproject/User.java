package com.khpi.diplom.taskproject;

/**
 * Created on 29/05/2017 23:50.
 */

public class User {

    private final String uid;
    private final String name;
    private final String email;

    User(){
        this("", " ", "");
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

