package adji.wimesh;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adji on 23-Mar-16.
 */
    public class Device {
       private String name,IPAddress,mac;
       private boolean isHotspot=false;


        public Device(){
            this.name="Phone";
            this.IPAddress="0.0.0.0";
        }

        public Device(String name, String IPAddress, String mac){
            this.name=name;
            this.mac=mac;
            this.IPAddress=IPAddress;

        }
        public void setHotspot(boolean hotspot){
            this.isHotspot=hotspot;
        }

    public boolean getHotspot(){
        return this.isHotspot;
    }

    public void setMac(String mac){
        this.mac=mac;
    }

    public String getMac(){
        return this.mac;
    }
        public void setName(String name){
            this.name=name;
        }

        public void setIPAddress(String ip){
            this.IPAddress=ip;
        }

        public String getName(){
            return this.name;
        }

       // public String getIPAddress(){
         //   return this.IPAddress;
       // }

    public static String getIPAddress() {
        boolean useIPv4=true;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }



//        public boolean fillClientList(){
//            //panggilmethodnya
//            String namaPonsel="Phone";
//            String ipAddress="ipAddressnya";
//            //Device(namaPonsel,ipAddress);
//
//            return true;
//        }
    }

