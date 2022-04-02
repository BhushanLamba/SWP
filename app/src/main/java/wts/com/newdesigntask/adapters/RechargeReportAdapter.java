package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.ShareRechargeReportActivity;
import wts.com.newdesigntask.models.RechargeReportModel;

public class RechargeReportAdapter extends RecyclerView.Adapter<RechargeReportAdapter.Viewholder> {


    ArrayList<RechargeReportModel> rechargeReportModelArrayList;
    Context context;
    Activity activity;

    public RechargeReportAdapter(ArrayList<RechargeReportModel> rechargeReportModelArrayList, Context context,Activity activity) {
        this.rechargeReportModelArrayList = rechargeReportModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report,parent,false);
       return new Viewholder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String operator=rechargeReportModelArrayList.get(position).getOperatorName();
        String number=rechargeReportModelArrayList.get(position).getMobileNo();
        String date=rechargeReportModelArrayList.get(position).getDate();
        String time=rechargeReportModelArrayList.get(position).getTime();
        String amount=rechargeReportModelArrayList.get(position).getAmount();
        String status=rechargeReportModelArrayList.get(position).getStatus();

        holder.tvOperator.setText("Recharge For "+operator);
        holder.tvNumber.setText("On "+number);
        holder.tvDateTime.setText(date+","+time);
        holder.tvAmount.setText(amount);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))
        {
            holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        }
        else
        {
            holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.failed));
        }


        holder.reportLayout.setOnClickListener(v->
        {

            String transactionId=rechargeReportModelArrayList.get(position).getTransactionId();
            String serviceType=rechargeReportModelArrayList.get(position).getServiceType();
            String openingBalance=rechargeReportModelArrayList.get(position).getOpeningBalance();
            String closingBalance=rechargeReportModelArrayList.get(position).getClosingBalance();
            String operator1=rechargeReportModelArrayList.get(position).getOperatorName();
            String commission=rechargeReportModelArrayList.get(position).getCommission();
            String status1=rechargeReportModelArrayList.get(position).getStatus();
            String number1=rechargeReportModelArrayList.get(position).getMobileNo();
            String amount1=rechargeReportModelArrayList.get(position).getAmount();
            String date1=rechargeReportModelArrayList.get(position).getDate();
            String time1=rechargeReportModelArrayList.get(position).getTime();

            Intent intent=new Intent(context, ShareRechargeReportActivity.class);
            intent.putExtra("number",number1);
            intent.putExtra("amount",amount1);
            intent.putExtra("date",date1);
            intent.putExtra("time",time1);
            intent.putExtra("transactionId",transactionId);
            intent.putExtra("serviceType",serviceType);
            intent.putExtra("openingBalance",openingBalance);
            intent.putExtra("closingBalance",closingBalance);
            intent.putExtra("operator",operator1);
            intent.putExtra("commission",commission);
            intent.putExtra("status",status1);

            activity.startActivity(intent);
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
        return rechargeReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvOperator,tvNumber,tvDateTime,tvAmount;
        ImageView imgOperator,imgStatus;
        ConstraintLayout reportLayout;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvOperator=itemView.findViewById(R.id.tv_operator);
            tvNumber=itemView.findViewById(R.id.tv_number);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            imgOperator=itemView.findViewById(R.id.img_operator);
            imgStatus=itemView.findViewById(R.id.img_status);
            reportLayout=itemView.findViewById(R.id.report_layout);
        }
    }
}
