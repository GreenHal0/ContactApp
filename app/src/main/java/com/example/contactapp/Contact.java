package com.example.contactapp;

import android.graphics.Bitmap;

public class Contact implements Comparable<Contact>  {

    private static int count=0;
    private int id;
    private String name, firstName, phone, email;
    Bitmap avatar;

    public Contact(String name, String firstName, String phone, String email) {
        this.name = name;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.avatar = null;
        this.id = ++count;
    }

    public Contact(String name, String firstName, String phone, String email, Bitmap avatar) {
        this.name = name;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.id = ++count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String[] getAll(){
        return new String[]{name, firstName, phone, email};
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap newAvatar) {
        avatar = newAvatar;
    }


    @Override
    public int compareTo(Contact o) {
        return this.firstName.compareTo(o.firstName);
    }
}
