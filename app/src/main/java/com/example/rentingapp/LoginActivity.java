package com.example.rentingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.rentingapp.Controllers.ActionsController.validateField;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.loadingDialog;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.successDialog;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    TextInputLayout tilUsername, tilPassword;
    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvSignUp;
    //LoadingDialog loadingDialog;
    SweetAlertDialog loadingDialog, successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Get fields from view
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        loadingDialog = loadingDialog(LoginActivity.this);
        successDialog = successDialog(LoginActivity.this, "Logged In Successfully!");


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if all fields are filled
                int count = 0;
                if(validateField(tilUsername, etUsername) )
                    count++;
                if(validateField(tilPassword, etPassword))
                    count++;
                if (count == 2) //if all the fields are filled..
                    loginUser(etUsername.getText().toString(), etPassword.getText().toString());
                else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please verify that are the fields are filled")
                            .show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignUpActivity();
            }
        });
    }

    /**
     * This function is responsible for starting a user session by implementing Parse.
     * @param username username
     * @param password password
     */
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        loadingDialog.show();
        //Navigate to the main activity if the user has signed in properly
        //try to log in using a different thread from the main one.
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    return;
                }
                loadingDialog.dismiss();
                successDialog.show();
                successDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        goMainActivity();
                    }
                });
                //Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Creates an intent that goes to the Main Activity
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Creates an intent that goes to the Sign Up Activity
     */
    private void goSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}