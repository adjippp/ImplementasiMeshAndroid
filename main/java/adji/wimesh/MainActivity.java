package adji.wimesh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void getWifiActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), WiFiActivity.class);
        startActivity(intent);
    }

    public void getConnectedPhone(View view) {
        Intent intent = new Intent(getApplicationContext(), ConnectedPhone.class);
        //System.out.println("aaa" );
        startActivity(intent);
    }

    public void getHelp(View view) {
        Intent intent = new Intent(getApplicationContext(), Help.class);
        //System.out.println("aaa" );
        startActivity(intent);
    }
   // public void getMobileDataActivity(View view) {
     //   Intent intent = new Intent(getApplicationContext(), MobileDataActivity.class);
     //   startActivity(intent);
   // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
