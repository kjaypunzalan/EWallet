package com.iacademy.e_wallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private EditText etProfileName, etProfileNumber, etProfileEmail, etNewPassword, etConfirmPassword;
    private TextView tvLogout, tvEditProfile;
    private ImageButton ibHome, ibProfile, ibLogout;
    private ImageView ivProfile;

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfileNumber = findViewById(R.id.etProfileNumber);
        etProfileName = findViewById(R.id.etProfileName);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        ibHome = findViewById(R.id.ibHome);
        ibProfile = findViewById(R.id.ibProfile);
        ibLogout = findViewById(R.id.ibLogout);
        ivProfile = findViewById(R.id.ivProfile);


        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        initializeContent();
        initializeButtons();
        editProfile();

    }
    private void initializeContent() {

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalletModel data = snapshot.getValue(WalletModel.class);
                etProfileName.setText(data.getName());
                etProfileNumber.setText(data.getNumber());
                etProfileEmail.setText(data.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeButtons() {

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
            }
        });

        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    private void editProfile() {
        //EDIT BUTTON
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etProfileName.getText().toString();
                String email = etProfileEmail.getText().toString();
                String number = etProfileNumber.getText().toString();
                String password = etNewPassword.getText().toString();

                //A. Empty Validation
                if (name.equals(""))
                    etProfileName.setError("Name is required.");
                if (email.equals(""))
                    etProfileName.setError("Email is required.");
                if (number.equals(""))
                    etProfileNumber.setError("Number is required.");

                //B. Validate Name
                if (name.length() > 50)
                    etProfileName.setError("Name should not exceed 50 characters.");
                if (name.length() < 3)
                    etProfileName.setError("Name should not be less than 3 characters.");
                if (!name.matches("^([^0-9]*)$"))
                    etProfileName.setError("Name should not contain numbers.");

                //C. Validate Number
                if (!number.matches("^[0-9]{11}$"))
                    etProfileNumber.setError("Number should be 11 numerical digits.");

                //D. Validate Email
                if (email.equals(""))
                    etProfileName.setError("Email is required.");

                /**************************
                 * E. edit to file
                 *------------------------*/
                if (number.matches("^[0-9]{11}$") && name.matches("^([^0-9]*)$")) {

                    //show popup
                    Toast.makeText(getApplicationContext(), "Successfully edited contact. Please wait for refresh.", Toast.LENGTH_SHORT).show();

                    //write to file
                    WalletModel.editProfile(name, email, number, mAuth);
                    startActivity(new Intent(ProfileActivity.this, LoadScreenActivity.class));
                    finish();
                }
            }
        });
    }
}