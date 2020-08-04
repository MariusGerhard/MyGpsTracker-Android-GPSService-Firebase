/**
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Report Activity Shows the calculated data in a TextView
 * Creates a LineGraph which displayed the average Speed all Speed data and the average altitude
 * Stores the data in the firebase Server noSql database
 */


package com.example.mygpstracker;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

 private String trackingData;
 private ArrayList  <TrackingEntry> trackingList;

 private double minSpeed;
 private double maxSpeed;
 private double averageSpeed;

 private double minAlt;
 private double maxAlt;
 private double averageAlt;

 private double distanceValue;

 private LineChart lineChart;

 private String data = "";

 private TextView textView;

    /**
     * Sets up the ListView
     * the LineGraph
     * the two Buttons
     * one allows the user to go back and track further
     * (data is stored and shown in MainActivity again)
     * Allows the user to go the ResultActivity which shows previous trips
     * Reads the data in a string and puts them in the Tracking Entry Arraylist
     * Reads Intent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        textView = findViewById(R.id.dataTextView);

        lineChart = findViewById(R.id.lineChart);

        trackingList = new ArrayList<>();

        // Reads the intent key "trackingData"
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                trackingData = null;
            } else {
                trackingData = extras.getString("trackingData");
            }
        } else {
            trackingData = (String) savedInstanceState.getSerializable("trackingData");
        }

        //Formats the dataString
        String [] dataEntry = trackingData.split(";");

        Log.d("CheckList","Amount of Entrys "+dataEntry.length);
        // Calls a Method which creates the ArrayList with the TrackingEntrys
        fillTrackingList(dataEntry);

        //Calculates the distance
        setDistanceValues();
        Log.d("Distance","Distance: "+distanceValue);


        for (TrackingEntry entry: trackingList) {
            Log.d("CheckList",entry.toString());
        }

        // Calculates the 3 different speed Values
        setSpeedValues();

        int stringAverageSpeed = (int)(averageSpeed*1000);

        Log.d("Speed","Min: "+minSpeed);
        Log.d("Speed","Max: "+maxSpeed);
        Log.d("Speed","Average"+(double)(stringAverageSpeed/1000));


        // Calculates the 3 different altitude values
        setAltitudeValues();

        Log.d("Alt","Min: "+minAlt);
        Log.d("Alt","Max: "+maxAlt);
        Log.d("Alt","Average"+averageAlt);

        //Writes data in the textView
        fillTextView();

        // Sets up the Chart
        makeChart();

    }

    /**
     * Creates our Chart uses MpAndroidChart Library
     * Creates 3 ArrayLists for Avg Alt AvgSpeed currentSpeed
     */
    private void makeChart() {

        Log.d("Chart","Start Chart");

        ArrayList<Entry> speedList = new ArrayList<>();
        ArrayList<Entry> avgSpeedList = new ArrayList<>();
        ArrayList<Entry> avgAltList = new ArrayList<>();

        int numberOfValues = trackingList.size();

        //Fills the data in the Arrays Entry (xValue,yValue)
        for(int i = 0; i < numberOfValues;i++){
            float avgSpeed = (float)averageSpeed;
            float avgAlt = (float)averageAlt;
            float curSpeed = (float)trackingList.get(i).getSpeed();

            Log.d("Chart","CurSpeed: " +curSpeed);

            avgSpeedList.add(new Entry(i*3,avgSpeed));
            speedList.add(new Entry(i*3,curSpeed));
            avgAltList.add(new Entry(i*3,avgAlt));
        }

        ArrayList<String> xAXES = new ArrayList<>();


        String[] xaxes = new String[xAXES.size()];
        for(int i=0; i<xAXES.size();i++){
            xaxes[i] = xAXES.get(i);
        }

        // More than one Array (Line in the Graph)
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        //Speed Graph setup
        LineDataSet lineDataSet1 = new LineDataSet(speedList,"Speed");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setLineWidth(2);

        //AvgSpeed setup
        LineDataSet lineDataSet2 = new LineDataSet(avgSpeedList,"AvgSpeedLine");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setLineWidth(3);

        //AvgAlt setup
        LineDataSet lineDataSet3 = new LineDataSet(avgAltList,"AvgAltLine");
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setColor(Color.MAGENTA);
        lineDataSet3.setLineWidth(3);

        //Add them to the List
        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
        lineDataSets.add(lineDataSet3);

        //setup for the xAxis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //Puts the data in the Graph
        lineChart.setData(new LineData(lineDataSets));
        lineChart.setVisibleXRangeMaximum(65f);

        //Chart description
        lineChart.getDescription().setText("All Speed");

        //Shows timer information when clicked
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Its measured every 3 seconds",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Needed to set up data after created
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Writes our data from the dataEntry String in the ArrayList
     * Splits them and put them in the correct format
     * Small Bug with the first value approach prevent from null values
     * @param dataEntry
     */
    private void  fillTrackingList(String [] dataEntry){

        boolean check = true;
        double longtitude;
        double latitude;
        double altitude;
        double speed;

        for (String entry: dataEntry) {
           String [] entryValues = entry.split(",");

           // Needed to kick out a null Value
           if(check){
               String [] str = entryValues[0].split("null");
               Log.d("CheckValue",str[1]);
               longtitude = Double.parseDouble(str[1]);
           }else{
               longtitude = Double.parseDouble(entryValues[0]);
           }

           // Rest of the data is parsed
           latitude = Double.parseDouble(entryValues[1]);
           altitude = Double.parseDouble(entryValues[2]);
           speed = Double.parseDouble(entryValues[3]);

           // Adds Entry to the List
            TrackingEntry trackingEntry = new TrackingEntry(longtitude,latitude,altitude,speed);
            trackingList.add(trackingEntry);
            Log.d("CheckValue",entryValues[0]);
            Log.d("lines","Added: "+trackingEntry.toString());

            check = false;
         }
    }

    /**
     * Calculates the 3 different Speed Values Min Max Avg
     */
    private void setSpeedValues(){
        minSpeed = trackingList.get(0).getSpeed();
        maxSpeed = trackingList.get(0).getSpeed();
        averageSpeed = trackingList.get(0).getSpeed();

        double sumSpeed =0.0;
        for (TrackingEntry entry:trackingList) {

            sumSpeed += entry.getSpeed();

            //sets min Speed
            if (minSpeed > entry.getSpeed()){
                minSpeed = entry.getSpeed();
            }
            //sets max Speed
            if (maxSpeed < entry.getSpeed()) {
                maxSpeed = entry.getSpeed();
            }

        }

        averageSpeed = sumSpeed/trackingList.size();
    }

    /**
     * Calculates the distance between each point
     * uses Lat and Longtitude
     * Location distance between returns a float with the distance in meter
     * sets overall distance
     */
    private void setDistanceValues(){
        float [] sumDistances = new float[1];

        distanceValue =0.0;

        for(int i= 0; i < trackingList.size()-1;i++){
            double startLong = trackingList.get(i).getLongtitude();
            double startLat = trackingList.get(i).getLatitude();
            double endLong = trackingList.get(i+1).getLongtitude();
            double endLat = trackingList.get(i+1).getLatitude();

            Location.distanceBetween(startLat,startLong,endLat,endLong,sumDistances);
            
            distanceValue += sumDistances[0];
        }

    }

    /**
     * Calculate the 3 required Altitude Values same approach like in the speed calculation
     */
    private void setAltitudeValues(){
        minAlt = trackingList.get(0).getAltitude();
        maxAlt = trackingList.get(0).getAltitude();
        averageAlt = trackingList.get(0).getAltitude();

        double sumAlt =0.0;

        for (TrackingEntry entry:trackingList) {

            sumAlt += entry.getAltitude();

            //sets min Speed
            if (minAlt > entry.getAltitude()){
                minAlt = entry.getAltitude();
            }
            //sets max Speed
            if (maxAlt < entry.getAltitude()) {
                maxAlt = entry.getAltitude();
            }

        }
        averageAlt = sumAlt/trackingList.size();
    }

    /**
     * Method to store the data on the FireBase Server
     * Creats a timestamp and reads the Id of the Entry to save
     * them in our ArrayList of Result Entrys
     * Is called after onSave
     * @param w
     */
    public void onSave(View w){

        //Timestamp in with customized format
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());

        //Gets the dataBaseConnection
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Reference to the collection Entrys
        DocumentReference newNodeRef = db.collection("Entrys").document();

        // Reads the data and put them in the ArrayList
        // Speed is rounded to prevent long numbers in the ResultActivity
        ResultEntry resultEntry = new ResultEntry(Math.round(averageSpeed * 1000) / 1000.0,
                averageAlt,
                distanceValue,
                timeStamp,
                newNodeRef.getId());

        Log.d("Result",resultEntry.toString());

        // Writes the data in the Database
        newNodeRef.set(resultEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
            /**
             * Writes data in the database Listener checks of success
             * Sends Toast to the User
             * If thre is a problem the user can try it again
             * Otherwise he is rooted to the Result Activity
             * with the new Entry at the Bottom of the ListView
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Added Entry",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Insert failed",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * On Click to fo back to MainActivity
     * Saves data
     * @param w
     */
    public void onBack(View w){
        onBackPressed();
    }

    /**
     * Need for the onBack click
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Formates the data and writs it in the textView
     */
    public void fillTextView(){

        data += "Distance: " + Math.round(distanceValue * 1000) / 1000.0 + "\n";
        data += "minSpeed: " + minSpeed +"\n";
        data += "maxSpeed: " + maxSpeed +"\n";
        data += "Avg Speed: "+ Math.round(averageSpeed * 1000) / 1000.0  +"\n";
        data += "minAltitude: "+minAlt +"\n";
        data += "maxAltitude: "+maxAlt+"\n";
        data += "Avg Alt: "+Math.round(averageAlt * 1000) / 1000.0  +"\n";

        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(data);

    }
}
