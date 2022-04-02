package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import wts.com.newdesigntask.R;

public class ShareRechargeReportActivity extends AppCompatActivity {

    TextView tvNumber,tvAmount,tvDateTime,tvTransactionId,tvServiceType,tvOpeningBalance,tvClosingBalance,tvOperator,tvCommission,tvTotalAmount;
    ImageView imgBack,imgShare,imgStatus;
    String number,amount,date,time,transactionId,serviceType,openingBalance,closingBalance,operator,commission,status;
    int FILE_PERMISSION = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recharge_report);

        initViews();

        number=getIntent().getStringExtra("number");
        amount=getIntent().getStringExtra("amount");
        date=getIntent().getStringExtra("date");
        time=getIntent().getStringExtra("time");
        transactionId=getIntent().getStringExtra("transactionId");
        serviceType=getIntent().getStringExtra("serviceType");
        openingBalance=getIntent().getStringExtra("openingBalance");
        closingBalance=getIntent().getStringExtra("closingBalance");
        operator=getIntent().getStringExtra("operator");
        commission=getIntent().getStringExtra("commission");
        status=getIntent().getStringExtra("status");

        setViews();

        imgBack.setOnClickListener(v->{
            finish();
        });

        imgShare.setOnClickListener(v->
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareRechargeReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareRechargeReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareRechargeReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.share_report_layout);
            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareRechargeReportActivity.this);
                permissionDialog.setTitle("Permission Required");
                permissionDialog.setMessage("You can set permission manually." + "\n" + "Settings-> App Permission -> Allow Storage permission.");
                permissionDialog.setCancelable(false);
                permissionDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                permissionDialog.show();

            }
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void setViews() {
        tvNumber.setText(number);
        tvAmount.setText("₹ "+amount);
        tvDateTime.setText(date+","+time);
        tvTransactionId.setText(transactionId);
        tvServiceType.setText(serviceType);
        tvOpeningBalance.setText("₹ "+openingBalance);
        tvClosingBalance.setText("₹ "+closingBalance);
        tvOperator.setText(operator);
        tvCommission.setText("₹ "+commission);
        tvTotalAmount.setText("₹ "+amount);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful"))
        {
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.success));
        }
        else
        {
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.failed));
        }
    }

    private void initViews() {
        tvNumber=findViewById(R.id.tv_number);
        tvAmount=findViewById(R.id.tv_amount);
        tvDateTime=findViewById(R.id.tv_date_time);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvServiceType=findViewById(R.id.tv_service_type);
        tvOpeningBalance=findViewById(R.id.tv_opening_balance);
        tvClosingBalance=findViewById(R.id.tv_closing_balance);
        tvOperator=findViewById(R.id.tv_operator);
        tvCommission=findViewById(R.id.tv_commission);
        tvTotalAmount=findViewById(R.id.tv_total_amount);
        imgBack=findViewById(R.id.img_back);
        imgShare=findViewById(R.id.img_share);
        imgStatus=findViewById(R.id.img_status);
    }
}