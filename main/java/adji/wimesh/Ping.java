package adji.wimesh;

import android.util.Log;

/**
 * Created by Adji on 14-Nov-15.
 */
public class Ping implements Runnable {

    Ping(){

    }

    @Override
    public void run() {
        while (HotSpot.pingToHotSpot()!="rto"){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WiFiActivity.isDc=true;
        WiFiActivity.isHotSpotDC=true;
        Log.d("Pinger","Ping gagal(RTO)");
    }
}
