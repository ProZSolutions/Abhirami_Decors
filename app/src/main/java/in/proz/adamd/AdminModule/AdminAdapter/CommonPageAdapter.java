package in.proz.adamd.AdminModule.AdminAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.List;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

public class CommonPageAdapter extends RecyclerView.Adapter<CommonPageAdapter.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Context context;
    int commonPos;
    RecyclerView recyclerView;
    // ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;

    public  CommonPageAdapter(Activity context, List<CommonPojo> commonPojoList, int commonPos,
                         RecyclerView recyclerView, MKLoader loader){

        this.context=context;
        this.commonPojoList=commonPojoList;
        this.commonPos = commonPos;
        this.recyclerView=recyclerView;
        this.loader = loader;
      /*  progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);*/
        commonClass = new CommonClass();

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.common_page_item_row,null);
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
        holder.emp_id.setText(commonPojo.getEmp_no());
        holder.time.setText(commonPojo.getPunch_in());
        holder.name.setText(commonPojo.getName());
        if(!TextUtils.isEmpty(commonPojo.getContact())){
            if(commonPojo.getContact().equals("0")){
                holder.mobile.setText("-");
            }else{
                holder.mobile.setText(commonPojo.getContact());
            }
        }else{
            holder.mobile.setText("-");
        }
        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(commonPojo.getContact())){
                    if(!commonPojo.getContact().equals("0")){
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + commonPojo.getContact()));
                        context.startActivity(intent);
                     }
                }
            }
        });
     }

    @Override
    public int getItemCount() {
        return commonPojoList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView emp_id,time,name,mobile;
        public ProductViewHolder(View view) {
            super(view);
            emp_id = view.findViewById(R.id.emp_id);
            time = view.findViewById(R.id.time);
            name = view.findViewById(R.id.name);
            mobile = view.findViewById(R.id.mobile);
        }
    }

}
