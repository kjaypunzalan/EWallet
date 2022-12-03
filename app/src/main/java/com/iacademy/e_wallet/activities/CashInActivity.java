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

public class CashInActivity extends AppCompatActivity {

    private EditText etAmount;
    private TextView tvCurrentBalance;
    private Button bConfirmCash;
    private ImageButton ibLogout;

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference senderReference;
    private double senderBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        etAmount = findViewById(R.id.etAmount);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        bConfirmCash = findViewById(R.id.bConfirmCash);
        ibLogout = findViewById(R.id.ibLogout);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();

        initializeContent();
        initializeButtons();
    }

    private void initializeContent() {

        senderReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalletModel data = snapshot.getValue(WalletModel.class);
                senderBalance = data.getBalance();
                tvCurrentBalance.setText(String.valueOf(senderBalance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeButtons() {

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

                    //start activity
                    startActivity(new Intent(CashInActivity.this, LoadScreenActivity.class));
                    finish();
                }

            }
        });
    }
}