package com.anhtong8x.phone_authenfirebase.realtimedb;

public class UserModel {
    String idUser;

    String name;

    String image;

    public UserModel() {
    }

    public UserModel(String idUser, String name, String image) {
        this.idUser = idUser;
        this.name = name;
        this.image = image;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
