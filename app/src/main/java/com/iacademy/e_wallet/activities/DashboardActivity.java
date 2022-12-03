package com.iacademy.e_wallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iacademy.e_wallet.R;
import com.iacademy.e_wallet.models.ContactsModel;
import com.iacademy.e_wallet.utils.BarcodeScanner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    //declare variables
    private TextView tvHomeName, tvBalance;
    private ImageButton ibCashin, ibScan, ibHome, ibProfile;
    private ImageView ivQR;

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //initialize variables
        tvBalance = findViewById(R.id.tvBalance);
        tvHomeName = findViewById(R.id.tvHomeName);
        ibCashin= findViewById(R.id.ibCashin);
        ivQR= findViewById(R.id.ivQR);
        ibScan= findViewById(R.id.ibScan);
        ibHome= findViewById(R.id.ibHome);
        ibProfile= findViewById(R.id.ibProfile);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });

        onStart();
        initializeContent();
        initializeButtons();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void initializeContent() {

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContactsModel data = snapshot.getValue(ContactsModel.class);
                double currentBalance = data.getBalance();
                tvBalance.setText(String.valueOf(currentBalance));
                tvHomeName.setText(data.getName());
                Picasso.get().load(data.getBarCodeURL()).into(ivQR);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeButtons() {
        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                finish();
            }
        });

        ibScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, BarcodeScanner.class));
                finish();
            }
        });

        ibCashin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, CashInActivity.class));
                finish();
            }
        });
    }
}