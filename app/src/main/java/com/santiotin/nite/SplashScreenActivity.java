package com.santiotin.nite;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null && user.isEmailVerified()){
                    finish();
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
                else {
                    finish();
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
            }
        }, 3000);

    }

}
