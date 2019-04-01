package adji.wimesh;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Adji on 04-Apr-16.
 */
public class ClientSocket extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    Socket clientSocket;
    OutputStream out;
    InputStream in;
    public BufferedWriter writer;
    String message;

   ClientSocket(String addr, int port){
        this.dstAddress = addr;
        this.dstPort = port;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            Log.d("masuk","tidak error");
            clientSocket = new Socket(this.dstAddress, this.dstPort);

            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write("I'm Client");
            writer.flush();
            do {
                message = reader.readLine();
            }while(message!="xx");

    /*
     * notice:
     * inputStream.read() will block if no data return
     */



        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //textResponse.setText(response);
        super.onPostExecute(result);
    }

}


