package com.example.myapptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isConnectedToInternet(context)) {
            Log.d("NetworkChangeReceiver", "Conectado à internet");
        } else {
            Log.d("NetworkChangeReceiver", "Sem conexão com a internet");
        }
    }
}
