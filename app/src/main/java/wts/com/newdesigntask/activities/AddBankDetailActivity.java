package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class AddBankDetailActivity extends AppCompatActivity {

    TextView tvBankName;
    EditText etAccountHolderName,etAccountNumber,etIfscCode;
    Button btnAddDetails;

    Spinner spinnerAccountType;
    ArrayList<String> accountTypeList;

    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;

    ArrayList<String> bankNameArrayList, bankIdList;
    String selectedBankName="select", selectedAccountType = "select",selectedBankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_detail);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddBankDetailActivity.this);
        userId= sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo= sharedPreferences.getString("deviceInfo",null);

        initViews();
        getBanks();

        btnAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputs())
                {
                    addBankDetails();
                }
                else
                {
                    Toast.makeText(AddBankDetailActivity.this, "All Fields Are Mandatory!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void addBankDetails() {
        ProgressDialog progressDialog =new ProgressDialog(AddBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        String accountHolderName=etAccountHolderName.getText().toString().trim();
        String accountHolderNumber=etAccountNumber.getText().toString().trim();
        String ifsc=etIfscCode.getText().toString().trim();
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().addBankDetails(userId,selectedBankId,accountHolderNumber,
                ifsc,accountHolderName,selectedAccountType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));

                        String statusCode=responseObject.getString("status");

                        if (statusCode.equalsIgnoreCase("201"))
                        {
                            String beneId=responseObject.getString("data");
                            progressDialog.dismiss();
                            Intent intent=new Intent(AddBankDetailActivity.this,UploadbankDocumentsActivity.class);
                            intent.putExtra("bene_id",beneId);
                            startActivity(intent);
                            finish();

                        }

                        else
                        {
                            String message=responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setCancelable(false)
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }





                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        return !selectedBankName.equalsIgnoreCase("select") && !TextUtils.isEmpty(etAccountHolderName.getText())
                && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscCode.getText());
    }

    private void initViews() {
        tvBankName=findViewById(R.id.tv_bank_name);
        etAccountHolderName=findViewById(R.id.et_account_holder_name);
        etAccountNumber=findViewById(R.id.et_account_number);
        etIfscCode=findViewById(R.id.et_ifsc_number);
        btnAddDetails=findViewById(R.id.btn_add_details);

        spinnerAccountType = findViewById(R.id.account_type_spinner);

        accountTypeList = new ArrayList<>();

        accountTypeList.add("RELATIVE");
        accountTypeList.add("PRIMARY");

        HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerAccountType, new HintAdapter<String>(AddBankDetailActivity.this, "Account type", accountTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedAccountType = itemAtPosition;
                    }
                });
        hintSpinner1.init();

    }

    private void getBanks() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankForAeps();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("status");

                        if (responseCode.equalsIgnoreCase("200")) {
                            String dataSTr = jsonObject.getString("data");

                            JSONArray transactionArray = new JSONArray(dataSTr);

                            bankNameArrayList = new ArrayList<>();
                            bankIdList = new ArrayList<>();

                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("bankName");
                                String bankId = transactionObject.getString("id");

                                bankNameArrayList.add(responsebankName);
                                bankIdList.add(bankId);

                            }

                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(AddBankDetailActivity.this, bankNameArrayList, "Select Bank", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            selectedBankId = bankIdList.get(position);
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });


                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }
}