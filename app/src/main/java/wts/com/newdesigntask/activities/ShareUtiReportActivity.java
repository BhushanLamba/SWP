package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.newdesigntask.R;

public class ShareUtiReportActivity extends AppCompatActivity {

    TextView tvTransactionId,tvCouponType,tvVleId,tvAmount,tvDate;
    ImageView imgStatus;
    AppCompatButton btnOk;

    String transactionId,couponType,vleId,amount,date,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_uti_report);
        intitViews();

        transactionId=getIntent().getStringExtra("responseTransactionId");
        couponType=getIntent().getStringExtra("couponType");
        vleId=getIntent().getStringExtra("vleId");
        amount=getIntent().getStringExtra("responseAmount");
        date=getIntent().getStringExtra("responseDateTime");
        status=getIntent().getStringExtra("responseStatus");

        tvTransactionId.setText(transactionId);
        tvCouponType.setText(couponType);
        tvVleId.setText(vleId);
        tvAmount.setText(amount);
        tvDate.setText(date);

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
        tvCouponType=findViewById(R.id.tv_coupon_type);
        tvVleId=findViewById(R.id.tv_vle_id);
        tvAmount=findViewById(R.id.tv_amount);
        tvDate=findViewById(R.id.tv_date);
        imgStatus=findViewById(R.id.img_status);
        btnOk=findViewById(R.id.btn_ok);
    }
}