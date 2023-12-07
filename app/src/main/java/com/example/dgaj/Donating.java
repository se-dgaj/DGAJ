package com.example.dgaj;

public class Donating {
    private String name, address, tel;
    private int imgResourceId; // 리소스 ID 저장

    public Donating(String name, String address, String tel, int imgResourceId) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.imgResourceId = imgResourceId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public int getImgResourceId() {
        return imgResourceId;
    }

}