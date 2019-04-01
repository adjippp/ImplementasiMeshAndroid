package adji.wimesh;

import java.util.Arrays;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.TimerTask;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class WiFiActivity extends Activity {
    public Long waktuConnect, waktuDisconnect;

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private BroadcastReceiver wifiReciever;
    private ArrayAdapter adapter;
    private final IntentFilter intentFilter = new IntentFilter();
    public static boolean isConnectToHotSpotRunning = false;
    private Handler handler;
    SupplicantState sup;
    Context mContext;
    int checker=0;
    int counter=0;
    public HotSpot hotspot;
    public WifiStatus ws;
    public ArrayList<ClientSocket> cs;
    public TextView msg,msg2;
    private Thread scannerThread = null;
    private Thread pingThread=null;
    public static boolean isDc=false;
    public static boolean isHotSpotDC=false;

    public Thread serverSocketThread;

    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test", "Hii");
        hotspot = new HotSpot(getApplicationContext());
        ws=new WifiStatus(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);
        msg = (TextView) findViewById(R.id.msg);
        msg2 = (TextView) findViewById(R.id.msg2);


        ListView listview = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1);

        listview.setAdapter(adapter);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        wifiReciever = new WiFiScanReceiver();
        //getClientList();
        System.out.println(hotspot.getHotSpotStatus());
        if(isNetworkConnected() && hotspot.getHotSpotStatus() == true){
            Toast.makeText(getApplicationContext(), "Hotspot Anda telah aktif",
                    Toast.LENGTH_SHORT).show();
            if(!serverSocketThread.isAlive()){
            this.serverSocketThread= new Thread(new ServerSocketThread(hotspot));
            this.serverSocketThread.start();}
            msg2.setText("Client yang Terhubung: " + counter);
            msg.setText("Ponsel Anda adalah Client");
        }
        else if (isNetworkConnected() == true && ws.isConnectedToAP()) {
            Toast.makeText(getApplicationContext(), "Ponsel Anda terhubung ke internet, tetapi Anda terhubung melalui hotspot",
                    Toast.LENGTH_SHORT).show();
            msg.setText("Ponsel Anda adalah Client");

        } else if (isNetworkConnected() == true && hotspot.getHotSpotStatus()==false) {
            Toast.makeText(getApplicationContext(), "Ponsel Anda terhubung ke internet, otomatis ponsel Anda akan dijadikan hotspot",
                    Toast.LENGTH_SHORT).show();
            hotspot.setPusat(true);
            hotspot.startHotSpot(true);
            msg.setText("Ponsel Anda adalah Hotspot");
            msg2.setText("Client yang Terhubung: " + counter);
            this.checker=0;
            System.out.println(hotspot.getHotSpotStatus());
            this.serverSocketThread= new Thread(new ServerSocketThread(hotspot));
            this.serverSocketThread.start();
//            try {
//                if (pingThread==null || !pingThread.isAlive()) {
//                    Log.d("Test","Start pinger");
//                    pingThread = new Thread(new Ping());
//                    pingThread.start();
//                }
//                Toast.makeText(getApplicationContext(), "Hotspot Sukses Ping!",
//                        Toast.LENGTH_SHORT).show();
//
//            } catch (Exception e) {
//
//            }


        }else {
            Toast.makeText(getApplicationContext(), "Ponsel Anda tidak terhubung ke internet",
                    Toast.LENGTH_SHORT).show();
            msg.setText("Ponsel Anda adalah Client");
        }
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReciever, filter);
        initProcedure();

        super.onCreate(savedInstanceState);

    }



    public String getSecurityMode(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] modes = {"WPA", "EAP", "WEP"};
        for (int i = modes.length - 1; i >= 0; i--) {
            if (cap.contains(modes[i])) {
                return modes[i];
            }
        }
        return "OPEN";
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public boolean connectToHotspot(ScanResult ap) {

        isConnectToHotSpotRunning = true;
        boolean result = false;
        WifiConfiguration wifiConf = new WifiConfiguration();


        wifiConf.SSID = "\"" + ap.SSID + "\"";
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int res = wifiManager.addNetwork(wifiConf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(res, true);
        if (result = wifiManager.reconnect()) {
            wifiManager.setWifiEnabled(true);
            this.waktuConnect = new Date().getTime();
        }
        isConnectToHotSpotRunning = false;
        return result;
    }


    public void removeWifiNetwork(String ssid) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (config.SSID.contains(ssid)) {
                    wifiManager.disableNetwork(config.networkId);
                    wifiManager.removeNetwork(config.networkId);
                }
            }
        }
        wifiManager.saveConfiguration();
    }

    public void initProcedure() {

//        adapter.clear();
        if (wifiManager == null) {
            // Device does not support Wi-Fi
            Toast.makeText(getApplicationContext(), "Oop! Your device does not support Wi-Fi",
                    Toast.LENGTH_SHORT).show();

        } else {
            if (!this.isNetworkConnected()) { // To turn on Wi-Fi
                if (!wifiManager.isWifiEnabled()) {

                    Toast.makeText(getApplicationContext(), "WiFi dinyalakan" +
                                    "\n" + "Memperlihatkan Hotspot",
                            Toast.LENGTH_SHORT).show();

                    wifiManager.setWifiEnabled(true);

                } else {
                    Toast.makeText(getApplicationContext(), "WiFi telah menyala" +
                                    "\n" + "Memperlihatkan Hotspot",
                            Toast.LENGTH_SHORT).show();
                }
                Log.d("TEST", "Ima scanning...");
                scannerThread = new Thread(new Scanner(wifiManager));
                scannerThread.start();
            } else if (this.isNetworkConnected() && ws.isConnectedToAP()) {
                if (!wifiManager.isWifiEnabled()) {

                    Toast.makeText(getApplicationContext(), "WiFi dinyalakan" +
                                    "\n" + "Memperlihatkan Hotspot",
                            Toast.LENGTH_SHORT).show();

                    wifiManager.setWifiEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "WiFi telah menyala" +
                                    "\n" + "Memperlihatkan Hotspot",
                            Toast.LENGTH_SHORT).show();
                }
                Log.d("TEST", "Ima connected...");
                scannerThread = new Thread(new Scanner(wifiManager));
                scannerThread.start();
            } else if (this.isNetworkConnected() && hotspot.getHotSpotStatus()==true) {
                Toast.makeText(getApplicationContext(), "Hotspot aktif",
                        Toast.LENGTH_SHORT).show();
            }
//            } else if (this.isNetworkConnected()) {
//
//                Toast.makeText(getApplicationContext(), "Hotspot dinyalakan",
//                        Toast.LENGTH_SHORT).show();
//                hotspot.setPusat(true);
//                hotspot.setAndStartHotSpot(true, "WiMesh");
//            }
               // else if (this.isNetworkConnected() && !ws.isConnectedToAP()) {
                //Toast.makeText(getApplicationContext(), "Hotspot dimatikan",
                //        Toast.LENGTH_SHORT).show();
               // hotspot.startHotSpot(false);
               // hotspot.setHotSpotStatus(false);
//                adapter.clear();
           // }
           else { // To turn off Wi-Fi
//                Toast.makeText(getApplicationContext(), "WiFi dimatikan",
//                        Toast.LENGTH_SHORT).show();
//                adapter.clear();
//
//                wifiManager.disconnect();
//                wifiManager.setWifiEnabled(false);
            }
        }
    }

    public boolean isIPObtained(){
        wifiInfo = wifiManager.getConnectionInfo();
        sup= wifiInfo.getSupplicantState();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(sup);
        if(state != NetworkInfo.DetailedState.CONNECTED){
            return false;
        }else{
            return true;
        }
    }

    class WiFiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isConnectedToWiMesh = false;
            adapter.clear();
            List<ScanResult> wifiScanResultList = hotspot.sortHotspotsByLevel();
            for (int i = 0; i < wifiScanResultList.size(); i++) {
                ScanResult accessPoint = wifiScanResultList.get(i);
                if (getSecurityMode(accessPoint).equalsIgnoreCase("OPEN")) {
                    adapter.add("SSID: " + accessPoint.SSID + " , Kekuatan sinyal: " + hotspot.getPowerPercentage(accessPoint.level) + " %");
                }
            }

            Log.d("TEST", "Recieve wifi");
