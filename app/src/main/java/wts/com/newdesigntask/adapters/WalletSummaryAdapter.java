package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.models.UpiReportModel;
import wts.com.newdesigntask.models.WalletSummaryModel;

public class WalletSummaryAdapter extends RecyclerView.Adapter<WalletSummaryAdapter.Viewholder>{

    ArrayList<WalletSummaryModel> walletSummaryModelArrayList;
    Context context;
    Activity activity;

    public WalletSummaryAdapter(ArrayList<WalletSummaryModel> walletSummaryModelArrayList, Context context, Activity activity) {
        this.walletSummaryModelArrayList = walletSummaryModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallet_summary,parent,false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String transactionId=walletSummaryModelArrayList.get(position).getTransactionId();
        String openingBalance=walletSummaryModelArrayList.get(position).getOpeningBalance();
        String closingBalance=walletSummaryModelArrayList.get(position).getClosingBalance();
        String date=walletSummaryModelArrayList.get(position).getDate();
        String time=walletSummaryModelArrayList.get(position).getTime();
        String amount=walletSummaryModelArrayList.get(position).getAmount();
        String transactionType=walletSummaryModelArrayList.get(position).getTransactionType();
        String remarks=walletSummaryModelArrayList.get(position).getRemarks();

        holder.tvTransactionId.setText("TXN ID -> "+transactionId);
        holder.tvOpeningClosingBalance.setText("₹ "+openingBalance+" -> "+"₹ "+closingBalance);
        holder.tvDateTime.setText(date+","+time);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvTransactionType.setText(transactionType);
        holder.tvRemarks.setText(remarks);
    }

    @Override
    public int getItemCount() {
        return walletSummaryModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvOpeningClosingBalance,tvDateTime,tvAmount,tvTransactionType,tvRemarks;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvOpeningClosingBalance=itemView.findViewById(R.id.tv_opening_closing_balance);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvTransactionType=itemView.findViewById(R.id.tv_transaction_type);
            tvRemarks=itemView.findViewById(R.id.tv_remarks);
        }
    }
}
