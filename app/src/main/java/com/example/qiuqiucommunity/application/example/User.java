package com.example.qiuqiucommunity.application.example;

public class User {
    private String FullName;
    private String PhoneNumber;
    private String PresentationAddress;
    private String Address;
    private String Id;
    private String Email;
    private String Password;
    private String Gender;
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

    public String getPresentationAddress() {
        return PresentationAddress;
    }

    public void setPresentationAddress(String presentationAddress) {
        PresentationAddress = presentationAddress;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
