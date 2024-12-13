package in.proz.adamd.AdminModule.ApprovalsAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminApprovalModal.LeaveAppModal;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.SQLiteDB.ClaimSQL;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimApprovalAdapter extends RecyclerView.Adapter<ClaimApprovalAdapter.ProductViewHolder> {
    List<LeaveAppModal> leaveAppModalList;
    String date_main,str_desc,decline_reson;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    List<String> list = new ArrayList<>();
    Context context;
    List<String> selectedLeaveList;
    ClaimSQL leaveIDNewSQL;
    int pos; RecyclerView recyclerView; MKLoader loader;
    TextView approve,decline;
    LinearLayout approve_layout;
    int adaposition;
    String branchID, serverDate, usableDate;
    List<String> selectedDeptID;
    ArrayList<Integer> projectListID1;
    CommonClass commonClass = new CommonClass();
    CheckBox check_all;
    int checkboxpos;


    public ClaimApprovalAdapter(Context context, List<LeaveAppModal> leaveAppModalList, int pos, RecyclerView recyclerView,
                         MKLoader loader, LinearLayout approve_layout, TextView decline, TextView approve,
                         int adaposition, String branchID, String serverDate, String usableDate,
                         List<String> selectedDeptID, ArrayList<Integer> projectListID1,CheckBox checkBox,int checkboxpos){
        this.adaposition =adaposition;
        this.checkboxpos=checkboxpos;
        this.check_all=checkBox;
        this.branchID = branchID;
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
        leaveIDNewSQL = new ClaimSQL(context);
        leaveIDNewSQL.getWritableDatabase();

notifyDataSetChanged();



        Log.d("callAlert"," emp "+commonClass.getSharedPref(context,"AdminEmpNo")+" name "+
                commonClass.getSharedPref(context,"AdminName")+" role "+commonClass.getSharedPref(context,"AdminRole")
                +" admin role name "+commonClass.getSharedPref(context,"AdminRoleName"));
    }
    @NonNull
    @Override
    public ClaimApprovalAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.ad_leave_item_row,null);
        /*TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);
*/
        return new ClaimApprovalAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimApprovalAdapter.ProductViewHolder holder, int position) {
        LeaveAppModal modal = leaveAppModalList.get(position);

        Log.d("callAlert"," post "+position);
        holder.name_details.setText(modal.getEmp_empno()+" | "+modal.getEmp_name());


        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkboxpos=0;
                }else{
                    checkboxpos=-1;
                }
                updateAdapter();
            }
        });


        if(modal.getStatus().equals("3")){
            //not yet approved
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.reactangle_padding_with_border_solid));
            holder.checkbox.setVisibility(View.VISIBLE);
        }else if(modal.getStatus().equals("9") || modal.getStatus().equals("6")){
            holder.checkbox.setVisibility(View.GONE);
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_red));
        }else if(modal.getStatus().equals("8")){
            //if (dateTo < today) { -> decliend
            //else approve , declien
             holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_red));
            holder.checkbox.setVisibility(View.GONE);
        }else if(modal.getStatus().equals("7")){
            holder.checkbox.setVisibility(View.GONE);
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_green));
        }else{
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.reactangle_padding_with_border_solid));
            holder.checkbox.setVisibility(View.GONE);
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
                modal.setChecked(isChecked);
                if(isChecked){
                    leaveIDNewSQL.insertData(modal.getClaim_id(),String.valueOf(position));
                    if(leaveIDNewSQL.getAllList().size()!=0){
                        approve_layout.setVisibility(View.VISIBLE);
                    }else{
                        approve_layout.setVisibility(View.GONE);
                    }

                }else{
                    leaveIDNewSQL.deleteRecord(modal.getClaim_id(),String.valueOf(position));
                    if(leaveIDNewSQL.getAllList().size()!=0){
                        approve_layout.setVisibility(View.VISIBLE);
                    }else{
                        approve_layout.setVisibility(View.GONE);
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
                    //callRetrofit(0);
                    callApprove();
                   // updateRetrofit(0,list,null);
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
                    //callRetrofit(1);
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View view= LayoutInflater.from(context).inflate(R.layout.common_block_reason,null);
                    ImageView close = view.findViewById(R.id.close);

                    EditText text =  view.findViewById(R.id.reason);
                    text.setHint("Decline Reason");
                    TextView ok_btn = view.findViewById(R.id.ok_btn);
                    LinearLayout decline = view.findViewById(R.id.decline);
                    decline.setVisibility(View.VISIBLE);
                    ok_btn.setVisibility(View.GONE);
                    builder.setView(view);
                    final AlertDialog mDialog = builder.create();
                    mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                    Window window = mDialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    window.setAttributes(wlp);

                    mDialog.create();
                    mDialog.show();
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(text.getText().toString())){
                                commonClass.showWarning((Activity) context,"Enter Decline Reason");
                            }else{
                                mDialog.dismiss();
                                decline_reson = text.getText().toString();
                                callRetrofit(1);
                            }

                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void updateAdapter() {

        if(checkboxpos==-1){
            for(int i=0;i<leaveAppModalList.size();i++){
                LeaveAppModal modal = leaveAppModalList.get(i);
                leaveIDNewSQL.DropTable();
                //leaveIDNewSQL.deleteRecord(modal.getClaim_id(),String.valueOf(i));
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
                    leaveIDNewSQL.insertData(modal.getClaim_id(), String.valueOf(i));
                    if (leaveIDNewSQL.getAllList().size() != 0) {
                        approve_layout.setVisibility(View.VISIBLE);
                    } else {
                        approve_layout.setVisibility(View.GONE);
                    }
                    modal.setChecked(true);
                }
            }
            notifyDataSetChanged();
        }
     }

    public String dateSplt(String date){
        String[] date_1= date.split(" ");
        String[] date_2 = date_1[0].split("-");
        String final_str = date_2[2]+"-"+date_2[1]+"-"+date_2[0];
        return final_str;
    }


    public void datePicker(TextView editText,int status){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date=String.valueOf(dayOfMonth);
                        String str_month=String.valueOf(monthOfYear+1);
                        if(dayOfMonth<=9){
                            str_date="0"+String.valueOf(dayOfMonth);
                        }
                        if((monthOfYear+1)<=9){
                            str_month="0"+String.valueOf(monthOfYear+1);
                        }
                        date_main = year+"-"+str_month+"-"+str_date;
                        editText.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void callApprove(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.activity_approve_dlg,null);
        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        TextView date = view.findViewById(R.id.date);
        EditText desc = view.findViewById(R.id.desc);
        ImageView cal = view.findViewById(R.id.cal);
        LinearLayout approve = view.findViewById(R.id.approve);

        date.setText(sdf1.format(new Date()));
        date_main=sdf.format(new Date());
        builder.setView(view);
        AlertDialog mDialog= builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(date,0);
            }
        });
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(date,0);
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(desc.getText().toString())){
                    commonClass.showWarning((Activity) context,"Enter Description");
                }else{
                    mDialog.dismiss();
                    str_desc = desc.getText().toString();
                    list = leaveIDNewSQL.getAllList();
                    updateRetrofit(0,list,null);
                }
            }
        });

    }

    private void callAlertDialog(LeaveAppModal modal,int pos) {
        Log.d("callAlert"," to "+modal.getTo()+" leave type "+modal.getLeave_type()+
                " leave id "+modal.getOvertime_id()+" type "+modal.getType()+" status "+modal.getStatus());
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.common_request_dialog,null);
        ImageView close = view.findViewById(R.id.close);
        TextView name = view.findViewById(R.id.name);
        ImageView status_icon = view.findViewById(R.id.status_icon);
        TextView emp_no = view.findViewById(R.id.emp_no);
        TextView dept = view.findViewById(R.id.dept);
        TextView date = view.findViewById(R.id.date);
        ImageView file_pick = view.findViewById(R.id.file_pick);
        TextView amount = view.findViewById(R.id.amount);
        TextView reason = view.findViewById(R.id.reason);
        TextView status = view.findViewById(R.id.status);
        TextView type = view.findViewById(R.id.type);
        TextView leave_type = view.findViewById(R.id.leave_type);
        LinearLayout decline = view.findViewById(R.id.decline);
        LinearLayout approve = view.findViewById(R.id.approve);
        LinearLayout payment_details_layout = view.findViewById(R.id.payment_details_layout);
        TextView payment_date = view.findViewById(R.id.payment_date);
        TextView payement_details = view.findViewById(R.id.payement_details);
        TextView decline_reason = view.findViewById(R.id.decline_reason);

        if(!TextUtils.isEmpty(modal.getDecline_reason())){
            payment_details_layout.setVisibility(View.VISIBLE);
            decline_reason.setText(modal.getDecline_reason());
            decline_reason.setVisibility(View.VISIBLE);
            payment_date.setVisibility(View.GONE);
            payement_details.setVisibility(View.GONE);
        }else if(!TextUtils.isEmpty(modal.getPayment_date())){
            payment_details_layout.setVisibility(View.VISIBLE);
            payment_date.setText(dateSplt(modal.getPayment_date()));
            payment_date.setVisibility(View.VISIBLE);
            payement_details.setText(modal.getPay_description());
            payement_details.setVisibility(View.VISIBLE);
            decline_reason.setVisibility(View.GONE);
        }else{
            payment_details_layout.setVisibility(View.GONE);
        }




         name.setText(modal.getEmp_name());
        emp_no.setText(modal.getEmp_empno());
        dept.setText(modal.getDep_name());
        if(!TextUtils.isEmpty(modal.getFrom()) && !TextUtils.isEmpty(modal.getTo())){
            date.setText(dateSplt(modal.getFrom())+" to "+dateSplt(modal.getTo()));
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
       /* type.setText(getTypeString(modal.getType(),modal.getLeave_type())); */

        if(!TextUtils.isEmpty(modal.getAmount())){
            leave_type.setText("Amount ₹"+modal.getAmount());
        }
        type.setText(modal.getType_label());
        Log.d("callAlert", " comparision   to da "+modal.getFrom()+"to "+
                modal.getTo()+"  today ");
        SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate=null,todayDate=null;
        int comparison=0;
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int getMonth=0;
        String today_date = date2.format(new Date());
        if(modal.getFrom()!=null) {
            try {
                if(TextUtils.isEmpty(modal.getTo())){
                    toDate = date2.parse(modal.getFrom());
                }else{
                    toDate = date2.parse(modal.getTo());
                }
                calendar.setTime(toDate);
                getMonth = calendar.get(Calendar.MONTH);
                todayDate = date2.parse(today_date);
                comparison = toDate.compareTo(todayDate);

                Log.d("callAlert", " comparision " + comparison+"  to da "+modal.getFrom()+"to "+
                        modal.getTo()+"  today "+today_date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        Log.d("getPostion"," pos "+pos+" status "+modal.getStatus()+" comparision "+comparison);


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
            if(comparison>0){
                approve.setVisibility(View.GONE);
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


        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;


        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);



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
                leaveIDNewSQL.insertData(modal.getClaim_id(),String.valueOf(pos));
                //callRetrofit(0);
              callApprove();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                leaveIDNewSQL.DropTable();
                leaveIDNewSQL.insertData(modal.getClaim_id(),String.valueOf(pos));


                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.common_block_reason,null);
                ImageView close = view.findViewById(R.id.close);

                EditText text =  view.findViewById(R.id.reason);
                text.setHint("Decline Reason");
                TextView ok_btn = view.findViewById(R.id.ok_btn);
                LinearLayout decline = view.findViewById(R.id.decline);
                decline.setVisibility(View.VISIBLE);
                ok_btn.setVisibility(View.GONE);
                builder.setView(view);
                final AlertDialog mDialog = builder.create();
                mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                Window window = mDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                window.setAttributes(wlp);

                mDialog.create();
                mDialog.show();
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(text.getText().toString())){
                            commonClass.showWarning((Activity) context,"Enter Decline Reason");
                        }else{
                            mDialog.dismiss();
                            decline_reson = text.getText().toString();
                            callRetrofit(1);
                        }

                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });


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
                    leaveType =  " ";
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

        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.common_request_warning,null);
        ImageView warning_icon = view.findViewById(R.id.warning_icon);
        TextView message = view.findViewById(R.id.message);
        TextView msg_text = view.findViewById(R.id.msg_text);
        TextView btn_ok = view.findViewById(R.id.btn_ok);
        ImageView close = view.findViewById(R.id.close);
        warning_icon.setVisibility(View.GONE);
        if(method==0){
            message.setText("Approve");
            msg_text.setText("Are you sure you want to Approve this request?");
        }else{
            message.setText("Decline");
            msg_text.setText("Are you sure you want to Decline this request?");
        }

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;


        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);



        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                updateRetrofit(method,list,mDialog);
            }
        });



    }
    public void  updateRetrofit(int method, List<String> list, AlertDialog mDialog){
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                commonClass.getDeviceID(context)).create(ApiInterface.class);
        Call<CommonPojo> call;
        if(method==0){
            //approve
            call = apiInterface.claimAproved(list,date_main,str_desc);
        }else{
            //decline
            call = apiInterface.claimDeclined(list,decline_reson);
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
                            str_desc=null;
                            decline_reson = null;
                            date_main = sdf.format(new Date());
                            commonClass.showSuccess((Activity) context,response.body().getData());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                   /* if(mDialog!=null){
                                        if(mDialog.isShowing()){*/
                                            Intent intent = new Intent(context, AdminNewApprovals.class);
                                            intent.putExtra("position",adaposition);
                                            intent.putExtra("branch",branchID);
                                            intent.putExtra("serverDate",serverDate);
                                            intent.putExtra("projectID",(Serializable) projectListID1);
                                            intent.putExtra("usableDate",usableDate);
                                            intent.putExtra("department",(Serializable) selectedDeptID);
                                            context.startActivity(intent);
                                        /*}
                                    }  */          }
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
    @Override
    public void onViewRecycled(@NonNull ClaimApprovalAdapter.ProductViewHolder holder) {
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
        TextView name_details;
        ImageView back_arrow;
        public ProductViewHolder(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            checkbox = view.findViewById(R.id.checkbox);
            name_details = view.findViewById(R.id.name_details);
            back_arrow = view.findViewById(R.id.back_arrow);
        }
    }
}

