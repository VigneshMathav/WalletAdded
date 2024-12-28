package com.vk.uplogictask.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vk.uplogictask.databinding.LytHistoryBinding;
import com.vk.uplogictask.model.AmountDetails;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HisterViewHolder>
{
    List<AmountDetails> amountDetails;
    DecimalFormat decimalFormat = new DecimalFormat("##0.00");

    public HistoryAdapter() {
    }

    public void setAmountDetails(List<AmountDetails> amountDetails) {
        this.amountDetails = amountDetails;
    }

    @NonNull
    @Override
    public HistoryAdapter.HisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LytHistoryBinding binding = LytHistoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new HisterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HisterViewHolder holder, int position) {
        AmountDetails details = amountDetails.get(position);
        if (details != null)
        {
            String currentDate = details.getDate();
            holder.binding.txtDateTime.setText(convertDateFormat(currentDate));
            holder.binding.txtAmount.setText(details.getDescription());
        }
    }

    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return amountDetails != null ? amountDetails.size() : 0;
    }

    public class HisterViewHolder extends RecyclerView.ViewHolder {

        LytHistoryBinding binding;

        public HisterViewHolder(@NonNull LytHistoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
