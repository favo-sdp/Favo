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
    // Model references
    private UserUtil userUtility = UserUtil.getSingleInstance();
    private FavorUtil favorUtility = FavorUtil.getSingleInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




}


