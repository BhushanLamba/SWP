package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.adapters.RechargeReportAdapter;
import wts.com.newdesigntask.adapters.UtiReportAdapter;
import wts.com.newdesigntask.models.RechargeReportModel;
import wts.com.newdesigntask.models.UtiReportModel;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class UtiReportActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UtiReportModel> utiReportModelArrayList;
    SharedPreferences sharedPreferences;
    String userId;
    String toDate,fromDate;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    ImageView imgNoDataFound;
    String couponType,vleId,transactionId,openingBalance,amount,commission,surcharge,tds,gst,closingBalance,status,date,quantity;
    ImageView imgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uti_report);

        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(UtiReportActivity.this);
        userId=sharedPreferences.getString("userid",null);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar fromCalendar = Calendar.getInstance();
        int year = fromCalendar.get(Calendar.YEAR);
        int month = fromCalendar.get(Calendar.MONTH);
        int day = fromCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        newDate1.add(Calendar.MONTH,-1);
        fromDate = webServiceDateFormat.format(newDate1.getTime());


        Calendar toCalendar = Calendar.getInstance();
        int toYear = toCalendar.get(Calendar.YEAR);
        int toMonth = toCalendar.get(Calendar.MONTH);
        int toDay = toCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate2 = Calendar.getInstance();
        newDate2.set(toYear, toMonth, toDay);
        toDate = webServiceDateFormat.format(newDate2.getTime());

        getReport();

        imgFilter.setOnClickListener(v->
        {
            showBottomDateDialog();
        });

    }

    private void showBottomDateDialog() {
        View view = getLayoutInflater().inflate(R.layout.filter_view_bottom, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        Button filterBtn = view.findViewById(R.id.filterBtn);
        TextView tvFromDate = view.findViewById(R.id.tv_from_date);
        TextView tvToDate = view.findViewById(R.id.tv_to_date);
        LinearLayout fromDateLayout = view.findViewById(R.id.from_date_layout);
        LinearLayout toDateLayout = view.findViewById(R.id.to_date_layout);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(UtiReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromDate.setText(simpleDateFormat.format(newDate1.getTime()));
                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);

                fromDatePicker.show();

            }
        });
        toDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(UtiReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);
                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));
                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);

                fromDatePicker.show();

            }
        });

        filterBtn.setOnClickListener(v -> {
            if (tvFromDate.getText().toString().equalsIgnoreCase("Select Date") ||
                    tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                new androidx.appcompat.app.AlertDialog.Builder(UtiReportActivity.this).setMessage("Please select both From date and To Date")
                        .setPositiveButton("Ok", null).show();
            } else {
                dialog.dismiss();
                getReport();
            }
        });

        dialog.show();

    }

    private void getReport() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(UtiReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUtiReport("",userId,fromDate,toDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {

                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("status");

                        if (statusCode.equalsIgnoreCase("200")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.GONE);


                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            utiReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                UtiReportModel utiReportModel = new UtiReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                couponType = jsonObject.getString("CouponTypes");
                                vleId = jsonObject.getString("VleId");
                                transactionId = jsonObject.getString("UniqueTransactionId");
                                openingBalance = jsonObject.getString("OpeningBal");
                                amount = jsonObject.getString("Amount");
                                commission = jsonObject.getString("Commission");
                                surcharge = jsonObject.getString("Surcharge");
                                tds = jsonObject.getString("Tds");
                                gst = jsonObject.getString("Gst");
                                closingBalance = jsonObject.getString("ClosingBal");
                                status = jsonObject.getString("Status");
                                date = jsonObject.getString("CreatedOn");
                                quantity = jsonObject.getString("Qty");

                                utiReportModel.setCouponType(couponType);
                                utiReportModel.setVleId(vleId);
                                utiReportModel.setTransactionId(transactionId);
                                utiReportModel.setOpeningBalance(openingBalance);
                                utiReportModel.setAmount(amount);
                                utiReportModel.setCommission(commission);
                                utiReportModel.setTds(tds);
                                utiReportModel.setGst(gst);
                                utiReportModel.setClosingBalance(closingBalance);
                                utiReportModel.setStatus(status);
                                utiReportModel.setQuantity(quantity);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = date.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    utiReportModel.setDate(outputDate);
                                    utiReportModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                utiReportModelArrayList.add(utiReportModel);

                            }


                            recyclerView.setLayoutManager(new LinearLayoutManager(UtiReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            UtiReportAdapter utiReportAdapter = new UtiReportAdapter(UtiReportActivity.this,UtiReportActivity.this,
                                    utiReportModelArrayList);
                            recyclerView.setAdapter(utiReportAdapter);
                            pDialog.dismiss();
                        }

                        else  {
                            pDialog.dismiss();

                            imgNoDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    pDialog.dismiss();
                    imgNoDataFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                imgNoDataFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

    }

    private void initViews() {
        recyclerView=findViewById(R.id.all_report_recycler);
        imgNoDataFound=findViewById(R.id.img_no_data_found);
        imgFilter=findViewById(R.id.img_filter);
    }
}