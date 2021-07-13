package com.example.rentingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class CreateItemActivity extends AppCompatActivity {
    EditText etItemName, etItemDescription;
    Button btnCancel, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        etItemName = findViewById(R.id.etItemName);
        etItemDescription = findViewById(R.id.etDescription);
        btnCancel = findViewById(R.id.btnCancel);
        btnCreate = findViewById(R.id.btnCreate);

    }
}