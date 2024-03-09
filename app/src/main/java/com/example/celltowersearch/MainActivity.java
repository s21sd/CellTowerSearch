package com.example.celltowersearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    public static class DistanceRangePair {
        public double distance;
        public int range;

        public DistanceRangePair(double distance, int range) {
            this.distance=distance;
            this.range= range;
        }
    }
}

public class MainActivity extends AppCompatActivity {
    private ArrayList<Tower> towers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        towers = readCSVFile();
//        double latitude = 20.3488139;
//        double longitude =85.8163151;

        double latitude = 27.171894;
        double longitude =81.210715 ;
        double[] location = {latitude, longitude};

        ArrayList<Tower.DistanceRangePair> pairs = findNearestTower(location, towers);
        if (!pairs.isEmpty()) {
            Log.d("Distance and index","Distance"+pairs.get(0).distance+ "Range "+pairs.get(0).range);
            Log.d("Distance and index","Distance"+pairs.get(1).distance+ "Range "+pairs.get(1).range);

//            for (Tower.DistanceRangePair pair : pairs) {
//                Log.d("Distance and Range", "Distance: " + pair.distance + ", Range: " + pair.range);
//                break;
//            }
        } else {
            Toast.makeText(MainActivity.this, "No towers found", Toast.LENGTH_SHORT).show();
        }
    }

    static ArrayList<Tower.DistanceRangePair> findNearestTower(double[] location, ArrayList<Tower> towers) {
        ArrayList<Tower.DistanceRangePair> pairs = new ArrayList<>();
        int cnt=0;
        for (Tower tower : towers) {
            cnt++;
            double distance = euclideanDistance(location[0], location[1], tower.latitude, tower.longitude);
            int range =cnt ;
            Tower.DistanceRangePair pair = new Tower.DistanceRangePair( distance, range);
            pairs.add(pair);
        }
        Collections.sort(pairs, new Comparator<Tower.DistanceRangePair>() {
            @Override
            public int compare(Tower.DistanceRangePair pair1, Tower.DistanceRangePair pair2) {
                // Compare based on distance
                return Double.compare(pair1.distance, pair2.distance);
            }
        });
        return pairs;
    }

    static double euclideanDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    private ArrayList<Tower> readCSVFile() {
        ArrayList<Tower> towers = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("Datalacks.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 9) { // Ensure that there are at least 9 columns
                    try {
                        double mcc = Double.parseDouble(data[1]);
                        double mnc = Double.parseDouble(data[2]);
                        double lac = Double.parseDouble(data[3]);
                        double cell = Double.parseDouble(data[4]);
                        double range = Double.parseDouble(data[8]);
                        double longitude = Double.parseDouble(data[6]);
                        double latitude = Double.parseDouble(data[7]);
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
