package ch.epfl.favo.presenter;
import ch.epfl.favo.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;


public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // decide here whether to navigate to Login or Main Activity

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        /**
         * Use block if we want to always open the login view.
         * SharedPreferences.Editor edt = pref.edit();
         * edt.putBoolean("activity_executed", false);
         * edt.apply();
         */

        if (appIsLoggedIn(pref)) {
            //Go to the main activity if already logged in
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            //Go to the sign-in activity if not logged in yet
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public boolean appIsLoggedIn(SharedPreferences pref){
        return pref.getBoolean("activity_executed", false);
    }
}
