package com.example.rentingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateItemActivity extends AppCompatActivity {
    EditText etItemName, etItemDescription, etPrice;
    Button btnCancel, btnCreate;
    Spinner spinnerCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        etItemName = findViewById(R.id.etItemName);
        etItemDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnCancel = findViewById(R.id.btnCancel);
        btnCreate = findViewById(R.id.btnCreate);

        spinnerCategories = findViewById(R.id.spinnerCategories);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(CreateItemActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateItem();
            }
        });
    }

    private void CreateItem() {
        String itemName = etItemName.getText().toString();
        String itemDescription = etItemDescription.getText().toString();
        String itemCategory = spinnerCategories.getSelectedItem().toString();
        Float itemPrice =  Float.valueOf(etPrice.getText().toString());




    }
}