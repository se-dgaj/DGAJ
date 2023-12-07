package com.example.dgaj;

public class Waiting {
    private String waiting_name;
    private String waiting_num;
    private String waiting_tel;
    private String cust_uid;
    
    public Waiting(String waiting_name, String waiting_num, String waiting_tel, String cust_uid){
        this.cust_uid = cust_uid;
        this.waiting_name = waiting_name;
        this.waiting_num = waiting_num;
        this.waiting_tel = waiting_tel;
    }

    public String getWaiting_name() {
        return waiting_name;
    }
    public String getWaiting_num() {
        return waiting_num;
    }
    public String getWaiting_tel() {
        return waiting_tel;
    }
    public String getWaiting_uid() { return cust_uid;}


}