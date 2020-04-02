package com.example.signup;

public class User {
    //we can't store pass in db
    public String name, email,status;
//blank constructor in order to read values back
    public User(){

    }
    //to initialize values ,we need a contructor
    public User(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }
}
