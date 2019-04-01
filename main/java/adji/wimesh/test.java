package adji.wimesh;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Adji on 16-Mar-16.
 */
public class test extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test", "Hii");
        System.out.println("123");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
    }
}
