package wts.com.newdesigntask.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.CreditDebitBalanceActivity;
import wts.com.newdesigntask.activities.FundTransferActivity;
import wts.com.newdesigntask.activities.LoginActivity;

public class MoreFragment extends Fragment {

    ConstraintLayout fundRequestLayout,logoutLayout,creditBalanceLayout,debitBalanceLayout;
    SharedPreferences sharedPreferences;
    String userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more, container, false);
        initViews(view);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        userType=sharedPreferences.getString("usertype",null);

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        fundRequestLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), FundTransferActivity.class));
        });


        if (!userType.equalsIgnoreCase("Retailer")) {
            creditBalanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CreditDebitBalanceActivity.class);
                    intent.putExtra("title", "Credit Balance");
                    startActivity(intent);
                }
            });

            debitBalanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CreditDebitBalanceActivity.class);
                    intent.putExtra("title", "Debit Balance");
                    startActivity(intent);
                }
            });
        }


        return view;
    }

    private void showLogoutDialog() {
        final View view1 = LayoutInflater.from(getContext()).inflate(R.layout.logout_dialog_layout, null, false);
        final AlertDialog logoutDialog = new AlertDialog.Builder(getContext()).create();
        logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutDialog.setCancelable(false);
        logoutDialog.setView(view1);
        logoutDialog.show();

        final Button btnCancel=logoutDialog.findViewById(R.id.btn_cancel);
        Button btnYes=logoutDialog.findViewById(R.id.btn_yes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    private void initViews(View view) {
        logoutLayout=view.findViewById(R.id.logout_layout);
        fundRequestLayout=view.findViewById(R.id.fund_request_layout);
        creditBalanceLayout=view.findViewById(R.id.credit_balance_layout);
        debitBalanceLayout=view.findViewById(R.id.debit_balance_layout);
    }
}