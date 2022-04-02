package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.models.RechargeReportModel;
import wts.com.newdesigntask.models.UpiReportModel;

public class UpiReportAdapter extends RecyclerView.Adapter<UpiReportAdapter.Viewholder>{
    ArrayList<UpiReportModel> upiReportModelArrayList;
    Context context;
    Activity activity;


    public UpiReportAdapter(ArrayList<UpiReportModel> upiReportModelArrayList, Context context, Activity activity) {
        this.upiReportModelArrayList = upiReportModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upi_report,parent,false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String transactionId=upiReportModelArrayList.get(position).getTransactionId();
        String openingBalance=upiReportModelArrayList.get(position).getOpeningBalance();
        String closingBalance=upiReportModelArrayList.get(position).getClosingBalance();
        String date=upiReportModelArrayList.get(position).getDate();
        String time=upiReportModelArrayList.get(position).getTime();
        String amount=upiReportModelArrayList.get(position).getAmount();
        String status=upiReportModelArrayList.get(position).getStatus();

        holder.tvTransactionId.setText("TXN ID -> "+transactionId);
        holder.tvOpeningClosingBalance.setText("₹ "+openingBalance+" -> "+"₹ "+closingBalance);
        holder.tvDateTime.setText(date+","+time);
        holder.tvAmount.setText(amount);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))
        {
            holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        }
        else if (status.equalsIgnoreCase("Success"))
        {
            holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
        }
        else
        {
            holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.failed));
        }
    }

    @Override
    public int getItemCount() {
        return upiReportModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Viewholder  extends RecyclerView.ViewHolder{
        TextView tvTransactionId,tvOpeningClosingBalance,tvDateTime,tvAmount;
        ImageView imgOperator,imgStatus;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvOpeningClosingBalance=itemView.findViewById(R.id.tv_opening_closing_balance);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            imgOperator=itemView.findViewById(R.id.img_operator);
            imgStatus=itemView.findViewById(R.id.img_status);
        }
    }
}
