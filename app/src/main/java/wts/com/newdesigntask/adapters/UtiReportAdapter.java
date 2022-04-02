package wts.com.newdesigntask.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.RechargeActivity;
import wts.com.newdesigntask.activities.RechargeStatusActivity;
import wts.com.newdesigntask.activities.ShareUtiReportActivity;
import wts.com.newdesigntask.models.UtiReportModel;

public class UtiReportAdapter extends RecyclerView.Adapter<UtiReportAdapter.Viewholder> {


    Activity activity;
    Context context;
    ArrayList<UtiReportModel> utiReportModelArrayList;


    public UtiReportAdapter(Activity activity, Context context, ArrayList<UtiReportModel> utiReportModelArrayList) {
        this.activity = activity;
        this.context = context;
        this.utiReportModelArrayList = utiReportModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_uti_report,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String couponType,amount,status;

        couponType=utiReportModelArrayList.get(position).getCouponType();
        amount=utiReportModelArrayList.get(position).getAmount();
        status=utiReportModelArrayList.get(position).getStatus();

        holder.tvCouponType.setText(couponType);
        holder.tvAmount.setText(amount);
        holder.tvStatus.setText(status);

        holder.utiReportLayout.setOnClickListener(v->
        {


            String responseDateTime=utiReportModelArrayList.get(position).getDate();
            String responseStatus=utiReportModelArrayList.get(position).getStatus();
            String couponType1=utiReportModelArrayList.get(position).getCouponType();
            String responseAmount=utiReportModelArrayList.get(position).getAmount();
            String responseTransactionId=utiReportModelArrayList.get(position).getTransactionId();
            String vleId=utiReportModelArrayList.get(position).getVleId();



                Intent intent = new Intent(context, ShareUtiReportActivity.class);
                intent.putExtra("responseTransactionId", responseTransactionId);
                intent.putExtra("couponType", couponType1);
                intent.putExtra("vleId", vleId);
                intent.putExtra("responseAmount", responseAmount);
                intent.putExtra("responseDateTime", responseDateTime);
                intent.putExtra("responseStatus", responseStatus);
                context.startActivity(intent);

        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return utiReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        TextView tvCouponType,tvAmount,tvStatus;

        LinearLayout utiReportLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvCouponType=itemView.findViewById(R.id.tv_coupon_type);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvStatus=itemView.findViewById(R.id.tv_status);
            utiReportLayout=itemView.findViewById(R.id.uti_report_layout);
        }
    }
}
