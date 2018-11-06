package com.example.upamanyu.stanfordhealthpp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class PastRecordsActivity extends AppCompatActivity {

    private GraphView respGraph, ppgGraph;
    private TextView nameTV;
    private Spinner dates;

    com.example.upamanyu.stanfordhealthpp.DBHelper mydb;

    LineGraphSeries<DataPoint> ppgSeries, respSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_records);

        respGraph = findViewById(R.id.respGraph);
        ppgGraph = findViewById(R.id.ppgGraph);
        nameTV = findViewById(R.id.nameTV);
        dates = findViewById(R.id.dates);

        final String bpm[] = new String[10];
        final String rr[] = new String[10];
        final String name[] = new String[10];
        final String time[] = new String[10];
        mydb = new com.example.upamanyu.stanfordhealthpp.DBHelper(this);
        Cursor rs = mydb.getData("Shubham_Rateria");
        int index = 0;
        while(rs != null && rs.moveToNext()){
            name[index] = rs.getString(1);
            bpm[index] = rs.getString(2);
            rr[index] = rs.getString(3);
            time[index] = rs.getString(4);
            index++;
        }

        nameTV.setText(name[0]);

        List<String> list = new ArrayList<>();
        for(int k = 0; k<index; k++){
            list.add(time[k]);
        }

        ArrayAdapter<String> adap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        dates.setAdapter(adap);

        dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String bpm_arr[] = bpm[i].split(",");
                String rr_arr[] = rr[i].split(",");
                Double bpm_int[] = new Double[bpm_arr.length];
                Double rr_int[] = new Double[rr_arr.length];

                ppgSeries = new LineGraphSeries<>();
                respSeries = new LineGraphSeries<>();

                for(int j=0; j<bpm_arr.length; j++){
                    try {
                        bpm_int[j] = Double.parseDouble(bpm_arr[j]);
                        rr_int[j] = Double.parseDouble(rr_arr[j]);

                        DataPoint resp = new DataPoint(j, rr_int[j]);
                        DataPoint ppg = new DataPoint(j, bpm_int[j]);
                        ppgSeries.appendData(ppg, false, bpm_arr.length);
                        respSeries.appendData(resp, false, bpm_arr.length);

                    }
                    catch(NumberFormatException e){
                        continue;
                    }
                }

                respGraph.removeAllSeries();
                ppgGraph.removeAllSeries();

                respGraph.addSeries(respSeries);
                ppgGraph.addSeries(ppgSeries);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
