package wts.com.newdesigntask.plansFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.activities.PlansActivity;
import wts.com.newdesigntask.adapters.MyPlansAdaper;
import wts.com.newdesigntask.models.PlansModel;

public class DataFragment extends Fragment {
    ArrayList<PlansModel> dataArrayList;

    ListView topUpList;

    MyPlansAdaper myPlansAdaper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_data, container, false);

        dataArrayList= PlansActivity.dataArrayList;
        topUpList=view.findViewById(R.id.top_up_list);
        myPlansAdaper=new MyPlansAdaper(getContext(),getActivity(),dataArrayList);

        topUpList.setAdapter(myPlansAdaper);




        return view;
    }
}