package in.proz.adamd.AdminModule.AdminAdapter;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminEmployee.AdminEmpActivityNew;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeAdapterNew extends RecyclerView.Adapter<EmployeeAdapterNew.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Activity context;
    String onDutyDate=null;
    int commonPos;
    boolean emp_status=false;
    RecyclerView recyclerView;
    // ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;

    public  EmployeeAdapterNew(Activity context, List<CommonPojo> commonPojoList, int commonPos,
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
        View view=inflater.inflate(R.layout.new_employee_row_items,null);
       /* TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);
*/
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = commonPojoList.get(position);
        holder.emp_id.setText(commonPojo.getEmp_no());
        holder.name.setText(commonPojo.getName());
        if(!TextUtils.isEmpty(commonPojo.getIs_blocked())){
            if(commonPojo.getIs_blocked().equals("1")){
                holder.user_status.setImageDrawable(context.getResources().getDrawable(R.drawable.active_user_icon));
            }else if(commonPojo.getIs_blocked().equals("0")){
                holder.user_status.setImageDrawable(context.getResources().getDrawable(R.drawable.block_user));
            }
        }else if(!TextUtils.isEmpty(commonPojo.getActive())){
            if(commonPojo.getActive().equals("2")){
                holder.user_status.setImageDrawable(context.getResources().getDrawable(R.drawable.relieve_icon));
            }
        }
        String value =String.valueOf(commonPojo.getName().charAt(0));
        holder.first_letter.setText(value);

        holder.user_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commonPojo.getIs_blocked().equals("1")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View view= LayoutInflater.from(context).inflate(R.layout.common_block_reason,null);
                    ImageView close = view.findViewById(R.id.close);

                    EditText text =  view.findViewById(R.id.reason);
                    TextView ok_btn = view.findViewById(R.id.ok_btn);
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
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(text.getText().toString())){
                                commonClass.showWarning(context,"Enter Block Reason");
                            }else{
                                mDialog.dismiss();
                                callAlertDialog(context,commonPojo,"Are you sure you want to\n block the user?",
                                        "block",text.getText().toString(), loader,"Block");
                            }

                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }else{


                    callAlertDialog(context,commonPojo,"Are you sure you want to\n unblock the user?",
                            "unblock",null, loader,"UnBlock");

                }
            }
        });

        holder.onduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emp_status=!emp_status;
                if(emp_status){
                    callOnDutyDialog(context,commonPojo);
                   // callOnDutyInsertMethod();
                }
            }
        });
        holder.reset_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(commonPojo.getIs_blocked())){
                    if(commonPojo.getIs_blocked().equals("1")) {

                        callAlertDialog(context,commonPojo,"Are you sure you want to\n reset the device?",
                                "reset",null,loader,"Reset");

                    }else{
                        commonClass.showWarning((Activity) context,"Your are not Active User to reset device");
                    }
                }else{
                    commonClass.showWarning((Activity) context,"Your are not Active User to reset device");
                }
            }
        });
        holder.emp_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployeeDetails(context,commonPojo);
            }
        }); holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployeeDetails(context,commonPojo);
            }
        });

    }
    public void EmployeeDetails(Context context, CommonPojo commonPojo){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.employee_dialog,null);
        ImageView close = view.findViewById(R.id.close);
        RelativeLayout reason_layout = view.findViewById(R.id.reason_layout);
        TextView emp_id = (TextView) view.findViewById(R.id.emp_no);
        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.id_val);
        TextView status = view.findViewById(R.id.status);
        TextView dept = view.findViewById(R.id.dept);
        TextView phone = view.findViewById(R.id.phone);
        TextView email = view.findViewById(R.id.email);
        email.setVisibility(View.GONE);
        TextView mail = view.findViewById(R.id.mail);
        TextView reason = view.findViewById(R.id.reason);
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
        if(!TextUtils.isEmpty(commonPojo.getComp_email())){
            email.setText(commonPojo.getComp_email());
            email.setVisibility(View.VISIBLE);
        }else{
            email.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(commonPojo.getIs_blocked())){
            if(commonPojo.getIs_blocked().equals("1")){
                status.setText("Active");
                status.setTextColor(context.getResources().getColor(R.color.n_green));
            }else if(commonPojo.getIs_blocked().equals("0")){
                status.setText("Blocked");
                status.setTextColor(context.getResources().getColor(R.color.war1));
            }
        }else if(!TextUtils.isEmpty(commonPojo.getActive())){
            if(commonPojo.getActive().equals("2")){
                status.setText("Relieved");
                status.setTextColor(context.getResources().getColor(R.color.color_primary));
            }
        }

        id.setText("Emp ID : "+commonPojo.getEmp_no());
        name.setText(commonPojo.getName());
        if (!TextUtils.isEmpty(commonPojo.getComp_email())){
            emp_id.setText(commonPojo.getComp_email());
        }else{
            emp_id.setText("-");
        }
        if(!TextUtils.isEmpty(commonPojo.getDept_name())){
            dept.setText(commonPojo.getDept_name());
        }else{
            dept.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(commonPojo.getContact())){
            if(commonPojo.getContact().length()<2){
                phone.setText("-");
            }else{
                phone.setText(commonPojo.getContact());
            }
        }else{
            phone.setText("-");
        }
        phone.setOnClickListener(new View.OnClickListener() {
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


        if(!TextUtils.isEmpty(commonPojo.getReason())){
            reason_layout.setVisibility(View.VISIBLE);
            reason.setText(commonPojo.getReason());
        }else{
            reason_layout.setVisibility(View.GONE);
            reason.setText("-");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }
    public void callOnDutyDialog(Activity context, CommonPojo commonPojo){
        AlertDialog.Builder builder=new AlertDialog.Builder(this.context);
        View view= LayoutInflater.from(this.context).inflate(R.layout.onduty_request,null);
        ImageView close = view.findViewById(R.id.close);
        ImageView mike = view.findViewById(R.id.mike);
        EditText selete_date = view.findViewById(R.id.selete_date);
        EditText edt_reason = view.findViewById(R.id.edt_reason);
        LinearLayout request_layout = view.findViewById(R.id.request_layout);
        LinearLayout reset_layout = view.findViewById(R.id.reset_layout);
        MKLoader loader1 = view.findViewById(R.id.loader);
        TextView id_val = view.findViewById(R.id.id_val);
        id_val.setText(commonPojo.getName()+"("+commonPojo.getEmp_no()+")");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        onDutyDate = sdf.format(new Date());
        selete_date.setText(sdf1.format(new Date()));




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
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emp_status=!emp_status;
                mDialog.dismiss();
            }
        });
        mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof AdminEmpActivityNew) { // Ensure context is the activity
                    ((AdminEmpActivityNew) context).recordVoiceToText(edt_reason); // Call the activity method
                }
            }
        });
        selete_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDatePicker(context,selete_date);
            }
        });
        reset_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emp_status=!emp_status;
                mDialog.dismiss();
            }
        });
        request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(onDutyDate)){
                    commonClass.showWarning(context,"Select OnDuty Date");
                }else if(TextUtils.isEmpty(edt_reason.getText().toString())){
                    commonClass.showWarning(context,"Enter Reason");
                }else{
                    request_layout.setEnabled(false);
                    loader1.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                            commonClass.getDeviceID(context)).create(ApiInterface.class);
                    Call<CommonPojo> call=null ;

                    call = apiInterface.insertOnDutyDialog(onDutyDate,
                            "5",edt_reason.getText().toString(),commonPojo.getEmp_id());


                    Log.d("insertMethod"," url "+call.request().url());
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            //  progressDialog.dismiss();
                            loader1.setVisibility(View.GONE);
                            request_layout.setEnabled(true);
                            if(response.isSuccessful()){
                                Log.d("insertMethod"," res[onse "+response.code());
                                if(response.code()==200){
                                    Log.d("claim_url"," respone "+response.body().getStatus());
                                    if(response.body().getStatus().equals("success")){
                                        commonClass.showSuccess(context,response.body().getData());

                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                if(context != null && !context.isFinishing() && !context.isDestroyed()) {

                                                    if (mDialog != null) {
                                                        if (mDialog.isShowing()) {
                                                            mDialog.dismiss();
                                                            Intent intent = new Intent(context, AdminNewApprovals.class);
                                                            intent.putExtra("position", 3);
                                                            context.startActivity(intent);
                                                        }
                                                    }

                                                }
                                            }
                                        }, 2500);

                                    }else{
                                        commonClass.showError(context,response.body().getData());
                                    }
                                }else{
                                    Log.d("insertMethod"," res "+response.code());
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
                            //progressDialog.dismiss();
                            loader1.setVisibility(View.GONE);
                            request_layout.setEnabled(true);
                            Log.d("claim_url"," on failure error "+t.getMessage());
                            commonClass.showError(context,t.getMessage());

                        }
                    });

                }
            }
        });
    }
    public void callDatePicker(Context context,EditText selete_date){
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
                        if((monthOfYear+1)<=9) {
                            str_month = "0" + String.valueOf(monthOfYear + 1);
                        }
                        onDutyDate=year + "-" + str_month + "-" + str_date;

                        selete_date.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
        long min = System.currentTimeMillis() -604800000L;
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(min);
        long mill = System.currentTimeMillis() ;
        datePickerDialog.getDatePicker().setMaxDate(mill);
        datePickerDialog.show();

    }


    public void callAlertDialog(Context context, CommonPojo commonPojo, String title,
                                String type, String reason, MKLoader loader,String btn_tit){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
        LinearLayout close = view.findViewById(R.id.cancel);
        LinearLayout approve = view.findViewById(R.id.decline);
        LinearLayout approve1 = view.findViewById(R.id.approve);
        approve1.setVisibility(View.GONE);
        approve.setVisibility(View.VISIBLE);
        TextView msg = view.findViewById(R.id.msg);
     /*   TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title.setText(btn_tit);*/

        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");
        msg.setText(title);
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
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callResetValues(loader,commonPojo.getEmp_no(),type,reason);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }

    private void callResetValues(MKLoader loader, String empNo, String type,String reason) {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(context,"token"),
                commonClass.getDeviceID(context)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.bloUnBlockReset(empNo,type,reason);
        Log.d("customers"," get headers invoice "+call.request().headers()+
                " url "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                Log.d("customers"," response code invoice "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")) {
                            commonClass.showSuccess(context,response.body().getData());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(context, AdminEmpActivityNew.class);
                                    context.startActivity(intent);
                                }
                            }, 1500);

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
                Log.d("customers"," fail "+t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commonPojoList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView emp_id,name,first_letter;
        LinearLayout linear;
        ImageView user_status,reset_user,onduty;
        public ProductViewHolder(View view) {
            super(view);
            linear = view.findViewById(R.id.linear);
            first_letter = view.findViewById(R.id.first_letter);
            emp_id = view.findViewById(R.id.emp_id);
            reset_user = view.findViewById(R.id.reset_user);
            name = view.findViewById(R.id.name);
            onduty = view.findViewById(R.id.onduty);
            user_status = view.findViewById(R.id.user_status);
        }
    }
}
