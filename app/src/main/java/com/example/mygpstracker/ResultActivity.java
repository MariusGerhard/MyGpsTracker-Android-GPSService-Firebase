/**
 * @author Marius Gerhard 3013381
 * @version 1.0
 * Shows previous tracking data in a listView
 * updates if the datasSet changes
 * Reads data from the firabase server
 * Shows date/time and the avagerage Speed
 * Button to delete all previous tracks ( from the array (listView updates) and the FirebaseServer
 * Restart to Restart a new Tour which can be stored
 */

package com.example.mygpstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Buttons and ListView are used in the class
 * a Firebase db and an ArrayList
 * And a Progressbar which is displayed during the reading time beacuse of the callBack
 */
public class ResultActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private ArrayList <String> dataArray;

    private ArrayAdapter<String> adapter;

    private ProgressBar progressBar;

    /**
     * Sets up ArrayList and Listview Database and firebase document
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        db = FirebaseFirestore.getInstance();

        dataArray = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataArray);
        listView.setAdapter(adapter);

        loadData();

    }

    /**
     * ButtonClick Method goes through all documents in Entrys
     * and disband them
     * Because FireBase do not allows to deleted a collection
     * End the End loadData is called for the refresh of the listView
     * @param w
     */
    public void onDelete(View w){

        // Hides progressBar before starting the callback
        progressBar.setVisibility(View.VISIBLE);

        // Reads the collection Entrys and add a Listener to check for issues
        // Shows Toast to inform the user
        db.collection("Entrys").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List <DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot entry: list) {
                        entry.getReference().delete();
                        Log.d("Delete", "Deleted");
                    }
                    // Sets Array back, calls loadData again
                    dataArray.clear();
                    loadData();
                    //shows the Progressbar during the callback
                    progressBar.setVisibility(View.INVISIBLE);

                }else{
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Reads all documents in the Entry collection of the Firebase Server
     * Has a progressBar which is shown during loading time
     * Goes through the documents ans stores date and average Speed in a String ArrayList
     * updates the ListView adapter which updates the ListView, otherwise ListView is empty
     */
    public void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Entrys").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List <DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot entry: list) {
                        double speed = entry.getDouble("averageSpeed");
                        String time = entry.getString("date");
                        dataArray.add("Date: "+ time + " Speed: " + speed);
                        Log.d("Load", "Speed"+ speed);
                    }

                 //   adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);

                }else{
                    Toast.makeText(getApplicationContext(),"List is Empty",Toast.LENGTH_LONG).show();
                  //  adapter.notifyDataSetChanged();
                }
                // adapter knows the data has changed listView updates
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * ButtonClick Method
     * Restarts the app and sets the services Back so a new Trip can be created
     * @param w
     */
    public void onRestart(View w){

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }
}
