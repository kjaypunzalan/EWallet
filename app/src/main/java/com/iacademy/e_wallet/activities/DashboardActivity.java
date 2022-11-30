package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iacademy.e_wallet.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvHomeUsername, tvBalance;
    private ImageButton ibCashin, ibQR, ibScan, ibHome, ibProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvBalance = findViewById(R.id.tvBalance);
        tvHomeUsername = findViewById(R.id.tvHomeUsername);
        ibCashin= findViewById(R.id.ibCashin);
        ibQR= findViewById(R.id.ibQR);
        ibScan= findViewById(R.id.ibScan);
        ibHome= findViewById(R.id.ibHome);
        ibProfile= findViewById(R.id.ibProfile);

    }
}