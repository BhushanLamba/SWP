package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.ShareAepsReportActivity;
import wts.com.newdesigntask.models.AepsModel;


public class AepsReportAdapter extends RecyclerView.Adapter<AepsReportAdapter.ViewHolder>
{
    ArrayList<AepsModel> aepsModelArrayList;
    boolean isInitialReport;
    Context context;

    public AepsReportAdapter(ArrayList<AepsModel> aepsModelArrayList, boolean isInitialReport, Context context) {
        this.aepsModelArrayList = aepsModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_aeps_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String transactionId=aepsModelArrayList.get(position).getTransactionId();
        String amount=aepsModelArrayList.get(position).getAmount();
        String comm=aepsModelArrayList.get(position).getComm();
        String balance=aepsModelArrayList.get(position).getNewbalance();
        String date=aepsModelArrayList.get(position).getDate();
        String status=aepsModelArrayList.get(position).getTxnStatus();
        String transactionType=aepsModelArrayList.get(position).getTxnType();
        String bankName=aepsModelArrayList.get(position).getBankName();
        String mobileNo=aepsModelArrayList.get(position).getMobileNo();
        String aadharNo=aepsModelArrayList.get(position).getAadharNo();
        String bankRrn=aepsModelArrayList.get(position).getBankRRN();
        String time=aepsModelArrayList.get(position).getTime();

        holder.tvTransactionId.setText(transactionId);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvComm.setText("₹ "+comm);
        holder.tvBalance.setText("₹ "+balance);
        holder.tvDateTime.setText(date);
        holder.tvStatus.setText(status);

        holder.reportLayout.setOnClickListener(v->
        {
            Intent intent =new Intent(context, ShareAepsReportActivity.class);
            intent.putExtra("transactionType",transactionType);
            intent.putExtra("bankName",bankName);
            intent.putExtra("responseMobileNumber",mobileNo);
            intent.putExtra("responseAadharNumber",aadharNo);
            intent.putExtra("responseBankRRN",bankRrn);
            intent.putExtra("transactionId",transactionId);
            intent.putExtra("status",status);
            intent.putExtra("responseAmount",amount);
            intent.putExtra("outputDate",date);
            intent.putExtra("outputTime",time);

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return aepsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvOwnerName,tvBcId,tvAmount,tvComm,tvCost,tvBalance,tvDateTime,tvStatus;
        ConstraintLayout reportLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_all_report_transaction_id);
            tvOwnerName=itemView.findViewById(R.id.tv_owner_name);
            tvBcId=itemView.findViewById(R.id.tv_bc_id);
            tvAmount=itemView.findViewById(R.id.tv_all_report_amount);
            tvComm=itemView.findViewById(R.id.tv_all_report_commission);
            tvCost=itemView.findViewById(R.id.tv_all_report_cost);
            tvBalance=itemView.findViewById(R.id.tv_new_balance);
            tvDateTime=itemView.findViewById(R.id.tv_all_report_date_time);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
            reportLayout=itemView.findViewById(R.id.report_layout);
        }
    }
}
