package com.example.aditi.petshelter;

public class User {
    public String name;
    public String email;
    public String password;
    public String city;
    public String pincode;
    public String state;
    public String phone;
    public String id;
    public String Imageurl;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name, String email, String password, String city, String pincode, String state, String phone,  String Imageurl) {
        this.id=id;
        this.name = name;
        this.email = email;
        this.password=password;
        this.city=city;
        this.pincode=pincode;
        this.state=state;
        this.phone=phone;

        this.Imageurl=Imageurl;

    }

}
