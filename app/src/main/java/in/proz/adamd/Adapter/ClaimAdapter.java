package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.List;

import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.DocumentView;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ProductViewHolder> {
    Activity context;
    ProgressDialog progressDialog;
    CommonClass commonClass;
    RecyclerView recyclerView;

    List<CommonPojo> commonPojoList;
    String claimType,formType;
    MKLoader loader;
    public ClaimAdapter(Activity context, List<CommonPojo> commonPojoList, RecyclerView recyclerView,String claimType,
                        String formType,MKLoader loader){
        this.context=context;
        this.loader=loader;
        this.commonPojoList=commonPojoList;
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.recyclerView = recyclerView;
        commonClass = new CommonClass();
        this.formType=formType;
        this.claimType=claimType;
        Log.d("leave_url"," con "+commonPojoList.size());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_claim_row_item,null);
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
        CommonPojo commonPojo =commonPojoList.get(position);
        Log.d("claim_adapter"," edit "+commonPojo.getStatus());

        holder.date.setText(commonPojo.getDate());
        holder.reason.setText(commonPojo.getReason());
        holder.amount.setText("₹"+commonPojo.getAmount());
        if(!TextUtils.isEmpty(commonPojo.getPayment_date())){
            if(commonPojo.getPayment_date()!=null) {
                holder.pay_date.setText(commonPojo.getPayment_date());
                holder.pay_desc.setText(commonPojo.getPay_description());
                holder.pay_desc.setVisibility(View.VISIBLE);
                holder.pay_date.setVisibility(View.VISIBLE);
            }else{
                holder.pay_date.setText("N/A");
                holder.pay_desc.setText("N/A");
                holder.date_layout.setVisibility(View.GONE);
                holder.desc_layout.setVisibility(View.GONE);
            }
        }else{
            holder.pay_date.setText("N/A");
            holder.pay_desc.setText("N/A");
            holder.date_layout.setVisibility(View.GONE);
            holder.desc_layout.setVisibility(View.GONE);
         }


        if(!TextUtils.isEmpty(commonPojo.getFiles())){
            if(commonPojo.getFiles()!=null) {
                if (commonPojo.getFiles().contains(".jpg") || commonPojo.getFiles().contains(".png") ||
                        commonPojo.getFiles().contains(".jpeg")) {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.document.setVisibility(View.GONE);
                } else {
                    holder.image.setVisibility(View.GONE);
                    holder.document.setVisibility(View.VISIBLE);
                }
            }else{
                holder.image.setVisibility(View.GONE);
                holder.document.setVisibility(View.GONE);
            }
        }else{
            holder.image.setVisibility(View.GONE);
            holder.document.setVisibility(View.GONE);
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
            public void onClick(View view) {
                Log.d("getList"," foramt  "+formType);
                if (formType.contains("advan")){
                    //advanced claim
                    Log.d("getList"," opt 1");
                    Intent intent = new Intent(context, ClaimActivity.class);
                    intent.putExtra("claim_type","advance_claim");
                    intent.putExtra("claimList",commonPojo);
                    context.startActivity(intent);
                }else if (formType.contains("claim")){
                    //normal claim
                    Log.d("getList"," opt 2");
                    Intent intent = new Intent(context, ClaimActivity.class);
                    intent.putExtra("claim_type","claim");
                    intent.putExtra("claimList",commonPojo);
                    context.startActivity(intent);
                }else{
                    //loan
                    Log.d("getList"," opt 3");
                    Intent intent = new Intent(context, LoanActivity.class);
                    intent.putExtra("claim_type","advance_claim");
                    intent.putExtra("claimList",commonPojo);
                    context.startActivity(intent);
                }
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getImage_url(),0);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",commonPojo.getFiles());
                intent.putExtra("type","0");
                intent.putExtra("intent_type",formType);
                context.startActivity(intent);
            }
        });
        holder.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getDocument_url(),1);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",commonPojo.getFiles());
                intent.putExtra("type","1");
                intent.putExtra("intent_type",formType);
                context.startActivity(intent);
            }
        });
       /* commonPojo.setStatus_code("2");
        commonPojo.setStatus("Approved");
        commonPojo.setApproved("HR");*/
        Log.d("status_content"," status "+commonPojo.getApproved()+" statis "+commonPojo.getStatus()+
                " status by "+commonPojo.getStatus_by()+" codev +"+commonPojo.getStatus_code() );
        Log.d("status_value"," code "+commonPojo.getStatus_code());
        if(commonPojo.getStatus_code().contains("1")){
            holder.approved_status.setText(commonPojo.getStatus());
            Log.d("status_value"," rejected");
            holder.approved_by.setText(commonPojo.getApproved());
            holder.request_layout.setVisibility(View.GONE);

            if(!TextUtils.isEmpty(commonPojo.getApproved()) && !TextUtils.isEmpty(commonPojo.getStatus())){
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.VISIBLE);
            }else{
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             //   holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.pink_50));
                holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
               // holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else if(commonPojo.getStatus_code().contains("2") || commonPojo.getStatus_code().contains("7")){
            holder.approved_status.setText(commonPojo.getStatus());
            holder.approved_by.setText(commonPojo.getApproved());

            if(!TextUtils.isEmpty(commonPojo.getApproved()) && !TextUtils.isEmpty(commonPojo.getStatus())){
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.VISIBLE);
            }else{
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.GONE);
            }
            Log.d("status_value"," approved");

             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_3_1));
                 holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
                 holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_green));

                 holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
                 holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);

                 holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));

            }
        }else if(commonPojo.getStatus_code().contains("3")){
            holder.approved_status.setText("Pending");
            Log.d("status_value"," pending ");
            holder.request_layout.setVisibility(View.VISIBLE);

            holder.approved_by.setText(commonPojo.getApproved());
            if(!TextUtils.isEmpty(commonPojo.getApproved()) && !TextUtils.isEmpty(commonPojo.getStatus())){
                 holder.approve_layout.setVisibility(View.VISIBLE);
            }else{
                 holder.approve_layout.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.purple_shade_50));
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_org));

                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.clock_icon, 0, 0, 0);


                //holder.approve_layout.setVisibility(View.GONE);
            }
        }else if(commonPojo.getStatus_code().contains("8") ||commonPojo.getStatus_code().contains("5")){
            holder.approved_status.setText(commonPojo.getStatus());
            Log.d("status_value"," cancelled ");
            holder.request_layout.setVisibility(View.GONE);

            holder.approved_by.setText(commonClass.getSharedPref(context,"EmppName"));
            if(!TextUtils.isEmpty(commonPojo.getApproved()) && !TextUtils.isEmpty(commonPojo.getStatus())){
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.VISIBLE);
            }else{
                holder.request_layout.setVisibility(View.GONE);
                holder.approve_layout.setVisibility(View.GONE);
            }
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_6_1));
                 holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                 holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                 holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                 holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));

                 holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));

              //  holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else if(commonPojo.getStatus_code().contains("6")){
            holder.approved_status.setText("Cancelled by you");
            Log.d("status_value"," cancelled ");
            holder.request_layout.setVisibility(View.GONE);

            holder.approved_by.setText("You");
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.blue_50));
                 holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                 holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                 holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                 holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));

                 holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));

                //  holder.approve_layout.setVisibility(View.VISIBLE);
            }
        }else {
            Log.d("status_value"," final");

        }

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
                        decline.setEnabled(false);
                        loader.setVisibility(View.VISIBLE);
                        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                                commonClass.getDeviceID(context)).create(ApiInterface.class);
                        Call<CommonPojo> call ;
                        if(claimType.equals("loan")){
                            call=apiInterface.cancelLoanRequest(commonPojo.getId());
                        }else{
                            call=apiInterface.cancelClaim(commonPojo.getId());
                        }

                        call.enqueue(new Callback<CommonPojo>() {
                            @Override
                            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                                //  progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                decline.setEnabled(true);
                                if(response.isSuccessful()){
                                    if(response.code()==200){
                                        if(response.body().getStatus().equals("success")){
                                            commonClass.showSuccess(context,response.body().getData());
                                            commonPojoList.get(position).setApproved("Cancelled");
                                            commonPojoList.get(position).setStatus("Cancelled By You");
                                            commonPojoList.get(position).setStatus_code("6");
                                            recyclerView.setAdapter(null);
                                            mDialog.dismiss();
                                            ClaimAdapter adapter=new ClaimAdapter(context,commonPojoList,recyclerView,
                                                    claimType,formType,loader);
                                            recyclerView.setAdapter(adapter);
                                        }else{
                                            commonClass.showError(context,response.body().getStatus());
                                        }
                                    }else if(response.code()==401 || response.code()==403){
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
                                        commonClass.showServerToast(context,mError.getError());
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
        Log.d("leave_url"," size "+commonPojoList.size());
        return commonPojoList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView date,amount,reason,approved_by,approved_status;
        RelativeLayout relativelayout;
        LinearLayout request_layout,approve_layout,more_layout,edit_dsr,date_layout,desc_layout;
        ImageView icon,more_icon,document,image;
        TextView pay_date,pay_desc;
        public ProductViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            date_layout = view.findViewById(R.id.date_layout);
            desc_layout = view.findViewById(R.id.desc_layout);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            document = view.findViewById(R.id.document);
            image = view.findViewById(R.id.image);
            more_layout = view.findViewById(R.id.more_layout);
            more_icon = view.findViewById(R.id.see_more);
            pay_date = view.findViewById(R.id.pay_date);
            pay_desc = view.findViewById(R.id.pay_desc);
            reason = view.findViewById(R.id.reason);
            amount = view.findViewById(R.id.amount);
            approved_status = view.findViewById(R.id.approved_status);
            approved_by = view.findViewById(R.id.approved_by);
            relativelayout = view.findViewById(R.id.relativelayout);
            request_layout = view.findViewById(R.id.request_layout);
            approve_layout = view.findViewById(R.id.approve_layout);
            approve_layout.setVisibility(View.GONE);
            icon = view.findViewById(R.id.icon);



        }
    }
}
