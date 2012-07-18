package com.example.testresponsecache;

import java.io.File;

import android.app.Application;
import android.util.Log;

public class ResponseApplication extends Application {
  public void onCreate() {
    super.onCreate();
    new Thread(){
      @Override
      public void run() {
        enableHttpResponseCache();
      }
    }.start();
  }
  private void enableHttpResponseCache(){
    try {
      long httpCacheSize = 10 * 1024 * 1024;
      File httpCacheDir = new File(getCacheDir(), "http");
      Class.forName("android.net.http.HttpResponseCache")
        .getMethod("install", File.class, long.class)
        .invoke(null, httpCacheDir, httpCacheSize);
    } catch (Exception e) {
      Log.e("===>", e.getMessage(), e);
    }
  }
}
