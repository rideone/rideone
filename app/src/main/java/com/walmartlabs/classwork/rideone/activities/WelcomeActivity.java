package com.walmartlabs.classwork.rideone.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.User;

import static android.view.View.VISIBLE;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //If user already exists, try to auto-login right away
        if(!autoLogin()) {
            showWelcomePage();
        }
    }

    private void showWelcomePage() {
        ViewGroup clWelcome = (ViewGroup) findViewById(R.id.clWelcome);
        clWelcome.setVisibility(VISIBLE);
    }

    @Override
    protected void onResume() {
        if(!autoLogin()) {
            showWelcomePage();
        }
        super.onResume();
    }

    private boolean autoLogin() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) { // start with existing user
            LoginActivity.login(user, this, new GetCallback<User>() {
                @Override
                public void done(User object, ParseException e) {
                    if(e != null) {
                        showWelcomePage();
                    }
                }

            });
            return true;
        } else {
            return false;
        }
    }

    public void onRegister(View view) {
        startActivity(new Intent(this, RegisterUserActivity.class));
    }

    public void onLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

}
