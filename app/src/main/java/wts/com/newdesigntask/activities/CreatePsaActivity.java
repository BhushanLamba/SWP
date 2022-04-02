package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class CreatePsaActivity extends AppCompatActivity {

    EditText etVleName,etLocation,etVleContactNo,etVleEmail,etVleShop,etVleAadhar,etVlePancard,etVlePincode;
    TextView tvState;
    AppCompatButton btnSubmit;

    String userId,name,location,contactNo,email,shop,aadhar,pancard,pincode,selectedState="NA";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_psa);
        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(CreatePsaActivity.this);
        name=sharedPreferences.getString("username",null);
        location=sharedPreferences.getString("address",null);
        contactNo=sharedPreferences.getString("mobileno",null);
        email=sharedPreferences.getString("email",null);
        shop=sharedPreferences.getString("shopName",null);
        aadhar=sharedPreferences.getString("adharcard",null);
        pancard=sharedPreferences.getString("pancard",null);
        userId=sharedPreferences.getString("userid",null);

        etVleName.setText(name);
        etLocation.setText(location);
        etVleContactNo.setText(contactNo);
        etVleEmail.setText(email);
        etVleShop.setText(shop);
        etVleAadhar.setText(aadhar);
        etVlePancard.setText(pancard);

        etVleName.setEnabled(false);
        etLocation.setEnabled(false);
        etVleContactNo.setEnabled(false);
        etVleEmail.setEnabled(false);
        etVleShop.setEnabled(false);
        etVleAadhar.setEnabled(false);
        etVlePancard.setEnabled(false);

        btnSubmit.setOnClickListener(v->
        {
            pincode=etVlePincode.getText().toString().trim();
            if (pincode.length()==6)
            {
                /*if (selectedState.equalsIgnoreCase("select"))
                {
                    createPsa();
                }
                else
                {
                    tvState.setError("Required");
                }*/
                createPsa();
            }
            else
            {
                etVlePincode.setError("Invalid");
            }
        });

    }

    private void createPsa() {
        final ProgressDialog progressDialog = new ProgressDialog(CreatePsaActivity.this);
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().createPsa("",userId,name,location,contactNo,email,
                shop,selectedState,pincode,aadhar,pancard);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");

                       progressDialog.dismiss();
                       new AlertDialog.Builder(CreatePsaActivity.this)
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
                        Toast.makeText(CreatePsaActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePsaActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreatePsaActivity.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        etVleName=findViewById(R.id.et_name);
        etLocation=findViewById(R.id.et_location);
        etVleContactNo=findViewById(R.id.et_contact_no);
        etVleEmail=findViewById(R.id.et_email);
        etVleShop=findViewById(R.id.et_shop);
        etVleAadhar=findViewById(R.id.et_aadhar);
        etVlePancard=findViewById(R.id.et_pancard);
        etVlePincode=findViewById(R.id.et_pincode);
        tvState=findViewById(R.id.tv_state_name);
        btnSubmit=findViewById(R.id.btn_submit);
    }
}