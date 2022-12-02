package com.iacademy.e_wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iacademy.e_wallet.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvHomeName, tvBalance;
    private ImageButton ibCashin, ibScan, ibHome, ibProfile;
    private ImageView ivQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvBalance = findViewById(R.id.tvBalance);
        tvHomeName = findViewById(R.id.tvHomeName);
        ibCashin= findViewById(R.id.ibCashin);
        ivQR= findViewById(R.id.ivQR);
        ibScan= findViewById(R.id.ibScan);
        ibHome= findViewById(R.id.ibHome);
        ibProfile= findViewById(R.id.ibProfile);

    }
}