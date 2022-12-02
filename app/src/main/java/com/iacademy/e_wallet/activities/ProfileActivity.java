package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.iacademy.e_wallet.R;

public class ProfileActivity extends AppCompatActivity {

    private EditText etProfileName, etProfileNumber, etProfileEmail, etNewPassword, etConfirmPassword;
    private TextView tvLogout, tvEditProfile;
    private ImageButton ibHome, ibProfile;
    private ImageView ivProfile;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfileNumber = findViewById(R.id.etProfileNumber);
        etProfileName = findViewById(R.id.etProfileName);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
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