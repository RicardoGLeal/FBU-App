package com.example.rentingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

public class FullSizeImageActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String photoUrl = getIntent().getStringExtra("photoUrl");

        setContentView(R.layout.activity_full_size_image);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        Glide.with(getApplicationContext())
                .load(photoUrl != null ? photoUrl : R.drawable.profile_image_empty)
                .placeholder(R.drawable.profile_image_empty)
                .into(ivProfileImage);
    }
}