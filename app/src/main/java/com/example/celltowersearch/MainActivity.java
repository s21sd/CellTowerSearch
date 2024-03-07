package com.example.celltowersearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Tower {

    double latitude;
    double longitude;
    double mcc;
    double mnc;
    double lac;
    double cell;
    double range;

    Tower(double latitude, double longitude, double mcc, double mnc, double lac, double cell, double range) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cell = cell;
        this.range = range;

    }
}

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    private ArrayList<Tower> towers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.name);
        searchButton = findViewById(R.id.save);

        towers = readCSVFile();
                    double latitude = 20.3488139;
                    double longitude = 85.8163151; // Example longitude, change as needed
                    double[] location = {latitude, longitude};
                    Tower nearestTower = findNearestTower(location, towers);
                    if (nearestTower != null) {
                        Log.d("Nearest Tower", "Latitude: " + nearestTower.latitude + " Log: " + nearestTower.longitude + " mcc: " + nearestTower.mcc + " mnc: " + nearestTower.mnc + " lac: " + nearestTower.lac + " cellid: " + nearestTower.cell + " range: " + nearestTower.range);
                        Toast.makeText(MainActivity.this, "Nearest Tower: Latitude=" + nearestTower.latitude + ", Longitude=" + nearestTower.longitude, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No towers found", Toast.LENGTH_SHORT).show();
                    }



    }

    static double euclideanDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

//    static double euclideanDistance(double x1, double y1, double x2, double y2) {
//        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
//    }
    static Tower findNearestTower(double[] location, ArrayList<Tower> towers) {
        double minDistance = Double.POSITIVE_INFINITY;
        Tower nearestTower = null;

//        Toast.makeText(MainActivity.this, towers.size(), Toast.LENGTH_SHORT).show();
        Log.d("Len","Length"+towers.size());
        for (Tower tower : towers) {
            double distance = euclideanDistance(location[0], location[1], tower.latitude, tower.longitude);
            if (distance < minDistance) {
                minDistance = distance;
                nearestTower = tower;
            }
        }

        return nearestTower;
    }

    private ArrayList<Tower> readCSVFile() {
        ArrayList<Tower> towers = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("cell_towers.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 9) { // Ensure that there are at least 9 columns
                    try {
                        double latitude = Double.parseDouble(data[7]);
                        double mcc = Double.parseDouble(data[1]);
                        double mnc = Double.parseDouble(data[2]);
                        double lac = Double.parseDouble(data[3]);
                        double cell = Double.parseDouble(data[4]);
                        double range = Double.parseDouble(data[8]);
                        double longitude = Double.parseDouble(data[6]);

                        towers.add(new Tower(latitude, longitude, mcc, mnc, lac, cell, range));
                    } catch (NumberFormatException e) {
                        Log.e("CSV Data", "Error parsing latitude or longitude: " + e.getMessage());
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return towers;
    }
}
