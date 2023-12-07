package com.example.dgaj;

public class Restaurant {
    private String name;
    private String cuisine;
    private String phoneNumber;
    private String address;
    private String description;
    private String businesshour;
    private String topMenu;
    private String opendata_id;
    private double distance;

    public Restaurant(String name, String cuisine, String phoneNumber, String address, String description, String businesshour, String topMenu, String opendata_id, int pickCount) {
        this.name = name;
        this.cuisine = cuisine;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.description = description;
        this.businesshour = businesshour;
        this.topMenu = topMenu;
        this.opendata_id = opendata_id;
        this.distance = distance;
    }
    public Restaurant(String name, String cuisine, String phoneNumber, String address) {
        this.name = name;
        this.cuisine = cuisine;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getBusinesshour() {
        return businesshour;
    }

    public String getTopMenu() {
        return topMenu;
    }

    public String getOpenData_id() {
        return opendata_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double dis){this.distance = dis;}
}
