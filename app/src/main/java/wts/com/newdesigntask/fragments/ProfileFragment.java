package wts.com.newdesigntask.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wts.com.newdesigntask.R;

public class ProfileFragment extends Fragment {

    TextView tvName,tvMobileNo,tvAadhar,tvPanCard,tvEmailId,tvUserType;
    String name,mobileNo,aadhar,pancard,emailId,userType;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());

        name=sharedPreferences.getString("username",null);
        mobileNo=sharedPreferences.getString("mobileno",null);
        aadhar=sharedPreferences.getString("adharcard",null);
        pancard=sharedPreferences.getString("pancard",null);
        emailId=sharedPreferences.getString("email",null);
        userType=sharedPreferences.getString("usertype",null);

        tvName.setText(name);
        tvMobileNo.setText(mobileNo);
        tvAadhar.setText(aadhar);
        tvPanCard.setText(pancard);
        tvEmailId.setText(emailId);
        tvUserType.setText(userType);

        return view;
    }

    private void initViews(View view) {
        tvName=view.findViewById(R.id.tv_name);
        tvMobileNo=view.findViewById(R.id.tv_mobile_no);
        tvAadhar=view.findViewById(R.id.tv_aadhar_card);
        tvPanCard=view.findViewById(R.id.tv_pancard);
        tvEmailId=view.findViewById(R.id.tv_email_id);
        tvUserType=view.findViewById(R.id.tv_user_type);
    }
}