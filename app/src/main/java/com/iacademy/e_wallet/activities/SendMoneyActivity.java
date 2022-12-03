package com.iacademy.e_wallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class SendMoneyActivity extends AppCompatActivity {

    //declare layout variables
    private EditText etNumber, etAmount;
    private TextView tvAvailBalance, tvSendName;
    private Button bSendMoney;
    private ImageButton ibLogout;

    //barcode/receiver variable
    private String barcodeValue, receiverName, receiverNumber;
    private double receiverBalance;

    //sender variables
    private String senderName, senderNumber;
    private double senderBalance;

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

    private void initializeButtons() {

        bSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amountToSend = Double.parseDouble(etAmount.getText().toString());
                double receiverTotal = receiverBalance + amountToSend;
                double senderTotal = senderBalance - amountToSend;

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

                    //start activity
                    startActivity(new Intent(SendMoneyActivity.this, LoadScreenActivity.class));
                    finish();
                }

            }
        });
    }

}