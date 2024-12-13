package in.proz.adamd.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ProductViewHolder>{
    Activity context ;
    List<CommonPojo>  meetingList;
    CommonClass commonClass = new CommonClass();
    RecyclerView recyclerView;
    MKLoader mkLoader;
    List<CommonPojo> getEmpList;
    List<String> empIDList,empEmailList;

    public MeetingAdapter(Activity context, List<CommonPojo>  meetingList, RecyclerView recyclerView,
                          MKLoader mkLoader, List<CommonPojo> getEmpList){
        this.context=context;
        this.meetingList = meetingList;
        this.recyclerView = recyclerView;
        this.mkLoader = mkLoader;
        this.getEmpList = getEmpList;
        empEmailList = new ArrayList<>();
        empIDList = new ArrayList<>();
        if(getEmpList.size()!=0){
            for(int i=0;i<getEmpList.size();i++){
                empIDList.add(getEmpList.get(i).getName());
                empEmailList.add(getEmpList.get(i).getComp_email());
            }
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.meeting_row_item,null);
      /*  TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);*/

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = meetingList.get(position);
        Log.d("getEmpListSize"," size "+getEmpList.size()+" is edit "+commonPojo.getIs_edit()+
                " is cancel "+commonPojo.getIs_cancel()+" stauts "+commonPojo.getStatus()+
                " is vari "+commonPojo.getIs_variable());
        if(TextUtils.isEmpty(commonPojo.getIs_cancel())){
            commonPojo.setIs_cancel("0");
        }

        if(!TextUtils.isEmpty(commonPojo.getReason())){
           holder.cancel_reason_layout.setVisibility(View.VISIBLE);
           holder.reason.setText(commonPojo.getReason());
        }else{
            holder.cancel_reason_layout.setVisibility(View.GONE);
        }
        holder.meeting_name.setText(commonPojo.getMeeting_name().toUpperCase());
        if(!TextUtils.isEmpty(commonPojo.getOther_type())){
            if(commonPojo.getOther_type()!=null){
                holder.other_type_layout.setVisibility(View.VISIBLE);
                holder.other_type.setText(commonPojo.getOther_type());
            }else{
                holder.other_type_layout.setVisibility(View.GONE);
            }
        }else{
            holder.other_type_layout.setVisibility(View.GONE);
        }
        holder.see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.more_layout.getVisibility()==View.VISIBLE){
                    holder.more_layout.setVisibility(View.GONE);
                }else{
                    holder.more_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        if(!TextUtils.isEmpty(commonPojo.getComments())){
            if(commonPojo.getComments()!=null){
                holder.comments_layout.setVisibility(View.VISIBLE);
                holder.comments.setText(commonPojo.getComments());
            }else{
                holder.comments_layout.setVisibility(View.GONE);
            }
        }else{
            holder.comments_layout.setVisibility(View.GONE);
        }

        holder.meeting_type.setText(commonPojo.getType());
        holder.lead.setText(commonPojo.getLead());
        Log.d("statusValues"," as "+commonPojo.getStatus()+"   var "+commonPojo.getIs_variable());
        if(commonPojo.getLoaction_type().equals("online")){
         //   holder.link.setText(commonPojo.getMeeting_link());
           // holder.location_tag.setText("Meeting Link");
            if(commonPojo.getStatus().equals("0")){
                if(commonPojo.getIs_variable().equals("1") || commonPojo.getIs_variable().equals("2")){
                    Log.d("statusValues"," con 1");
                    String formt=" <a  href="+commonPojo.getMeeting_link()+" style='color: #5ba2fe; text-decoration: underline;'>"
                            +commonPojo.getMeeting_link()+"</a> ";
                    holder.link.setText(Html.fromHtml(formt, Html.FROM_HTML_MODE_LEGACY));
                    holder.link.setLinkTextColor(context.getColor(R.color.n_dark_blue));
                    holder.link.setMovementMethod(LinkMovementMethod.getInstance());
                }else{
                    Log.d("statusValues"," con 2");
                    holder.link.setText(commonPojo.getMeeting_link());
                    holder.link.setTextColor(context.getColor(R.color.black));
                }

            }else{
                Log.d("statusValues"," con 3");
                holder.link.setTextColor(context.getColor(R.color.black));
                holder.link.setText(commonPojo.getMeeting_link());
            }

        }else{
            Log.d("statusValues"," con 4");
            holder.link.setTextColor(context.getColor(R.color.black));
            holder.link.setText(commonPojo.getLocation_details());
          //  holder.location_tag.setText("Location");
        }

        //holder.participate.setText(getParticipateDetails(empIDList,empEmailList,commonPojo.getParticipate()));
        if(!TextUtils.isEmpty(commonPojo.getParticipate())){
             holder.participate.setText(commonPojo.getParticipate());
        }
        if(!TextUtils.isEmpty(commonPojo.getInvite_mail())){
            holder.invite_mail.setText(commonPojo.getInvite_mail());
            holder.invite_layout.setVisibility(View.VISIBLE);
        }else{
            holder.invite_layout.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(commonPojo.getFrom_date())){
            String[] split = commonPojo.getFrom_date().split(" ");
            if(split.length>0){
                String[] spl1= split[0].split("-");
                holder.from_date_time.setText(spl1[2]+"-"+spl1[1]+"-"+spl1[0]+"\n"+commonPojo.getF_time());
            }
        }
        if(!TextUtils.isEmpty(commonPojo.getTo_date())){
            String[] split = commonPojo.getTo_date().split(" ");
            if(split.length>0){
                String[] spl1= split[0].split("-");
                holder.to_date_tim.setText(spl1[2]+"-"+spl1[1]+"-"+spl1[0]+"\n"+commonPojo.getT_time());
            }
        }

        if(!TextUtils.isEmpty(commonPojo.getAttachments())){
            if(commonPojo.getAttachments()!=null){
                holder.document.setVisibility(View.VISIBLE);
            }else{
                holder.document.setVisibility(View.GONE);
            }
        }else{
            holder.document.setVisibility(View.GONE);
        }
        holder.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commonPDFURL="https://docs.google.com/gview?embedded=true&url="+commonPojo.getAttachments();
                Log.d("document_url"," pdf url "+commonPDFURL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commonPDFURL));
                context.startActivity(browserIntent);
            }
        });
        if(!TextUtils.isEmpty(commonPojo.getIs_edit())){
            if(commonPojo.getIs_edit().equals("2")){
                holder.edit_dsr.setVisibility(View.GONE);
            }else{
                if(commonPojo.getIs_cancel().equals("1")){
                    if(commonPojo.getStatus().equals("0")) {
                        if(commonPojo.getIs_variable().equals("2") || commonPojo.getIs_variable().equals("1")){
                            holder.edit_dsr.setVisibility(View.VISIBLE);
                        }else{
                            holder.edit_dsr.setVisibility(View.GONE);
                        }
                    }else{
                        holder.edit_dsr.setVisibility(View.GONE);
                    }
                }else{
                    holder.edit_dsr.setVisibility(View.GONE);
                }
            }
         }else{
            holder.edit_dsr.setVisibility(View.GONE);
        }
        if(commonPojo.getStatus().equals("0")){
            if(commonPojo.getIs_variable().equals("1")){
                holder.approved_status.setText("On Going");
            }else  if(commonPojo.getIs_variable().equals("2")){
                holder.approved_status.setText("Yet To Start");
            }else  if(commonPojo.getIs_variable().equals("3")){
                holder.approved_status.setText("Completed");
            }
        }else{
            if(commonPojo.getStatus().contains("6")){
                holder.approved_status.setText("Cancelled by you");
            }else{
                holder.approved_status.setText("Manager Cancelled");
            }
        }


        if(commonPojo.getStatus().equals("0")){
            if(!commonPojo.getIs_variable().equals("3")){
                if(commonPojo.getIs_cancel().equals("1")) {
                    holder.request_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.request_layout.setVisibility(View.GONE);
                }
            }else{
                holder.request_layout.setVisibility(View.GONE);
            }
           // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.d_clr_3_1));
            holder.approved_status.setTextColor(context.getColorStateList(R.color.n_green));
            holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
            holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.approve_icon, 0, 0, 0);
        }else{
            holder.request_layout.setVisibility(View.GONE);
            holder.edit_dsr.setVisibility(View.GONE);
           // holder.relativelayout.setBackgroundTintList(context.getColorStateList(R.color.pink_50));
             holder.approved_status.setTextColor(context.getColorStateList(R.color.war1));
            holder.approved_status.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
            holder.approved_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.decline_icon, 0, 0, 0);
         }



        holder.request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.cancel_reason,null);
                ImageView back_arrow = view.findViewById(R.id.back_arrow);
                EditText text =   view.findViewById(R.id.msg_text);
                TextView cancel_meeting = view.findViewById(R.id.cancel_meeting);

                 builder.setView(view);
                final android.app.AlertDialog mDialog = builder.create();
                mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                Window window = mDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                window.setAttributes(wlp);


                mDialog.setCancelable(false);
                mDialog.create();
                mDialog.show();

                back_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                cancel_meeting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if(TextUtils.isEmpty(text.getText().toString())){
                        text.setError("Enter Reason");
                    }else {
                        cancel_meeting.setEnabled(false);
                        mDialog.dismiss();
                                // progressDialog.show();
                                mkLoader.setVisibility(View.VISIBLE);
                                ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context, "token"),
                                        commonClass.getDeviceID(context)).create(ApiInterface.class);
                                Call<CommonPojo> call;

                                call = apiInterface.meetingCancel(commonPojo.getId(),text.getText().toString());


                                call.enqueue(new Callback<CommonPojo>() {
                                    @Override
                                    public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                                        //  progressDialog.dismiss();
                                        cancel_meeting.setEnabled(true);
                                        mkLoader.setVisibility(View.GONE);
                                         if (response.isSuccessful()) {
                                            if (response.code() == 200) {
                                                if (response.body().getStatus().equals("success")) {
                                                    commonClass.showSuccess(context, response.body().getData());
                                                    meetingList.get(position).setApproved("Cancelled By You");
                                                    meetingList.get(position).setStatus("Cancelled By You");
                                                    meetingList.get(position).setStatus_code("6");
                                                    recyclerView.setAdapter(null);
                                                     MeetingAdapter assetAdapter = new MeetingAdapter(context, meetingList,
                                                            recyclerView, mkLoader, getEmpList);
                                                    recyclerView.setAdapter(assetAdapter);
                                                } else {
                                                    commonClass.showError(context, response.body().getStatus());
                                                }
                                            } else if(response.code()==401 || response.code()==403){
                                                commonClass.showError(context,"UnAuthendicated");
                                                commonClass.clearAlldata(context);
                                            }else {
                                                Gson gson = new GsonBuilder().create();
                                                CommonPojo mError = new CommonPojo();
                                                try {
                                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                                    commonClass.showError(context, mError.getError());
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

                                    @Override
                                    public void onFailure(Call<CommonPojo> call, Throwable t) {
                                        mkLoader.setVisibility(View.GONE);
                                        cancel_meeting.setEnabled(true);
                                     }
                                });


                    }
                    }
                });

            }
        });
        holder.edit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeetingActivity.class);
                intent.putExtra("saved_details", commonPojo);
                intent.putExtra("saved_details_asset", commonPojo);
                context.startActivity(intent);
               /* if(commonPojo.getIs_edit().equals("2")){
                    commonClass.showWarning(context,"You Can't Edit this Meeting");
                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                    alert.show();
                }*/
            }
        });
    }

    private String getParticipateDetails(List<String> empIDList, List<String> empEmailList, String participate) {
        Log.d("searchIndex"," emp ID List "+empIDList.size());
        String empNameList="";
            if(empIDList.size()!=0){
               String[] as =  participate.split(",");
                if(as.length!=0){
                    for(int k=0;k<as.length;k++){
                        int index = empEmailList.indexOf(as[k]);
                        if(index>=0){
                          String name = empIDList.get(index);
                        empNameList = empNameList+(k+1)+" . "+name+"\n";
                        }
                        Log.d("searchIndex"," as "+index);

                    }
                }
            }else{
                empNameList = participate;
            }
            return empNameList;
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView meeting_name,meeting_type,lead,link,other_type,comments,participate,invite_mail,from_date_time
                ,to_date_tim,approved_status,reason;
        //location_tag

        LinearLayout edit_dsr,more_layout,other_type_layout,comments_layout,issue_details_layout,
                request_layout,cancel_reason_layout,invite_layout;
        RelativeLayout relativelayout;
        ImageView see_more,document;
        public ProductViewHolder(View view) {
            super(view);
            meeting_name = view.findViewById(R.id.meeting_name);
            invite_layout = view.findViewById(R.id.invite_layout);
            reason = view.findViewById(R.id.reason);
            cancel_reason_layout = view.findViewById(R.id.cancel_reason_layout);
         //   location_tag = view.findViewById(R.id.location_tag);
            other_type_layout = view.findViewById(R.id.other_type_layout);
            request_layout = view.findViewById(R.id.request_layout);
            comments_layout = view.findViewById(R.id.comments_layout);
            document = view.findViewById(R.id.document);
             issue_details_layout = view.findViewById(R.id.issue_details_layout);
             meeting_type = view.findViewById(R.id.meeting_type);
            lead = view.findViewById(R.id.lead);
            see_more = view.findViewById(R.id.see_more);
            more_layout = view.findViewById(R.id.more_layout);
            link = view.findViewById(R.id.link);
            other_type = view.findViewById(R.id.other_type);
            comments = view.findViewById(R.id.comments);
            participate = view.findViewById(R.id.participate);
            invite_mail = view.findViewById(R.id.invite_mail);
            from_date_time = view.findViewById(R.id.from_date_time);
            to_date_tim = view.findViewById(R.id.to_date_tim);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            approved_status = view.findViewById(R.id.approved_status);
             relativelayout = view.findViewById(R.id.relativelayout);




        }
    }
}
