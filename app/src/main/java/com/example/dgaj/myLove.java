package com.example.dgaj;

public class myLove {
    private String name;
    private String tel;
    private String time;
    private String opendata_id;
    private String address_gu;

    public myLove(String name, String tel, String time, String opendata_id, String address_gu) {
        this.name = name;
        this.tel = tel;
        this.time = time;
        this.opendata_id = opendata_id;
        this.address_gu = address_gu;
    }

    // Getter 메서드 추가
    public String getOpendata_id() {
        return opendata_id;
    }
    public String getAddress_gu() {
        return address_gu;
    }
    public String getName() {
        return name;
    }
    public String getTel() {
        return tel;
    }
    public String getTime() {
        return time;
    }


}
