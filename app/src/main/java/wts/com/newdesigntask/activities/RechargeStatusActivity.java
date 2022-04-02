package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.newdesigntask.R;

public class RechargeStatusActivity extends AppCompatActivity {

    TextView tvTransactionId,tvNumber,tvOperator,tvAmount,tvDate,tvTime;
    ImageView imgStatus;
    AppCompatButton btnOk;

    String transactionId,number,operator,amount,date,time,status;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_status);
        
        intitViews();
        transactionId=getIntent().getStringExtra("responseTransactionId");
        number=getIntent().getStringExtra("responseNumber");
        operator=getIntent().getStringExtra("responseOperator");
        amount=getIntent().getStringExtra("responseAmount");
        date=getIntent().getStringExtra("outputDate");
        time=getIntent().getStringExtra("outputTime");
        status=getIntent().getStringExtra("responseStatus");

        tvTransactionId.setText(transactionId);
        tvNumber.setText(number);
        tvOperator.setText(operator);
        tvAmount.setText(amount);
        tvDate.setText(date);
        tvTime.setText(time);

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
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvNumber=findViewById(R.id.tv_number);
        tvOperator=findViewById(R.id.tv_operator);
        tvAmount=findViewById(R.id.tv_amount);
        tvDate=findViewById(R.id.tv_date);
        tvTime=findViewById(R.id.tv_time);
        imgStatus=findViewById(R.id.img_status);
        btnOk=findViewById(R.id.btn_ok);
    }
}