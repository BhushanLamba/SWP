package wts.com.newdesigntask.activities;

import static org.json.JSONObject.wrap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class AddMoneyActivity extends AppCompatActivity {

    TextView tvBalance,tvUserName;
    EditText etAmount;
    SharedPreferences sharedPreferences;
    String userId,userName;

    ImageView imgGpay,imgPaytm,imgPhonePe,imgAmazon,imgBhim;
    String amount,appName;

    public static final int PAYMENT_REQUEST = 4400;
    static final String AMAZON_PAY = "in.amazon.mShop.android.shopping";
    static final String BHIM_UPI = "in.org.npci.upiapp";
    static final String GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user";
    static final String PHONE_PE = "com.phonepe.app";
    static final String PAYTM = "net.one97.paytm";

    String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AddMoneyActivity.this, R.color.blue));
        //////CHANGE COLOR OF STATUS BAR

        initViews();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddMoneyActivity.this);
        userId=sharedPreferences.getString("userid",null);
        userName=sharedPreferences.getString("username",null);
        tvUserName.setText(userName);
        getBalance();

        imgGpay.setOnClickListener(v->
        {
            if (checkInternetState()) {
                amount = etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Google Pay";
                    String packageName = GOOGLE_PAY;
                    insertUpiPaymentInfo(packageName);
                } else {
                    showSnackbar("Please enter amount");
                }
            } else {
                showSnackbar("No Internet Access");
            }
        });

        imgPaytm.setOnClickListener(v->
        {
            if (checkInternetState()) {
                amount = etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Pay Tm";
                    String packageName = PAYTM;
                    insertUpiPaymentInfo(packageName);
                } else {
                    showSnackbar("Please enter amount");
                }
            } else {
                showSnackbar("No Internet Access");
            }
        });

        imgPhonePe.setOnClickListener(v->
        {
            if (checkInternetState()) {
                amount = etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Phone Pe";
                    String packageName = PHONE_PE;
                    insertUpiPaymentInfo(packageName);
                } else {
                    showSnackbar("Please enter amount");
                }
            } else {
                showSnackbar("No Internet Access");
            }
        });

        imgAmazon.setOnClickListener(v->
        {
            if (checkInternetState()) {
                amount = etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Amazon Pay";
                    String packageName = AMAZON_PAY;
                    insertUpiPaymentInfo(packageName);
                } else {
                    showSnackbar("Please enter amount");
                }
            } else {
                showSnackbar("No Internet Access");
            }
        });

        imgBhim.setOnClickListener(v->
        {
            if (checkInternetState()) {
                amount = etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Bhim UPI";
                    String packageName = BHIM_UPI;
                    insertUpiPaymentInfo(packageName);
                } else {
                    showSnackbar("Please enter amount");
                }
            } else {
                showSnackbar("No Internet Access");
            }
        });


    }

    private void insertUpiPaymentInfo(String packageName) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call=RetrofitClient.getInstance().getApi().updateUpiDetails("",userId,amount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("status");
                        if (responseCode.equalsIgnoreCase("200"))
                        {
                            pDialog.dismiss();
                            uniqueId=responseObject.getString("data");
                            payNow(packageName);
                        }
                        else
                        {
                            pDialog.dismiss();
                            showSnackbar("Something went wrong.");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackbar("Something went wrong.");
                    }
                }
                else
                {
                    pDialog.dismiss();
                    showSnackbar("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar(t.getMessage());
            }
        });

    }

    private void payNow(String packageName) {
        try {
            Uri uri = new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", "swarazpay@icici")
                    .appendQueryParameter("pn", "Swaraj Pay")
                    .appendQueryParameter("mc", "")
                    .appendQueryParameter("tr", uniqueId)
                    .appendQueryParameter("tn", "Top Up wallet")
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("cu", "INR")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent, PAYMENT_REQUEST);
        } catch (Exception e) {
            appNotInstall();
        }
    }

    @SuppressLint("SetTextI18n")
    private void appNotInstall() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        final LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.device_not_connected, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AppCompatButton btnOK=convertView.findViewById(R.id.btn_ok);

        btnOK.setOnClickListener(v->
        {
            alertDialog.dismiss();
        });

        alertDialog.setView(convertView);
        alertDialog.show();
        alertDialog.setCancelable(false);

    }

    private void getBalance() {
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().getBalance("",userId,"main");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                    String balance=responseObject.getString("data");

                    tvBalance.setText("₹ "+balance);

                } catch (JSONException e) {
                    e.printStackTrace();
                    tvBalance.setText("₹ 00.0");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tvBalance.setText("₹ 00.0");
            }
        });
    }

    private void initViews() {
        tvBalance=findViewById(R.id.tv_balance);
        tvUserName=findViewById(R.id.tv_username);
        imgGpay=findViewById(R.id.img_gpay);
        imgPaytm=findViewById(R.id.img_paytm);
        imgPhonePe=findViewById(R.id.img_phonepe);
        imgAmazon=findViewById(R.id.img_amazon);
        imgBhim=findViewById(R.id.img_bhim);
        etAmount=findViewById(R.id.et_amount);
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_money_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String response = data.getStringExtra("response");
                String responseData = getJson(bundle);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("Status");
                    if (status.equalsIgnoreCase("Success")) {
                        String txnId = jsonObject.getString("txnId");
                        updateBalanceUPI("Success", txnId);
                    } else if (status.equalsIgnoreCase("Failed")) {
                        updateBalanceUPI("Failed", "NA");
                    } else {
                        updateBalanceUPI("Failed", "NA");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBalanceUPI(String status,String upiTxnId) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().insertUpiDetails("AUTH_KEY",userId,uniqueId,status);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String res_code = jsonObject.getString("status");
                        if (res_code.equalsIgnoreCase("200")){
                            pDialog.dismiss();
                            successDialog();
                        }else
                            {
                            pDialog.dismiss();
                            showSnackbar("Transaction failed, Due to technical error");
                        }
                    }catch (Exception e){
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackbar("Something went wrong");
                    }
                }else {
                    pDialog.dismiss();
                    showSnackbar(""+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("Something went wrong");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void successDialog() {
        final View view1 = LayoutInflater.from(AddMoneyActivity.this).inflate(R.layout.recharge_status_layout, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(AddMoneyActivity.this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setView(view1);
        alertDialog.show();
        TextView tv_recharge_dialog_title = alertDialog.findViewById(R.id.tv_recharge_dialog_title);
        TextView tvRechargeDialogNumber = alertDialog.findViewById(R.id.tv_recharge_dialogue_number);
        TextView tvRechargeDialogStatus = alertDialog.findViewById(R.id.tv_recharge_dialogue_status);
        TextView tvRechargeDialogAmount = alertDialog.findViewById(R.id.tv_recharge_dialogue_amount);
        Button btnRechargeDialog = alertDialog.findViewById(R.id.btn_recharge_dialog);
        tv_recharge_dialog_title.setText("Payment Successful!");
        tvRechargeDialogNumber.setText("Transaction ID : " + uniqueId);
        tvRechargeDialogAmount.setText("Amount : " + amount);
        tvRechargeDialogStatus.setText("Status : " + "Success");
        btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();

            }
        });
    }

    private String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }



}