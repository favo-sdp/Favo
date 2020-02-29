package ch.epfl.favo.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.favo.R;


public class LoginActivity extends AppCompatActivity {
    private String userEmail;
    private String userPw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userEmail = findViewById(R.id.user_email).toString();
        userPw = findViewById(R.id.user_password).toString();
        //TODO: Login Logic Andrea
        finish();
    }
}
