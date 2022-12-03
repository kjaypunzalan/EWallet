package com.iacademy.e_wallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iacademy.e_wallet.R;
import com.iacademy.e_wallet.models.WalletModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegEmail, etRegPassword, etPhoneNumber, etName;
    private ImageButton btnRegister;
    private TextView tvLogin;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instantiate variables
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etName = findViewById(R.id.etName);
        tvLogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void createUser(){
        String name = etName.getText().toString();
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();
        String number = etPhoneNumber.getText().toString();
        Double balance = 0.00;

        if(TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }else if(TextUtils.isEmpty(number)){
            etRegPassword.setError("Phone Number cannot be empty");
            etRegPassword.requestFocus();
        }else if(!number.matches("^[0-9]{11}$")){
            etRegPassword.setError("Number should be 11 numerical digits.");
            etRegPassword.requestFocus();
        }else if(TextUtils.isEmpty(name)){
            etRegPassword.setError("Name cannot be empty");
            etRegPassword.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                    (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (currentUser.isEmailVerified()){
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else{
                                    currentUser.sendEmailVerification();
                                    Toast.makeText(RegisterActivity.this, "Please check your email to " +
                                            "verify your account", Toast.LENGTH_SHORT).show();
                                    //WRITE CONTACT TO FILE
                                    WalletModel.writeNewUser(name, email, number, balance, getApplicationContext(), mAuth);
                                }
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else{
                                Toast.makeText(RegisterActivity.this, "Registration Error: " + task
                                        .getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

}