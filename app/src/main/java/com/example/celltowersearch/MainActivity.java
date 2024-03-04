package com.example.celltowersearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CellTower{
    String radio;
    int mcc;
    int  net;
    int area;
    int cell;
    int unit;
    double lon;
    double lat;
    int range;
    int samples;
    int changeable;
    long created;
    long updated;
    int averageSignal;

    public CellTower(String radio, int mcc, int net, int area, int cell, int unit,
                     double lon, double lat, int range, int samples, int changeable,
                     long created, long updated, int averageSignal) {
        this.radio = radio;
        this.mcc = mcc;
        this.net = net;
        this.area = area;
        this.cell = cell;
        this.unit = unit;
        this.lon = lon;
        this.lat = lat;
        this.range = range;
        this.samples = samples;
        this.changeable = changeable;
        this.created = created;
        this.updated = updated;
        this.averageSignal = averageSignal;
    }
}

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;

    private ArrayList<Double> latitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.name);
        searchButton = findViewById(R.id.save);


        // First off all I want to read the data which is present in the csv file then from there ...
        latitudes = readCSVFile();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    // Calling the Binary Search on the data present in the latitudes array list if present it will return the true otherwise false
                    boolean found = binarySearchLatitude(query);
                    if (found) {
                        Toast.makeText(MainActivity.this, "Latitude " + query + " is present in the file", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Latitude " + query + " is not present in the file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a latitude value to search", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Binary Search Function to just check If my data is present or not
    private boolean binarySearchLatitude(String query) {

        Collections.sort(latitudes);
        int index = Collections.binarySearch(latitudes, Double.parseDouble(query));
        return index >= 0;
    }

    private ArrayList<Double> readCSVFile() {
        ArrayList<Double> latitudes = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("cell_towers.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length > 0) {
                    try {
                        double latitude = Double.parseDouble(data[7]);
                        latitudes.add(latitude);
                        Log.d("CSV Data", "Latitude: " + latitude);
                    } catch (NumberFormatException e) {

                        Log.e("CSV Data", "Error parsing latitude: " + data[7]);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latitudes;
    }
}
