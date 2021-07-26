package com.example.rentingapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.rentingapp.MainActivity;
import com.example.rentingapp.R;

public class SendSmsDialogFragment extends DialogFragment {

    public static final String TAG = "SendSmsDialogFragment";
    EditText etMessage;
    Button btnSend;
    String cellphone;

    /**
     * Method used to instantiate this fragment with a cellphone number received
     * @return ComposeFragment
     */
    public static SendSmsDialogFragment newInstance(String cellphone) {
        SendSmsDialogFragment frag = new SendSmsDialogFragment();
        Bundle args = new Bundle();
        args.putString("cellphone", cellphone);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Inflates the layout.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_sms, container);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get fields from view
        cellphone = getArguments().getString("cellphone");
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check condition
                if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    //When permission is granted
                    //Create method
                    sendMessage();
                } else {
                    //When permission is not granted
                    //Request permission
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 100);
                }
            }
        });
    }
    private void sendMessage() {
        //Get values from edit text
        String sMessage = etMessage.getText().toString().trim();
        if(!cellphone.equals("") && !sMessage.equals("")){
            //When both edit text value not equal to blank
            //Initialize sms manager
            SmsManager smsManager = SmsManager.getDefault();
            //Send text message
            smsManager.sendTextMessage(cellphone, null, sMessage, null, null);
            //Display toast
            Toast.makeText(getContext(), "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        }
        else {
            //When edit text value is blank
            //Display toast
            Toast.makeText(getContext(), "Enter value first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //When permission is granted
            //Call method
            sendMessage();
        } else {
            //When permission is denied
            //Display toast
            Toast.makeText(getContext(), "Permission DENIED!", Toast.LENGTH_SHORT).show();
        }
    }
}

