package in.proz.adamd.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.List;

import in.proz.adamd.ModalClass.AttendanceListSubModal;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ProductViewHolder>{
    Context context;
    List<AttendanceListSubModal> attendanceListSubModalList;
    ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;


    public AttendanceAdapter(Context context, List<AttendanceListSubModal> attendanceListSubModalList, MKLoader loader){
        this.attendanceListSubModalList=attendanceListSubModalList;
        this.context=context;
        this.loader = loader;
        commonClass = new CommonClass();
     }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.attendance_row_teim,null);
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
        AttendanceListSubModal modal = attendanceListSubModalList.get(position);
        String str_date="";
        if(!TextUtils.isEmpty(modal.getDate())){
            String[] split = modal.getDate().split("-");
            str_date =split[2]+"-"+split[1]+"-"+split[0];
        }
        if(modal.getWeek_end().equals("Yes")){
            holder.contentLayout.setVisibility(View.GONE);
            holder.leaveText.setText("WeekEnd : "+str_date);
            holder.leaveText.setVisibility(View.VISIBLE);
           // holder.mainrv.setBackground(null);
        }else if(modal.getHoliday().equals("1")){
            holder.contentLayout.setVisibility(View.GONE);
            holder.leaveText.setText("Holiday : "+str_date);
            holder.leaveText.setVisibility(View.VISIBLE);
           // holder.mainrv.setBackground(null);
        }
        else if(modal.getLeave().equals("1")){
            holder.contentLayout.setVisibility(View.GONE);
            holder.leaveText.setText("Leave : "+str_date);
            holder.leaveText.setVisibility(View.VISIBLE);
           // holder.mainrv.setBackground(null);
        }else{
            str_date="";

            holder.date.setText(modal.getWeek_date());
                 holder.contentLayout.setVisibility(View.VISIBLE);
                 holder.leaveText.setVisibility(View.GONE);

                     if (modal.getIntimeList().size() != 0) {
                         TodayAdapter adapter = new TodayAdapter(context, modal.getIntimeList(), modal.getOuttimeList(),
                                 modal.getWorkhoursList(), 0,modal.getPin_work_locationList(),
                                 modal.getPout_work_locationList(),loader);
                         holder.attendanceRV.setAdapter(adapter);
                     }


        }
    }






    @Override
    public int getItemCount() {
        return attendanceListSubModalList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView date,leaveText;
        RecyclerView attendanceRV;
        RelativeLayout mainrv;
        LinearLayout contentLayout;
        public ProductViewHolder(View view) {
            super(view);
            mainrv = view.findViewById(R.id.mainrv);
            attendanceRV = view.findViewById(R.id.attendanceRV);
            contentLayout = view.findViewById(R.id.contentLayout);
            leaveText = view.findViewById(R.id.leaveText);
            date = view.findViewById(R.id.date);
            GridLayoutManager layoutManager=new GridLayoutManager(context,1);
            attendanceRV.setLayoutManager(layoutManager);



        }
    }
}
