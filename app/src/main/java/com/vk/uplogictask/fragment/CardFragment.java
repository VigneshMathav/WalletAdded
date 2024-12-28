package com.vk.uplogictask.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.uplogictask.R;
import com.vk.uplogictask.activity.AddCardActivity;
import com.vk.uplogictask.activity.HistoryActivity;
import com.vk.uplogictask.databinding.DialogAddWalletBinding;
import com.vk.uplogictask.databinding.FragmentCardBinding;
import com.vk.uplogictask.helper.ProgressDisplay;
import com.vk.uplogictask.helper.Session;
import com.vk.uplogictask.model.AmountDetails;
import com.vk.uplogictask.model.CardDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardFragment extends Fragment {

    FragmentCardBinding binding;
    DialogAddWalletBinding dialog_binding;
    Session session;
    Dialog dialog;
    ProgressDisplay progressDisplay;
    String userId = "";



    public CardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardBinding.inflate(inflater, container, false);
        session = new Session(requireContext());
        progressDisplay = new ProgressDisplay(requireContext());
        userId = session.getData(Session.USER_ID);

        binding.btnAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                walletDialog();

            }
        });
        binding.ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddCardActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void walletDialog() {

        dialog_binding = DialogAddWalletBinding.inflate(getLayoutInflater());
        dialog = new Dialog(requireContext(), R.style.TransparentDialog);
        dialog.setContentView(dialog_binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);


        int widthInPx = (int) (300 * getResources().getDisplayMetrics().density);
//        int heightInPx = (int) (300 * getResources().getDisplayMetrics().density);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = widthInPx;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item,
                getResources().getStringArray(R.array.spinner_value));
        dialog_binding.spinner.setAdapter(adapter);

        dialog_binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog_binding.btnAdd.setOnClickListener(v -> walletDetails());

        dialog.show();
    }

    private void walletDetails() {

        String method = dialog_binding.spinner.getSelectedItem().toString();
        String description = dialog_binding.edtDescription.getText().toString();
        String amount = dialog_binding.edtAmount.getText().toString();

        if (description.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.req_desc))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        if (amount.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.req_amount))
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

            progressDisplay.showProgress();

            AmountDetails amountDetails = new AmountDetails();
            amountDetails.uploadAmountDetails(userId, method, description, getCurrentDateTime(), Double.valueOf(amount));

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("walletTransaction");
            String transactionId = databaseReference.push().getKey();

            assert transactionId != null;
            databaseReference.child(transactionId).setValue(amountDetails)
                    .addOnSuccessListener(unused -> {
                        dialog.dismiss();
                        progressDisplay.hideProgress();
                        getWalletDetails();
                        Toast.makeText(requireContext(), "Transaction Successfully", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> {
                        progressDisplay.hideProgress();
                        Toast.makeText(requireContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
                    });

        }


    }

    private void getWalletDetails() {

        progressDisplay.showProgress();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("walletTransaction");
        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<AmountDetails> amountDetails = new ArrayList<>();
                        if (snapshot.exists())
                        {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                AmountDetails details = dataSnapshot.getValue(AmountDetails.class);
                                if (details != null)
                                {
                                    amountDetails.add(details);
                                }
                            }
                            progressDisplay.hideProgress();
                            setCardData(amountDetails);
                        }
                        else
                        {
                            progressDisplay.hideProgress();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDisplay.hideProgress();

                    }
                }
        );




    }

    @SuppressLint("SetTextI18n")
    private void setCardData(List<AmountDetails> amountDetails) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");

        if (amountDetails != null && !amountDetails.isEmpty()) {
            double amount = 0, cash = 0, credit = 0, debit = 0, bank = 0;

            for (AmountDetails details : amountDetails) {
                amount += details.getAmount();
                if (details.getMethod().equalsIgnoreCase("Money Cash")) {
                    cash += details.getAmount();
                }
                if (details.getMethod().equalsIgnoreCase("Debit Card")) {
                    debit += details.getAmount();
                }
                if (details.getMethod().equalsIgnoreCase("Bank Account")) {
                    bank += details.getAmount();
                }
                if (details.getMethod().equalsIgnoreCase("Credit Card")) {
                    credit += details.getAmount();
                }
            }
            binding.txtAmount.setText("$"+(decimalFormat.format(amount)));
            binding.cashAmount.setText("$ " + (decimalFormat.format(cash)));
            binding.debitAmount.setText("$ " + (decimalFormat.format(debit)));
            binding.bankAmount.setText("$ " + (decimalFormat.format(bank)));
            binding.creditAmount.setText("$ " + (decimalFormat.format(credit)));
        }
    }


    private void getCardDetails()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cards");

        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CardDetails card = new CardDetails();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CardDetails card1 = dataSnapshot.getValue(CardDetails.class);
                                if (card1 != null) {
                                    card = card1;
                                }
                            }
                            setCardData(card);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setCardData(CardDetails card)
    {
        if (card != null)
        {
            String cardNumber = card.getCardNumber() != null && !card.getCardNumber().isEmpty() ? card.getCardNumber() : "";
            binding.tvDebitCard.setText(validCard(cardNumber));
            binding.tvBank.setText(validCard(cardNumber));
            binding.tvCreditCard.setText(validCard(cardNumber));
            binding.currentDate.setText(getCurrentDateTime());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getWalletDetails();
        getCardDetails();
    }


    public static String validCard(String cardNumber)
    {
        String cleanCardNumber = cardNumber.replaceAll("[^\\d]", "");

        if (cleanCardNumber.length() != 16) {
            throw new IllegalArgumentException("Invalid card number length. Card number must have 16 digits.");
        }

        String maskedSection = "**** **** ****";
        String lastFourDigits = cleanCardNumber.substring(cleanCardNumber.length() - 4);

        return maskedSection + " " + lastFourDigits;
    }

    public static String getCurrentDateTime()
    {
        return String.valueOf(DateFormat.format("EEE, MMM dd, yyyy", new Date().getTime()));
    }

}