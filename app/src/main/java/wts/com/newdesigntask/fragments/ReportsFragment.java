package wts.com.newdesigntask.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.AepsReportActivity;
import wts.com.newdesigntask.activities.BbpsReportActivity;
import wts.com.newdesigntask.activities.RechargeReportActivity;
import wts.com.newdesigntask.activities.SettlementReportActivity;
import wts.com.newdesigntask.activities.UpiReportActivity;
import wts.com.newdesigntask.activities.UtiReportActivity;
import wts.com.newdesigntask.activities.WalletSummaryActivity;

public class ReportsFragment extends Fragment {

    ConstraintLayout rechargeReportLayout,upiReportLayout,walletSummaryLayout,utiReportLayout,
            bbpsReportLayout,aepsReportLayout,settlementReportLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reports, container, false);
        initViews(view);

        rechargeReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), RechargeReportActivity.class));
        });

        upiReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), UpiReportActivity.class));
        });

        walletSummaryLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), WalletSummaryActivity.class));
        });

        utiReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), UtiReportActivity.class));
        });

        bbpsReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), BbpsReportActivity.class));

        });

        aepsReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), AepsReportActivity.class));
        });

        settlementReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), SettlementReportActivity.class));
        });

        return view;
    }

    private void initViews(View view) {
        rechargeReportLayout=view.findViewById(R.id.recharge_reports_layout);
        upiReportLayout=view.findViewById(R.id.upi_report_layout);
        walletSummaryLayout=view.findViewById(R.id.wallet_summary_layout);
        utiReportLayout=view.findViewById(R.id.uti_report_layout);
        bbpsReportLayout=view.findViewById(R.id.bbps_reports_layout);
        aepsReportLayout=view.findViewById(R.id.aeps_reports_layout);
        settlementReportLayout=view.findViewById(R.id.settlement_reports_layout);
    }
}