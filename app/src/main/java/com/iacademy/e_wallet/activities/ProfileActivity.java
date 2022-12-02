package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iacademy.e_wallet.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileNumber, tvProfileEmail, tvProfilePassword;
    private ImageButton ibHome, ibProfile;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileNumber = findViewById(R.id.tvProfileNumber);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfilePassword = findViewById(R.id.tvProfilePassword);
        ibHome = findViewById(R.id.ibHome);
        ibProfile = findViewById(R.id.ibProfile);
        ivProfile = findViewById(R.id.ivProfile);

    }
}