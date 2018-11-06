package com.example.upamanyu.stanfordhealthpp;

import android.app.Activity;
import android.os.Bundle;

import android.database.Cursor;

import com.example.upamanyu.stanfordhealthpp.DBHelper;

public class displayData extends Activity {

    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        String bpm = "";
        String rr = "";
        mydb = new DBHelper(this);
        Cursor rs = mydb.getData("Shubham_Rateria");
        if(rs != null && rs.moveToNext()){
            bpm = rs.getString(2);
            rr = rs.getString(3);
        }
        String bpm_arr[] = bpm.split(",");
        String rr_arr[] = rr.split(",");
        Double bpm_int[] = new Double[bpm_arr.length];
        Double rr_int[] = new Double[rr_arr.length];
        int index = 0;
        for(String val : bpm_arr){
            try {
                bpm_int[index] = Double.parseDouble(val);
                index++;
            }
            catch(NumberFormatException e){continue;}
        }

    }
}