//            if(isHotSpotDC){
//                hotspot.startHotSpot(false);
//                wifiManager.setWifiEnabled(true);
//                hotspot.setPusat(false);
//                msg.setText("Ponsel Anda adalah Client");
//                msg2.setText("");
//                if(!scannerThread.isAlive()){
//                scannerThread = new Thread(new Scanner(wifiManager));
//               www.githu scannerThread.start();}
//            }
            if (!isNetworkConnected() || isDc) {
                wifiManager.disconnect();
                Log.d("Test", "Imma scanning!");
                for (int i = 0; i < wifiScanResultList.size(); i++) {
                    ScanResult accessPoint = wifiScanResultList.get(i);
                    if (getSecurityMode(accessPoint).equalsIgnoreCase("OPEN")) {
                        if (!isConnectedToWiMesh) {
                            isConnectedToWiMesh = connectToHotspot(accessPoint);
                            msg.setText("Ponsel Anda adalah Client");
                        }
                    }
                }
                if (isConnectedToWiMesh) {
                    isDc=false;
                    Log.d("TEST", "Connection made");
//                    ( wifiManager.getConnectionInfo().getSSID().equals("0x") || HotSpot.ipIntToString(wifiManager.getDhcpInfo().gateway).equals("0.0.0.0"))
                    long currentTime=new Date().getTime();
                    while (isIPObtained()==false && (currentTime+5000>new Date().getTime())) {
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    Log.d("TEST", "internet ready");

                    if(!HotSpot.ipIntToString(wifiManager.getDhcpInfo().gateway).equals("0.0.0.0")){
                        Toast.makeText(getApplicationContext(), "Kamu terkoneksi dengan " + wifiManager.getConnectionInfo().getSSID() + "\n" + HotSpot.ipIntToString(wifiManager.getDhcpInfo().gateway) + "\n " + waktuConnect,
                                Toast.LENGTH_SHORT).show();
                        ClientSocket clientSocket=new ClientSocket(hotspot.getHostIPAddress(),8080);
                        clientSocket.execute();
                    }
                    try {
                        if (pingThread==null || !pingThread.isAlive()) {
                            Log.d("Test","Start pinger");
                            pingThread = new Thread(new Ping());
                            pingThread.start();
                        }
                        Toast.makeText(getApplicationContext(), "Sukses Ping!",
                                Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada jaringan terbuka yang tersedia, mencoba scan ulang",
                            Toast.LENGTH_SHORT).show();
                    Log.d("TEST", "Tidak ada jaringan, mencoba scan ulang");
                }


            } else if ((hotspot.getHotSpotStatus() == true) || (isNetworkConnected() && !hotspot.isConnectedToAP())) {
                adapter.add("Tidak menampilkan list SSID");

            } else {
                if (pingThread==null || !pingThread.isAlive()) {
                    Log.d("Test","Start pinger");
                    pingThread = new Thread(new Ping());
                    pingThread.start();
                }
                Log.d("TEST","Connected");
            }
        }


    }

    public void onToggleClicked(View view) {
        if ((!hotspot.isConnectedToAP() && hotspot.getHotSpotStatus()==true) || this.isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Hotspot dimatikan",
                    Toast.LENGTH_SHORT).show();
            hotspot.startHotSpot(false);
            hotspot.setPusat(false);
            adapter.clear();
            //unregisterReceiver(wifiReciever);
            msg.setText("Ponsel Anda bukan Hotspot ataupun Client");
            //unregisterReceiver(wifiReciever);
           // hotspot.setHotSpotStatus(false);
        }else if(wifiManager.isWifiEnabled()==false && hotspot.getHotSpotStatus()==false){
            Toast.makeText(getApplicationContext(), "WiFi telah dimatikan",
                    Toast.LENGTH_SHORT).show();
            adapter.clear();
        } else {
            Toast.makeText(getApplicationContext(), "WiFi dimatikan",
                    Toast.LENGTH_SHORT).show();
            adapter.clear();
            msg.setText("Ponsel Anda bukan Hotspot ataupun Client");
            Scanner.loop = false;
            wifiManager.disconnect();
            wifiManager.setWifiEnabled(false);
            unregisterReceiver(wifiReciever);
        }
        //Intent intent = new Intent(getApplicationContext(), WiFiActivity.class);
       // startActivity(intent);
    }

//    public void onBackPressed(){
//
//        adapter.clear();
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class); startActivity(intent);
//        unregisterReceiver(wifiReciever);
//
//    }
//    public void onClicked(View view) {
//        unregisterReceiver(wifiReciever);
//       finish();
//    }

    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for SCAN_RESULTS_AVAILABLE_ACTION

        //receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        //registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(wifiReciever);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wi_fi, menu);
        return true;
    }

    // ProgressDialog d = new ProgressDialog(WiFiActivity.this);
    // @Override
    //protected void onPreExecute() {
    //   super.onPr;
    //  d.setTitle("Turning WiFi AP " + (true?"on":"off") + "...");
    //  d.setMessage("...please wait a moment.");
    //  d.show();
    //}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
