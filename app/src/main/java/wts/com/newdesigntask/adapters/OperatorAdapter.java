package wts.com.newdesigntask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wts.com.newdesigntask.R;
import wts.com.newdesigntask.models.OperatorsModel;
import wts.com.newdesigntask.myInterface.OperatorInterface;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.MyViewHolder> {

    ArrayList<OperatorsModel> operatorModelList;

    OperatorInterface operatorInterface;
    Context context;

   public void setMyInterface(OperatorInterface myInterface)
   {
       this.operatorInterface = myInterface;
   }

    public OperatorAdapter(ArrayList<OperatorsModel> arrayList, Context context) {
        this.operatorModelList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_operator_list, null, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.operatorName.setText(operatorModelList.get(position).getOperatorName());
        String operatorImg= operatorModelList.get(position).getOperatorImage();
        Picasso.get().load(operatorImg).into(holder.operatorImg);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                //dialog.dismiss();

                String operatorName= operatorModelList.get(position).getOperatorName();
                String operatorId= operatorModelList.get(position).getOperatorId();
                String operatorImage= operatorModelList.get(position).getOperatorImage();

                operatorInterface.operatorData(operatorName,operatorId,operatorImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return operatorModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView operatorName;
        ImageView operatorImg;
        LinearLayout linearLayout;

        public MyViewHolder( View itemView) {
            super(itemView);

            operatorName = itemView.findViewById(R.id.operatorName);
            operatorImg = itemView.findViewById(R.id.img_operator);
            linearLayout = itemView.findViewById(R.id.recyclerViewItemMainLayout);

        }
    }

}
