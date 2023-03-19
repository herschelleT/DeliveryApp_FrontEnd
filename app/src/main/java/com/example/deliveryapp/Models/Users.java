package com.example.deliveryapp.Models;

public class Users {
    String profilepic,Username ,mail,Password,UserID,order,phone;

    public Users(String profilepic, String username, String mail, String password, String userID, String order,String phone) {
        this.profilepic = profilepic;
        this.Username = username;
        this.mail = mail;
        this.Password = password;
        this.UserID = userID;
        this.order = order;
        this.phone=phone;
    }
    public Users(){}
    public Users(String username, String mail,String phone) {
        this.Username = username;
        this.mail = mail;
        this.phone=phone;
    }


    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public String getphone() {
        return  phone;
    }
}