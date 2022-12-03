package com.iacademy.e_wallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iacademy.e_wallet.R;
import com.iacademy.e_wallet.models.WalletModel;

import java.util.ArrayList;

public class CashInActivity extends AppCompatActivity {

    private EditText etAmount;
    private TextView tvCurrentBalance;
    private Button bConfirmCash;
    private ImageButton ibHome, ibProfile, ibLogout;

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference senderReference;
    private String senderNumber;
    private double senderBalance;

    //SEND SMS
    private static final int PERMISSIONS_REQUEST_SMS_SEND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        etAmount = findViewById(R.id.etAmount);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        bConfirmCash = findViewById(R.id.bConfirmCash);
        ibHome = findViewById(R.id.ibHome);
        ibProfile = findViewById(R.id.ibProfile);
        ibLogout = findViewById(R.id.ibLogout);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        double amountToSend, senderTotal;

        initializeContent();
        initializeButtons();
    }

    /*******************
     * REQUEST PERMISSION
     *******************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {super
            .onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSIONS_REQUEST_SMS_SEND:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                    double amountToSend =0, senderTotal=0;
                    sendSMS(amountToSend, senderTotal);
                }
                break;
            default:
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); finish();
                break;
        }
    }

    private void initializeContent() {

        senderReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalletModel data = snapshot.getValue(WalletModel.class);
                senderBalance = data.getBalance();
                senderNumber = data.getNumber();
                tvCurrentBalance.setText(String.valueOf(senderBalance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendSMS(double amountToSend, double senderTotal) {
        //send sms
        SmsManager sms = SmsManager.getDefault();
        String senderMessage, receiverMessage;

        //SEND MESSAGE
        senderMessage = "You have DEPOSITED PHP " + amountToSend + " of PKash to your account. "
                + "Your new balance is PHP " + senderTotal;

        //SEND TEXT MESSAGE DEPENDING ON MESSAGE LENGTH
        ArrayList<String> messagelist = sms.divideMessage(senderMessage);
        sms.sendTextMessage(senderNumber, null, senderMessage, null, null);
    }

    private void initializeButtons() {

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CashInActivity.this, DashboardActivity.class));
                finish();
            }
        });

        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CashInActivity.this, ProfileActivity.class));
                finish();
            }
        });

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(CashInActivity.this, LoginActivity.class));
                finish();
            }
        });

        bConfirmCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amountToSend = Double.parseDouble(etAmount.getText().toString());

                if (amountToSend < 0) {
                    Toast.makeText(getApplicationContext(), "Negative value is not allowed", Toast.LENGTH_SHORT).show();
                } else {
                    double senderTotal = senderBalance + amountToSend;

                    //send money
                    WalletModel.depositMoney(amountToSend, senderTotal, mAuth);

                    //send SMS
                    if(ContextCompat.checkSelfPermission(CashInActivity.this, Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED){
                        sendSMS(amountToSend, senderTotal);
                    } else{
                        ActivityCompat.requestPermissions(CashInActivity.this, new String[]{Manifest
                                .permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS_SEND);
                    }




                    //start activity
                    startActivity(new Intent(CashInActivity.this, LoadScreenActivity.class));
                    finish();
                }

            }
        });
    }
}