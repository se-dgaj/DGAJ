package com.example.dgaj;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class GetMyGeoPoint {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation; // 마지막 위치 저장

    public void getLocation(Context context, LocationCallback callback) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lastLocation = location;
                locationManager.removeUpdates(this); //중복리스너제거
                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        //마지막 위치 사용
        if (lastLocation != null) {
            callback.onLocationReceived(lastLocation.getLatitude(), lastLocation.getLongitude());
            String message = "위치를 불러올 수 없음";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
    }
}
