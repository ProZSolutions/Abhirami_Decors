package in.proz.adamd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
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

import in.proz.adamd.R;
import in.proz.adamd.Request.RequestActivity;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Activity context;
    int commonPos;
    RecyclerView recyclerView;
   // ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;
    public  AssetAdapter(Activity context, List<CommonPojo> commonPojoList, int commonPos,
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
        View view=inflater.inflate(R.layout.asset_row_item,null);
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
        if(TextUtils.isEmpty(commonPojo.getIs_edit())){
           commonPojo.setIs_edit("0");
       }
        Log.d("is_edit_dsr","status "+commonPojo.getIs_edit());
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

        if(!TextUtils.isEmpty(commonPojo.getIs_edit())){
                if(commonPojo.getIs_edit().contains("2")){
                    holder.edit_dsr.setVisibility(View.GONE);
                }else{
                    String stats = commonPojo.getStatus().toLowerCase();
                    if(stats.equals("pending")){
                        holder.config_layout.setVisibility(View.GONE);
                        holder.issue_details_layout.setVisibility(View.GONE);
                        holder.issue_date_layout.setVisibility(View.GONE);
                        holder.edit_dsr.setVisibility(View.VISIBLE);
                    }else{
                        holder.edit_dsr.setVisibility(View.GONE);
                    }
                }
        }else{
            String stats = commonPojo.getStatus().toLowerCase();
            if(stats.equals("pending")){
                holder.config_layout.setVisibility(View.GONE);
                holder.issue_details_layout.setVisibility(View.GONE);
                holder.issue_date_layout.setVisibility(View.GONE);
                holder.edit_dsr.setVisibility(View.VISIBLE);
            }else{
                holder.edit_dsr.setVisibility(View.GONE);
            }
        }

        holder.date.setText(commonPojo.getDate());
        holder.config.setText(commonPojo.getConfiguration());
        holder.desc.setText(commonPojo.getDetails());
        holder.main_cat.setText(commonPojo.getCategory_name());
        holder.qty.setText(commonPojo.getQuantity());
        holder.sub_cat.setText(commonPojo.getSubcategory_name());
        holder.asset_name.setText(commonPojo.getAsset_name());
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

        //commonPojo.setImage_url("https://4.bp.blogspot.com/-hUcx4fAPgSY/WaVTsADjR0I/AAAAAAAABzg/uVO_SlJAHTUFZWpa8CNuQfoDcgTU2elhACLcBGAs/s1600/4.jpeg");
        //commonPojo.setDocument_url("https://www.google.com/");




        holder.edit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commonPojo.getIs_edit().equals("2")){
                    commonClass.showWarning(context,"You Can't Edit this Asset");
                }else {
                    Intent intent = new Intent(context, RequestActivity.class);
                    intent.putExtra("saved_details", commonPojo);
                    intent.putExtra("saved_details_asset", commonPojo);
                    context.startActivity(intent);
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
            if(stats.contains("pending")){
                holder.approved_status.setTextColor(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_org));
                holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.clock_icon, 0, 0, 0);
                holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.n_org));
                holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                holder.request_layout.setVisibility(View.VISIBLE);
                holder.edit_dsr.setVisibility(View.VISIBLE);
                holder.approve_layout.setVisibility(View.GONE);

            }else if(stats.contains("appro")){
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
            }else{


                if(stats.contains("cancel")){
                    holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
                    holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
                    holder.approve_layout.setBackgroundTintList(context.getColorStateList(R.color.war1));
                    holder.approved_by.setTextColor(context.getColorStateList(R.color.white));
                    holder.icon.setImageTintList(context.getColorStateList(R.color.white));
                    holder.request_layout.setVisibility(View.GONE);
                    holder.edit_dsr.setVisibility(View.GONE);
                    holder.approve_layout.setVisibility(View.GONE);
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

                       call=apiInterface.assetCancel(commonPojo.getId());


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
                                           commonPojoList.get(position).setStatus(commonClass.getSharedPref(context,"EmppName"));
                                           commonPojoList.get(position).setStatus_code("6");
                                           recyclerView.setAdapter(null);
                                           mDialog.dismiss();
                                           AssetAdapter assetAdapter = new AssetAdapter(context,commonPojoList,0,recyclerView, loader);
                                           recyclerView.setAdapter(assetAdapter);
                                       }else{
                                           commonClass.showError(context,response.body().getStatus());
                                       }
                                   }else if(response.code()==401 || response.code()==403){
                                       commonClass.showError(context,"UnAuthendicated");
                                       commonClass.clearAlldata(context);
                                   } else{
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
                                   if(response.code()==401 || response.code()==403){
                                       commonClass.showError(context,"UnAuthendicated");
                                       commonClass.clearAlldata(context);
                                   }else {
                                       Gson gson = new GsonBuilder().create();
                                       CommonPojo mError = new CommonPojo();
                                       try {
                                           mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                           Log.d("thumbnail_url", " error " + mError.getError());
                                           commonClass.showError(context, mError.getError());
                                           //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                       } catch (IOException e) {
                                           // handle failure to read error
                                           Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                       }
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<CommonPojo> call, Throwable t) {
                               decline.setEnabled(true);
                               loader.setVisibility(View.GONE);
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
        TextView date,desc,qty,config,main_cat,sub_cat,asset_name,approved_status,approved_by,issue_date,issue_details;
        LinearLayout edit_dsr,more_layout,request_layout,approve_layout,issue_details_layout,issue_date_layout,config_layout;
        RelativeLayout relativelayout;
        ImageView icon,see_more,image,document;
        public ProductViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            document = view.findViewById(R.id.document);
            document.setVisibility(View.GONE);
            image = view.findViewById(R.id.image);
            config_layout = view.findViewById(R.id.config_layout);
            image.setVisibility(View.GONE);
            issue_details_layout = view.findViewById(R.id.issue_details_layout);
            issue_date_layout = view.findViewById(R.id.issue_date_layout);
            issue_details = view.findViewById(R.id.issue_details);
            issue_date = view.findViewById(R.id.issue_date);
            see_more = view.findViewById(R.id.see_more);
            more_layout = view.findViewById(R.id.more_layout);
            main_cat = view.findViewById(R.id.main_cat);
            sub_cat = view.findViewById(R.id.sub_cat);
            asset_name = view.findViewById(R.id.asset_name);
            desc = view.findViewById(R.id.desc);
            qty = view.findViewById(R.id.qty);
            icon = view.findViewById(R.id.icon);
            config = view.findViewById(R.id.config);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            approved_status = view.findViewById(R.id.approved_status);
            request_layout = view.findViewById(R.id.request_layout);
            relativelayout = view.findViewById(R.id.relativelayout);
            approve_layout = view.findViewById(R.id.approve_layout);
            approved_by = view.findViewById(R.id.approved_by);




        }
    }
}
