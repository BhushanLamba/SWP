package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class PurchaseCouponActivity extends AppCompatActivity {

    TextView tvVleId,tvAmount;
    Spinner spinnerCouponType;
    EditText etQuantity;
    AppCompatButton btnSubmit,btnCalculate;

    String[] couponTypeArr = {"Physical Coupon", "E-Coupon"};
    String[] couponIdArr = {"13", "12"};


    String vleId,responseAmount="select",selectedCouponType="select",selectedCouponId,quantity,userId;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_coupon);

        initViews();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(PurchaseCouponActivity.this);
        userId=sharedPreferences.getString("userid",null);
        vleId=getIntent().getStringExtra("vleId");

        tvVleId.setText(vleId);


        btnCalculate.setOnClickListener(view -> {
            if (!selectedCouponType.equalsIgnoreCase("select") || !TextUtils.isEmpty(etQuantity.getText()))
            {
                calculateAmount();
            }
            else
            {
                Toast.makeText(PurchaseCouponActivity.this, "All above fields are mandatory.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v->{
            if (!responseAmount.equalsIgnoreCase("select") ||!selectedCouponType.equalsIgnoreCase("select") || !TextUtils.isEmpty(etQuantity.getText()))
            {
                purchaseCoupon();
            }
            else
            {
                Toast.makeText(PurchaseCouponActivity.this, "All above fields are mandatory.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void purchaseCoupon() {
        final ProgressDialog progressDialog = new ProgressDialog(PurchaseCouponActivity.this);
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        quantity=etQuantity.getText().toString().trim();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().purchaseCoupon("",userId,selectedCouponId,quantity,
                responseAmount,vleId,selectedCouponType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");

                        progressDialog.dismiss();
                        new AlertDialog.Builder(PurchaseCouponActivity.this)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAmount() {
        final ProgressDialog progressDialog = new ProgressDialog(PurchaseCouponActivity.this);
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        quantity=etQuantity.getText().toString().trim();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().calculateCouponAmount("",userId,selectedCouponId,quantity);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String statusCode=responseObject.getString("status");
                        responseAmount=responseObject.getString("data");

                        if (statusCode.equalsIgnoreCase("200"))
                        {

                            tvAmount.setText("₹ "+responseAmount);
                            progressDialog.dismiss();
                        }

                        else
                        {
                            Toast.makeText(PurchaseCouponActivity.this, responseAmount, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            tvAmount.setText("₹ ");
                            responseAmount="select";

                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PurchaseCouponActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initViews() {
        tvVleId=findViewById(R.id.tv_vle_id);
        tvAmount=findViewById(R.id.tv_amount);
        spinnerCouponType=findViewById(R.id.spinner_coupon_type);
        etQuantity=findViewById(R.id.et_quantity);
        btnSubmit=findViewById(R.id.btn_submit);
        btnCalculate=findViewById(R.id.btn_calculate);

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerCouponType,
                new HintAdapter<>(PurchaseCouponActivity.this, "Coupon Type", Arrays.asList(couponTypeArr)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedCouponType = couponTypeArr[position];
                        selectedCouponId = couponIdArr[position];
                    }
                });
        hintSpinner.init();
    }
}