package com.example.upamanyu.stanfordhealthpp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.sql.Timestamp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


import com.example.upamanyu.stanfordhealthpp.DBHelper;
public class MainActivity extends AppCompatActivity {

    private GraphView ppgGraph, respGraph;
    private TextView nameTV, status;
    private Button triageBtn, connectBtn, dataDisplay;

    private LineGraphSeries<DataPoint> ppgSeries;
    private LineGraphSeries<DataPoint> respSeries;

    private double ppgTime, respTime;
    private double respRate;
    private double deltaT = 1.0;
    WebSocket ws;
    OkHttpClient client;
    String bpm;
    String rr;
    String name;

    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Permission already Granted
            //Do your work here
            //Perform operations here only which requires permission
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        respRate = 1;
        ppgTime = 0.0;
        respTime = 0.0;
        nameTV = findViewById(R.id.nameTV);
        ppgGraph = findViewById(R.id.liveHR);
        respGraph = findViewById(R.id.liveRR);
        triageBtn = findViewById(R.id.triageBtn);
        connectBtn = findViewById(R.id.connectBtn);
        dataDisplay = findViewById(R.id.data);
        status = findViewById(R.id.profileImg);
        status.setBackgroundColor(Color.parseColor("#ffffff"));
        status.setText("CHILD STATUS");

        client = new OkHttpClient();
        mydb = new DBHelper(this);

        final Intent i = new Intent(this, PastRecordsActivity.class);

        triageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(respRate == 1) {
                    status.setBackgroundColor(Color.parseColor("#ff0000"));
                    status.setText("NEEDS MEDICAL ATTENTION");
                    respRate = 0;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Magic here
                        }
                    }, 4000); // Millisecond 1000 = 1 sec

                    Intent i = new Intent(MainActivity.this, TriageMapsActivity.class);
                    startActivity(i);
                }
                else{
                    respRate = 1;
                    status.setBackgroundColor(Color.parseColor("#00ff00"));
                    status.setText("NORMAL");
                }

            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect("ws://10.21.155.54:8083");
            }
        });

        dataDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
            }
        });
        ppgSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(ppgTime, 0)
        });
        ppgGraph.addSeries(ppgSeries);

        respSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(respTime, 0)
        });
        respGraph.addSeries(respSeries);

        Viewport viewport1 = ppgGraph.getViewport();
        viewport1.setScrollable(true);
        viewport1.setXAxisBoundsManual(true);
        viewport1.setMinX(0);
        viewport1.setMaxX(500);

        Viewport viewport2 = respGraph.getViewport();
        viewport2.setScrollable(true);
        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(0);
        viewport2.setMaxX(500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Do your work here
                //Perform operations here only which requires permission
            }
        }
    }


    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("I am a Disco Dancer pam pam pa ram !");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
            try {
                JSONObject data = new JSONObject(text);
                name = data.getString("identity");
                name = name.substring(2,name.length()-2);
                nameTV.setText(name);

                if(name.contains("bham"))
                    respRate = 1;
                else
                    respRate = 0;

                String bpm_ = data.getString("bpm");
                String rr_ = data.getString("rr");

                respTime += deltaT;
                ppgTime += deltaT;

                try {
                    DataPoint ppg = new DataPoint(ppgTime, Double.parseDouble(bpm_));
                    ppgSeries.appendData(ppg, true, 2000);

                    DataPoint resp = new DataPoint(respTime, Double.parseDouble(rr_));
                    respSeries.appendData(resp, true, 2000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                bpm += ","+bpm_;
                rr += ","+rr_;
            }
            catch(Exception e){}

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            mydb.insert_data(name,bpm,rr, String.valueOf(timestamp));
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            mydb.insert_data(name,bpm,rr, String.valueOf(timestamp));
            output("Database Updated " + name);
        }
        public void output(String text){
            Log.d("OUT", text);
        }
    }

    public void connect(String host){
        Request request = new Request.Builder().url(host).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        ws.send("HelloWorld");
    }
}
