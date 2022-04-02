package wts.com.newdesigntask.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class LoginActivity extends AppCompatActivity {

    EditText etUserId, etUserPassword;

    Button btnLogin, btnSignUp, btnForgetPassword;

    String userid, username, usertype, mobileno, pancard, aadharCard, emailId,agentId,address,shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inhitViews();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {
                    if (checkInternetState()) {

                        login();


                    } else {
                        showSnackbar();
                    }
                }
            }
        });

    }

    private void login() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait while logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String loginUserName = etUserId.getText().toString().trim();
        final String password = etUserPassword.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login( "",loginUserName, password, "deviceId", "deviceInfo");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("status");

                        if (statuscode.equalsIgnoreCase("200")) {


                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            userid = jsonObject.getString("UserId");
                            username = jsonObject.getString("Name");
                            usertype = jsonObject.getString("RoleType");
                            mobileno = jsonObject.getString("MobileNo");
                            pancard = jsonObject.getString("PanCard");
                            aadharCard = jsonObject.getString("AadharNo");
                            emailId = jsonObject.getString("EmailId");
                            agentId = jsonObject.getString("AgentId");
                            address = jsonObject.getString("Address");
                            shop = jsonObject.getString("ShopName");


                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("pancard", pancard);
                            editor.putString("mobileno", mobileno);
                            editor.putString("usertype", usertype);
                            editor.putString("adharcard", aadharCard);
                            editor.putString("loginUserName", loginUserName);
                            editor.putString("password", password);
                            editor.putString("email", emailId);
                            editor.putString("agentId", agentId);
                            editor.putString("address", address);
                            editor.putString("shopName", shop);
                            editor.apply();

                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeDashboardActivity.class);
                            finish();
                            startActivity(intent);
                        }  else {
                            String message=jsonObject1.getString("data");

                            progressDialog.dismiss();
                            new AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null).show();
                        e.printStackTrace();
                    }

                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                progressDialog.dismiss();
                new AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null).show();

            }
        });
    }

    private void inhitViews() {
        etUserId = findViewById(R.id.et_user_id);
        etUserPassword = findViewById(R.id.et_user_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnForgetPassword = findViewById(R.id.btn_forget_password);
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etUserId.getText())) {
            if (!TextUtils.isEmpty(etUserPassword.getText())) {

                return true;
            } else {
                etUserPassword.setError("Password can't be empty.");
                return false;
            }
        } else {
            etUserId.setError("User ID can't be empty.");
            return false;
        }
    }
}