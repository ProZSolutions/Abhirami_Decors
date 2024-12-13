package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ProductViewHolder>{
    Activity context;
    ProgressDialog progressDialog;
    CommonClass commonClass;
    RecyclerView recyclerView;

    List<CommonPojo> commonPojoList;
    public ShiftAdapter(Activity context, List<CommonPojo> commonPojoList, RecyclerView recyclerView){
        this.context=context;
        this.commonPojoList=commonPojoList;
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.recyclerView = recyclerView;
        commonClass = new CommonClass();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_shift_row,null);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = commonPojoList.get(position);
        holder.shift_name.setText(commonPojo.getShift_name());
      //  holder.start_time.setText(commonPojo.getStart_time());
    //    holder.end_time.setText(commonPojo.getEnd_time());
        if(!TextUtils.isEmpty(commonPojo.getHis_isflexi())){
            if(commonPojo.getHis_isflexi().contains("0")){
                holder.flex.setText("No");
            }else{
                holder.flex.setText("Yes");
            }
        }
        if(!TextUtils.isEmpty(commonPojo.getStart_time())){
            String[] arr = commonPojo.getStart_time().split(":");
            StringBuilder sb = new StringBuilder();
            int hourOfDay = Integer.parseInt(arr[0]);
            if(hourOfDay>=12){
                sb.append(hourOfDay-12).append( ":" ).append(arr[1]).append(" PM");
            }else{
                sb.append(hourOfDay).append( ":" ).append(arr[1]).append(" AM");
            }
            holder.start_time.setText(sb);
        }
        if(!TextUtils.isEmpty(commonPojo.getEnd_time())){
            String[] arr = commonPojo.getEnd_time().split(":");
            StringBuilder sb = new StringBuilder();
            int hourOfDay = Integer.parseInt(arr[0]);
            if(hourOfDay>=12){
                sb.append(hourOfDay-12).append( ":" ).append(arr[1]).append(" PM");
            }else{
                sb.append(hourOfDay).append( ":" ).append(arr[1]).append(" AM");
            }
            holder.end_time.setText(sb);
        }

    }

    @Override
    public int getItemCount() {
        return commonPojoList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView shift_name,start_time,end_time,flex;

        public ProductViewHolder(View view) {
            super(view);
            shift_name = view.findViewById(R.id.shift_name);
            start_time = view.findViewById(R.id.start_time);
            end_time = view.findViewById(R.id.end_time);
            flex = view.findViewById(R.id.flex);




        }
    }
}
