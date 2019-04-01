package adji.wimesh;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Adji on 25-Nov-15.
 */
public class WifiStatus {
    Context mContext;
    WifiManager mWifiManager;
    WifiInfo mWifiInfo;

    public WifiStatus(Context c) {
        mContext=c;
        mWifiManager=(WifiManager)  mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public boolean isConnectedToAP(){
        ConnectivityManager connectivity = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isTheWifiEnabled() {
        if (!mWifiManager.isWifiEnabled() ) {
            return false;
        }else{
            return true;
        }
    }

    public boolean setWifiEnabled(){
        if(!isTheWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
            return true;
        }else{
            return false;
        }
    }

    public boolean setWifiDisabled(){
        if(isTheWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
            return true;
        }else{
            return false;
        }
    }

    public boolean  isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
