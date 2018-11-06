package com.example.upamanyu.stanfordhealthpp;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadURL{

    public String readURL(String url) throws IOException {
        String data = "";
        InputStream is = null;
        HttpURLConnection urLConn = null;

        try{
            URL myUrl = new URL(url);
            urLConn = (HttpURLConnection) myUrl.openConnection();
            urLConn.connect();

            is = urLConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();

            String line = "";

            while((line=br.readLine()) != null)
                sb.append(line);

            data = sb.toString();
            Log.d("DATA", data);
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            is.close();
            urLConn.disconnect();
        }

        return data;
    }

}
