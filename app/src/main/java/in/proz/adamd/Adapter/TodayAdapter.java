package in.proz.adamd.Adapter;

import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.List;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class TodayAdapter  extends RecyclerView.Adapter<TodayAdapter.ProductViewHolder>{
    Context context;
    List<String> inTimeList,outTimeList,workHrsList,punchInList,punchOutList;
 //   ProgressDialog progressDialog;
    CommonClass commonClass;
    int type;
    MKLoader loader;
    

    public TodayAdapter(Context context, List<String> inTimeList, List<String> outTimeList,
                        List<String> workHrsList,int type,List<String> punchInList,List<String> punchOutList,MKLoader loader){
        this.inTimeList=inTimeList;
        this.outTimeList=outTimeList;
        this.workHrsList=workHrsList;
        this.type = type;
        this.context=context;
        commonClass = new CommonClass();
        this.punchInList =punchInList;
        this.punchOutList=punchOutList;
/*        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");*/
        this.loader = loader;
        commonClass = new CommonClass();


    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.attendance_today,null);
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
public String dateSplit(String date){
    String str_date="";
    if(!TextUtils.isEmpty(date)){
        String[] split = date.split(" ");
        Log.d("spl_ln"," ln "+split.length);
      /*  for (int i=0;i<split.length;i++){
            Log.d("spl_ln"," item "+split[i]);
        }
        String[] spl=split[0].split("-");
        for (int i=0;i<spl.length;i++){
            Log.d("spl_ln"," item child "+spl[i]);
        }*/
        String[] spl=split[0].split("-");
        String time= getTime(split[1]);
        str_date =spl[2]+"-"+spl[1]+"-"+spl[0]+" "+time;
    }
    return str_date;
}
    private String getTime(String string) {
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
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if(position!=0){
            holder.checkouttag.setVisibility(View.GONE);
            holder.checkintag.setVisibility(View.GONE);
            holder.workingtag.setVisibility(View.GONE);
        }
        if(inTimeList.get(position)!=null) {
            if (TextUtils.isEmpty(inTimeList.get(position))) {
                holder.intime.setText("\n-");
            } else {
                holder.intime.setText(dateSplit(inTimeList.get(position)));
            }
        }else{
            holder.intime.setText("\n-");
        }
        if(outTimeList.get(position)!=null) {
            if (TextUtils.isEmpty(outTimeList.get(position))) {
                holder.out_time.setText("\n-");
            } else {
                holder.out_time.setText(dateSplit(outTimeList.get(position)));
            }
        }else{
            holder.out_time.setText("\n-");
        }
        if(workHrsList.get(position)!=null) {
            if (TextUtils.isEmpty(workHrsList.get(position))) {
                holder.working_hr.setText("\n-");
            } else {
                holder.working_hr.setText(workHrsList.get(position));
            }
        }else{
            holder.working_hr.setText("\n-");
        }

        if(type==0){
            holder.checkhricon.setVisibility(View.VISIBLE);
            holder.checkinicon.setVisibility(View.VISIBLE);
            holder.checkouticon.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(punchInList.get(position))){
                if(punchInList.get(position).equals("home")){
                    holder.checkinicon.setImageDrawable(context.getDrawable(R.drawable.att_home_icon));
                }else if(punchInList.get(position).equals("client")){
                    holder.checkinicon.setImageDrawable(context.getDrawable(R.drawable.att_client1));
                }else{
                    holder.checkinicon.setImageDrawable(context.getDrawable(R.drawable.att_office_icon1));
                }
            }
            if(!TextUtils.isEmpty(punchOutList.get(position))){
                if(punchOutList.get(position).equals("home")){
                    holder.checkouticon.setImageDrawable(context.getDrawable(R.drawable.att_home_icon));
                }else if(punchOutList.get(position).equals("client")){
                    holder.checkouticon.setImageDrawable(context.getDrawable(R.drawable.att_client1));
                }else{
                    holder.checkouticon.setImageDrawable(context.getDrawable(R.drawable.att_office_icon1));
                }
            }
        }


    }






    @Override
    public int getItemCount() {
        return inTimeList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView intime,out_time,working_hr,checkintag,checkouttag,workingtag;
        ImageView icon1,icon2,icon3,checkinicon,checkouticon,checkhricon;

        public ProductViewHolder(View view) {
            super(view);
            intime = view.findViewById(R.id.intime);
            checkintag = view.findViewById(R.id.checkintag);
            checkouttag = view.findViewById(R.id.checkouttag);
            workingtag = view.findViewById(R.id.workingtag);
            out_time=view.findViewById(R.id.out_time);
            working_hr = view.findViewById(R.id.working_hr);
            icon1 = view.findViewById(R.id.icon1);
            icon2 = view.findViewById(R.id.icon2);
            icon3 = view.findViewById(R.id.icon3);
            checkinicon = view.findViewById(R.id.checkinicon);
            checkouticon = view.findViewById(R.id.checkouticon);
            checkhricon = view.findViewById(R.id.checkhricon);
            if (type==0){
                icon3.setVisibility(View.GONE);
                icon2.setVisibility(View.GONE);
                icon1.setVisibility(View.GONE);
                checkinicon.setVisibility(View.GONE);
                checkouticon.setVisibility(View.GONE);
                checkhricon.setVisibility(View.GONE);
            }



        }
    }
}
