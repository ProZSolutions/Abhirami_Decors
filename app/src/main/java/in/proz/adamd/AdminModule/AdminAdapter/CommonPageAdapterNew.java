package in.proz.adamd.AdminModule.AdminAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.tuyenmonkey.mkloader.MKLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

public class CommonPageAdapterNew extends RecyclerView.Adapter<CommonPageAdapterNew.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Context context;
    int commonPos;
    RecyclerView recyclerView;
    // ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;

    public  CommonPageAdapterNew(Activity context, List<CommonPojo> commonPojoList, int commonPos,
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
        View view=inflater.inflate(R.layout.new_common_page_item_row,null);
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
        CommonPojo commonPojo = commonPojoList.get(position);
        holder.emp_id.setText(commonPojo.getEmp_no());
        if(!TextUtils.isEmpty(commonPojo.getPunch_in())){
            holder.time.setText(getTime(commonPojo.getPunch_in()));
            holder.time.setVisibility(View.VISIBLE);
            if(getBeforeAfter(commonPojo.getPunch_in()).equals("1")){
                holder.time.setTextColor(context.getColor(R.color.n_green));
                holder.time.setCompoundDrawableTintList(context.getColorStateList(R.color.n_green));
            }else{
                holder.time.setTextColor(context.getColor(R.color.war1));
                holder.time.setCompoundDrawableTintList(context.getColorStateList(R.color.war1));
            }
        }else{
            holder.time.setVisibility(View.GONE);
        }
        holder.name.setText(commonPojo.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployeeDetails(context,commonPojo);
            }
        });
        String value =String.valueOf(commonPojo.getName().charAt(0));
        holder.first_letter.setText(value);
        if(!TextUtils.isEmpty(commonPojo.getContact())){
            if(commonPojo.getContact().equals("0")){
                holder.mobile.setText("-");
            }else{
                holder.mobile.setText(commonPojo.getContact());
            }
        }else{
            holder.mobile.setText("-");
        }
        holder.mobile.setOnClickListener(new View.OnClickListener() {
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
    }

    public void EmployeeDetails(Context context, CommonPojo commonPojo){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.employee_dialog,null);
        ImageView close = view.findViewById(R.id.close);
        RelativeLayout reason_layout = view.findViewById(R.id.reason_layout);
        TextView emp_id = (TextView) view.findViewById(R.id.emp_no);
        emp_id.setVisibility(View.VISIBLE);
        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.id_val);
        TextView email = view.findViewById(R.id.email);
        TextView status = view.findViewById(R.id.status);
        TextView dept = view.findViewById(R.id.dept);
        TextView phone = view.findViewById(R.id.phone);
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
        }else{
            status.setVisibility(View.GONE);
        }

        id.setText("Emp ID : "+commonPojo.getEmp_no());
        name.setText(commonPojo.getName());
        emp_id.setVisibility(View.GONE);
       /* if (!TextUtils.isEmpty(commonPojo.getComp_email())){
            emp_id.setText(commonPojo.getComp_email());
        }else{
            emp_id.setText("-");
        }*/
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
    private String getBeforeAfter(String string ){
        String type="1";
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // Parse the input date string
            Date inputDate = sdf.parse(string);

            // Create a Calendar instance for 10:30 AM on the same date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date tenThirtyAM = calendar.getTime();

            // Check if the input time is before or after 10:30 AM
            if (inputDate.before(tenThirtyAM)) {
                type="1";
             } else {
                type ="2";
             }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return type;
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
    public int getItemCount() {
        return commonPojoList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView emp_id,time,name,mobile,first_letter;
        RelativeLayout relativeLayout;
        public ProductViewHolder(View view) {
            super(view);
            emp_id = view.findViewById(R.id.emp_id);
            time = view.findViewById(R.id.time);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            name = view.findViewById(R.id.name);
            mobile = view.findViewById(R.id.mobile);
            first_letter = view.findViewById(R.id.first_letter);
        }
    }

}
