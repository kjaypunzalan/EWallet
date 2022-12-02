package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iacademy.e_wallet.R;

public class CashInActivity extends AppCompatActivity {

    private EditText etAmount;
    private TextView tvCurrentBalance;
    private Button bConfirmCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        etAmount = findViewById(R.id.etAmount);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        bConfirmCash = findViewById(R.id.bConfirmCash);
    }
}