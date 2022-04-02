package wts.com.newdesigntask.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import wts.com.newdesigntask.R;

public class ShareBbpsReportActivity extends AppCompatActivity {

    TextView tvOperator, tvConsumerNo, tvConsumerName, tvOpeningBal, tvAmount, tvCommission, tvSurcharge, tvClosingBalance, tvDatetime,
            tvTransactionId;
    ImageView imgStatus;
    AppCompatButton btnOk;

    String operator, consumerNo, consumerName, openingBalance, amount, commission, surcharge, closingBalance, date, time, transactionId,status;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bbps_report);
        intitViews();

        operator=getIntent().getStringExtra("operator");
        consumerNo=getIntent().getStringExtra("consumerNo");
        consumerName=getIntent().getStringExtra("consumerName");
        openingBalance=getIntent().getStringExtra("openingBalance");
        amount=getIntent().getStringExtra("amount");
        commission=getIntent().getStringExtra("commission");
        surcharge=getIntent().getStringExtra("surcharge");
        closingBalance=getIntent().getStringExtra("closingBalance");
        date=getIntent().getStringExtra("date");
        time=getIntent().getStringExtra("time");
        transactionId=getIntent().getStringExtra("transactionId");
        status=getIntent().getStringExtra("status");


        tvOperator.setText(operator);
        tvConsumerNo.setText(consumerNo);
        tvConsumerName.setText(consumerName);
        tvOpeningBal.setText("₹ "+openingBalance);
        tvAmount.setText("₹ "+amount);
        tvCommission.setText("₹ "+commission);
        tvSurcharge.setText("₹ "+surcharge);
        tvClosingBalance.setText("₹ "+closingBalance);
        tvDatetime.setText(date+" "+time);
        tvTransactionId.setText(transactionId);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.success));
        else if (status.equalsIgnoreCase("Pending"))
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.pending));
        else
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.failed));

        btnOk.setOnClickListener(v->
        {
            finish();
        });

    }

    private void intitViews() {
        tvOperator=findViewById(R.id.tv_operator);
        tvConsumerNo=findViewById(R.id.tv_consumer_no);
        tvConsumerName=findViewById(R.id.tv_consumer_name);
        tvOpeningBal=findViewById(R.id.tv_opening_balance);
        tvAmount=findViewById(R.id.tv_amount);
        tvCommission=findViewById(R.id.tv_commission);
        tvSurcharge=findViewById(R.id.tv_surcharge);
        tvClosingBalance=findViewById(R.id.tv_closing_balance);
        tvDatetime=findViewById(R.id.tv_date_time);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        imgStatus=findViewById(R.id.img_status);
        btnOk=findViewById(R.id.btn_ok);
    }
}