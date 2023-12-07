package com.example.dgaj;

public class Toplist {
    private String top_address_gu;
    private String RES_ID;
    private String totallove;

    public Toplist(String top_address_gu, String totallove, String RES_ID){
        this.RES_ID = RES_ID;
        this.top_address_gu = top_address_gu;
        this.totallove = totallove;
    }
    public String getTop_address_gu() { return top_address_gu; }
    public String getTotallove() {
        return totallove;
    }
    public String getWait_RES_ID() { return RES_ID;}
}
