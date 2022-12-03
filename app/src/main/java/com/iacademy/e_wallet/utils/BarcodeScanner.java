package com.iacademy.e_wallet.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.iacademy.e_wallet.R;
import com.iacademy.e_wallet.activities.SendMoneyActivity;

import java.io.IOException;

public class BarcodeScanner extends AppCompatActivity {

    //barcode
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;

    //DATA
    TextView txtBarcodeValue;
    String barcode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        //CameraView
        txtBarcodeValue = findViewById(R.id.tv_txtBarcodeValue);
        txtBarcodeValue.setVisibility(View.GONE);
        surfaceView = findViewById(R.id.surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {

        //START SCANNER INITIALIZATION
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        //GET PERMISSION
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeScanner.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //START BARCODE DETECTOR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {              /****** VALUE *******/
                        @Override
                        public void run() {
                            //GET BARCODE VALUE
                            barcode = barcodes.valueAt(0).displayValue;

                            //GO TO SENDMONEY
                            Intent intent = new Intent(BarcodeScanner.this, SendMoneyActivity.class);
                            intent.putExtra("barcode", barcode);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}