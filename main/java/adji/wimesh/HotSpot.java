package adji.wimesh;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Adji on 05-Nov-15.
 */
public class HotSpot {
    WifiManager mWifiManager;
    WifiInfo mWifiInfo ;
    Context mContext;
    boolean hotSpotStatus = false;
    Timer mTimer;
    boolean pusat=false;

    public  HotSpot(Context c) {
        mContext=c;
        mWifiManager=(WifiManager)  mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();

    }


    public void setPusat(boolean pusat){
        this.pusat=pusat;

    }

    public boolean getPusat(){
        return this.pusat;
    }
    public String getDefaultGateway(){
        DhcpInfo info=mWifiManager.getDhcpInfo();
        return ipIntToString(info.gateway);
    }

    public static String pingToHotSpot() {
        String pingResult = "rto";
        String pingCmd = "/system/bin/ping -c 1 www.google.co.id";// + this.getHostIPAddress();
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


    public List<ScanResult> sortHotspotsByLevel(){
        List<ScanResult> hotspotList=mWifiManager.getScanResults();
        List<ScanResult> sorthotspotsList=new ArrayList<ScanResult>();
        ScanResult result;
        while(!hotspotList.isEmpty()){
            result=getHotspotMaxLevel(hotspotList);
            sorthotspotsList.add(result);
            hotspotList.remove(result);
        }

        return sorthotspotsList;
    }

    public int getPowerPercentage(int power) {
        int i = 0;
        if (power <= -100) {
            i = 0;
        } else {
            i = 100 + power;
        }

        if(i>100){
            i=100;
        }
        return i;
    }

    public String getSSIDName(){
        WifiConfiguration netConfig = new WifiConfiguration();
        return netConfig.SSID;
    }
    public void changeSSIDName(){
        WifiConfiguration netConfig = new WifiConfiguration();
        if(!netConfig.SSID.equalsIgnoreCase("WiMesh")){
            netConfig.SSID = "WiMesh";
        }
    }
    public void doPingEveryOneMinute(){
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pingToHotSpot();
            }
        },0,60000);
        //while(true){
           // try {
               // this.pingToHotSpot();
               // Thread.sleep(60000);
           // }catch(Exception e){}
       // }
    }

    public ScanResult getHotspotMaxLevel(List<ScanResult> hotspotList){

        if (hotspotList != null) {
            final int size = hotspotList.size();
            if (size == 0){
                return null;
            } else {
                ScanResult maxSignal = hotspotList.get(0);

                for (ScanResult result : hotspotList) {
                    if (WifiManager.compareSignalLevel(maxSignal.level,
                            result.level) < 0) {
                        maxSignal = result;
                    }
                }
                return maxSignal;
            }
        }else{
            return null;
        }

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

    public boolean setAndStartHotSpot(boolean enable, String SSID)
    {
        //For simple implementation I am creating the open hotspot.
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for(Method mMethod: mMethods){
            {
                if(mMethod.getName().equals("setWifiApEnabled")) {
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = SSID;
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    try{
                        mMethod.invoke(mWifiManager, netConfig,true);
                    }catch(Exception e)
                    {
                        return false;
                    }
                    startHotSpot(enable);
                }
            }
        }
        return enable;
    }

    public boolean getHotSpotStatus(){
        Method[] wmMethods = mWifiManager.getClass().getDeclaredMethods();
        for(Method method: wmMethods){
            if(method.getName().equals("isWifiApEnabled")) {
                try {
                    return (Boolean) method.invoke(mWifiManager);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
    }
    return false;
    }

    public boolean startHotSpot(boolean enable) {
        mWifiManager.setWifiEnabled(false);
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for (Method mMethod : mMethods) {
            if (mMethod.getName().equals("setWifiApEnabled")) {
                try {
                    mMethod.invoke(mWifiManager, null, enable);
                    return true;
                } catch (Exception ex) {

                }
                break;
            }
        }
        return false;
    }

    public String getHostIPAddress(){
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        byte[] ipAddress = convert2Bytes(dhcpInfo.serverAddress);
        try {
            String apIpAddr = InetAddress.getByAddress(ipAddress).getHostAddress();
            return apIpAddr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static byte[] convert2Bytes(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        return addressBytes;
    }

    public String getDeviceIPAddress(){
        if(mWifiInfo!=null){
            int ip = mWifiInfo.getIpAddress();
            return ipIntToString(ip);
        }
        return null;
    }

    /**
     * Method untuk Conversion Ip Address From Int to String
     *
     * @param ipInt Ip as Int
     * @return Ip as String
     */
    public static String ipIntToString(int ipInt) {
        String ip = "";
        for (int i = 0; i < 4; i++) {
            ip = ip + ((ipInt >> i * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    public void scanAndConnect(){

    }
}
