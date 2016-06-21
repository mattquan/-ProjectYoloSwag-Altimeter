package com.example.lance_000.altimeter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private float altitude = 0;
    private float currentAtmosphere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentAtmosphere = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        new MyAsyncTask().execute();
        if(sensor!=null)
        {
            Log.e("hey","hey");
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            TextView tv = (TextView) findViewById(R.id.TV);
            tv.setText("none");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        TextView myTextView = (TextView) (findViewById(R.id.TV));
        myTextView.setText(String.valueOf(altitude));

        float pressure = event.values[0];
        altitude = SensorManager.getAltitude(currentAtmosphere, pressure);

        TextView textView = (TextView) findViewById(R.id.TV);
        textView.setText(String.valueOf(altitude));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {



    }
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            URL url = null;
            try {
                url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Livingston%2C%20nj%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream in = null;
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);

                    Log.e("result",result.toString());
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();            }

            return null;

        }

        protected void onPostExecute (String result) {
            try {
                JSONObject json = new JSONObject(result).optJSONObject("query").optJSONObject("results").optJSONObject("channel").optJSONObject("atmosphere");
                double pressure = json.optDouble("pressure");
                currentAtmosphere = (float) pressure;
                TextView tv = (TextView) findViewById(R.id.TV2);
                tv.setText("current atmosphere:"+pressure);
            } catch (JSONException e) {
                Log.e("caught",e.toString());
                e.printStackTrace();
            };

        }
    }

}
