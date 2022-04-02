package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.fragments.HomeFragment;
import wts.com.newdesigntask.fragments.MoreFragment;
import wts.com.newdesigntask.fragments.ProfileFragment;
import wts.com.newdesigntask.fragments.ReportsFragment;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class HomeDashboardActivity extends AppCompatActivity {

    LinearLayout homeLayout,profileLayout,reportLayout,moreLayout;
    ImageView imgHome,imgProfile,imgReport,imgMore;
    TextView tvAddMoney,tvBalance;
    String userId;
    public static String balance;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);
        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this);
        userId=sharedPreferences.getString("userid",null);

        loadFragment(new HomeFragment());

        handleClickEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    private void getBalance() {
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().getBalance("",userId,"main");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                    balance=responseObject.getString("data");

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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }

    private void handleClickEvents() {

        tvAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeDashboardActivity.this,AddMoneyActivity.class));
            }
        });


        homeLayout.setOnClickListener(v->
        {
            imgHome.setImageResource(R.drawable.home1);
            imgProfile.setImageResource(R.drawable.user);
            imgReport.setImageResource(R.drawable.report);
            imgMore.setImageResource(R.drawable.more);

            loadFragment(new HomeFragment());
        });

        imgProfile.setOnClickListener(v->
        {
            imgHome.setImageResource(R.drawable.home);
            imgProfile.setImageResource(R.drawable.user1);
            imgReport.setImageResource(R.drawable.report);
            imgMore.setImageResource(R.drawable.more);

            loadFragment(new ProfileFragment());
        });

        imgReport.setOnClickListener(v->
        {
            imgHome.setImageResource(R.drawable.home);
            imgProfile.setImageResource(R.drawable.user);
            imgReport.setImageResource(R.drawable.report1);
            imgMore.setImageResource(R.drawable.more);

            loadFragment(new ReportsFragment());
        });

        imgMore.setOnClickListener(v->
        {
            imgHome.setImageResource(R.drawable.home);
            imgProfile.setImageResource(R.drawable.user);
            imgReport.setImageResource(R.drawable.report);
            imgMore.setImageResource(R.drawable.more1);
            loadFragment(new MoreFragment());
        });
    }

    private void initViews() {
        homeLayout=findViewById(R.id.home_layout);
        profileLayout=findViewById(R.id.profile_layout);
        reportLayout=findViewById(R.id.report_layout);
        moreLayout=findViewById(R.id.more_layout);
        imgHome=findViewById(R.id.img_home);
        imgProfile=findViewById(R.id.img_profile);
        imgReport=findViewById(R.id.img_report);
        imgMore=findViewById(R.id.img_more);
        tvAddMoney=findViewById(R.id.tv_add_money);
        tvBalance=findViewById(R.id.tv_main_wallet);
    }
}