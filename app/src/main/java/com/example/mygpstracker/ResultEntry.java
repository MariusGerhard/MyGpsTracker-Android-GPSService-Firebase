/**
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Model Class for the Data which is used to save data before it is stored in the dataBase
 * Stores: average Speed, Average Altitude, distance, a date, and a entryId for the FireStore
 * Getter are not used but the are available for future usage (dataBase)
 * Can print itself toString is overwritten
 */
package com.example.mygpstracker;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ResultEntry {

    private double averageSpeed;
    private double averageAlt;
    private double distance;
    private String date;
    private String entryID;

    public ResultEntry(double averageSpeed,
                       double averageAlt,
                       double distance,
                       String date,
                       String entryID) {

        this.entryID = entryID;
        this.averageSpeed = averageSpeed;
        this.averageAlt = averageAlt;
        this.distance = distance;
        this.date = date;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getAverageAlt() {
        return averageAlt;
    }

    public double getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

    public String getEntryID() {
        return entryID;
    }

    @Override
    public String toString() {
        return "ResultEntry{" +
                "averageSpeed=" + averageSpeed +
                ", averageAlt=" + averageAlt +
                ", distance=" + distance +
                ", date='" + date + '\'' +
                ", entryID='" + entryID + '\'' +
                '}';
    }
}
