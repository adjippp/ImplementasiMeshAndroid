package adji.wimesh;

import android.net.wifi.WifiManager;

/**
 * Created by Adji on 14-Nov-15.
 */
public class Scanner implements Runnable {

    private WifiManager wm;
    public static boolean loop=true;

    Scanner(WifiManager wm){
        this.wm=wm;
    }

    public void run(){
        this.loop=true;
        while(this.loop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wm.startScan();
        }
    }
}
