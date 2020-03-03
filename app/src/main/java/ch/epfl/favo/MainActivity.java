package ch.epfl.favo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "ch.epfl.favo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the button */
    public void openMap(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MapsActivity.class);
        Log.d("Debug",  "maps");
        startActivity(intent);
    }

}
