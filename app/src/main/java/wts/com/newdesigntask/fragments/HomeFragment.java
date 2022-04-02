package wts.com.newdesigntask.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.CreatePsaActivity;
import wts.com.newdesigntask.activities.ElectricityActivity;
import wts.com.newdesigntask.activities.HomeDashboardActivity;
import wts.com.newdesigntask.activities.OnBoardPaySprintActivity;
import wts.com.newdesigntask.activities.PaySprintActivity;
import wts.com.newdesigntask.activities.PurchaseCouponActivity;
import wts.com.newdesigntask.activities.RechargeActivity;
import wts.com.newdesigntask.activities.SettlementActivity;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    ArrayList<SlideModel> mySliderList;
    LinearLayout prepaidLayout,dthLayout,aepsLayout,electricityLayout,purchaseCouponLayout;
    LinearLayout cashWithdrawLayout,balanceEnquiryLayout,miniStatementLayout,aadharPayLayout,settlementLayout;

    String headerKey = "1Vx1IGJMp/8Y7oMQtJcr0gj3gMsIEUy0SyDMkousZ0c=";
    String bodyKey = "pBjJSkMmtHhyOjkvP4YxPr6q+grB3HX1FF7OjAoJuLM=";
    String merchantKey = "C9jiYoYT+FzVpbqortkmPaUq+yFkHd5GFFC8GfrdYeY=";
    String merchantId = "610451056";
    int REQ_CODE_AEPS = 1001;
    String agentid,userId;
    SharedPreferences sharedPreferences;
    String aepsServiceType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        inhitViews(view);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        agentid=sharedPreferences.getString("agentId",null);
        userId=sharedPreferences.getString("userid",null);

        setImageSlider();
        handleClickEvents();

        return view;
    }

    private void handleClickEvents() {
        prepaidLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RechargeActivity.class);
            intent.putExtra("service", "PREPAID");
            intent.putExtra("balance", HomeDashboardActivity.balance);
            startActivity(intent);
        });

        dthLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RechargeActivity.class);
            intent.putExtra("service", "DTH");
            intent.putExtra("balance", HomeDashboardActivity.balance);
            startActivity(intent);
        });

        aepsLayout.setOnClickListener(view -> {
            /*Intent intent = new Intent(getActivity(), AepsHome.class);
            Utility utility = Utility.getInstance();
            intent.putExtra("header", utility.encryptHeader(getHeaderJson(), headerKey));
            intent.putExtra("body", utility.encryptBody(getBodyJson(), bodyKey));
            intent.putExtra("receipt", true);
            startActivityForResult(intent, REQ_CODE_AEPS);*/
            //startActivity(new Intent(getContext(), PaySprintActivity.class));

        });

        electricityLayout.setOnClickListener(view -> {
            Intent intent=new Intent(getContext(), ElectricityActivity.class);
            intent.putExtra("service","Electricity");
            intent.putExtra("serviceId","6");
            intent.putExtra("balance", HomeDashboardActivity.balance);

            startActivity(intent);
        });

        purchaseCouponLayout.setOnClickListener(view -> {
            checkPsaStatus();
        });

        cashWithdrawLayout.setOnClickListener(v->
        {
            aepsServiceType="cw";
            checkKycStatus();
            /*Intent intent=new Intent(getContext(),PaySprintActivity.class);
            intent.putExtra("transactionType",aepsServiceType);
            intent.putExtra("balance","500");
            startActivity(intent);*/
        });

        balanceEnquiryLayout.setOnClickListener(v->
        {
            aepsServiceType="be";
            checkKycStatus();
            /*Intent intent=new Intent(getContext(),PaySprintActivity.class);
            intent.putExtra("transactionType","be");
            intent.putExtra("balance","500");
            startActivity(intent);*/
        });

        miniStatementLayout.setOnClickListener(v->
        {
            aepsServiceType="ms";
            checkKycStatus();
           /* Intent intent=new Intent(getContext(),PaySprintActivity.class);
            intent.putExtra("transactionType","ms");
            intent.putExtra("balance","500");
            startActivity(intent);*/
        });

        aadharPayLayout.setOnClickListener(v->
        {
            aepsServiceType="m";
            checkKycStatus();
            /*Intent intent=new Intent(getContext(),PaySprintActivity.class);
            intent.putExtra("transactionType","m");
            intent.putExtra("balance","500");
            startActivity(intent);*/
        });

        settlementLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), SettlementActivity.class));
        });

    }

    private void checkKycStatus() {
        final AlertDialog pDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkUserKycStatus(userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("data");

                        Intent intent;
                        if (responseCode.equalsIgnoreCase("true")) {
                            intent = new Intent(getContext(), PaySprintActivity.class);
                            intent.putExtra("transactionType",aepsServiceType);
                            intent.putExtra("balance","500");
                        } else {
                            intent = new Intent(getContext(), OnBoardPaySprintActivity.class);
                        }
                        startActivity(intent);

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
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


    private void checkPsaStatus() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().checkPsaStatus("",userId);
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
                            JSONObject dataObject=responseObject.getJSONObject("data");
                            String psaStatus=dataObject.getString("PsaStatus");
                            String vleId=dataObject.getString("Name");

                            if (psaStatus.equalsIgnoreCase("1"))
                            {
                                //PSA already created. Now Proceed To purchase coupon
                                Intent intent=new Intent(getContext(),PurchaseCouponActivity.class);
                                intent.putExtra("vleId",vleId);
                                startActivity(intent);


                            }
                            else
                            {
                                //PSA Not Created. First Create PSA
                                startActivity(new Intent(getContext(), CreatePsaActivity.class));
                            }
                            progressDialog.dismiss();

                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Please try after some time.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Please try after some time.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private String getHeaderJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantId", merchantId);
            jsonObject.put("Timestamp", Utility.getCurrentTimeStamp());
            jsonObject.put("merchantKey", merchantKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }*/

    private String getBodyJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AgentId", agentid);
            jsonObject.put("merchantService", "AEPS");
            jsonObject.put("Version", "1.0");
            jsonObject.put("Mobile", "test mobile no");
            jsonObject.put("Email", "test email id");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }


    private void setImageSlider() {
        mySliderList = new ArrayList<>();
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));

        imageSlider.setImageList(mySliderList, ScaleTypes.FIT);
    }

    private void inhitViews(View view) {
        imageSlider = view.findViewById(R.id.image_slider);
        prepaidLayout = view.findViewById(R.id.prepaid_layout);
        dthLayout = view.findViewById(R.id.dth_layout);
        aepsLayout = view.findViewById(R.id.aeps_layout);
        settlementLayout = view.findViewById(R.id.settlement_layout);
        electricityLayout = view.findViewById(R.id.electricity_layout);
        purchaseCouponLayout = view.findViewById(R.id.purchase_coupon_layout);
        cashWithdrawLayout = view.findViewById(R.id.cash_withdraw_layout);
        balanceEnquiryLayout = view.findViewById(R.id.balance_enquiry_layout);
        miniStatementLayout = view.findViewById(R.id.mini_statement_layout);
        aadharPayLayout = view.findViewById(R.id.aadhar_pay_layout);
    }
}