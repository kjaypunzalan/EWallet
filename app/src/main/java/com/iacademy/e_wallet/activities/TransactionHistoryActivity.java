package com.iacademy.e_wallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iacademy.e_wallet.adapters.TransactionAdapter;
import com.iacademy.e_wallet.models.TransactionModel;
import com.iacademy.e_wallet.models.WalletModel;
import com.iacademy.e_wallet.utils.RecyclerOnItemClickListener;
import com.iacademy.e_wallet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity implements RecyclerOnItemClickListener {

    //DECLARE VARIABLES
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private TransactionModel contactInfo;
    private RecyclerView rvTransactionList;
    private ArrayList<TransactionModel> listModels = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();

    private ImageButton ibHome, ibProfile;
    private String name;
    private String email;
    private String number;

    private RecyclerOnItemClickListener roicl;


    //FIREBASE VARIABLES
    private FirebaseAuth mAuth;                     //authorization
    private DatabaseReference mReference;   //realtime database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        //instantiate variables
        rvTransactionList = findViewById(R.id.rv_transactionHistory);
        ibHome= findViewById(R.id.ibHome);
        ibProfile= findViewById(R.id.ibProfile);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        //Recycler View logic
        roicl = this::onItemClick;
        rvTransactionList.setLayoutManager(new LinearLayoutManager(TransactionHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
        rvTransactionList.setAdapter(new TransactionAdapter(listModels, TransactionHistoryActivity.this, roicl)); //set adaptor that provide child views

        //call void methods
        initList();
        initializeButtons();
    }

    /***************************
     * A. INITIALIZE DATA
     *------------------------*/
    private void initList() {

        mReference = FirebaseDatabase.getInstance().getReference().child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("TransactionHistory");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    listModels.add(data.getValue(TransactionModel.class));
                    keys.add(data.getKey());
                }

                TransactionAdapter contactsAdapter = new TransactionAdapter(listModels, getApplicationContext());
                contactsAdapter.notifyDataSetChanged();
                rvTransactionList.setAdapter(new TransactionAdapter(listModels, getApplicationContext(), roicl));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "FAILURE TO READ DATABASE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeButtons() {

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionHistoryActivity.this, DashboardActivity.class));
                finish();
            }
        });

        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionHistoryActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onItemClick(View childView, int position) {

    }
}