package com.example.dgaj;

public class myWaiting {
    private String wait_rest_name;
    private String wait_rest_addr;
    private String wait_rest_FD;
    private String RES_ID;

    public myWaiting(String wait_rest_name, String wait_rest_addr, String wait_rest_FD, String RES_ID){
        this.RES_ID = RES_ID;
        this.wait_rest_name = wait_rest_name;
        this.wait_rest_addr = wait_rest_addr;
        this.wait_rest_FD = wait_rest_FD;
    }

    public String getWait_rest_name() {
        return wait_rest_name;
    }
    public String getWait_rest_addr() {
        return wait_rest_addr;
    }
    public String getWait_rest_FD() {
        return wait_rest_FD;
    }
    public String getWait_RES_ID() { return RES_ID;}
}
