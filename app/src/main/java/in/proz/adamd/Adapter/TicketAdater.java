package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
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

import in.proz.adamd.DocumentView;
import in.proz.adamd.R;
import in.proz.adamd.Request.RequestActivity;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketAdater extends RecyclerView.Adapter<TicketAdater.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Activity context;
    ProgressDialog progressDialog;
    CommonClass commonClass=new CommonClass();
    RecyclerView recyclerView;
    MKLoader loader;
    public  TicketAdater(Activity context,List<CommonPojo> commonPojoList,RecyclerView recyclerView,MKLoader loader){
        this.context=context;
        this.commonPojoList=commonPojoList;
        this.recyclerView = recyclerView;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        this.loader=loader;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.ticket_row_item,null);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);

        return new ProductViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = commonPojoList.get(position);

        holder.date.setText(commonPojo.getDate());
        holder.config.setText(commonPojo.getConfiguration());
        holder.desc.setText(commonPojo.getDetails());
        holder.ticket_name.setText(commonPojo.getPriority());
        holder.qty.setText(commonPojo.getQuantity());
        holder.ticket_type.setText(commonPojo.getType_name());
        if(!TextUtils.isEmpty(commonPojo.getIssue_date())){
            if(commonPojo.getIssue_date()!=null) {
                holder.issue_date_layout.setVisibility(View.VISIBLE);
                holder.issue_date.setText(commonPojo.getIssue_date());
            }else{
                holder.issue_date_layout.setVisibility(View.GONE);
                holder.issue_date.setText("");
            }
        }else{
            holder.issue_date_layout.setVisibility(View.GONE);
            holder.issue_date.setText("");
        }
        if(!TextUtils.isEmpty(commonPojo.getIssued_details())){
            if(commonPojo.getIssued_details()!=null) {
                holder.issue_details_layout.setVisibility(View.VISIBLE);
                holder.issue_details.setText(commonPojo.getIssued_details());
            }else{
                holder.issue_details_layout.setVisibility(View.VISIBLE);
                holder.issue_details.setText("");
            }
        }else{
            holder.issue_details_layout.setVisibility(View.VISIBLE);
            holder.issue_details.setText("");
        }
        Log.d("getImage"," get url "+commonPojo.getImage());

        if(!TextUtils.isEmpty(commonPojo.getImage())){
            if(commonPojo.getImage()!=null) {
                if (commonPojo.getImage().contains(".jpg") ||
                        commonPojo.getImage().contains(".png") ||
                        commonPojo.getImage().contains(".jpeg")) {
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



        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getImage_url(),0);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",commonPojo.getImage());
                intent.putExtra("type","0");
                intent.putExtra("intent_type","ticket");
                context.startActivity(intent);
            }
        });
        holder.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getDocument_url(),1);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",commonPojo.getImage());
                intent.putExtra("type","1");
                intent.putExtra("intent_type","ticket");
                context.startActivity(intent);
            }
        });
        if(!TextUtils.isEmpty(commonPojo.getIs_edit())){
            if(commonPojo.getIs_edit().contains("2")){
                holder.edit_dsr.setVisibility(View.GONE);
            }else{
                String stats = commonPojo.getStatus().toLowerCase();
                if(stats.equals("pending")){
                    holder.edit_dsr.setVisibility(View.VISIBLE);
                    holder.issue_details_layout.setVisibility(View.GONE);
                }else{
                    holder.edit_dsr.setVisibility(View.GONE);
                }
            }
        }else{
            String stats = commonPojo.getStatus().toLowerCase();
            if(stats.equals("pending")){
                holder.issue_details_layout.setVisibility(View.GONE);
                holder.edit_dsr.setVisibility(View.VISIBLE);
            }else{
                holder.edit_dsr.setVisibility(View.GONE);
            }
        }
        holder.edit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commonPojo.getIs_edit().equals("2")){
                    commonClass.showWarning(context,"You Can't Edit this Asset");
                }else {
                   /* AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Would you like to edit?");
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.create();
                    alert.show();*/
                    Intent intent = new Intent(context, RequestActivity.class);
                    intent.putExtra("saved_details", commonPojo);
                    context.startActivity(intent);
                }
            }
        });

        holder.see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.more_layout.getVisibility()==View.VISIBLE){
                    String mystring=new String("See More");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    //holder.see_more.setText(context.getText(R.string.see_more));
                    holder.more_layout.setVisibility(View.GONE);
                }else{
                    holder.more_layout.setVisibility(View.VISIBLE);
                    String mystring=new String("See Less");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    //holder.see_more.setText(context.getText(R.string.see_less));
                }
            }
        });


        if(!TextUtils.isEmpty(commonPojo.getStatus())){
            holder.approved_status.setText(commonPojo.getStatus());

            String stats = commonPojo.getStatus().toLowerCase();
            if(stats.equals("pending")){
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.clock_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_org));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.edit_dsr.setVisibility(View.VISIBLE);
                holder.approve_layout.setVisibility(View.GONE);

            }else if(stats.contains("approved")){
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_green));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
                holder.edit_dsr.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(commonPojo.getStatus())){
                    holder.approve_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.approve_layout.setVisibility(View.GONE);
                }

            }else if(stats.contains("decl") || stats.contains("cancel")){
                holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
                holder.edit_dsr.setVisibility(View.GONE);
                if(stats.contains("cancel")){
                    holder.approve_layout.setVisibility(View.GONE);
                }else{

                    if(!TextUtils.isEmpty(commonPojo.getStatus())){
                        holder.approve_layout.setVisibility(View.VISIBLE);
                    }else{
                        holder.approve_layout.setVisibility(View.GONE);
                    }
                }
            }else{
                holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.GONE);
                holder.edit_dsr.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(commonPojo.getStatus())){
                    holder.approve_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.approve_layout.setVisibility(View.GONE);
                }
            }
        }


        holder.request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
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

                        call=apiInterface.ticketCancel(commonPojo.getId());


                        call.enqueue(new Callback<CommonPojo>() {
                            @Override
                            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                                //progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                decline.setEnabled(true);
                                if(response.isSuccessful()){
                                    if(response.code()==200){
                                        if(response.body().getStatus().equals("success")){
                                            commonClass.showSuccess(context,response.body().getData());
                                            commonPojoList.get(position).setApproved("Cancelled");
                                            commonPojoList.get(position).setStatus(commonClass.getSharedPref(context,"EmppName"));
                                            commonPojoList.get(position).setStatus_code("6");
                                            recyclerView.setAdapter(null);
                                            mDialog.dismiss();
                                            TicketAdater adater = new TicketAdater(context,commonPojoList,recyclerView,loader);
                                            recyclerView.setAdapter(adater);

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
        TextView date,ticket_type,ticket_name,desc,qty,config,approved_status,approved_by,issue_date,issue_details;
        LinearLayout edit_dsr,more_layout,request_layout,approve_layout,issue_details_layout,issue_date_layout;
        RelativeLayout relativelayout;
        ImageView icon,see_more,image,document;
        public ProductViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            document = view.findViewById(R.id.document);
            image = view.findViewById(R.id.image);
            issue_details_layout = view.findViewById(R.id.issue_details_layout);
            issue_date_layout = view.findViewById(R.id.issue_date_layout);
            issue_details = view.findViewById(R.id.issue_details);
            issue_date = view.findViewById(R.id.issue_date);
            ticket_type = view.findViewById(R.id.ticket_type);
            ticket_name = view.findViewById(R.id.ticket_name);
            desc = view.findViewById(R.id.desc);
            qty = view.findViewById(R.id.qty);
            config = view.findViewById(R.id.config);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            icon = view.findViewById(R.id.icon);
            see_more = view.findViewById(R.id.see_more);
            more_layout = view.findViewById(R.id.more_layout);
            approved_status = view.findViewById(R.id.approved_status);
            request_layout = view.findViewById(R.id.request_layout);
            relativelayout = view.findViewById(R.id.relativelayout);
            approve_layout = view.findViewById(R.id.approve_layout);
            approved_by = view.findViewById(R.id.approved_by);




        }
    }
}
