package com.example.dgaj;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

public class ChangeAddressToGeoPoint {

    double latitude;
    double longitude;
    String address = "";

    ChangeAddressToGeoPoint(){}

    void setAddress(String address){
        this.address = address;
    }


    String getAddress(){
        return address;
    }


    Location getGeoPoint(Context mcontext) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한 좌표에 대해 두 개 이상의 이름이 존재 할 수 있기에 주소 배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);

                this.latitude = lat;
                this.longitude = lon;

            }
        }
        return loc;
    }

}
