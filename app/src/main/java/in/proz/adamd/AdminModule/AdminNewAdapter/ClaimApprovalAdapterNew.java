package in.proz.adamd.AdminModule.AdminNewAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.RadioButton;
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
import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimApprovalAdapterNew extends RecyclerView.Adapter<ClaimApprovalAdapterNew.ProductViewHolder> {
    List<LeaveAppModal> leaveAppModalList;
    String date_main,str_desc,decline_reson;
    boolean alertbol=false,alertapp=false,alertdic=false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    List<String> list = new ArrayList<>();
    Context context;
    String dash;
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
    RelativeLayout tagLayout;


    public ClaimApprovalAdapterNew(Context context, List<LeaveAppModal> leaveAppModalList, int pos, RecyclerView recyclerView,
                                   MKLoader loader, LinearLayout approve_layout, TextView decline, TextView approve,
                                   int adaposition, String branchID, String serverDate, String usableDate,
                                   List<String> selectedDeptID, ArrayList<Integer> projectListID1,
                                   CheckBox checkBox, int checkboxpos,RelativeLayout tagLayout,String dash){
        this.adaposition =adaposition;
        this.dash=dash;
        this.tagLayout=tagLayout;
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
        tagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callClaimDialog();
            }
        });


        Log.d("callAlert"," emp "+commonClass.getSharedPref(context,"AdminEmpNo")+" name "+
                commonClass.getSharedPref(context,"AdminName")+" role "+commonClass.getSharedPref(context,"AdminRole")
                +" admin role name "+commonClass.getSharedPref(context,"AdminRoleName"));
    }

    private void callClaimDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_claim_radio);
        dialog.setTitle("This is my custom dialog box");
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd_1);
        RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);
        rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rd2.setChecked(false);
                }
            }
        });
        rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rd1.setChecked(false);
                }
            }
        });
        LinearLayout btn_continue1 = dialog.findViewById(R.id.approve);
        // now that the dialog is set up, it's time to show it
        dialog.show();
        btn_continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rd1.isChecked() || rd2.isChecked()) {
                    String type = "0";
                    if (rd1.isChecked()) {
                        type = "claim";
                    }
                    if (rd2.isChecked()) {
                        type = "advance_claim";
                    }
                    Intent intent123 = new Intent(context, ClaimActivity.class);
                    intent123.putExtra("claim_type", type);
                    intent123.putExtra("position",4);
                    context.startActivity(intent123);
                } else {
                    commonClass.showWarning((Activity) context, "Please Select any option");
                }
            }
        });
    }

    @NonNull
    @Override
    public ClaimApprovalAdapterNew.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.new_approval_item_row,null);
        return new ClaimApprovalAdapterNew.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimApprovalAdapterNew.ProductViewHolder holder, int position) {
        LeaveAppModal modal = leaveAppModalList.get(position);




        Log.d("callAlert"," post "+position);
        holder.name_details.setText(modal.getEmp_name());
        holder.employee_id.setText( " - "+modal.getEmp_empno());
        if(!TextUtils.isEmpty(modal.getReason())){
            holder.reason.setVisibility(View.VISIBLE);
            holder.reason.setText(modal.getReason());
        }
        if(!TextUtils.isEmpty(modal.getAmount())){
            holder.leave_type.setText("Amount");
            holder.leave_type.setVisibility(View.VISIBLE);
            holder.date_leave.setVisibility(View.VISIBLE);
            holder.date_leave.setText("₹"+modal.getAmount());
        }
        String value =String.valueOf(modal.getEmp_name().charAt(0));
        holder.first_letter.setText(value);
        check_all.setVisibility(View.VISIBLE);
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
        holder.status.setVisibility(View.VISIBLE);  // Default visibility
        holder.status.setText("");
        holder.status.setBackgroundTintList(null);

        if(modal.getStatus().equals("3")){
            modal.setCheckMyStatus(modal.getStatus());
             holder.status.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
        }else if(modal.getStatus().equals("9") || modal.getStatus().equals("6")){
            modal.setCheckMyStatus(modal.getStatus());
            holder.checkbox.setVisibility(View.GONE);
            if(modal.getStatus().equals("9")){
                holder.status.setText("Auto Cancelled");
            }else{
                holder.status.setText("Cancelled");
            }
            holder.status.setTextColor(context.getResources().getColor(R.color.warning_color));

            // holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_red));
        }else if(modal.getStatus().equals("8")){
            modal.setCheckMyStatus(modal.getStatus());
            //if (dateTo < today) { -> decliend
            //else approve , declien
            holder.status.setText("Declined");
            holder.status.setTextColor(context.getResources().getColor(R.color.failure_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.failure_color_shade_20));
           //  holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_red));
            holder.checkbox.setVisibility(View.GONE);
        }else if(modal.getStatus().equals("7")){
            modal.setCheckMyStatus(modal.getStatus());
            holder.checkbox.setVisibility(View.GONE);
            holder.status.setText("Approved");
            holder.status.setTextColor(context.getResources().getColor(R.color.success_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.success_color_20));
          //  holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.ad_leave_green));
        }else{
            modal.setCheckMyStatus(modal.getStatus());
            holder.status.setText("Cancelled");
            holder.status.setTextColor(context.getResources().getColor(R.color.warning_color));
            holder.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.warning_color_shade_20));
           // holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.reactangle_padding_with_border_solid));
            holder.checkbox.setVisibility(View.GONE);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertbol=!alertbol;
                if(alertbol){
                    callAlertDialog(modal,pos);
                }
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
                    LinearLayout block_btn = view.findViewById(R.id.block_btn);

                    EditText text =  view.findViewById(R.id.reason);
                    text.setHint("Decline Reason");
                    TextView ok_btn = view.findViewById(R.id.ok_btn);
                    LinearLayout decline = view.findViewById(R.id.decline);
                    decline.setVisibility(View.VISIBLE);
                     block_btn.setVisibility(View.GONE);
                    builder.setView(view);
                    final AlertDialog mDialog = builder.create();
                    mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                   /* Window window = mDialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    window.setAttributes(wlp);*/

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
                                list = leaveIDNewSQL.getAllList();
                                updateRetrofit(1,list,mDialog);
                                //callRetrofit(1);
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
                    if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminEmpNo")) &&
                            !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminRole")) &&
                            !TextUtils.isEmpty(commonClass.getSharedPref(context,"AdminName"))) {
                        if (commonClass.getSharedPref(context, "AdminEmpNo").equals(modal.getEmp_empno()) &&
                                commonClass.getSharedPref(context, "role_no").equals("70")) {
                            modal.setChecked(false);
                        } else {
                            leaveIDNewSQL.insertData(modal.getClaim_id(), String.valueOf(i));
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
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.common_request_dialog,null);
        ImageView close = view.findViewById(R.id.close);
        TextView id = view.findViewById(R.id.id);
        LinearLayout alignment_layout = view.findViewById(R.id.alignment_layout);
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
        id.setText("Emp ID : "+modal.getEmp_empno());
       // emp_no.setText(modal.getEmail());
        emp_no.setVisibility(View.GONE);
        dept.setText(modal.getDep_name());
        if(!TextUtils.isEmpty(modal.getFrom()) && !TextUtils.isEmpty(modal.getTo())){
            if(modal.getFrom().equals(modal.getTo())){
                date.setText(dateSplt(modal.getFrom())+" to "+dateSplt(modal.getTo()));
            }else{
                date.setText(dateSplt(modal.getFrom()));
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
       /* type.setText(getTypeString(modal.getType(),modal.getLeave_type())); */

        if(!TextUtils.isEmpty(modal.getAmount())){

            leave_type.setText("Amount ₹ "+modal.getAmount());
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

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
                 params.gravity= Gravity.RIGHT;
                alignment_layout.setLayoutParams(params);
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
                params.gravity= Gravity.RIGHT;
                alignment_layout.setLayoutParams(params);
                status.setText(modal.getApprove_name()+" Approved");
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.approve_icon));
                status_icon.setImageTintList(context.getResources().getColorStateList(R.color.success_color));
                status.setTextColor(context.getResources().getColor(R.color.success_color));

            }

        }else if(modal.getStatus().equals("8")){
            if(comparison>0){
                approve.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                params.gravity= Gravity.LEFT;
                alignment_layout.setLayoutParams(params);
                status_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled_icon_2));
                status.setText(modal.getApprove_name()+" Declined");
                status.setTextColor(context.getResources().getColor(R.color.failure_color));
            }else{
                if(currentMonth==getMonth) {
                    approve.setVisibility(View.VISIBLE);
                }else{
                    approve.setVisibility(View.GONE);
                }
                params.gravity= Gravity.LEFT;
                alignment_layout.setLayoutParams(params);
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
        layoutParams.gravity = Gravity.CENTER  ;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);*/



        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                alertbol=!alertbol;
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                alertbol=!alertbol;
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
                alertbol=!alertbol;
                leaveIDNewSQL.DropTable();
                leaveIDNewSQL.insertData(modal.getClaim_id(),String.valueOf(pos));


                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.common_block_reason,null);
                ImageView close = view.findViewById(R.id.close);

                EditText text =  view.findViewById(R.id.reason);
                text.setHint("Decline Reason");
                 LinearLayout ok_btn = view.findViewById(R.id.block_btn);
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
                            list = leaveIDNewSQL.getAllList();
                            updateRetrofit(1,list,mDialog);

                            // callRetrofit(1);
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
                    leaveType = "";
                }
            } else {
                if (type.equals("1")) {
                    leaveType = "Casual Leave";
                } else if (type.equals("2")) {
                    leaveType = "Sick Leave";
                } else if (type.equals("3")) {
                    leaveType = "Medical Leave";
                } else {
                    leaveType = "";
                }
            }
        }
        else{
            leaveType = "";
        }
        return leaveType;
    }
    public void callRetrofit(int method){

        list = leaveIDNewSQL.getAllList();

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
         LinearLayout approve = view.findViewById(R.id.approve);
         LinearLayout cancel = view.findViewById(R.id.cancel);


        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;


        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER  ;
        layoutParams.y = 700;
        mDialog.getWindow().setAttributes(layoutParams);



        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
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
        Log.d("leaveadapter"," url as "+call.request().url()+" size "+list.size());
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
    public void onViewRecycled(@NonNull ClaimApprovalAdapterNew.ProductViewHolder holder) {
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
        TextView name_details,first_letter,employee_id,status,leave_type,reason,date_leave;
        ImageView back_arrow;
        public ProductViewHolder(View view) {
            super(view);
            leave_type = view.findViewById(R.id.leave_type);
            date_leave = view.findViewById(R.id.date_leave);
            reason = view.findViewById(R.id.reason);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            checkbox = view.findViewById(R.id.checkbox);
            name_details = view.findViewById(R.id.name_details);
            back_arrow = view.findViewById(R.id.back_arrow);
            first_letter = view.findViewById(R.id.first_letter);
            employee_id = view.findViewById(R.id.employee_id);
            status = view.findViewById(R.id.status);
        }
    }
}

