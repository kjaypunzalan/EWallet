package com.iacademy.e_wallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iacademy.e_wallet.R;

public class SendMoneyActivity extends AppCompatActivity {

    private EditText etNumber, etAmount;
    private TextView tvAvailBalance;
    private Button bSendMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        etNumber = findViewById(R.id.etNumber);
        etAmount = findViewById(R.id.etAmount);
        tvAvailBalance = findViewById(R.id.tvAvailBalance);
        bSendMoney = findViewById(R.id.bSendMoney);
    }
}