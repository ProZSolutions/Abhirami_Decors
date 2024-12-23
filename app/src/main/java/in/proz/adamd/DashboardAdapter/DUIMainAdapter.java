package in.proz.adamd.DashboardAdapter;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
import static androidx.core.content.ContextCompat.startActivity;

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

import in.proz.adamd.Adapter.ShiftAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.CommonJson.DashboardContent;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

public class DUIMainAdapter extends  RecyclerView.Adapter<DUIMainAdapter.ProductViewHolder>{
    List<DashboardContent> employeeCard;
    Activity context;
    CommonClass commonClass = new CommonClass();
    public DUIMainAdapter(Activity context, List<DashboardContent> employeeCard){
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
                if (modal.getTag().equals("summary")){
                    Intent adminsummary = new Intent(context, AdminNewDashboard.class);
                    context.startActivity(adminsummary);
                }else{
                    List<Integer> projectListID = new ArrayList<>();
                    List<String> selectedDeptID = new ArrayList<>();
                    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String sd = serverDateFormat.format(new Date());
                    String ud = usableDateFormat.format(new Date());
                    commonClass.putSharedPref(context,"dash","dash");
                    Intent intent = new Intent(context, AdminNewApprovals.class);
                    intent.putExtra("position","0");
                    intent.putExtra("branch","ALL");
                    intent.putExtra("dash","dash");
                    intent.putExtra("projectID",(Serializable) projectListID);
                    intent.putExtra("department",(Serializable) selectedDeptID) ;
                    context.startActivity(intent);
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
