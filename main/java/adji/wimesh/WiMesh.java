package adji.wimesh;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by Adji on 14-Nov-15.
 */
public class WiMesh {

    public final static int SERVER=1;
    public final static int CLIENT=2;
    private Activity activity;
    private Thread scannerThread=null;
    private Thread recieverThread=null;
    private WifiManager wifiManager;
    private int actAs;


    public WiMesh(Activity activity) {
        this.activity = activity;
    }

    public void start() {
        if (isNetworkConnected() && !isConnectedToAP()) {
            this.actAs=WiMesh.SERVER;
            //sebagai hotspot
            startHotSpot(true);
        } else {
            this.actAs=WiMesh.CLIENT;
            //sebagai client, melakukan prosedur wimesh
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isConnectedToAP() {
        ConnectivityManager connectivity = (ConnectivityManager) this.activity
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

    public boolean startHotSpot(boolean enable) {
        wifiManager.setWifiEnabled(false);
        Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
        for (Method mMethod : mMethods) {
            if (mMethod.getName().equals("setWifiApEnabled")) {
                try {
                    mMethod.invoke(wifiManager, null, enable);
                    return true;
                } catch (Exception ex) {

                }
                break;
            }
        }
        return false;
    }
}
