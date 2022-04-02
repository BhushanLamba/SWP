package wts.com.newdesigntask.activities;

import static wts.com.newdesigntask.activities.HomeDashboardActivity.balance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class SettlementActivity extends AppCompatActivity {

    Spinner spinnerAccountType, spinnerTransactionType, spinnerBankName;
    ArrayList<String> accountTypeList, transactionTypeList, bankNameList, accountHolderNameList,
            ifscCodeList, accountNumberList, beneIdList;
    String selectedTransactionType = "IMPS",
            selectedAccountType = "saving", userId, userName, selectedBankName = "select", selectedBeneId;
    boolean isWallet = true;
    EditText etAmount, etAccountHolderName, etAccountNumber, etIfscNumber;
    AppCompatButton btnSubmit, btnAddMoreBanks;
    SharedPreferences sharedPreferences;
    String amount, selectedAccountHolderName, selectedAccountNumber, selectedIfscNumber, panCard, mobileNo;
    SimpleDateFormat webServiceDateFormat;
    String currentDate;

    TextView tvBalance;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);

        inhitViews();
        setViews();

        tvBalance.setText("₹ "+balance);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettlementActivity.this);
        userId = sharedPreferences.getString("userid", null);
        panCard = sharedPreferences.getString("pancard", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        userName = sharedPreferences.getString("username", null);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        getAccountDetails();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        currentDate = webServiceDateFormat.format(newDate1.getTime());


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                        doSettlement();
                    } else {
                        showSnackbar("Select Above Details");
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
        });

        btnAddMoreBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(SettlementActivity.this, AddBankDetailActivity.class));
            }
        });

    }

    private void getAccountDetails() {
        final AlertDialog pDialog = new AlertDialog.Builder(SettlementActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAccountDetails(userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));


                        String responseCode = responseObject.getString("status");

                        if (responseCode.equalsIgnoreCase("200")) {
                            bankNameList = new ArrayList<>();
                            accountHolderNameList = new ArrayList<>();
                            accountNumberList = new ArrayList<>();
                            ifscCodeList = new ArrayList<>();
                            beneIdList = new ArrayList<>();

                            String dataStr = responseObject.getString("data");

                            JSONArray dataArray = new JSONArray(dataStr);
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String bankName = dataObject.getString("bankname");
                                String accountHolderName = dataObject.getString("name");
                                String accountNo = dataObject.getString("account");
                                String ifscCode = dataObject.getString("ifsc");
                                String beneId = dataObject.getString("beneid");

                                bankNameList.add(bankName);
                                accountHolderNameList.add(accountHolderName);
                                accountNumberList.add(accountNo);
                                ifscCodeList.add(ifscCode);
                                beneIdList.add(beneId);

                            }

                            if (bankNameList.size() < 5) {
                                btnAddMoreBanks.setVisibility(View.VISIBLE);
                            }

                            HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerBankName, new HintAdapter<String>(SettlementActivity.this, "Bank Name", bankNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankNameList.get(position);
                                            selectedAccountHolderName = accountHolderNameList.get(position);
                                            selectedAccountNumber = accountNumberList.get(position);
                                            selectedIfscNumber = ifscCodeList.get(position);
                                            selectedBeneId = beneIdList.get(position);

                                            etAccountHolderName.setText(selectedAccountHolderName);
                                            etAccountNumber.setText(selectedAccountNumber);
                                            etIfscNumber.setText(selectedIfscNumber);

                                            etAccountHolderName.setEnabled(false);
                                            etAccountNumber.setEnabled(false);
                                            etIfscNumber.setEnabled(false);
                                        }
                                    });
                            hintSpinner1.init();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                    .setMessage("Please add Bank Details first.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(SettlementActivity.this, AddBankDetailActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void doSettlement() {
        final AlertDialog pDialog = new AlertDialog.Builder(SettlementActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        /*if (selectedTransactionType.equalsIgnoreCase("IMPS"))
            selectedTransactionType = "IFS";
        else if (selectedTransactionType.equalsIgnoreCase("RTGS"))
            selectedTransactionType = "RGS";
        else if (selectedTransactionType.equalsIgnoreCase("NEFT"))
            selectedTransactionType = "NEFT";*/

        /*if (selectedPaymentType.equalsIgnoreCase("WALLET"))
            selectedPaymentType = "2";
        else
            selectedPaymentType = "1";
*/

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().moveToBank(userId, selectedBeneId, amount,
                selectedTransactionType, selectedAccountNumber, selectedBankName, selectedIfscNumber, selectedAccountHolderName,
                selectedAccountType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("status");
                        if (responseCode.equalsIgnoreCase("200") || responseCode.equalsIgnoreCase("400")) {
                            pDialog.dismiss();


                            String status = responseObject.getString("message");
                            new AlertDialog.Builder(SettlementActivity.this)
                                    .setTitle("Transaction Status")
                                    .setMessage(status)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong." + t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {

        if (!TextUtils.isEmpty(etAmount.getText()) && !selectedTransactionType.equalsIgnoreCase("select")
                && !TextUtils.isEmpty(etAccountHolderName.getText()) && !selectedBankName.equalsIgnoreCase("select")
                && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscNumber.getText())
                && !selectedAccountType.equalsIgnoreCase("select")) {
            amount = etAmount.getText().toString().trim();
            selectedAccountHolderName = etAccountHolderName.getText().toString().trim();
            selectedAccountNumber = etAccountNumber.getText().toString().trim();
            selectedIfscNumber = etIfscNumber.getText().toString().trim();
            return true;
        } else {
            return false;
        }
    }

    private void setViews() {
        accountTypeList = new ArrayList<>();
        transactionTypeList = new ArrayList<>();


        accountTypeList.add("Saving");
        accountTypeList.add("Current");

        transactionTypeList.add("IMPS");
        transactionTypeList.add("NEFT");
        transactionTypeList.add("RTGS");

        HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerAccountType, new HintAdapter<>(SettlementActivity.this, "Account type", accountTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedAccountType = itemAtPosition;
                    }
                });
        hintSpinner1.init();


        HintSpinner<String> hintSpinner3 = new HintSpinner<>(spinnerTransactionType, new HintAdapter<>(SettlementActivity.this, "Transaction Mode", transactionTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedTransactionType = itemAtPosition;

                    }
                });
        hintSpinner3.init();

    }

    private void inhitViews() {

        etAmount = findViewById(R.id.et_amount);
        etAccountHolderName = findViewById(R.id.et_account_holder_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etIfscNumber = findViewById(R.id.et_ifsc_number);
        spinnerAccountType = findViewById(R.id.account_type_spinner);
        spinnerTransactionType = findViewById(R.id.transaction_type_spinner);
        spinnerBankName = findViewById(R.id.bank_name_spinner);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddMoreBanks = findViewById(R.id.btn_add_banks);
        tvBalance = findViewById(R.id.tv_balance);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.settlement_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}