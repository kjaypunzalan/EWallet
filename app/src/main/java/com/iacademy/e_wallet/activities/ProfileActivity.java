package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.iacademy.e_wallet.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileNumber, tvProfileEmail, tvProfilePassword, tvLogout;
    private ImageButton ibHome, ibProfile;
    private ImageView ivProfile;

    private FirebaseAuth mAuth;

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
        tvLogout = findViewById(R.id.tvLogout);


        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}