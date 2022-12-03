package com.iacademy.e_wallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iacademy.e_wallet.R;
import com.iacademy.e_wallet.models.TransactionModel;
import com.iacademy.e_wallet.models.WalletModel;
import com.iacademy.e_wallet.utils.RecyclerOnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    //declare variables
    private ArrayList<TransactionModel> listModels;
    private Context context;
    private RecyclerOnItemClickListener mItemClickListener;


    //constructor
    public TransactionAdapter(ArrayList<TransactionModel> listModels, Context context, RecyclerOnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
        this.listModels = listModels;
        this.context = context;
    }

    public TransactionAdapter(ArrayList<TransactionModel> listModels, Context context) {
        this.listModels = listModels;
        this.context = context;
    }

    //create view each time
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history_list, parent, false));
    }

    //add information
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String type = listModels.get(position).getTransactionType();

        switch (type){
            case "DEPOSIT":
                holder.tvAmount.setText(String.valueOf(listModels.get(position).getAmountSent()));
                holder.tvName.setText("");
                holder.tvNumber.setText("");
                holder.tvTimeAndDate.setText(listModels.get(position).getTimeAndDate());
                holder.tvTransactionType.setText(type);
                break;
            case "RECEIVED MONEY":
                holder.tvAmount.setText(String.valueOf(listModels.get(position).getAmountReceived()));
                holder.tvName.setText(String.valueOf(listModels.get(position).getSenderName()));
                holder.tvNumber.setText(String.valueOf(listModels.get(position).getSenderNumber()));
                holder.tvTimeAndDate.setText(listModels.get(position).getTimeAndDate());
                holder.tvTransactionType.setText(type);
                break;
            case "SENT MONEY":
                holder.tvAmount.setText(String.valueOf(listModels.get(position).getAmountSent()));
                holder.tvName.setText(String.valueOf(listModels.get(position).getReceiverName()));
                holder.tvNumber.setText(String.valueOf(listModels.get(position).getReceiverNumber()));
                holder.tvTimeAndDate.setText(listModels.get(position).getTimeAndDate());
                holder.tvTransactionType.setText(type);
                break;

        }
    }

    //returns how many items on the list
    @Override
    public int getItemCount() {
        return listModels.size();
    }

    //declare and instantiate variables
    public class ViewHolder extends RecyclerView.ViewHolder{

        //declare variables
        public TextView tvAmount;
        public TextView tvName;
        public TextView tvNumber;
        public TextView tvTimeAndDate;
        public TextView tvTransactionType;

        //instantiate variable
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvTimeAndDate = itemView.findViewById(R.id.tv_timeAndDate);
            tvTransactionType = itemView.findViewById(R.id.tv_transactionType);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }

            });
        }
    }
}
