/**
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Service which provides the GPS data
 * Uses LocationListener and LocationManager
 * the interval is 3000 milliseconds
 */

package com.example.mygpstracker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class GpsService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private final int measureTimer = 3000;

    /**
     * Is needed because of the Service class (extends Service)
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Sets up the Location listener
     * measures the data long alt lat and speed and sets it out as a string
     */
    @SuppressLint("MissingPermission")
    public void onCreate(){
        listener = new LocationListener() {
            /**
             * Collects all data from the locations listener
             * and broadcast them in the app
             * its called every 3 seconds when the location changed
             * sets up the new location
             * otherwise it sends the old location if it not changed
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("coordinates",
                        location.getLongitude()+","
                                +location.getLatitude()+","
                                +location.getAltitude()+","
                                +location.getSpeed()+";");
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // Disable warning we already check the permission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,measureTimer,0,listener);


    }

    /**
     * Removes the listener
     * to prevent dataLeaks
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }
}
