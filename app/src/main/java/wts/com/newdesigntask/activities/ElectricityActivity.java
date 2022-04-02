package wts.com.newdesigntask.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class ElectricityActivity extends AppCompatActivity {

    EditText etConsumerNumber;
    TextView tvOperator;
    Button btnProceed, btnCancel;

    ImageView imgBack;
    TextView tvTitle,tvBalance;

    SharedPreferences sharedPreferences;
    String userid;
    String selectedOperatorId = "select";
    ArrayList<String> operatorNameList, operatorIdList;
    String billNumber, dueDate, customerName, amount, billFetchId,billDate;
    SpinnerDialog operatorDialog;
    String serviceType, serviceId;
    String mobileNumber;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);
        inhitViews();

        serviceType = getIntent().getStringExtra("service");
        serviceId = getIntent().getStringExtra("serviceId");
        String balance = getIntent().getStringExtra("balance");
        tvTitle.setText(serviceType);
        tvBalance.setText("₹ "+balance);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricityActivity.this);
        userid = sharedPreferences.getString("userid", null);
        mobileNumber = sharedPreferences.getString("mobileno", null);

        imgBack.setOnClickListener(v ->
        {
            finish();
        });

        getOperators();

        btnCancel.setOnClickListener(v -> finish());

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                        //showMpinDialog();
                        fetchBill();
                        //showTpinDialog();
                    } else {
                        showSnackbar("Above fields are mandatory.");
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
        });


    }

    private void fetchBill() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().fetchBill("", userid, serviceId, selectedOperatorId, consumerNumber,
                mobileNumber);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("status");

                        if (responseCode.equalsIgnoreCase("200")) {

                            JSONObject dataObject = responseObject.getJSONObject("data");

                            amount = dataObject.getString("DueAmount");
                            dueDate = dataObject.getString("DueDate");
                            customerName = dataObject.getString("ConsumerName");
                            billNumber = dataObject.getString("ConsumerNo");
                            billFetchId = dataObject.getString("fetchBillID");
                            billDate = dataObject.getString("BillDate");


                            pDialog.dismiss();


                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_dialog_layout, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvCostumerName = view1.findViewById(R.id.tv_customer_name);
                            final TextView tvBillDate = view1.findViewById(R.id.tv_bill_date);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            final TextView tvDueDate = view1.findViewById(R.id.tv_due_date);
                            TextView tvCaNumber = view1.findViewById(R.id.tv_tel);
                            Button btnProceedToPay = view1.findViewById(R.id.btn_proceed_to_pay);
                            Button btnCancel = view1.findViewById(R.id.btn_cancel);

                            tvCostumerName.setText(customerName);
                            tvBillDate.setText(dueDate);
                            tvBillAmount.setText("₹ " + amount);
                            tvDueDate.setText(dueDate);
                            tvCaNumber.setText(billNumber);

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    builder.dismiss();
                                }
                            });

                            btnProceedToPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    payBill();
                                    builder.dismiss();

                                }
                            });
                            builder.show();

                        } else {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                            .setTitle("Message")
                            .setMessage("Something went wrong\nPlease Try After Sometime")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                        .setTitle("Message")
                        .setMessage("Something went wrong\nPlease Try After Sometime")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void payBill() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();

        /*Call<JsonObject> call = RetrofitClient.getInstance().getApi().payElectricityBill(userid, consumerNumber, mobileNumber, customerName, amount, billNumber, inputPar1,
                "103.82.190.195", "Electricity", macAddress);*/

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBill("",userid,"",selectedOperatorId,consumerNumber,
                mobileNumber,amount,billFetchId,customerName,billDate,dueDate);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("status");
                        if (responseCode.equalsIgnoreCase("200") || responseCode.equalsIgnoreCase("400")) {
/*
                                JSONObject dataObject = responseObject.getJSONObject("data");

                                custId = transactionObject.getString("custid");
                                tokenKey = transactionObject.getString("TOKENKEY");
                                sType = transactionObject.getString("Stype");
                                number = transactionObject.getString("number");
                                amount = transactionObject.getString("amount");
                                comm = transactionObject.getString("comm");
                                surcharge = transactionObject.getString("surcharge");
                                cost = transactionObject.getString("cost");
                                balance = transactionObject.getString("balance");
                                tDateTime = transactionObject.getString("tdatetime");
                                accountNo = transactionObject.getString("accountno");
                                status = transactionObject.getString("status");
                                transactionId = transactionObject.getString("transactionid");
                                brId = transactionObject.getString("brid");
                                opName = transactionObject.getString("opname");*/
                            String status = responseObject.getString("message");

                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(billNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                    finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this)
                                    .setTitle("Message!!!")
                                    .setCancelable(false)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this)
                                .setTitle("Message!!!")
                                .setCancelable(false)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this)
                            .setTitle("Message!!!")
                            .setCancelable(false)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this)
                        .setTitle("Message!!!")
                        .setCancelable(false)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private boolean checkInputs() {
        return !TextUtils.isEmpty(etConsumerNumber.getText()) &&
                !selectedOperatorId.equalsIgnoreCase("select");
    }

    private void getOperators() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ElectricityActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators("AUTH_KEY", "deviceId",
                "deviceInfo", userid, serviceId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        JSONArray jsonArray = jsonObject1.getJSONArray("data");

                        //ArrayList<OperatorsModel> operatorModelArrayList = new ArrayList<>();
                        operatorNameList = new ArrayList<>();
                        operatorIdList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //OperatorsModel operatorModel = new OperatorsModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            /*operatorModel.setOperatorName(operatorName);
                            operatorModel.setOperatorId(operatorId);

                            operatorModelArrayList.add(operatorModel);*/


                            String operatorName = jsonObject.getString("OperatorName");
                            operatorNameList.add(operatorName);
                            String operatorId = jsonObject.getString("Id");
                            operatorIdList.add(operatorId);

                        }

                        tvOperator.setOnClickListener(v -> {
                            operatorDialog = new SpinnerDialog(ElectricityActivity.this, operatorNameList, "Select Operator", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                            operatorDialog.setCancellable(true);
                            operatorDialog.setShowKeyboard(false);
                            operatorDialog.bindOnSpinerListener((item, position) -> {
                                tvOperator.setText(item);
                                selectedOperatorId = operatorIdList.get(position);

                            });

                            operatorDialog.showSpinerDialog();
                        });
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this).setTitle("Status")
                                .setMessage("Please try after some time.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", (dialog, which) -> finish()).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Status")
                            .setMessage("Please try after some time.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialog, which) -> finish()).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this).setTitle("Status")
                        .setMessage("Please try after some time.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialog, which) -> finish()).show();
            }
        });

    }

    private void inhitViews() {

        etConsumerNumber = findViewById(R.id.et_consumer_number);
        tvOperator = findViewById(R.id.tv_operator_name);
        btnProceed = findViewById(R.id.btn_proceed);
        btnCancel = findViewById(R.id.btn_cancel);
        tvTitle = findViewById(R.id.activity_title);
        tvBalance = findViewById(R.id.tv_balance);
        imgBack = findViewById(R.id.back_button);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.electricity_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}