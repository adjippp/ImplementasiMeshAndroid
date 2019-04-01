package adji.wimesh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Adji on 16-Mar-16.
 */
public class ConnectedPhone extends Activity{
    private ArrayAdapter adapter;
    private ArrayList<Device> phone;
    private String nama= "Phone";
    private int counter=0;
    protected void onCreate(Bundle savedInstanceState) {
        this.phone = new ArrayList<Device>();
        Log.d("Test", "Hii");
        System.out.println("123");
        //hotspot = new HotSpot(getApplicationContext());
        //ws=new WifiStatus(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ListView listview = (ListView) findViewById(R.id.listView);
//
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
//
        listview.setAdapter(adapter);
        getClientList();


    }

    public boolean nameChecker(){
        this.counter++;
        return true;
    }

    public void getClientList() {
        System.out.println("aaa" );
        int macCount = 0;
        BufferedReader br = null;
        int cek = 0;

        try {
            br = new BufferedReader(new FileReader("/proc/net/arp/"));

            String line = new String();
            String isi;
            //while((isi = br.readLine()) != null) {
           // System.out.println(isi);}
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null ) {
                    // Basic sanity check
                    String mac = splitted[3];
                    System.out.println("Mac : Outside If "+ mac );
                    if (mac.matches("..:..:..:..:..:..")) {

                   /* ClientList.add("Client(" + macCount + ")");
                    IpAddr.add(splitted[0]);
                    HWAddr.add(splitted[3]);
                    Device.add(splitted[5]);*/
                        if(!mac.equals("00:00:00:00:00:00")) {
                            macCount++;

                            System.out.println("Mac : " + mac + " IP Address : " + splitted[0]);
                            System.out.println("Mac_Count  " + macCount + " MAC_ADDRESS  " + mac);
                            while(cek==0) {
                                Device temp=new Device();
                                String s = pingToClient(splitted[0]);
                                //Thread.sleep(50);
                                //boolean check=checkPing(splitted[0]);
                                if(s.equalsIgnoreCase("rto") ){
                                    System.out.println(splitted[0] + " RTO");
                                    cek=1;
                                }else if(s.equalsIgnoreCase("ping success") ){
                                    System.out.println(splitted[0] + " Sukses");
                                    temp.setName(this.nama+" "+this.counter);
                                    temp.setMac(mac);
                                    temp.setIPAddress(splitted[0]);
                                    this.phone.add(temp);
                                    adapter.add("Mac : " +this.phone.get(this.phone.size()).getMac() + " IP Address : " + this.phone.get(this.phone.size()).getIPAddress()+" Name : "+this.phone.get(this.phone.size()).getName());
                                    cek=1;
                                }
                                this.counter++;
                            }
                            cek=0;

                            //System.out.println("test masuk");

                        }
                    }
               /* for (int i = 0; i < splitted.length; i++)
                    System.out.println("Addressssssss     "+ splitted[i]);*/

                }
            }
            if(adapter.isEmpty()){
                adapter.add("Tidak ada Client yang terhubung");
            }
        } catch(Exception e) {
            Toast.makeText(
                    getApplicationContext(),
                    "Tidak ada ponsel yang terhubung", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onBackPressed(){

        adapter.clear();
        finish();


    }

    public static String pingToClient(String ip){
        String pingResult = "rto";
        String pingCmd = "/system/bin/ping -c 1 "+ip;// + this.getHostIPAddress();
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String inputLine;
            Log.d("TEST","Start readline");
            while ((inputLine = in.readLine()) != null) {
                Log.d("TEST",inputLine);
                if (inputLine.contains("1 received"))
                    pingResult = "ping success";
            }
            Log.d("TEST","End readline");
            Log.d("TEST",pingResult);
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return pingResult;
    }

    public static boolean checkPing(String ip)
    {
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " +ip);

            int mExitValue = mIpAddrProcess.waitFor();
            boolean reachable = (mExitValue==0);
            return reachable;
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println("Exception: " + ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Exception: " + e);
        }
        return false;
    }
//    public void onToggleClicked(View view) {
//        if (!hotspot.isConnectedToAP()) {
//            Toast.makeText(getApplicationContext(), "Hotspot dimatikan",
//                    Toast.LENGTH_SHORT).show();
//            hotspot.startHotSpot(false);
//        } else {
//            Toast.makeText(getApplicationContext(), "WiFi dimatikan",
//                    Toast.LENGTH_SHORT).show();
////        adapter.clear();
//            Scanner.loop = false;
//            wifiManager.disconnect();
//            wifiManager.setWifiEnabled(false);
//            unregisterReceiver(wifiReciever);
//        }
}
