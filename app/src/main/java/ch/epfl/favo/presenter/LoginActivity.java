package ch.epfl.favo.presenter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import ch.epfl.favo.R;
import ch.epfl.favo.models.UserUtil;

public class LoginActivity extends AppCompatActivity {
    TextView userEmail;
    TextView userPw;


    Button loginButton;

    // Callbacks
    View.OnClickListener onLoginButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userEmail = (EditText) findViewById(R.id.user_email);
            userPw = (EditText) findViewById(R.id.user_password);
            login(userEmail.getText().toString(),userPw.getText().toString());
            finish();
            return;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(onLoginButtonPressed);
    }

    public void login(String userEmail,String userPw) {


        /**
         * TODO: Implement Login Logic
         * UserUtil.getSingleInstance().logInAccount(userEmail,userPw);
         */

        Toast.makeText(getBaseContext(), "Signed in!", Toast.LENGTH_SHORT).show();


        //set activity_executed so that the app doesn't show the login page anymore
        setActivityExecuted();
        //if login successful go into main activity class
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return;
    }
    public void setActivityExecuted(){
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", true);
        edt.apply();
    }
}
