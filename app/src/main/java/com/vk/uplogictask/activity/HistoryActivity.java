package com.vk.uplogictask.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.uplogictask.adapter.HistoryAdapter;
import com.vk.uplogictask.databinding.ActivityHistoryBinding;
import com.vk.uplogictask.helper.ProgressDisplay;
import com.vk.uplogictask.helper.Session;
import com.vk.uplogictask.model.AmountDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ActivityHistoryBinding binding;

    ProgressDisplay progressDisplay;
    HistoryAdapter historyAdapter;
    Session session;
    DecimalFormat decimalFormat = new DecimalFormat("##0.00");

    List<AmountDetails> amountDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDisplay = new ProgressDisplay(this);
        session = new Session(this);

        binding.lytBack.setOnClickListener(v -> finish());


        getTransactionData();

        Log.d("TAG", "onCreate: " );
    }

    private void getTransactionData()
    {
        Log.d("TAG", "onCreate: " );
        String userId = session.getData(Session.USER_ID);
        Log.d("TAG", "onCreate: " + userId);
        if (!userId.isEmpty())
        {
            progressDisplay.hideProgress();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("walletTransaction");

            Log.d("TAG", "onCreate: " + userId);
            databaseReference.orderByChild("userId").equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    AmountDetails details = dataSnapshot.getValue(AmountDetails.class);
                                    if (details != null) {
                                        Log.d("TAG", "onCreate: " + userId);
                                        amountDetails.add(details);
                                        Log.d("TAG", "onDataChange: " + amountDetails);
                                    }
                                }
                                progressDisplay.hideProgress();
                                setTransactionData(amountDetails);
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
                    });
        }
    }

    private void setTransactionData(List<AmountDetails> amountDetails)
    {
        if (amountDetails != null)
        {
            double amount = 0;
            for (AmountDetails details : amountDetails)
            {
                if (details != null)
                {
                    amount += details.getAmount();
                    Log.d("TAG", "setTransactionData: " + amount);
                }
            }
            binding.tvAmount.setText("$".concat(decimalFormat.format(amount)));

            historyAdapter = new HistoryAdapter();
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
            historyAdapter.setAmountDetails(amountDetails);
            binding.recyclerview.setAdapter(historyAdapter);

        }
    }
}