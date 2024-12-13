package in.proz.adamd.AdminModule.AdminNewAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminApprovalModal.LeaveAppModal;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.SQLiteDB.WeekOffSQL;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeekOffApprovalAdapterNew extends RecyclerView.Adapter<WeekOffApprovalAdapterNew.ProductViewHolder> {
    List<LeaveAppModal> leaveAppModalList;
    List<String> list = new ArrayList<>();
    Context context;
    List<String> selectedLeaveList;String dash;
    WeekOffSQL leaveIDNewSQL;
    int pos; RecyclerView recyclerView; MKLoader loader;
    TextView approve,decline;
    LinearLayout approve_layout;
    int adaposition;
    String branchID, serverDate, usableDate;
    List<String> selectedDeptID;
    ArrayList<Integer> projectListID1;
    CommonClass commonClass = new CommonClass();
    int checkboxpos;
    CheckBox checkBox;


    public WeekOffApprovalAdapterNew(Context context, List<LeaveAppModal> leaveAppModalList, int pos, RecyclerView recyclerView,
                                     MKLoader loader, LinearLayout approve_layout, TextView decline, TextView approve,
                                     int adaposition, String branchID, String serverDate, String usableDate,
                                     List<String> selectedDeptID, ArrayList<Integer> projectListID1,
                                     CheckBox checkBox, int checkboxpos,String dash){
        this.adaposition =adaposition;
        this.checkboxpos = checkboxpos;
        this.checkBox=checkBox;
        this.branchID = branchID;
        this.dash=dash;
        this.serverDate =serverDate;
        this.usableDate = usableDate;
        this.selectedDeptID = selectedDeptID;
        this.projectListID1 = projectListID1;
        this.leaveAppModalList = leaveAppModalList;
        this.context=context;
        selectedLeaveList = new ArrayList<>();
        this.pos=pos;
        this.recyclerView=recyclerView;
        this.loader=loader;
        this.approve_layout = approve_layout;
        this.approve = approve;
        this.decline = decline;
        leaveIDNewSQL = new WeekOffSQL(context);
        leaveIDNewSQL.getWritableDatabase();

        updateAdapter1();

        Log.d("callAlert"," emp "+commonClass.getSharedPref(context,"AdminEmpNo")+" name "+
                commonClass.getSharedPref(context,"AdminName")+" role "+commonClass.getSharedPref(context,"AdminRole")
                +" admin role name "+commonClass.getSharedPref(context,"AdminRoleName"));
    }
    @NonNull
    @Override
    public WeekOffApprovalAdapterNew.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.new_approval_item_row,null);
        /*TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);
*/
        return new WeekOffApprovalAdapterNew.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekOffApprovalAdapterNew.ProductViewHolder holder, int position) {
        LeaveAppModal modal = leaveAppModalList.get(position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkboxpos=0;
                }else{
                    checkboxpos=-1;
                }
                updateAdapter1();
            }
        });

        Log.d("callAlert"," post "+position);
         holder.name_details.setText(modal.getEmp_name());
        holder.employee_id.setText(" - "+modal.getEmp_empno());
        String value =String.valueOf(modal.getEmp_name().charAt(0));
        holder.first_letter.setText(value);

        holder.status.setVisibility(View.VISIBLE);  // Default visibility
        holder.status.setText("");
        holder.status.setBackgroundTintList(null);
        if(modal.getStatus().equals("3")){
            //not yet approved
            holder.status.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
        }else if(modal.getStatus().equals("9") || modal.getStatus().equals("6")){
            holder.checkbox.setVisibility(View.GONE);
            if(modal.getStatus().equals("9")){
                holder.status.setText("Auto Cancelled");
            }else{
                holder.status.setText("Cancelled");
            }

            holder.status.setTextColor(context.getResources().getColor(R.color.warning_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.warning_color_shade_20));
        }else if(modal.getStatus().equals("8")){
            holder.status.setText("Declined");
            holder.status.setTextColor(context.getResources().getColor(R.color.failure_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.failure_color_shade_20));
            holder.checkbox.setVisibility(View.GONE);
        }else if(modal.getStatus().equals("7")){
            holder.checkbox.setVisibility(View.GONE);
            holder.status.setText("Approved");
            holder.status.setTextColor(context.getResources().getColor(R.color.success_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.success_color_20));
        }else{
            holder.status.setText("Cancelled");
            holder.status.setTextColor(context.getResources().getColor(R.color.warning_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.warning_color_shade_20));
            //holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.reactangle_padding_with_border_solid));
            holder.checkbox.setVisibility(View.VISIBLE);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAlertDialog(modal,pos);
            }
        });


        Log.d("getDetailsCheck"," pos "+position+" getdata "+ leaveIDNewSQL.selectRow(modal.getOvertime_id(),
                String.valueOf(position)));
        holder.checkbox.setChecked(modal.isChecked());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminEmpNo")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminRole")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminName"))) {
                    if (commonClass.getSharedPref(context, "AdminEmpNo").equals(modal.getEmp_empno()) &&
                            commonClass.getSharedPref(context, "role_no").equals("70")) {
                        modal.setChecked(false);
                    } else {
                        modal.setChecked(isChecked);
                        if(isChecked){
                            leaveIDNewSQL.insertData(modal.getWeekoff_id(),String.valueOf(position));
                            if(leaveIDNewSQL.getAllList().size()!=0){
                                approve_layout.setVisibility(View.VISIBLE);
                            }else{
                                approve_layout.setVisibility(View.GONE);
                            }

                        }else{
                            leaveIDNewSQL.deleteRecord(modal.getWeekoff_id(),String.valueOf(position));
                            if(leaveIDNewSQL.getAllList().size()!=0){
                                approve_layout.setVisibility(View.VISIBLE);
                            }else{
                                approve_layout.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = leaveIDNewSQL.getAllList();
                if(list.size()==0){
                    commonClass.showWarning((Activity) context,"Select Request");
                }else {
                    list = leaveIDNewSQL.getAllList();
                    updateAdapter(0,list);
                    //callRetrofit(0);
                }

            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = leaveIDNewSQL.getAllList();

                if(list.size()==0){
                    commonClass.showWarning((Activity) context,"Select Request");
                }else {
                    callRetrofit(1);
                }
            }
        });
    }

    private void updateAdapter1() {
        if(checkboxpos==-1){
            for(int i=0;i<leaveAppModalList.size();i++){
                LeaveAppModal modal = leaveAppModalList.get(i);
                leaveIDNewSQL.DropTable();
                // leaveIDNewSQL.deleteRecord(modal.getClaim_id(),String.valueOf(i));
                if(leaveIDNewSQL.getAllList().size()!=0){
                    approve_layout.setVisibility(View.VISIBLE);
                }else{
                    approve_layout.setVisibility(View.GONE);
                }
                modal.setChecked(false);
            }
            notifyDataSetChanged();
        }else{
            for(int i=0;i<leaveAppModalList.size();i++){
                LeaveAppModal modal = leaveAppModalList.get(i);
                if(modal.getStatus().equals("3")) {

                    if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminEmpNo")) &&
                            !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminRole")) &&
                            !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminName"))) {
                        if (commonClass.getSharedPref(context, "AdminEmpNo").equals(modal.getEmp_empno()) &&
                                commonClass.getSharedPref(context, "role_no").equals("70")) {
                            modal.setChecked(false);
                        } else {
                            leaveIDNewSQL.insertData(modal.getWeekoff_id(), String.valueOf(i));
                            if (leaveIDNewSQL.getAllList().size() != 0) {
                                approve_layout.setVisibility(View.VISIBLE);
                            } else {
                                approve_layout.setVisibility(View.GONE);
                            }
                            modal.setChecked(true);
                        }
                    }



                }
            }
            notifyDataSetChanged();
        }

    }

    private void updateAdapter(int method, List<String> list) {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                commonClass.getDeviceID(context)).create(ApiInterface.class);
        Call<CommonPojo> call;
        if(method==0){
            //approve
            call = apiInterface.weekoffApproved(list);
        }else{
            //decline
            call = apiInterface.weekoffDeclined(list);
        }
        Log.d("leaveadapter"," url as "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                Log.d("leaveadapter"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("leaveadapter"," status "+response.body().getStatus()+" data "+
                                response.body().getData());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess((Activity) context,response.body().getData());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    /*if(mDialog!=null){
                                        if(mDialog.isShowing()){*/
                                    commonClass.putSharedPref(context,"dash","dash");
                                    leaveIDNewSQL.deleteAll();

                                    Intent intent = new Intent(context, AdminNewApprovals.class);
                                            intent.putExtra("position",adaposition);
                                            intent.putExtra("branch",branchID);
                                            intent.putExtra("dash","dash");
                                            intent.putExtra("serverDate",serverDate);
                                            intent.putExtra("projectID",(Serializable) projectListID1);
                                            intent.putExtra("usableDate",usableDate);
                                            intent.putExtra("department",(Serializable) selectedDeptID);
                                            context.startActivity(intent);
                                     /*   }
                                    }*/            }
                            }, 2500);

                        }else{
                            commonClass.showError((Activity) context,response.body().getError());

                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError((Activity) context,mError.getError());
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

                        commonClass.showError((Activity) context,mError.getError());
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
                Log.d("leaveadapter"," error "+t.getMessage());
            }
        });
    }

    public String dateSplt(String date){
        String[] date_1= date.split(" ");
        String[] date_2 = date_1[0].split("-");
        String final_str = date_2[2]+"-"+date_2[1]+"-"+date_2[0];
        return final_str;
    }

    private void callAlertDialog(LeaveAppModal modal,int pos) {
        Log.d("callAlert"," to "+modal.getTo()+" leave type "+modal.getLeave_type()+
                " leave id "+modal.getOvertime_id()+" type "+modal.getType()+" status "+modal.getStatus());
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.common_request_dialog,null);
        ImageView close = view.findViewById(R.id.close);
        LinearLayout alignment_layout = view.findViewById(R.id.alignment_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView name = view.findViewById(R.id.name);
        ImageView status_icon = view.findViewById(R.id.status_icon);
        TextView emp_no = view.findViewById(R.id.emp_no);
        TextView dept = view.findViewById(R.id.dept);
        TextView date = view.findViewById(R.id.date);
        ImageView file_pick = view.findViewById(R.id.file_pick);
        TextView amount = view.findViewById(R.id.amount);
        TextView reason = view.findViewById(R.id.reason);
        TextView status = view.findViewById(R.id.status);
        TextView id = view.findViewById(R.id.id);
        TextView type = view.findViewById(R.id.type);
        TextView leave_type = view.findViewById(R.id.leave_type);
        LinearLayout decline = view.findViewById(R.id.decline);
        LinearLayout approve = view.findViewById(R.id.approve);

        id.setText("Emp ID : "+modal.getEmp_empno());
        name.setText(modal.getEmp_name() );
        emp_no.setVisibility(View.GONE);
        emp_no.setText(modal.getEmail());
        dept.setText(modal.getDep_name());
        if(!TextUtils.isEmpty(modal.getFrom()) && !TextUtils.isEmpty(modal.getTo())){
            if(modal.getFrom().equals(modal.getTo())){
                date.setText(dateSplt(modal.getFrom()));
            }else{
                date.setText(dateSplt(modal.getFrom())+" to "+dateSplt(modal.getTo()));
            }
        }else{
            date.setText(dateSplt(modal.getFrom()));
        }
        if(!TextUtils.isEmpty(modal.getReason())){
            reason.setText(modal.getReason());
        }
        if(!TextUtils.isEmpty(modal.getFile())){
            file_pick.setVisibility(View.VISIBLE);
        }else{
            file_pick.setVisibility(View.GONE);
        }
        file_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(modal.getFile()));
                context.startActivity(i);
            }
        });
       /* type.setText(getTypeString(modal.getType(),modal.getLeave_type()));
        leave_type.setText(getLeaveType(modal.getLeave_type(),modal.getType()));*/
        type.setText("OnDuty");
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int getMonth=0;
        SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate=null,todayDate=null;
        int comparison=0;
        String today_date = date2.format(new Date());
        try {
            if(TextUtils.isEmpty(modal.getTo())){
                toDate = date2.parse(modal.getFrom());
            }else{
                toDate = date2.parse(modal.getTo());
            }
            todayDate = date2.parse(today_date);
            comparison = toDate.compareTo(todayDate);
            calendar.setTime(toDate);
            getMonth = calendar.get(Calendar.MONTH);

            Log.d("callAlert"," comparision "+comparison);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }



        if(modal.getStatus().equals("3")){
            status.setText(null);
            approve.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);
        }else if(modal.getStatus().equals("6") ){
            approve.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
            status.setText("Cancelled");
            status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled_icon_2));
            status.setTextColor(context.getResources().getColor(R.color.failure_color));
        }else if(modal.getStatus().equals("7") ){
            params.gravity= Gravity.RIGHT;
            alignment_layout.setLayoutParams(params);
            if(comparison>0){
                approve.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.approve_icon));
                status_icon.setImageTintList(context.getResources().getColorStateList(R.color.success_color));
                status.setText(modal.getApprove_name()+" Approved");
                status.setTextColor(context.getResources().getColor(R.color.success_color));
            }else{
                approve.setVisibility(View.GONE);
                if(currentMonth==getMonth) {
                    decline.setVisibility(View.VISIBLE);
                }else{
                    decline.setVisibility(View.GONE);
                }
                status.setText(modal.getApprove_name()+" Approved");
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.approve_icon));
                status_icon.setImageTintList(context.getResources().getColorStateList(R.color.success_color));
                status.setTextColor(context.getResources().getColor(R.color.success_color));

            }

        }else if(modal.getStatus().equals("8")){
            params.gravity= Gravity.LEFT;
            alignment_layout.setLayoutParams(params);
            if(comparison>0){
                approve.setVisibility(View.VISIBLE);
                decline.setVisibility(View.GONE);
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled_icon_2));
                status.setText(modal.getApprove_name()+" Declined");
                status.setTextColor(context.getResources().getColor(R.color.failure_color));
            }else{
                if(currentMonth==getMonth) {
                    approve.setVisibility(View.VISIBLE);
                }else{
                    approve.setVisibility(View.GONE);
                }
                 decline.setVisibility(View.GONE);
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled_icon_2));
                status.setText(modal.getApprove_name()+" Declined");
                status.setTextColor(context.getResources().getColor(R.color.failure_color));
            }
        }else if(modal.getStatus().equals("9")){
            status.setText("Auto Cancelled");
            status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled_icon_2));
            status.setTextColor(context.getResources().getColor(R.color.failure_color));
        }


        if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminName"))) {
            if (commonClass.getSharedPref(context, "AdminEmpNo").equals(modal.getEmp_empno()) &&
                    commonClass.getSharedPref(context, "role_no").equals("70")) {
                decline.setVisibility(View.GONE);
                approve.setVisibility(View.GONE);
            }
        }

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;


       /* WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER ;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);*/



        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                leaveIDNewSQL.DropTable();
                leaveIDNewSQL.insertData(modal.getWeekoff_id(),String.valueOf(pos));
                list = leaveIDNewSQL.getAllList();
                updateAdapter(0,list);
               // callRetrofit(0);
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                leaveIDNewSQL.DropTable();
                leaveIDNewSQL.insertData(modal.getWeekoff_id(),String.valueOf(pos));
                callRetrofit(1);
            }
        });

    }

    public String getLeaveType(String type,String leave){
        String str_type="";
        if(!TextUtils.isEmpty(type) && !TextUtils.isEmpty(leave)){
            if(type.equals("3")){
                str_type="Permission";
            }else if(type.equals("1")){
                str_type ="Half Day";
            }else if(type.equals("2")){
                str_type="Full Day";
            }else if(type.equals("4")){
                if(leave.equals("1")){
                    str_type="Alternative";
                }else if(leave.equals("2")){
                    str_type="Alternative";
                }else{
                    str_type="Alternative";
                }
            }else{
                str_type="Alternative";
            }
        }else{
            str_type="Alternative";
        }


        return str_type;
    }

    public String getTypeString(String type,String leave_type){
        String leaveType="";
        if(!TextUtils.isEmpty(leave_type)&& !TextUtils.isEmpty(type)) {
            if (leave_type.equals("4")) {
                if (type.equals("1")) {
                    leaveType = "Casual Leave";
                } else if (type.equals("2")) {
                    leaveType = "Sick Leave";
                } else {
                    leaveType = " ";
                }
            } else {
                if (type.equals("1")) {
                    leaveType = "Casual Leave";
                } else if (type.equals("2")) {
                    leaveType = "Sick Leave";
                } else if (type.equals("3")) {
                    leaveType = "Medical Leave";
                } else {
                    leaveType = " ";
                }
            }
        }
        else{
            leaveType = " ";
        }
        return leaveType;
    }
    public void callRetrofit(int method){

        list = leaveIDNewSQL.getAllList();

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
        LinearLayout close = view.findViewById(R.id.cancel);
        LinearLayout approve = view.findViewById(R.id.approve);

        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

/*
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER ;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);*/



        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
               updateAdapter(method,list);
            }
        });



    }
    @Override
    public void onViewRecycled(@NonNull WeekOffApprovalAdapterNew.ProductViewHolder holder) {
        super.onViewRecycled(holder);
        holder.checkbox.setOnCheckedChangeListener(null);
    }

    @Override
    public int getItemCount() {
        return leaveAppModalList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        CheckBox checkbox;
        TextView name_details,first_letter,employee_id,status;
        ImageView back_arrow;
        public ProductViewHolder(View view) {
            super(view);
            first_letter = view.findViewById(R.id.first_letter);
            employee_id = view.findViewById(R.id.employee_id);
            status = view.findViewById(R.id.status);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            checkbox = view.findViewById(R.id.checkbox);
            name_details = view.findViewById(R.id.name_details);
            back_arrow = view.findViewById(R.id.back_arrow);
        }
    }
}

