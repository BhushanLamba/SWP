package wts.com.newdesigntask.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.adapters.CircleAdapter;
import wts.com.newdesigntask.adapters.OperatorAdapter;
import wts.com.newdesigntask.adapters.RechargeReportAdapter;
import wts.com.newdesigntask.models.OperatorsModel;
import wts.com.newdesigntask.models.RechargeReportModel;
import wts.com.newdesigntask.myInterface.CircleInterface;
import wts.com.newdesigntask.myInterface.OperatorInterface;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class RechargeActivity extends AppCompatActivity {

    String service;
    LinearLayout mobileOperatorContainer;
    ConstraintLayout dthOperatorContainer;
    TextView tvDthOperator;
    LinearLayout operatorLayout, circleLayout;
    TextView tvOperator, tvCircle;
    String userId;
    SharedPreferences sharedPreferences;
    Dialog operatorDialog, circleDialog;
    String selectedOperatorId, selectedStateName = "select", selectedOperatorImage, selectedOperatorName = "Select Operator";
    Button btnRecharge;
    EditText etRechargeNumber, etAmount;

    RecyclerView recyclerView;
    ArrayList<RechargeReportModel> rechargeReportModelArrayList;

    boolean isBrowsePlans = false;
    String rechargeNumber;

    Button btnMyPlans, btnBrowsePlans, btnMyInfo, btnDthPlans,btnHeavyRefresh;
    TextView tvDescription;
    String monthlyRecharge, customerName, status, nextRecharge, lastRechargeAmount, planName, balance;

    String[] stateNameArray = {"Assam", "Bihar Jharkhand", "Chennai", "Delhi NCR", "Gujarat", "Haryana",
            "Himachal Pradesh", "Jammu Kashmir", "Karnataka", "Kerala", "Kolkata", "Madhya Pradesh Chhattisgarh", "Maharashtra Goa",
            "Mumbai", "North East", "Orissa", "Punjab", "Rajasthan", "Tamil Nadu", "UP East", "UP West", "West Bengal"};

    ImageView imgDirector;
    private static final int PICK_CONTACT = 120;

    TextView tvTitle,tvBalance;
    ImageView imgOperator;

    @SuppressLint({"IntentReset", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initViews();
        service = getIntent().getStringExtra("service");
        String balance = getIntent().getStringExtra("balance");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this);
        userId = sharedPreferences.getString("userid", null);

        tvTitle.setText(service+" Recharge");
        tvBalance.setText("₹ "+balance);

        setViews();
        getService(service);
        getRecentReport();
        setCircle();


        operatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operatorDialog.show();
            }
        });

        circleLayout.setOnClickListener(view -> circleDialog.show());

        dthOperatorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operatorDialog.show();
            }
        });

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {

                    String number = etRechargeNumber.getText().toString().trim();
                    final String amount = etAmount.getText().toString().trim();
                    //doRecharge();
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setMessage("Amount = " + amount + "\n" + "Number = " + number + "\n" + "Operator = " + selectedOperatorName)
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    doRecharge();
                                }
                            }).show();

                }
            }
        });

        etRechargeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    if (service.equalsIgnoreCase("PREPAID") || service.equalsIgnoreCase("Postpaid")) {

                        isBrowsePlans = false;
                        rechargeNumber = etRechargeNumber.getText().toString();
                        hideKeyBoard();
                        getOperatorAndCirce();
                    }
                }

            }
        });

        btnBrowsePlans.setOnClickListener(v -> {
            if (checkInputNumber()) {
                Intent intent = new Intent(RechargeActivity.this, PlansActivity.class);
                intent.putExtra("operator", selectedOperatorName);
                intent.putExtra("commcircle", selectedStateName);
                startActivityForResult(intent, 1);
            }
        });

        btnMyPlans.setOnClickListener(view -> {

            if (checkInputsForMyPlans()) {
                String mobile = etRechargeNumber.getText().toString().trim();

                Intent intent = new Intent(RechargeActivity.this, MplanActivity.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("operator", selectedOperatorName);
                startActivityForResult(intent, 1);
            }

        });

        btnMyInfo.setOnClickListener(v -> {

            if (checkDthNumberForPlans()) {
                getDthUserInfo();
            }
        });

        btnDthPlans.setOnClickListener(view -> {

            if (checkDthNumberForPlans()) {
                Intent intent = new Intent(RechargeActivity.this, DthPlansActivity.class);
                intent.putExtra("operator", selectedOperatorName);
                startActivityForResult(intent, 1);
            }


        });

        imgDirector.setOnClickListener(view -> {
            @SuppressLint("IntentReset") Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, PICK_CONTACT);
        });

    }

    private void getDthUserInfo() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();


        String number = etRechargeNumber.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApiPlans().getDthUserInfo(selectedOperatorName, number);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        TextView tvMonthlyRecharge, tvBalance, tvCustomerName, tvStatus, tvNextRechargeDate, tvLastRechargeAmount, tvPlanName;
                        Button btnOk;
                        JSONArray recordsArray = responseObject.getJSONArray("records");
                        for (int i = 0; i < recordsArray.length(); i++) {
                            JSONObject recordsObject = recordsArray.getJSONObject(i);

                            try {
                                monthlyRecharge = recordsObject.getString("MonthlyRecharge");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                balance = recordsObject.getString("Balance");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                customerName = recordsObject.getString("customerName");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                status = recordsObject.getString("status");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                nextRecharge = recordsObject.getString("NextRechargeDate");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                lastRechargeAmount = recordsObject.getString("lastrechargeamount");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                planName = recordsObject.getString("planname");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.dth_user_info_dialog, null, false);
                        final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setCancelable(false);
                        builder.setView(view1);

                        tvMonthlyRecharge = view1.findViewById(R.id.tv_monthly_recharge);
                        tvBalance = view1.findViewById(R.id.tv_balance);
                        tvCustomerName = view1.findViewById(R.id.tv_customer_name);
                        tvStatus = view1.findViewById(R.id.tv_status);
                        tvNextRechargeDate = view1.findViewById(R.id.tv_next_recharge_date);
                        tvLastRechargeAmount = view1.findViewById(R.id.tv_last_recharge_amount);
                        tvPlanName = view1.findViewById(R.id.tv_plan_name);
                        btnOk = view1.findViewById(R.id.btn_ok);

                        tvMonthlyRecharge.setText("₹ " + monthlyRecharge);
                        tvBalance.setText("₹ " + balance);
                        tvCustomerName.setText(customerName);
                        tvStatus.setText(status);
                        tvNextRechargeDate.setText(nextRecharge);
                        tvLastRechargeAmount.setText("₹ " + lastRechargeAmount);
                        tvPlanName.setText(planName);

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builder.dismiss();
                                pDialog.dismiss();
                            }
                        });

                        builder.show();
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                                .setTitle("Alert!!!")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {

                    new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                            .setTitle("Alert!!!")
                            .setPositiveButton("OK", null)
                            .show();
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                        .setTitle("Alert!!!")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private boolean checkDthNumberForPlans() {
        if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
            return true;
        } else {
            new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }

    }

    private boolean checkInputsForMyPlans() {
        if (etRechargeNumber.length() == 10) {

            if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                return true;
            } else {
                new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                        .setPositiveButton("Ok", null).show();
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private boolean checkInputNumber() {
        if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
            return true;

        } else {
            new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                    .setPositiveButton("Ok", null).show();
            return false;

        }
    }

    private void setCircle() {
        circleDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
        circleDialog.setContentView(R.layout.circle_dialog);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

        circleDialog.getWindow().setLayout(width, height);

        circleDialog.getWindow().setGravity(Gravity.BOTTOM);
        circleDialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back);
        circleDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);

        RecyclerView rv = circleDialog.findViewById(R.id.recyclerView);
        ImageView cancelImg = circleDialog.findViewById(R.id.cancelImg);
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleDialog.dismiss();
            }
        });

        CircleAdapter circleAdapter = new CircleAdapter(stateNameArray);
        rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
        rv.setAdapter(circleAdapter);

        circleAdapter.setMyInterface(new CircleInterface() {
            @Override
            public void circleData(String circleName, String circleCode) {
                circleDialog.dismiss();
                selectedStateName = circleName;
                tvCircle.setText(circleName);
            }
        });

    }

    private void getOperatorAndCirce() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();


        rechargeNumber = etRechargeNumber.getText().toString();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperatorCircle("", rechargeNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        JSONObject recordsObject = responseObject.getJSONObject("data");

                        selectedOperatorName = recordsObject.getString("OperatorName");
                        selectedOperatorId = recordsObject.getString("OperatorId");
                        selectedStateName = recordsObject.getString("ComCircle");
                        selectedOperatorImage = recordsObject.getString("OpImage");

                        tvOperator.setText(selectedOperatorName);
                        tvCircle.setText(selectedStateName);
                        Picasso.get().load(selectedOperatorImage).into(imgOperator);

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void getRecentReport() {

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getRecentReport("", userId, service);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("status");

                        if (statusCode.equalsIgnoreCase("200")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            rechargeReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                RechargeReportModel rechargeReportModel = new RechargeReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String transactionId = jsonObject.getString("TransactionId");
                                String operatorName = jsonObject.getString("OperatorName");
                                String mobileNo = jsonObject.getString("MobileNo");
                                String amount = jsonObject.getString("Amount");
                                String commission = jsonObject.getString("Commission");
                                String closingBalance = jsonObject.getString("ClosingBal");
                                String openingBalance = jsonObject.getString("OpeningBal");
                                String transactionDate = jsonObject.getString("TransactionDate");
                                String status = jsonObject.getString("Status");
                                String serviceType = jsonObject.getString("ServiceType");
                                //imgUrl = jsonObject.getString("Image");
                                //brId = jsonObject.getString("brid");

                                rechargeReportModel.setTransactionId(transactionId);
                                rechargeReportModel.setOperatorName(operatorName);
                                rechargeReportModel.setMobileNo(mobileNo);
                                rechargeReportModel.setAmount(amount);
                                rechargeReportModel.setCommission(commission);
                                rechargeReportModel.setStatus(status);
                                rechargeReportModel.setServiceType(serviceType);
                                rechargeReportModel.setOpeningBalance(openingBalance);
                                rechargeReportModel.setClosingBalance(closingBalance);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = transactionDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    rechargeReportModel.setDate(outputDate);
                                    rechargeReportModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                rechargeReportModel.setStatus(status);

                                rechargeReportModelArrayList.add(rechargeReportModel);

                            }


                            recyclerView.setLayoutManager(new LinearLayoutManager(RechargeActivity.this,
                                    RecyclerView.VERTICAL, false));

                            RechargeReportAdapter rechargeReportAdapter = new RechargeReportAdapter(rechargeReportModelArrayList,
                                    RechargeActivity.this, RechargeActivity.this);
                            recyclerView.setAdapter(rechargeReportAdapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void doRecharge() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String number = etRechargeNumber.getText().toString().trim();
        final String amount = etAmount.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doRecharge("AUTH_KEY", userId, amount, service, number, selectedOperatorId);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statuscode = jsonObject1.getString("status");


                        if (statuscode.equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String responseNumber = jsonObject.getString("MobileNo");
                            String responseStatus = jsonObject.getString("Status");
                            String responseAmount = jsonObject.getString("Amount");
                            String responseTransactionId = jsonObject.getString("TransactionId");
                            String responseDateTime = jsonObject.getString("TransactionDate");
                            String responseOperator = jsonObject.getString("OperatorName");


                            @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                            String[] splitDate = responseDateTime.split("T");
                            try {
                                Date date = inputDateFormat.parse(splitDate[0]);
                                Date time = simpleDateFormat.parse(splitDate[1]);


                                String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                String outputTime = new SimpleDateFormat("hh:mm a").format(time);
                                Intent intent = new Intent(RechargeActivity.this, RechargeStatusActivity.class);
                                intent.putExtra("responseStatus", responseStatus);
                                intent.putExtra("responseNumber", responseNumber);
                                intent.putExtra("responseAmount", responseAmount);
                                intent.putExtra("responseTransactionId", responseTransactionId);
                                intent.putExtra("responseOperator", responseOperator);
                                intent.putExtra("outputDate", outputDate);
                                intent.putExtra("outputTime", outputTime);
                                startActivity(intent);
                                finish();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("400")) {
                            pDialog.dismiss();
                            String status = jsonObject1.getString("transactions");
                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);
                            builder.show();

                            ImageView imgRechargeDialogIcon = view1.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = view1.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogStatus = view1.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = view1.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = view1.findViewById(R.id.btn_recharge_dialog);

                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                            tvRechargeDialogStatus.setTextColor(Color.RED);
                            tvRechargeDialogStatus.setText("Status : " + status);
                            imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .setPositiveButton("Ok", null).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etRechargeNumber.getText())) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;
                } else {
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etAmount.setError("Amount can't be empty.");
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private void getService(final String serviceName) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getServices("AUTH_KEY", "deviceId", "deviceInfo", "userid");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("status");

                        if (statuscode.equalsIgnoreCase("200")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String serviceid = jsonObject.getString("Id");
                                String responseServiceName = jsonObject.getString("ServiceName");

                                if (responseServiceName.equalsIgnoreCase(serviceName)) {

                                    pDialog.dismiss();
                                    getOperators(serviceid);
                                    break;
                                }
                            }
                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            String errorMessage = jsonObject1.getString("response_msg");

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            pDialog.dismiss();

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();

                        new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                .setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
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
                new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                        .setTitle("Alert")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        });

    }

    private void getOperators(String serviceid) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators("AUTH_KEY", "deviceId", "deviceInfo", userId, serviceid);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        JSONArray jsonArray = jsonObject1.getJSONArray("data");

                        ArrayList<OperatorsModel> operatorModelArrayList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            OperatorsModel operatorModel = new OperatorsModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String operatorName = jsonObject.getString("OperatorName");

                            String operatorId = jsonObject.getString("Id");
                            String operatorImage = jsonObject.getString("Image");

                            operatorModel.setOperatorName(operatorName);
                            operatorModel.setOperatorId(operatorId);
                            operatorModel.setOperatorImage("http://dashboard.allcardfind.com/" + operatorImage);

                            operatorModelArrayList.add(operatorModel);

                        }

                        operatorDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
                        operatorDialog.setContentView(R.layout.operator_dialog);

                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
                        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

                        operatorDialog.getWindow().setLayout(width, height);

                        operatorDialog.getWindow().setGravity(Gravity.BOTTOM);
                        operatorDialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back);
                        operatorDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);


                        RecyclerView rv = operatorDialog.findViewById(R.id.recyclerView);
                        ImageView cancelImg = operatorDialog.findViewById(R.id.cancelImg);
                        cancelImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                operatorDialog.dismiss();
                            }
                        });
                        OperatorAdapter recyclerViewItemAdapter = new OperatorAdapter(operatorModelArrayList, RechargeActivity.this);
                        rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
                        rv.setAdapter(recyclerViewItemAdapter);

                        recyclerViewItemAdapter.setMyInterface(new OperatorInterface() {
                            @Override
                            public void operatorData(String operatorName, String operatorId,String operatorImage) {
                                operatorDialog.dismiss();
                                selectedOperatorId = operatorId;
                                selectedOperatorName = operatorName;
                                selectedOperatorImage=operatorImage;
                                if (service.equalsIgnoreCase("DTH")) {
                                    tvDthOperator.setText(selectedOperatorName);
                                } else {
                                    tvOperator.setText(operatorName);

                                }
                                Picasso.get().load(selectedOperatorImage).into(imgOperator);


                            }
                        });

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
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
                new AlertDialog.Builder(RechargeActivity.this).setTitle("Failed")
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private void initViews() {
        mobileOperatorContainer = findViewById(R.id.mobile_operator_container);
        dthOperatorContainer = findViewById(R.id.dth_operator_container);
        operatorLayout = findViewById(R.id.operator_layout);
        circleLayout = findViewById(R.id.circle_layout);
        tvOperator = findViewById(R.id.tv_operator);
        tvDthOperator = findViewById(R.id.tv_dth_operator);
        tvCircle = findViewById(R.id.tv_circle);
        btnRecharge = findViewById(R.id.btn_recharge);
        etRechargeNumber = findViewById(R.id.et_recharge_number);
        etAmount = findViewById(R.id.et_recharge_amount);
        recyclerView = findViewById(R.id.all_report_recycler);
        imgDirector = findViewById(R.id.img_directory);
        tvTitle = findViewById(R.id.tv_title);
        tvBalance = findViewById(R.id.tv_balance);
        imgOperator = findViewById(R.id.img_service_type);


        btnMyPlans = findViewById(R.id.btn_my_palns);
        btnBrowsePlans = findViewById(R.id.btn_browse_palns);
        btnMyInfo = findViewById(R.id.btn_my_info);
        btnDthPlans = findViewById(R.id.btn_browse_palns_dth);
        btnHeavyRefresh = findViewById(R.id.btn_heavy_refresh);
        tvDescription = findViewById(R.id.tv_description);
    }

    @SuppressLint("SetTextI18n")
    private void setViews() {
        if (service.equalsIgnoreCase("PREPAID")) {
            dthOperatorContainer.setVisibility(View.GONE);
            btnDthPlans.setVisibility(View.GONE);
            btnMyInfo.setVisibility(View.GONE);
            btnHeavyRefresh.setVisibility(View.GONE);
            etRechargeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});


        }
        else if (service.equalsIgnoreCase("Dth")) {
            mobileOperatorContainer.setVisibility(View.GONE);
            btnBrowsePlans.setVisibility(View.GONE);
            btnMyPlans.setVisibility(View.GONE);

        }
        else {
            dthOperatorContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                if (c != null && c.moveToFirst()) {
                    String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (number.contains("+91")) {
                        number = number.replace("+91", "");
                    }
                    if (number.contains(" ")) {
                        number = number.replace(" ", "");
                    }
                    number = number.trim();
                    etRechargeNumber.setText(number);

                }
            }
        }


        if (requestCode == 1) {
            if (data != null) {
                String amountPlan = data.getStringExtra("amount");
                String desc = data.getStringExtra("desc");
                etAmount.setText(amountPlan);
                tvDescription.setText(desc);

            }
        }
    }
}