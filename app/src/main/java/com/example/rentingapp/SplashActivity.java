package com.example.rentingapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class is in charge of showing the Splash Activity when the usser opens the app.
 */
public class SplashActivity extends AppCompatActivity
{
    private ImageView ivLogo;
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get references from view.
        ivLogo = findViewById(R.id.ivLogo);

        //Makes a rotation animation to the logo.
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(ivLogo, "rotation", 0f, 360f);
        //1 second of duration.
        rotateAnimation.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimation);
        animatorSet.start();
        //After the animation, goes to the Login Activity.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(homeIntent);
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}
