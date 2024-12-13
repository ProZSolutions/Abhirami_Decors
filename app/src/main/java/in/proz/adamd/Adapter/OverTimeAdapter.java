package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverTimeAdapter extends RecyclerView.Adapter<OverTimeAdapter.ProductViewHolder> {
    Activity context;
    RecyclerView recyclerView;
    List<CommonPojo> commonPojoList;
    CommonClass commonClass = new CommonClass();
    MKLoader loader;
    int pos;
    public OverTimeAdapter(Activity context, List<CommonPojo> commonPojoList, RecyclerView recyclerView, MKLoader loader,
                         int pos){
        this.context=context;
        this.pos =pos;
        this.commonPojoList=commonPojoList;
        this.recyclerView=recyclerView;
        this.loader=loader;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_overtime_row,null);
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

    private String getTime(String string) {
        String[] arr1=string.split(" ");
        String[] arr2=arr1[1].split(":");
        int rt=0;
        Integer hr = Integer.parseInt(arr2[0]);
        String str_date;
        if(hr>=12){
            if(hr!=12){
                hr=hr-12;
            }

            str_date = hr+":"+arr2[1]+" PM";

        }else{
            str_date = hr+":"+arr2[1]+" AM";
        }
        return str_date;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = commonPojoList.get(position);
        holder.leave_type.setText(commonPojo.getLeave_type());
        holder.reason.setText(commonPojo.getReason());
        holder.date.setText(commonPojo.getDate());
        holder.from_date_time.setText(commonPojo.getFrom());
        holder.to_date_tim.setText(commonPojo.getTo());


        Log.d("status_value"," status "+commonPojo.getStatus());
        if(commonPojo.getStatus()!=null){
            String staut = commonPojo.getStatus().toLowerCase(Locale.ROOT);
            if(staut.startsWith("pend")){
                holder.approved_status.setText("Pending");
                Log.d("status_value"," pending ");
                holder.relativelayout.setVisibility(View.VISIBLE);
                holder.approved_by.setText("Pending");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.purple_shade_50));
                    holder.approved_status.setTextColor(context.getColorStateList(R.color.n_org));
                    holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_org));
                    holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.clock_icon, 0, 0, 0);
                    holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_org));
                    holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                    holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                    holder.request_layout.setVisibility(View.VISIBLE);
                    holder.approve_layout.setVisibility(View.GONE);
                }
            }else if(staut.contains("appr")  ){
                holder.approved_status.setText(commonPojo.getStatus());
                holder.approved_by.setText(commonPojo.getStatus_by());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_3_1));
                    holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
                    holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
                    holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);
                    holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_green));
                    holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                    holder.icon.setImageTintList(context.getColorStateList(R.color.white));

                }
                Log.d("status_value"," approved "+commonPojo.getStatus_by()+" label "+commonPojo.getStatus_label());
                if(!TextUtils.isEmpty(commonPojo.getStatus_by())){
                    holder.relativelayout.setVisibility(View.VISIBLE);
                    holder.request_layout.setVisibility(View.GONE);
                    holder.approve_layout.setVisibility(View.VISIBLE);

                }else {
                    holder.request_layout.setVisibility(View.GONE);
                    holder.approve_layout.setVisibility(View.GONE);
                }

            }else if(staut.startsWith("can") || staut.startsWith("dec") || staut.startsWith("rej")){
                holder.approved_status.setText(commonPojo.getStatus());
                Log.d("status_value"," rejected");
                if(staut.startsWith("dec")){
                    holder.approved_by.setText(commonPojo.getApprove_name());
                }else{
                    if(!TextUtils.isEmpty(commonPojo.getStatus_by())){
                        holder.approved_by.setText(commonPojo.getStatus_by());
                    }if(!TextUtils.isEmpty(commonPojo.getApprove_name())){
                        holder.approved_by.setText(commonPojo.getApprove_name());
                    }else{
                        holder.approved_by.setText(commonClass.getSharedPref(context,"EmppName"));
                    }
                }

                holder.relativelayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.pink_50));
                    holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                    holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                    holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                    holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                    holder.request_layout.setVisibility(View.GONE);
                    holder.approve_layout.setVisibility(View.VISIBLE);
                }
            }else{
                holder.approved_status.setText(commonPojo.getStatus());
                Log.d("status_value"," cancelled ");
                holder.approved_by.setText(commonPojo.getStatus_by());
                holder.relativelayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                    holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                    holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                    holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                    holder.request_layout.setVisibility(View.GONE);
                    holder.approve_layout.setVisibility(View.VISIBLE);
                }
            }
        }
        if(!TextUtils.isEmpty(commonPojo.getIs_edit())){
            if(commonPojo.getIs_edit().contains("2")){
                holder.edit_dsr.setVisibility(View.GONE);
            }else{
                String stats = commonPojo.getStatus().toLowerCase();
                if(stats.equals("pending")){
                    holder.edit_dsr.setVisibility(View.VISIBLE);
                }else{
                    holder.edit_dsr.setVisibility(View.GONE);
                }
            }
        }else{
            String stats = commonPojo.getStatus().toLowerCase();
            if(stats.equals("pending")){
                holder.edit_dsr.setVisibility(View.VISIBLE);
            }else{
                holder.edit_dsr.setVisibility(View.GONE);
            }
        }
        holder.edit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OverTime.class);
                intent.putExtra("overtime",commonPojo);
                context.startActivity(intent);
            }
        });
        holder.more_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.more_layout.getVisibility()==View.VISIBLE){
                    holder.more_layout.setVisibility(View.GONE);
                }else{
                    holder.more_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
                TextView msg = view.findViewById(R.id.msg);
                TextView cancel_text = view.findViewById(R.id.cancel_text);
                TextView btn_title1 = view.findViewById(R.id.btn_title1);
                TextView btn_title = view.findViewById(R.id.btn_title);
                btn_title1.setText("YES");
                btn_title.setText("YES");
                cancel_text.setText("NO");
                msg.setText("Are you sure you want to Cancel?");
                LinearLayout approve = view.findViewById(R.id.approve);
                approve.setVisibility(View.GONE);
                LinearLayout decline = view.findViewById(R.id.decline);
                decline.setVisibility(View.VISIBLE);
                LinearLayout cancel = view.findViewById(R.id.cancel);
                builder.setView(view);
                final AlertDialog mDialog = builder.create();
                mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                mDialog.create();
                mDialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        decline.setEnabled(false);
                        loader.setVisibility(View.VISIBLE);
                        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                                commonClass.getDeviceID(context)).create(ApiInterface.class);
                        Call<CommonPojo> call = apiInterface.cancelOverTime(commonPojo.getId());
                        Log.d("OndutyList"," url as "+call.request().url()+" on duty id "+commonPojo.getId());
                        call.enqueue(new Callback<CommonPojo>() {
                            @Override
                            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                                // progressDialog.dismiss();
                                Log.d("OndutyList"," cide "+response.code());
                                decline.setEnabled(true);
                                loader.setVisibility(View.GONE);
                                if(response.isSuccessful()){
                                    if(response.code()==200){
                                        if(response.body().getStatus().equals("success")){
                                           /* commonClass.showSuccess(context,response.body().getData());
                                            commonPojoList.get(position).setApproved("Cancelled");
                                            commonPojoList.get(position).setStatus_code("6");
                                            recyclerView.setAdapter(null);
                                            mDialog.dismiss();
                                            OverTimeAdapter adapter=new OverTimeAdapter(context,commonPojoList,recyclerView,loader);
                                            recyclerView.setAdapter(adapter);
                                            ((OnDuty)context).getList();*/
                                            commonClass.showSuccess(context,response.body().getData());
                                            new Handler().postDelayed(new Runnable() {
                                                public void run() {
                                                    Intent intent = new Intent(context, OverTime.class);
                                                    context.startActivity(intent);
                                                }
                                            }, 2500);
                                           /* if (context instanceof LeaveActivity) {
                                                ((LeaveActivity)context).getLeaveList();
                                            }*/
                                        }else{
                                            commonClass.showError(context,response.body().getStatus());
                                        }
                                    }else{
                                        Gson gson = new GsonBuilder().create();
                                        CommonPojo mError = new CommonPojo();
                                        try {
                                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                            commonClass.showError(context,mError.getError());
                                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                        } catch (IOException e) {
                                            // handle failure to read error
                                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                        }
                                    }
                                }else{
                                    Gson gson = new GsonBuilder().create();
                                    CommonPojo mError = new CommonPojo();
                                    try {
                                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                        Log.d("thumbnail_url"," error "+mError.getError());
                                        commonClass.showError(context,mError.getError());
                                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        // handle failure to read error
                                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<CommonPojo> call, Throwable t) {
                                loader.setVisibility(View.GONE);
                                decline.setEnabled(true);
                                Log.d("OndutyList"," error "+t.getMessage());
                                commonClass.showError(context,t.getMessage());
                            }
                        });

                    }
                });



            }
        });
    }

    @Override
    public int getItemCount() {
        return commonPojoList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView leave_type,date,approved_status,approved_by,reason,from_date_time,to_date_tim;
        RelativeLayout relativelayout;
        LinearLayout request_layout,approve_layout,edit_dsr,more_layout;
        ImageView icon,more_icon;
        public ProductViewHolder(View view) {
            super(view);
            leave_type = view.findViewById(R.id.leave_type);
            more_icon = view.findViewById(R.id.more_icon);
            date = view.findViewById(R.id.date);
            more_layout = view.findViewById(R.id.more_layout);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            to_date_tim = view.findViewById(R.id.to_date_tim);
            from_date_time = view.findViewById(R.id.from_date_time);
            reason = view.findViewById(R.id.reason);
            approved_status = view.findViewById(R.id.approved_status);
            approved_by = view.findViewById(R.id.approved_by);
            relativelayout = view.findViewById(R.id.relativelayout);
            request_layout = view.findViewById(R.id.request_layout);
            approve_layout = view.findViewById(R.id.approve_layout);
            icon = view.findViewById(R.id.icon);
            relativelayout.setVisibility(View.GONE);



        }
    }
}
