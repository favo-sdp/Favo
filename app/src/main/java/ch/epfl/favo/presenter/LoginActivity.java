package ch.epfl.favo.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.favo.R;
import ch.epfl.favo.exceptions.NotImplementedException;


public class LoginActivity extends AppCompatActivity {
    private String userEmail;
    private String userPw;
    Button loginButton;
    TextView emailInput;
    TextView pw;
    // Callbacks
    View.OnClickListener onLoginButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userEmail = findViewById(R.id.user_email).toString();
            userPw = findViewById(R.id.user_password).toString();
            login();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // find UI elements
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(onLoginButtonPressed);
        //Set up
        //displayToast();

        //throw new NotImplementedException();

    }

    /**
     * Will show pop up message if login fails
     */
    //public void displayToast(){
    //    LayoutInflater inflater = getLayoutInflater();
    //    View layout = inflater.inflate(R.layout.custom_toast,
    //            (ViewGroup) findViewById(R.id.custom_toast_container));
    //    TextView text = (TextView) layout.findViewById(R.id.text_invalid_input);
    //    text.setText("Login Failed");
//
    //    Toast toast = new Toast(getApplicationContext());
    //    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    //    toast.setDuration(Toast.LENGTH_SHORT);
    //    toast.setView(layout);
    //    toast.show();
    //}
    public void login(){
        //TODO: Login Logic Andrea
        throw new NotImplementedException();
    }
}
