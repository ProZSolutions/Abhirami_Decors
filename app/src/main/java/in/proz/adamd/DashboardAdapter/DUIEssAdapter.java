package in.proz.adamd.DashboardAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.Calendar.CalendarActivity;
import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.CommonJson.DashboardContent;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class DUIEssAdapter extends  RecyclerView.Adapter<DUIEssAdapter.ProductViewHolder>{
    List<DashboardContent> employeeCard;
    String SendToID ="-1";

    Context context;
    CommonClass commonClass = new CommonClass();
    List<String> requestList = new ArrayList<>();
    public DUIEssAdapter(Activity context, List<DashboardContent> employeeCard){
        this.context=context;
        this.employeeCard=employeeCard;


        requestList.add("Permission");
        requestList.add("Loan");
        requestList.add("Claim");
     }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_dashboard_with_passinf,null);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DashboardContent modal = employeeCard.get(position);
        holder.image.setImageResource(modal.getImage());
        holder.title.setText(modal.getTitle());
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modal.getTag().contains("meeting")){
                    Intent meeting = new Intent(context, MeetingActivity.class);
                    context.startActivity(meeting);
                }else if(modal.getTag().contains("attendance")){
                    Intent inten1 = new Intent(context, AttendanceActivity.class);
                    context.startActivity(inten1);
                }else if(modal.getTag().contains("calendar")){
                    Intent calendar = new Intent(context, CalendarActivity.class);
                    context.startActivity(calendar);
                }else if(modal.getTag().contains("request")){
                    callAlert();
                }
            }
        });
    }

    private void callAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set title
        builder.setTitle("Select Request");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = requestList.toArray(new String[0]);
        //   String[] stringArray = empNameList.toArray(new String[0]);

        boolean[] selectedNames = new boolean[stringArray.length];



        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendToID = String.valueOf(which);
                 //dialog.dismiss();
                int po = Integer.parseInt(SendToID);
                dialog.dismiss();
                SendToID="-1";
                Log.d("selectedPOS"," it "+po);
                if(po>=0) {
                    Log.d("selectedPOS", " call if");
                    if (po == 0) {
                        //ticket
                        Intent intent5 = new Intent(context, LeaveActivity.class);
                        intent5.putExtra("position", 4);
                       context.startActivity(intent5);

                    }else  if (po == 1) {
                        Intent intent2 = new Intent(context, LoanActivity.class);
                        intent2.putExtra("position", 0);
                        context.startActivity(intent2);
                    } else if (po == 2) {
                        Intent intent4 = new Intent(context, ClaimActivity.class);
                        intent4.putExtra("claim_type", "claim");
                        intent4.putExtra("position", 1);
                        context.startActivity(intent4);
                    }
                }
            }
        });



        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.n_org));
    }

    @Override
    public int getItemCount() {
        return employeeCard.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        LinearLayout linear;

        public ProductViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            image = view.findViewById(R.id.image);
            linear = view.findViewById(R.id.linear);
        }
    }
}

