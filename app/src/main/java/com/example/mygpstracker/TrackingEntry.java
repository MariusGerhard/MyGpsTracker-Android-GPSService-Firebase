/**
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Model Class which store the ArrayData in the ArrayList of the Report Activity
 * longtitude, speed, latitude and altitude
 * can print itself
 */
package com.example.mygpstracker;

public class TrackingEntry {

    private double latitude;
    private double longtitude;
    private double speed;
    private double altitude;

    TrackingEntry(double longtitude, double latitude, double altitude, double speed){
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.speed = speed;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "TrackingEntry{" +
                "latitude=" + latitude +
                ", longtitude=" + longtitude +
                ", speed=" + speed +
                ", altitude=" + altitude +
                '}';
    }

}
