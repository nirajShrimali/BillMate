package com.example.billmate;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ScannerFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private DecoratedBarcodeView barcodeScannerView;
    private Button manualEntryButton;
    private TextView scannerHint;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        barcodeScannerView = view.findViewById(R.id.barcode_scanner);
        manualEntryButton = view.findViewById(R.id.btn_manual_entry);
        scannerHint = view.findViewById(R.id.tv_scanner_hint);
        auth = FirebaseAuth.getInstance();

        manualEntryButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Manual Entry Coming Soon!", Toast.LENGTH_SHORT).show()
        );

        checkCameraPermission();
        return view;
    }

    private void setupBarcodeScanner() {
        List<com.google.zxing.BarcodeFormat> formats = Arrays.asList(
                com.google.zxing.BarcodeFormat.CODE_128,
                com.google.zxing.BarcodeFormat.QR_CODE,
                com.google.zxing.BarcodeFormat.EAN_13
        );
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    String scannedCode = result.getText();
                    barcodeScannerView.pause(); // Pause scanning to avoid multiple triggers
                    fetchProductDetails(scannedCode);
                }
            }

            @Override
            public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {
                // Optional: Visual feedback
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            barcodeScannerView.resume();
            setupBarcodeScanner();
        }
    }

    private void fetchProductDetails(String barcode) {
        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        // Your Volley network request code here
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeScannerView.resume();
                setupBarcodeScanner();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to scan barcodes.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @OptIn(markerClass = ExperimentalGetImage.class)


    // Helper method to get the correct rotation for any API level
    private int getRotationDegrees(ImageProxy imageProxy) {
        // For devices with API >= 30, use this method
        return imageProxy.getImageInfo().getRotationDegrees();
    }

    private void addBillToDatabase(String personName) {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference billsRef = database.getReference("users").child(uid).child("bills");

        // Generate sample bill
        String billId = billsRef.push().getKey();
        Bill bill = new Bill(billId, personName, "Total Amount: $123"); // Create a Bill class

        billsRef.child(billId).setValue(bill)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Bill added for " + personName, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add bill.", Toast.LENGTH_SHORT).show()
                );
    }
}
