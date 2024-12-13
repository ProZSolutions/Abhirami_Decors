package in.proz.adamd.Adapter;

import android.app.Activity;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.DocumentView;
import in.proz.adamd.ModalClass.DSRSubModal;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class DSRAdapter extends RecyclerView.Adapter<DSRAdapter.ProductViewHolder>{
    List<DSRSubModal> dsrSubModalList;
    Activity context;
    SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy");
    String yesterDay;

    CommonClass commonClass = new CommonClass();
    LinearLayout frame_layout;
    public  DSRAdapter(Activity context, List<DSRSubModal> dsrSubModalList, LinearLayout frame_layout){
        this.context=context;
        this.dsrSubModalList = dsrSubModalList;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date = cal.getTime();
        yesterDay=displayFormat.format(date);
        this.frame_layout = frame_layout;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.dsr_row_item,null);
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
        DSRSubModal modal = dsrSubModalList.get(position);
        holder.date.setText(modal.getDate());

        String date_for =displayFormat.format(new Date());
        if(modal.getDate().equals(date_for)){
            frame_layout.setVisibility(View.GONE);
        }

        holder.planned_work.setText(modal.getPlanned_work());
      /*  if(modal.getProject_name().size()!=0){
            String listString = String.join(",\n ", modal.getProject_name());*/

        //}
         if(!TextUtils.isEmpty(modal.getDate())){

            if(modal.getIs_update().equals("0") || modal.getIs_update().equals("1")){

            String date = displayFormat.format(new Date());
           // holder.edit_dsr.setVisibility(View.VISIBLE);
             if(date.equals(modal.getDate())){
                holder.edit_dsr.setVisibility(View.VISIBLE);
            }else {
                 if (yesterDay.equals(modal.getDate())) {
                     SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
                     SimpleDateFormat sdf1 = new SimpleDateFormat("mm", Locale.getDefault());
                     String currentDateandTime = sdf.format(new Date());
                     String currentDateMin = sdf1.format(new Date());
                     int value = Integer.parseInt(currentDateandTime);
                     int value1 = Integer.parseInt(currentDateMin);
                     Log.d("getValue", " ca " + value + " currentDateMin " + value1);
                     if ((value >= 0 && value <= 9)) {
                         if ((value1 >= 0 && value1 <= 59)) {
                             holder.edit_dsr.setVisibility(View.VISIBLE);
                         } else {
                             holder.edit_dsr.setVisibility(View.GONE);
                         }
                     } else {
                         holder.edit_dsr.setVisibility(View.GONE);
                     }
                 } else {
                     holder.edit_dsr.setVisibility(View.GONE);
                 }
             }
            }else {
                holder.edit_dsr.setVisibility(View.GONE);
            }
        }


        if(!TextUtils.isEmpty(modal.getCompleted_activity())){
            holder.completed_work.setText(modal.getCompleted_activity());
        }
        if(modal.getGit_url()!=null){
            if(modal.getGit_url().size()!=0){
                String listString = String.join(",", modal.getGit_url());
                holder.git_url.setText(listString);

            }
        }
        if(!TextUtils.isEmpty(modal.getImage_url())){
            if(modal.getImage_url()!=null) {
                holder.image.setVisibility(View.VISIBLE);
            }else{
                holder.image.setVisibility(View.GONE);
            }
        }else{
            holder.image.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(modal.getDocument_url())){
            if(modal.getDocument_url()!=null) {
                holder.document.setVisibility(View.VISIBLE);
            }else{
                holder.document.setVisibility(View.GONE);
            }
        }else{
            holder.document.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getImage_url(),0);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",modal.getImage_url());
                intent.putExtra("type","0");
                intent.putExtra("intent_type","dsr");
                context.startActivity(intent);
            }
        });
        holder.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //commonClass.showDocumentImage(context,commonPojo.getDocument_url(),1);
                Intent intent = new Intent(context, DocumentView.class);
                intent.putExtra("url",modal.getDocument_url());
                intent.putExtra("type","1");
                intent.putExtra("intent_type","dsr");
                context.startActivity(intent);
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
        holder.edit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, DSRActivity.class);
                in.putExtra("edit_content", modal);
                context.startActivity(in);
                  /*  AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Would you like to update DSR?");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.create();
                    alert.show();*/

            }
        });
     }


    @Override
    public int getItemCount() {
        return dsrSubModalList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
         TextView date,planned_work,completed_work,git_url,other_name;
         LinearLayout edit_dsr,more_dsr,more_layout,other_layout;
         ImageView see_more,image,document;
        public ProductViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            image = view.findViewById(R.id.image);
            document = view.findViewById(R.id.document);
            document.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            other_name = view.findViewById(R.id.other_name);
            other_layout = view.findViewById(R.id.other_layout);
            see_more = view.findViewById(R.id.see_more);
            more_layout = view.findViewById(R.id.more_layout);
             planned_work = view.findViewById(R.id.planned_work);
            edit_dsr = view.findViewById(R.id.edit_dsr);
            more_dsr = view.findViewById(R.id.more_dsr);
            completed_work = view.findViewById(R.id.completed_work);
            git_url = view.findViewById(R.id.git_url);




        }
    }
}
