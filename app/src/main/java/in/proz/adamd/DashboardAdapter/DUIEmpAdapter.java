package in.proz.adamd.DashboardAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class DUIEmpAdapter extends  RecyclerView.Adapter<DUIEmpAdapter.ProductViewHolder> {
    List<DashboardContent> employeeCard;
    Activity context;
    CommonClass commonClass = new CommonClass();
    public DUIEmpAdapter(Activity context, List<DashboardContent> employeeCard){
        this.context=context;
        this.employeeCard=employeeCard;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_dashboard_ui,null);

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
                switch (modal.getTag()){
                    case "attendance":
                        Intent inten1 = new Intent(context, AttendanceActivity.class);
                        context.startActivity(inten1);
                        break;
                    case "leave":
                        Intent inten = new Intent(context, LeaveActivity.class);
                        context.startActivity(inten);
                        break;
                    case "dsr":
                        Intent dsr = new Intent(context, DSRActivity.class);
                        context.startActivity(dsr);
                        break;
                    case "overtime":
                        Intent intentwsr1 = new Intent(context, OverTime.class);
                        context.startActivity(intentwsr1);
                        break;
                    case "meeting":
                        Intent meeting1 = new Intent(context, MeetingActivity.class);
                        context.startActivity(meeting1);
                        break;
                    case "loan":
                        Intent loan = new Intent(context, LoanActivity.class);
                        context.startActivity(loan);
                        break;
                    case "reimbursement":
                        callAlert();
                        break;
                    case "calendar":
                        Intent calendar1 = new Intent(context, CalendarActivity.class);
                        context.startActivity(calendar1);
                        break;

                }

            }
        });
    }

    private void callAlert() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_claim_radio);
        dialog.setTitle("This is my custom dialog box");
        dialog.setCancelable(true);
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
                dialog.dismiss();
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
                    context.startActivity(intent123);
                } else {
                    commonClass.showWarning(context, "Please Select any option");
                }
            }
        });


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
