package com.example.testresponsecache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

  private final String TAG = getClass().getSimpleName();
  ImageView img;
  Button msg;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    img = (ImageView) this.findViewById(R.id.img);
    msg = (Button) this.findViewById(R.id.msg);
    msg.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new InternetTask().execute();
      }
    });
  }

  @SuppressLint("NewApi")
  class InternetTask extends AsyncTask<String, String, Boolean> {
    Bitmap bitmap;
    String jsonStr;

    @SuppressLint("NewApi")
    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
      img.setImageBitmap(bitmap);
      msg.setText(jsonStr);
    }

    @Override
    protected Boolean doInBackground(String... params) {
      // Test download image
      try {
        URL url = new URL("http://jasoncheng.tw/1.png");
        HttpURLConnection conn = (HttpURLConnection) (url.openConnection());
        conn.connect();
        InputStream is = conn.getInputStream();
        BitmapFactory.Options ops = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeStream(is, null, ops);
        is.close();
        conn.disconnect();
      } catch (Exception e) {
        Log.e(TAG, e.getMessage(), e);
      }

      // Test download JSON data
      try {
        URL url = new URL("http://jasoncheng.tw/1.json");
        HttpURLConnection conn = (HttpURLConnection) (url.openConnection());
        conn.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        jsonStr = reader.readLine();
        InputStream is = conn.getInputStream();
        is.close();
        conn.disconnect();
      } catch (Exception e) {
        Log.e(TAG, e.getMessage(), e);
      }
      return true;
    }

  }
}
