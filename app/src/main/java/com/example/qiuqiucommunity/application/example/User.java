package com.example.qiuqiucommunity.application.example;

public class User {
    private String FullName;
    private String PhoneNumber;
    public User() {
        //Default of Firebase about constructor much be none
    }
    public User (String fullName, String phoneNumber)
    {
        FullName = fullName;
        PhoneNumber = phoneNumber;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
