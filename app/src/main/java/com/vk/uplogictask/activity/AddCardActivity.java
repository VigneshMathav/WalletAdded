package com.vk.uplogictask.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vk.uplogictask.R;
import com.vk.uplogictask.databinding.ActivityAddCardBinding;
import com.vk.uplogictask.helper.ProgressDisplay;
import com.vk.uplogictask.helper.Session;
import com.vk.uplogictask.helper.Validation;
import com.vk.uplogictask.model.AmountDetails;
import com.vk.uplogictask.model.CardDetails;

import java.util.ArrayList;
import java.util.List;

public class AddCardActivity extends AppCompatActivity {
    ActivityAddCardBinding binding;
    ProgressDisplay progressDisplay;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDisplay = new ProgressDisplay(AddCardActivity.this);
        session = new Session(AddCardActivity.this);

        binding.lytBack.setOnClickListener(v -> finish());

        binding.btnAddCard.setOnClickListener(v -> addCardData());

        binding.txtScanCard.setOnClickListener(v -> openScanner());
    }

    private void openScanner()
    {
        String amount = binding.edtAmount.getText().toString();
        if (!amount.isEmpty())
        {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        }
        else
        {
            Toast.makeText(this, "Enter amount to open scanner!", Toast.LENGTH_SHORT).show();

        }
    }

    private void generateQRCode()
    {
        String userId = session.getData(Session.USER_ID);
        Bitmap bitmap = qRCode(userId);

        binding.ivScan.setImageBitmap(bitmap);
    }

    private void addCardData()
    {
        String userId = session.getData(Session.USER_ID);
        String cardNumber = binding.edtCardNumber.getText().toString();
        String expiryDate = binding.edtExpireDate.getText().toString();
        String cardHolder = binding.edtCardHolder.getText().toString();
        String cvv = binding.edtCvv.getText().toString();

        if (!cardNumber.isEmpty() && !expiryDate.isEmpty() && !cardHolder.isEmpty() && !cvv.isEmpty())
        {
            if (Validation.isValidExpiryDate(expiryDate))
            {
                progressDisplay.showProgress();
                CardDetails cardDetails = new CardDetails();
                cardDetails.uploadCardDetails(userId, cardNumber, expiryDate, cardHolder, cvv);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CardDetails");

                databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String cardId = childSnapshot.getKey();
                                assert cardId != null;

                                databaseReference.child(cardId).setValue(cardDetails)
                                        .addOnSuccessListener(unused -> {
                                            progressDisplay.hideProgress();
                                            getCardDetails();
                                            Toast.makeText(AddCardActivity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDisplay.hideProgress();
                                            Toast.makeText(AddCardActivity.this, "Card not added", Toast.LENGTH_SHORT).show();

                                        });
                            }
                        }
                        else
                        {
                            String cardId = databaseReference.push().getKey();
                            assert cardId != null;

                            databaseReference.child(cardId).setValue(cardDetails)
                                    .addOnSuccessListener(aVoid -> {
                                        progressDisplay.hideProgress();
                                        getCardDetails();
                                        Toast.makeText(AddCardActivity.this, "Card saved successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDisplay.hideProgress();
                                        Toast.makeText(AddCardActivity.this, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();

                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDisplay.hideProgress();
                        Toast.makeText(AddCardActivity.this, "Failed to check existing cards, try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                if (cardNumber.isEmpty())
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddCardActivity.this);
                    builder.setMessage(getString(R.string.invalid_card))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    if (!Validation.isValidExpiryDate(expiryDate))
                    {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddCardActivity.this);
                        builder.setMessage(getString(R.string.expire_date))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        }
        else
        {
            List<String> missingFields = new ArrayList<>();
            if (cardNumber.isEmpty()) {
                missingFields.add("card number");
            }
            if (cardHolder.isEmpty()) {
                missingFields.add("holder name");
            }
            if (cvv.isEmpty()) {
                missingFields.add("CVV");
            }
            if (expiryDate.isEmpty()) {
                missingFields.add("expiry date");
            }

            if (!missingFields.isEmpty())
            {
                String errorMessage = "Please enter " + String.join(", ", missingFields);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddCardActivity.this);
                builder.setMessage(getString(Integer.parseInt(errorMessage)))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void getCardDetails()
    {
        String userId = session.getData(Session.USER_ID);
        progressDisplay.showProgress();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CardDetails");

        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CardDetails cardDetails = new CardDetails();
                        if (snapshot.exists())
                        {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CardDetails details = dataSnapshot.getValue(CardDetails.class);
                                if (details != null) {
                                    cardDetails = details;
                                }
                            }
                            progressDisplay.hideProgress();
                            setCardData(cardDetails);
                        } else {
                            progressDisplay.hideProgress();
                            Log.e("Firebase", "No Cards found for userId: " + userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDisplay.hideProgress();
                        Log.e("Firebase Error", "Failed to retrieve Cards: " + error.getMessage());
                    }
                });
    }

    private void setCardData(CardDetails details)
    {
        if (details != null)
        {
            binding.edtCardNumber.setText(details.getCardNumber());
            binding.txtCardNumber.setText(details.getCardNumber());
            binding.txtCardNumber1.setText(details.getCardNumber());
            binding.edtExpireDate.setText(details.getCardExpireDate());
            binding.edtCardHolder.setText(details.getCardHolder());
            binding.edtCvv.setText(details.getCardCVV());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardDetails();
        generateQRCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null)
            {
                updateBalanceToScannedUser(result.getContents());
            }
            else
            {
                Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateBalanceToScannedUser(String scannedUserId) {
        String currentUserId = session.getData(Session.USER_ID);

        DatabaseReference transactionReference = FirebaseDatabase.getInstance().getReference("walletTransaction");

        transactionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                        AmountDetails amountDetails = transactionSnapshot.getValue(AmountDetails.class);

                        if (amountDetails != null && scannedUserId.equals(amountDetails.getUserId())) {
                            amountDetails.setUserId(currentUserId);

                            String newTransactionId = transactionReference.push().getKey();
                            assert newTransactionId != null;

                            transactionReference.child(newTransactionId).setValue(amountDetails)
                                    .addOnSuccessListener(unused -> {
                                        finish();
                                        progressDisplay.hideProgress();
                                        Toast.makeText(AddCardActivity.this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDisplay.hideProgress();
                                        Toast.makeText(AddCardActivity.this, "Failed to update transaction", Toast.LENGTH_SHORT).show();

                                    });
                        }
                    }
                } else {
                    progressDisplay.hideProgress();
                    Toast.makeText(AddCardActivity.this, "No transactions found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDisplay.hideProgress();
            }
        });


    }


    public static Bitmap qRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}