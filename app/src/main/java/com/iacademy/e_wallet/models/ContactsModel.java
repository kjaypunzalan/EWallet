package com.iacademy.e_wallet.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iacademy.e_wallet.adapters.ContactsAdapter;
import com.iacademy.e_wallet.utils.RecyclerOnItemClickListener;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ContactsModel {

    //user information
    private String name, email, number, barCodeURL;
    private double balance;


    //constructor
    public ContactsModel(){};
    //create user constructor
    public ContactsModel(String name, String email, String number, String barCodeURL, double balance) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.balance = balance;
        this.barCodeURL = barCodeURL;
    }

    //setters and getters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getBarCodeURL() { return barCodeURL; }
    public void setBarCodeURL(String barCodeURL) { this.barCodeURL = barCodeURL; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }



    /**************************
     * A. read from firebase
     *------------------------*/
    public static void readFromFile(
            ArrayList<ContactsModel> listModels,
            ArrayList<String> keys,
            RecyclerView rvList,
            Context context,
            DatabaseReference mReference,
            FirebaseAuth mAuth,
            RecyclerOnItemClickListener item) {


        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    listModels.add(data.getValue(ContactsModel.class));
                    keys.add(data.getKey());
                }

                ContactsAdapter contactsAdapter = new ContactsAdapter(listModels, context);
                contactsAdapter.notifyDataSetChanged();
                rvList.setAdapter(new ContactsAdapter(listModels, context, item));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "FAILURE TO READ DATABASE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**************************
     * B. write to firebase
     *------------------------*/
    public static void writeNewUser(
            String name,
            String email,
            String number,
            double balance,
            Context context,
            FirebaseAuth mAuth) {

        /*********************
         * NOTES
         * DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
         * StorageReference storageRef = FirebaseStorage.getInstance().getReference();
         *
         */
        //FIREBASE INSTANTIATION
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();       //firebase instance
        final DatabaseReference reference = firebaseDB.getReference();      //firebase reference

        //REFERENCE USER AND CONTACT
        DatabaseReference referenceUser = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid());  //user uid
        DatabaseReference referenceContact = referenceUser.child("WalletDetails");                                          //push contact key

        /*********************
         * QR CODE!!!!!!!
         *********************/
        //initializing MultiFormatWriter for QR code
        String barcodeValue = mAuth.getCurrentUser().getUid();
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            //INITIALIZE FIREBASE STORAGE
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference barcodeRef = storageRef.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("image - " + number + ".jpg");

            //ENCODE AND GET BITMAP
            BitMatrix mMatrix = mWriter.encode(barcodeValue, BarcodeFormat.QR_CODE, 400,400); //encode user id!!!
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap bitmap = mEncoder.createBitmap(mMatrix); //creating bitmap of code

            //COMPRESS IMAGE BITMAP
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //UPLOAD TO FIREBASE
            UploadTask uploadTask = barcodeRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    barcodeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //PUT TO REALTIME DATABASE
                            String barCodeReference = uri.toString();
                            DatabaseReference referenceImage = referenceUser.child(referenceContact.getKey()).child("imageURL");
                            referenceImage.setValue(barCodeReference);

                            //POPULATE DATA
                            ContactsModel contactInfo = new ContactsModel(name, email, number, barCodeReference, balance);
                            referenceContact.setValue(contactInfo);

                            //SEND TOAST
                            Toast.makeText(context, "SUCCESSFULLY REGISTERED USER", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(context, "ERROR: DID NOT CREATE USER", Toast.LENGTH_SHORT).show();
        }
    }

    /**************************
     * C. edit to firebase
     *------------------------*/
    public static void editProfile(String name,
                                   String email,
                                   String number,
                                   FirebaseAuth mAuth) {

        HashMap hashMap = new HashMap();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("number", number);

        /*********************
         * NOTES
         * DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
         * StorageReference storageRef = FirebaseStorage.getInstance().getReference();
         *
         */
        //FIREBASE INSTANTIATION
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();       //firebase instance
        final DatabaseReference reference = firebaseDB.getReference();      //firebase reference

        //REFERENCE USER
        DatabaseReference referenceUser = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        referenceUser.updateChildren(hashMap);  //updateInformation
    }

    /**************************
     * D. send money / update firebase
     *------------------------*/
    public static void sendMoney(
            double amountSent,
            double receiverNewBalance,
            double senderNewBalance,
            String receiverName,
            String receiverNumber,
            String senderName,
            String senderNumber,
            String barcodeValue,
            FirebaseAuth mAuth,
            boolean isSender) {

        //FIREBASE INSTANTIATION
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();       //firebase instance
        final DatabaseReference reference = firebaseDB.getReference();      //firebase reference

        /*********************
         * HASHMAP UPDATES
         *-------------------*/
        HashMap receiverHashMap = new HashMap();
        receiverHashMap.put("balance", receiverNewBalance);

        HashMap senderHashMap = new HashMap();
        senderHashMap.put("balance", senderNewBalance);


        /*********************
         * RECEIVER UPDATE
         *-------------------*/
        //REFERENCE USER AND CONTACT
        DatabaseReference receiverReference = reference.child("PKash").child("Users").child(barcodeValue).child("WalletDetails");
        receiverReference.updateChildren(receiverHashMap);  //updateInformation

        /*********************
         * SENDER UPDATE
         *-------------------*/
        //POPULATE DATA
        DatabaseReference senderReference = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        senderReference.updateChildren(senderHashMap);  //updateInformation



        /*******************************
         * TRANSACTION HISTORY
         *-----------------------------*/
        //REFERENCE USER AND CONTACT
        DatabaseReference receiverTransactionRef = reference.child("PKash").child("Users").child(barcodeValue).child("TransactionHistory").push();
        DatabaseReference senderTransactionRef = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("TransactionHistory").push();
        Date currentTime = Calendar.getInstance().getTime();
        String timeAndDate = String.valueOf(currentTime);

        if(isSender == true) {
            //you have sent PHP AMOUNTSENT to RECEIVERNAME (RECEIVERNUMBER) on DATAANDTIME.
            //your new balance is PHP SENDERBALANCE
            TransactionModel addHistory = new TransactionModel(amountSent, receiverName, receiverNumber, timeAndDate, senderNewBalance);
            senderTransactionRef.child("amountSent").setValue(addHistory.getAmountSent());
            senderTransactionRef.child("receiverName").setValue(addHistory.getReceiverName());
            senderTransactionRef.child("receiverNumber").setValue(addHistory.getReceiverNumber());
            senderTransactionRef.child("timeAndDate").setValue(addHistory.getTimeAndDate());
            senderTransactionRef.child("senderNewBalance").setValue(addHistory.getSenderNewBalance());
            senderTransactionRef.child("transactionType").setValue("SENT MONEY");
        } else {
            //you have received PHP AMOUNTSENT from SENDERNAME (SENDERNUMBER) on DATAANDTIME.
            //your new balance is PHP RECEIVERBALANCE
            TransactionModel addHistory = new TransactionModel(senderName, senderNumber, timeAndDate, receiverNewBalance, amountSent);
            receiverTransactionRef.child("senderName").setValue(addHistory.getSenderName());
            receiverTransactionRef.child("senderNumber").setValue(addHistory.getSenderNumber());
            receiverTransactionRef.child("timeAndDate").setValue(addHistory.getTimeAndDate());
            receiverTransactionRef.child("receiverNewBalance").setValue(addHistory.getReceiverNewBalance());
            receiverTransactionRef.child("amountReceived").setValue(addHistory.getAmountReceived());
            receiverTransactionRef.child("transactionType").setValue("RECEIVED MONEY");
        }

    }



    /**************************
     * E. deposit / update firebase
     *------------------------*/
    public static void depositMoney(double amountSent, double senderTotal, FirebaseAuth mAuth) {

        //FIREBASE INSTANTIATION
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();       //firebase instance
        final DatabaseReference reference = firebaseDB.getReference();      //firebase reference

        /*********************
         * HASHMAP UPDATES
         *-------------------*/
        HashMap senderHashMap = new HashMap();
        senderHashMap.put("balance", senderTotal);

        /*********************
         * SENDER UPDATE
         *-------------------*/
        //POPULATE DATA
        DatabaseReference senderReference = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("WalletDetails");
        senderReference.updateChildren(senderHashMap);  //updateInformation


        /*******************************
         * TRANSACTION HISTORY
         *-----------------------------*/
        //REFERENCE USER AND CONTACT
        DatabaseReference senderTransactionRef = reference.child("PKash").child("Users").child(mAuth.getCurrentUser().getUid()).child("TransactionHistory").push();  //user uid
        Date currentTime = Calendar.getInstance().getTime();
        String timeAndDate = String.valueOf(currentTime);


        //you have deposited PHP AMOUNTSENT on DATAANDTIME
        //your new balance is PHP SENDERBALANCE
        TransactionModel addHistory = new TransactionModel(amountSent, timeAndDate, senderTotal);
        senderTransactionRef.child("amountSent").setValue(addHistory.getAmountSent());
        senderTransactionRef.child("timeAndDate").setValue(addHistory.getTimeAndDate());
        senderTransactionRef.child("senderNewBalance").setValue(addHistory.getSenderNewBalance());
        senderTransactionRef.child("transactionType").setValue("DEPOSIT");
    }

}
