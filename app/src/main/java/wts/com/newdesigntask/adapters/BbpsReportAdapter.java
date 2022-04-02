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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.ShareBbpsReportActivity;
import wts.com.newdesigntask.models.BbpsReportModel;

public class BbpsReportAdapter extends RecyclerView.Adapter<BbpsReportAdapter.Viewholder> {


    ArrayList<BbpsReportModel> bbpsReportModelArrayList;
    Activity activity;
    Context context;

    public BbpsReportAdapter(ArrayList<BbpsReportModel> bbpsReportModelArrayList, Activity activity, Context context) {
        this.bbpsReportModelArrayList = bbpsReportModelArrayList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bbps_report,parent,false);

       return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String operator,consumerNo,amount,date,time,status;

        operator=bbpsReportModelArrayList.get(position).getOperator();
        consumerNo=bbpsReportModelArrayList.get(position).getConsumerNo();
        status=bbpsReportModelArrayList.get(position).getStatus();
        date=bbpsReportModelArrayList.get(position).getDate();
        time=bbpsReportModelArrayList.get(position).getTime();
        amount=bbpsReportModelArrayList.get(position).getAmount();

        holder.tvOperatorConsumerNo.setText(operator+"\n"+consumerNo);
        holder.tvStatus.setText(status);
        holder.tvDateTime.setText(date+"\n"+time);
        holder.tvAmount.setText("₹ "+amount);

        holder.bbpsReportLayout.setOnClickListener(v->{
            String operator1,consumerNo1,consumerName,openingBalance,amount1,commission,
                    surcharge, closingBalance,date1,time1,transactionId,status1;


            operator1=bbpsReportModelArrayList.get(position).getOperator();
            consumerNo1=bbpsReportModelArrayList.get(position).getConsumerNo();
            consumerName=bbpsReportModelArrayList.get(position).getConsumerName();
            openingBalance=bbpsReportModelArrayList.get(position).getOpeningBalance();
            amount1=bbpsReportModelArrayList.get(position).getAmount();
            commission=bbpsReportModelArrayList.get(position).getCommission();
            surcharge=bbpsReportModelArrayList.get(position).getSurcharge();
            closingBalance=bbpsReportModelArrayList.get(position).getClosingBalance();
            date1=bbpsReportModelArrayList.get(position).getDate();
            time1=bbpsReportModelArrayList.get(position).getTime();
            transactionId=bbpsReportModelArrayList.get(position).getTransactionId();
            status1=bbpsReportModelArrayList.get(position).getStatus();

            Intent intent=new Intent(context, ShareBbpsReportActivity.class);
            intent.putExtra("operator",operator1);
            intent.putExtra("consumerNo",consumerNo1);
            intent.putExtra("consumerName",consumerName);
            intent.putExtra("openingBalance",openingBalance);
            intent.putExtra("amount",amount1);
            intent.putExtra("commission",commission);
            intent.putExtra("surcharge",surcharge);
            intent.putExtra("closingBalance",closingBalance);
            intent.putExtra("date",date1);
            intent.putExtra("time",time1);
            intent.putExtra("transactionId",transactionId);
            intent.putExtra("status",status1);
            activity.startActivity(intent);
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
        return bbpsReportModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvOperatorConsumerNo,tvStatus,tvDateTime,tvAmount;
        LinearLayout bbpsReportLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvOperatorConsumerNo=itemView.findViewById(R.id.tv_operator_consumer_no);
            tvStatus=itemView.findViewById(R.id.tv_status);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            bbpsReportLayout=itemView.findViewById(R.id.bbps_reports_layout);
        }
    }
}
