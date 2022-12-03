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

public class SendMoneyActivity extends AppCompatActivity {

    //declare layout variables
    private EditText etNumber, etAmount;
    private TextView tvAvailBalance, tvSendName;
    private Button bSendMoney;
    private ImageButton ibHome, ibProfile, ibLogout;

    //barcode/receiver variable
    private String barcodeValue, receiverName, receiverNumber;
    private double receiverBalance;

    //sender variables
    private String senderName, senderNumber;
    private double senderBalance;

    //SEND SMS
    private static final int PERMISSIONS_REQUEST_SMS_SEND = 1;

    double amountToSend, receiverTotal, senderTotal;

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference receiverReference, senderReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        //initialize variables
        etNumber = findViewById(R.id.etNumber);
        etAmount = findViewById(R.id.etAmount);
        tvAvailBalance = findViewById(R.id.tvAvailBalance);
        tvSendName = findViewById(R.id.tvSendName);
        bSendMoney = findViewById(R.id.bSendMoney);
        ibHome = findViewById(R.id.ibHome);
        ibProfile = findViewById(R.id.ibProfile);
        ibLogout = findViewById(R.id.ibLogout);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();

        //get barcode value
        Bundle intent_data = getIntent().getExtras();
        if (intent_data != null) {
            barcodeValue = intent_data.getString("barcode");
        }

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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeButtons();
                } else {
                    // Permission Denied
                }
                break;
            default:
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); finish();
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void initializeContent() {

        //GET RECEIVER INFORMATION
        receiverReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(barcodeValue).child("WalletDetails");
        receiverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                WalletModel data = snapshot.getValue(WalletModel.class);
                receiverName = data.getName();
                receiverNumber = data.getNumber();
                receiverBalance = data.getBalance();

                //set layout value
                etNumber.setText(receiverNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failure to read data", Toast.LENGTH_SHORT).show();
            }
        });

        //GET SENDER INFORMATION
        senderReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                WalletModel data = snapshot.getValue(WalletModel.class);
                senderName = data.getName();
                senderNumber = data.getNumber();
                senderBalance = data.getBalance();

                //set layout value
                tvAvailBalance.setText(String.valueOf(senderBalance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failure to read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendSMS() {
        //send sms
        SmsManager sms = SmsManager.getDefault();
        String senderMessage, receiverMessage;

        //SEND MESSAGE
        senderMessage = "You have SENT PHP " + amountToSend + " of PKash to " + receiverName
                + " (" + receiverNumber + "). Your new balance is PHP " + senderTotal;
        receiverMessage = "You have RECEIVED PHP " + amountToSend + " of PKash from " + senderName
                + " (" + senderNumber + "). Your new balance is PHP " + receiverTotal;

        //SEND TEXT MESSAGE DEPENDING ON MESSAGE LENGTH
        ArrayList<String> messagelist = sms.divideMessage(senderMessage);
        sms.sendMultipartTextMessage(senderNumber, null, messagelist, null, null);
        messagelist = sms.divideMessage(receiverMessage);
        sms.sendMultipartTextMessage(receiverNumber, null, messagelist, null, null);
    }

    private void initializeButtons() {

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendMoneyActivity.this, DashboardActivity.class));
                finish();
            }
        });

        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendMoneyActivity.this, ProfileActivity.class));
                finish();
            }
        });

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(SendMoneyActivity.this, LoginActivity.class));
                finish();
            }
        });

        bSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INITIALIZE COMPUTATIONS
                amountToSend = Double.parseDouble(etAmount.getText().toString());
                receiverTotal = receiverBalance + amountToSend;
                senderTotal = senderBalance - amountToSend;

                if (amountToSend >  senderBalance)
                    Toast.makeText(getApplicationContext(), "Insufficient Fund.", Toast.LENGTH_SHORT).show();
                else if (amountToSend < 0)
                    Toast.makeText(getApplicationContext(), "Negative value is not allowed", Toast.LENGTH_SHORT).show();
                else if (amountToSend <=  senderBalance) {

                    //send money
                    WalletModel.sendMoney(
                            amountToSend, receiverTotal, senderTotal,
                            receiverName, receiverNumber,
                            senderName, senderNumber,
                            barcodeValue, mAuth);

                    //send SMS
                    boolean loop = true;
                    while (loop) {
                        if(ContextCompat.checkSelfPermission(SendMoneyActivity.this, Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_GRANTED){
                            sendSMS();
                            break;
                        } else{
                            ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{Manifest
                                    .permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS_SEND);
                        }
                    }

                    startActivity(new Intent(SendMoneyActivity.this, LoadScreenActivity.class));
                    finish();

                }

            }
        });
    }

}