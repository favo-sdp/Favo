package ch.epfl.favo.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ch.epfl.favo.R;
import ch.epfl.favo.models.FavorUtil;
import ch.epfl.favo.models.UserUtil;

/**
 * This will control the general view of our app. It will contain 3 tabs. On the first tab it will have the map
 * and the favor request pop-up. On the second tab it will contain the list view of previous favors. On the third tab
 * it will contain account information. These tabs will be implemented in more detail in the other presenter classes
 */
public class MainActivity extends AppCompatActivity {
    //user in the example
    static String EXTRA_MESSAGE;
    //Sample CODE:
    ///** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.mainName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // UI references
    Button loginButton;


    // Model references
    private UserUtil userUtility = UserUtil.getSingleInstance();
    private FavorUtil favorUtility = FavorUtil.getSingleInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login); //TODO: Find a way to access login activity only the first time app is launched
        setContentView(R.layout.activity_login);//Then we want to access the main activity
    }


}


