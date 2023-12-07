package com.example.dgaj;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChangeGeoPointToAddress {

     double latitude = 0;
     double longitude = 0;

    ChangeGeoPointToAddress(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
     void setLatitude(double latitude){
         this.latitude = latitude;
     }

     void setLongitude(double longitude){
         this.longitude = longitude;
     }
     double getLatitude(){return latitude;}
     double getLongitude(){return longitude;}

    String getAddress(Context mContext) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(latitude, longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;

                }
            }

        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }
}


