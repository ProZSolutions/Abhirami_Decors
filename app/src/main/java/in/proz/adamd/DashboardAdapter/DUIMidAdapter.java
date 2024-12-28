package in.proz.adamd.DashboardAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminEmployee.AdminEmpActivityNew;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.AdminModule.AdminWSRLayout;
import in.proz.adamd.CommonJson.DashboardContent;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class DUIMidAdapter extends  RecyclerView.Adapter<DUIMidAdapter.ProductViewHolder>{
    List<DashboardContent> employeeCard;
    Activity context;
    CommonClass commonClass = new CommonClass();
    public DUIMidAdapter(Activity context, List<DashboardContent> employeeCard){
        this.context=context;
        this.employeeCard=employeeCard;
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
                if (modal.getTag().contains("employee")){
                    Intent adminemp = new Intent(context, AdminEmpActivityNew.class);
                    context.startActivity(adminemp);
                }else if(modal.getTag().contains("dsr")){
                    Intent intentwsr = new Intent(context, AdminWSRLayout.class);
                    context.startActivity(intentwsr);
                }else if(modal.getTag().contains("onduty")){
                    Intent intenton = new Intent(context, OnDuty.class);
                    intenton.putExtra("position",3);
                    context.startActivity(intenton);
                }else{
                    Intent intentot = new Intent(context, OverTime.class);
                    intentot.putExtra("position",4);
                    context.startActivity(intentot);
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

