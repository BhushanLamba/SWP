package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.models.SettlementReportModel;

public class SettlementReportAdapter extends RecyclerView.Adapter<SettlementReportAdapter.ViewHolder>
{

    ArrayList<SettlementReportModel> settlementReportModelArrayList;
    boolean isInitialReport;
    Context context;

    public SettlementReportAdapter(ArrayList<SettlementReportModel> settlementReportModelArrayList, boolean isInitialReport, Context context) {
        this.settlementReportModelArrayList = settlementReportModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_settlement_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name=settlementReportModelArrayList.get(position).getName();
        String surcharge=settlementReportModelArrayList.get(position).getSurcharge();
        String amount=settlementReportModelArrayList.get(position).getAmount();
        String paymentType=settlementReportModelArrayList.get(position).getPaymentType();
        String reqDate=settlementReportModelArrayList.get(position).getReqDate();
        String accountNumber=settlementReportModelArrayList.get(position).getAccountNo();
        String status=settlementReportModelArrayList.get(position).getStatus();

        holder.tvName.setText(name);
        holder.tvSurcharge.setText("₹ "+surcharge);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvPaymentType.setText(paymentType);
        holder.tvReqDate.setText(reqDate);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return settlementReportModelArrayList.size();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvSurcharge,tvAmount,tvPaymentType,tvReqDate,tvAccountNumber,tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            tvSurcharge=itemView.findViewById(R.id.tv_surcharge);
            tvAmount=itemView.findViewById(R.id.tv_all_report_amount);
            tvPaymentType=itemView.findViewById(R.id.tv_payment_type);
            tvReqDate=itemView.findViewById(R.id.tv_all_report_date_time);
            tvAccountNumber=itemView.findViewById(R.id.tv_account_number);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
        }
    }
}
