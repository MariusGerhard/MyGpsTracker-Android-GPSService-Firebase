/***
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Main Activity
 * Handles the GPS Permission.
 * First Starts sends a pop up and user has to accept GPS
 * A textView Shows the Data which is received by Gps Service
 * I added a scrollbar as a bonus because i never did it before
 * There is a Button which toggles between start and stop
 * Its needed to track again after visiting the Report Activity
 * Color Green Start
 * Color Red Stop --> Stops leads to the Report activity
 * Used onClick listener, its a different approach to the Button ins the ResultActivity
 */


package com.example.mygpstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Every Class Variables
 * A Button a TextView
 * The broadcastReceiver collections the information from the GPS Service
 */
public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView textView;

    private boolean isStart;

    private String trackingData;

    private BroadcastReceiver broadcastReceiver;

    /**
     * Permission Handling
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION };

    /**
     * onResume is used because its called after on Create where the Gps service is set up
     * It Collects our data with the BroadcastReceiver
     * Appends the new data to a String which is shown in a simple TextView with a scrollbar
     * It reads Latitude, Logtitude, Altitude and current Speed
     * He looks for Intents with the name coordinates
     */
    @Override
    protected void onResume() {
        super.onResume();
        textView.append("Measured Data: \n");
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String [] entry = intent.getExtras().get("coordinates").toString().split(",");

                    textView.append("\n" +"Long: "+ entry[0]);
                    textView.append("\n" +"Lat: "+ entry[1]);
                    textView.append("\n"+"Alt: "+entry[2]);
                    textView.append("\n"+"Speed: "+entry[3].split(";")[0]);
                    textView.append("\n");

                    trackingData += (intent.getExtras().get("coordinates")).toString();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    /**
     * onDestroy is needed to prevent endless running services
     * it has to disband manually
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * Sets up the used Display elements
     * sets isStart = true for the toggle Button
     * calls runtimePermission which asks for permissions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.button);

        textView = findViewById((R.id.textView));

        isStart = true;

        btnStart.setBackgroundColor(Color.GREEN);
        btnStart.setText("Start tracking");

        runtimePermissions();

    }

    /** Enables the Buttons after checking permissions
     * Click listener for the toggle Button
     * Calls the intent and sends the data in form of of a csv
     *
     */
    private void enableButtons() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStart) {
                    Intent startIntent = new Intent(getApplicationContext(), GpsService.class);
                    startService(startIntent);

                    btnStart.setBackgroundColor(Color.RED);
                    btnStart.setText("Stop -Results");
                }else{
                    Intent stopIntent = new Intent(getApplicationContext(),GpsService.class);
                    stopService(stopIntent);

                    btnStart.setBackgroundColor(Color.GREEN);
                    btnStart.setText("Start tracking");

                    Intent reportIntent = new Intent(MainActivity.this,ReportActivity.class);
                    reportIntent.putExtra("trackingData", trackingData);
                    startActivity(reportIntent);
                }
                isStart = !isStart;
            }
        });

    }

    /**
     * Asks for the Users permissions in the running app
     * Its need for android SDK > 25 otherwise the App crash
     * But user can accept after a crash restart and everything works
     * This approach fixes the crash
     */
    private void runtimePermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    /**
     * Checks Permissions
     * If the user accepts them the Buttons get enabled
     * Prevents the App for crash after the user has not accept the permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                enableButtons();
                break;
        }
    }
}
