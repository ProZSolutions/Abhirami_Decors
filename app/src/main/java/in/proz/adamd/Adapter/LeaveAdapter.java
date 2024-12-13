package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import in.proz.adamd.DocumentView;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.ProductViewHolder>{
    Activity context;
   // ProgressDialog progressDialog;
    CommonClass commonClass;
    RecyclerView recyclerView;

    List<CommonPojo> commonPojoList;
    MKLoader loader;
    public LeaveAdapter(Activity context, List<CommonPojo> commonPojoList, RecyclerView recyclerView,MKLoader loader){
        this.context=context;
        this.commonPojoList=commonPojoList;
        this.loader= loader;
      /*  progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);*/
        this.recyclerView = recyclerView;
        commonClass = new CommonClass();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_leave_row,null);
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

    private int departHRMin(String string, int i) {
         String[] arr1=string.split(":");
        int rt=0;
        if(i==0){
            // hr
            rt=Integer.parseInt(arr1[0]);
        }else{
            //min
            rt=Integer.parseInt(arr1[1]);
        }
        return rt;
    }
    private String getTime(String string, int i) {
        String[] arr1=string.split(":");
        int rt=0;
         Integer hr = Integer.parseInt(arr1[0]);
         String str_date;
         if(hr>12){
             hr=hr-12;
             str_date = hr+":"+arr1[1]+" PM";

         }else{
             str_date = hr+":"+arr1[1]+" AM";
         }
        return str_date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo =commonPojoList.get(position);
        if(!TextUtils.isEmpty(commonPojo.getType())){
            if(commonPojo.getType().equals("N/A")){
                holder.leave_type.setText(commonPojo.getLeave_type());
            }else{
                holder.leave_type.setText(commonPojo.getLeave_type()+"("+commonPojo.getType()+")");
            }
        }else{
            holder.leave_type.setText(commonPojo.getLeave_type());
        }
        if(!TextUtils.isEmpty(commonPojo.getLeave_type())){
            String str= commonPojo.getLeave_type().toString();
            if(str.contains("half")){
                holder.duration.setText(commonPojo.getFrom());
            }else{
                if(commonPojo.getLeave_type().equals("Permission")){
                    LocalTime startTime = LocalTime.of(departHRMin(commonPojo.getFrom_time(),0), departHRMin(commonPojo.getFrom_time(),1)); // 8:30 AM
                    LocalTime endTime = LocalTime.of(departHRMin(commonPojo.getTo_time(),0), departHRMin(commonPojo.getTo_time(),1));  // 2:45 PM
                    Duration duration = Duration.between(startTime, endTime);

                    // Get the difference in hours
                    long hrs_diff = duration.toHours();
                    long mins_diff= duration.toMinutes()%60;
                    holder.duration.setText(commonPojo.getFrom());
                    holder.time_layout.setVisibility(View.VISIBLE);

                        holder.time.setText(getTime(commonPojo.getFrom_time(),0)+" to "+getTime(commonPojo.getTo_time(),0)+"("+hrs_diff+"Hrs "+
                                mins_diff+" mins)");


                }else{
                    if(!TextUtils.isEmpty(commonPojo.getTo())){
                        if(commonPojo.getTo().contains("1970")){
                            holder.duration.setText(commonPojo.getFrom());
                        }else{
                            holder.duration.setText(commonPojo.getFrom()+" TO "+commonPojo.getTo());
                        }
                    }else{
                        holder.duration.setText(commonPojo.getFrom());
                    }
                }

            }
        }
        Log.d("status_value"," edit "+commonPojo.getIs_edit());
        if(commonPojo.getStatus_code().contains("1")){
            holder.approved_status.setText("Rejected by");
            Log.d("status_value"," rejected");
            holder.approved_by.setText(commonPojo.getApproved());
            holder.relativelayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(commonPojo.getApproved())){
                holder.approve_layout.setVisibility(View.VISIBLE);

            }else{
                holder.approve_layout.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

               // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.pink_50));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));

                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);


                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
              //  holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else if(commonPojo.getStatus_code().contains("2")){
            holder.approved_status.setText("Approved by");
            Log.d("status_value"," approved");
            holder.approved_by.setText(commonPojo.getApproved());
            holder.relativelayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(commonPojo.getApproved())){
                holder.approve_layout.setVisibility(View.VISIBLE);

            }else{
                holder.approve_layout.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              //  holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_3_1));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_green));

                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
              //  holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else if(commonPojo.getStatus_code().contains("3")){
            holder.approved_status.setText("Pending");
            Log.d("status_value"," pending ");
            holder.relativelayout.setVisibility(View.VISIBLE);
            holder.approved_by.setText(commonPojo.getApproved());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.purple_shade_50));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.clock_icon, 0, 0, 0);
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_org));
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_org));

                holder.request_layout.setVisibility(View.VISIBLE);
                holder.approve_layout.setVisibility(View.GONE);
            }
        }else if(commonPojo.getStatus_code().contains("6")){
            holder.approved_status.setText("Cancelled by");
            Log.d("status_value"," cancelled ");
            holder.approved_by.setText(commonPojo.getApproved());
            holder.relativelayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(commonPojo.getApproved())){
                holder.approve_layout.setVisibility(View.VISIBLE);

            }else{
                holder.approve_layout.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              //  holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_6_1));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));

                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
              //  holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else {
            holder.approved_status.setText(commonPojo.getStatus());
            Log.d("status_value"," cancelled ");
            holder.approved_by.setText(commonPojo.getApproved());
            if(!TextUtils.isEmpty(commonPojo.getApproved())){
                holder.approve_layout.setVisibility(View.VISIBLE);

            }else{
                holder.approve_layout.setVisibility(View.GONE);
            }
            holder.relativelayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_6_1));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_green));

                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
            }
        }

        holder.reason.setText(commonPojo.getReason());
        if(!TextUtils.isEmpty(commonPojo.getFile())){
            if(commonPojo.getFile()!=null) {
                holder.image.setVisibility(View.VISIBLE);
            }else{
                holder.image.setVisibility(View.GONE);
            }
        }else{
            holder.image.setVisibility(View.GONE);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getImage_url(),0);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",commonPojo.getFile());
                intent.putExtra("type","0");
                intent.putExtra("intent_type","Leave");
                context.startActivity(intent);
            }
        });
        holder.more_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                cancel_text.setText("NO");


                TextView btn_title1 = view.findViewById(R.id.btn_title1);
                TextView btn_title = view.findViewById(R.id.btn_title);
                btn_title1.setText("YES");
                btn_title.setText("YES");

                msg.setText("Are you sure you want to Cancel Leave?");
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
                        decline.setEnabled(false);
                        loader.setVisibility(View.VISIBLE);
                        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                                commonClass.getDeviceID(context)).create(ApiInterface.class);
                        Call<CommonPojo> call = apiInterface.cancelLeave(commonPojo.getId());
                        call.enqueue(new Callback<CommonPojo>() {
                            @Override
                            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                                // progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                decline.setEnabled(true);
                                if(response.isSuccessful()){
                                    if(response.code()==200){
                                        if(response.body().getStatus().equals("success")){
                                            commonClass.showSuccess(context,response.body().getData());
                                            commonPojoList.get(position).setApproved(commonClass.getSharedPref(context,"EmppName"));
                                            commonPojoList.get(position).setStatus_code("6");
                                            recyclerView.setAdapter(null);
                                            mDialog.dismiss();
                                             LeaveAdapter adapter=new LeaveAdapter(context,commonPojoList,recyclerView,loader);
                                            recyclerView.setAdapter(adapter);
                                            ((LeaveActivity)context).getLeaveList();
                                           /* if (context instanceof LeaveActivity) {
                                                ((LeaveActivity)context).getLeaveList();
                                            }*/
                                        }else{
                                            commonClass.showError(context,response.body().getStatus());
                                        }
                                    }
                                    else if(response.code()==401 || response.code()==403){
                                        commonClass.showError(context,"UnAuthendicated");
                                        commonClass.clearAlldata(context);
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
                                }
                                else if(response.code()==401 || response.code()==403){
                                    commonClass.showError(context,"UnAuthendicated");
                                    commonClass.clearAlldata(context);
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
        TextView leave_type,duration,approved_status,approved_by,reason;
         RelativeLayout relativelayout;
        LinearLayout request_layout,approve_layout;
        ImageView icon,see_more,image,more_icon;
        LinearLayout more_layout,time_layout;
        TextView time;
        public ProductViewHolder(View view) {
            super(view);
            leave_type = view.findViewById(R.id.leave_type);
            image = view.findViewById(R.id.image);
            time = view.findViewById(R.id.time);
            time_layout = view.findViewById(R.id.time_layout);
            more_icon = view.findViewById(R.id.more_icon);
            reason = view.findViewById(R.id.reason);
            see_more = view.findViewById(R.id.see_more);
            more_layout = view.findViewById(R.id.more_layout);
            duration = view.findViewById(R.id.duration);
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
