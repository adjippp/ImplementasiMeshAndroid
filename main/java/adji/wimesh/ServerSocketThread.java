package adji.wimesh;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Adji on 04-Apr-16.
 */
public class ServerSocketThread implements Runnable {

    static final int SocketServerPORT = 8080;
    int count = 0;
    String message = "";
    ServerSocket serverSocket;
    public ArrayList<Thread> clientList;
    HotSpot ht;

    ServerSocketThread(HotSpot ht){
        this.ht=ht;
    }

    @Override
    public void run() {
        boolean isListening=true;
        this.clientList = new ArrayList();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.serverSocket = new ServerSocket(SocketServerPORT);
            Log.d("SocketTest", "Server listening in port:" + SocketServerPORT);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        while (isListening) {
            try {
                Socket socket = serverSocket.accept();
                Thread h = new Thread(new SocketHandler(
                        socket, this, count));
                this.clientList.add(h);
                count++;
                message += "Client #" + count + " terhubung " + socket.getInetAddress()
                        + ":" + socket.getLocalPort() + "\n";
                Log.d("SocketTest", message);
                h.start();
            } catch (Exception e) {

                Log.d("SocketTest", "Server stopped listening");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void disconnectClient(SocketHandler sh) {

        this.clientList.remove(sh);
    }

    public int getCount() {
        return this.count;
    }

}

class SocketHandler implements Runnable {

    private Socket connectedSocket;
    OutputStream out;
    InputStream inp;
    public BufferedWriter writer;
    ServerSocketThread motherSocket;
    String message;
    int cnt;

    SocketHandler(Socket socket, ServerSocketThread motherSocket, int c) {
        connectedSocket = socket;
        motherSocket = motherSocket;
        cnt = c;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = "Hello from Android, you are #" + cnt;
        Log.d("SocketServer","Client Handler #"+cnt);
        //disini nerima/ngirim message dari/ke client
        try {
            out = connectedSocket.getOutputStream();
            inp = connectedSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inp));
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write("Kamu terhubung dengan saya...");
            writer.flush();
            Log.d("ServerSocket","Listening client #"+cnt);
            do {
                message = reader.readLine();
                Log.d("ServerSocket", message);
            } while (message.equals("xqx"));
            inp.close();
            out.close();
            this.connectedSocket.close();
            this.motherSocket.clientList.remove(this);
            Log.d("SocketTest", "Client disconnect");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.motherSocket.clientList.remove(this);
            Log.d("SocketTest", "Error on socket handler");
        }

    }


    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}