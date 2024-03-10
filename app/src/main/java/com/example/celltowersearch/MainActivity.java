package com.example.celltowersearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

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
        public int index;

        public DistanceRangePair(double distance, int index) {
            this.distance=distance;
            this.index= index;
        }
    }
}

public class MainActivity extends AppCompatActivity {
    private ArrayList<Tower> towers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Read CSV file
            CSVReader reader = new CSVReader(new FileReader("Nine.csv"));
            List<String[]> lines = reader.readAll();
            reader.close();

            // Sort based on latitude
            lines.sort(Comparator.comparingDouble(o -> Double.parseDouble(o[6])));

            // Rewrite sorted data to CSV file
            CSVWriter writer = new CSVWriter(new FileWriter("Nine.csv"));
            writer.writeAll(lines);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

//        towers = readCSVFile();
//        Log.d("Tower Length","length"+towers.size());
////        double latitude = 20.3488139;
////        double longitude =85.8163151;
//
//        double latitude = 27.1722561;
//        double longitude =81.2105063 ;
//        double[] location = {latitude, longitude};
//
//        ArrayList<Tower.DistanceRangePair> pairs = findNearestTower(location, towers);
//        if (!pairs.isEmpty()) {
//            Log.d("Length of pair","len"+pairs.size());
//            Log.d("Distance and index","Distance"+pairs.get(0).distance+ "Range "+pairs.get(0).index);
//            Log.d("Distance and index","Distance"+pairs.get(1).distance+ "Range "+pairs.get(1).index);
//
////            for (Tower.DistanceRangePair pair : pairs) {
////                Log.d("Distance and Range", "Distance: " + pair.distance + ", Range: " + pair.range);
////                break;
////            }
//        } else {
//            Toast.makeText(MainActivity.this, "No towers found", Toast.LENGTH_SHORT).show();
//        }
//    }

    static ArrayList<Tower.DistanceRangePair> findNearestTower(double[] location, ArrayList<Tower> towers) {
        ArrayList<Tower.DistanceRangePair> pairs = new ArrayList<>();
        for (Tower tower : towers) {
            int cnt = towers.indexOf(tower);
            double distance = euclideanDistance(location[0], location[1], tower.latitude, tower.longitude);
            Tower.DistanceRangePair pair = new Tower.DistanceRangePair( distance, cnt);
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
            InputStream inputStream = getAssets().open("cell_towers4.csv");
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