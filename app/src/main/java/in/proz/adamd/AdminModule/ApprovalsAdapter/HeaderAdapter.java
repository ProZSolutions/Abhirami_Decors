package in.proz.adamd.AdminModule.ApprovalsAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.SQLiteDB.ClaimSQL;
import in.proz.adamd.AdminModule.SQLiteDB.LoanSQL;
import in.proz.adamd.AdminModule.SQLiteDB.OnDutySQL;
import in.proz.adamd.AdminModule.SQLiteDB.OverTimeSQL;
import in.proz.adamd.AdminModule.SQLiteDB.WeekOffSQL;
import in.proz.adamd.R;
import in.proz.adamd.SQLiteDB.LeaveIDNewSQL;

public class HeaderAdapter  extends RecyclerView.Adapter<HeaderAdapter.ProductViewHolder>{
    Context context;
    List<String> titleList;
    RecyclerView recyclerView;
    List<Integer> imageList;
    String serverDate, usableDate,branchID;
    List<String> departmentID;
    ArrayList<Integer> projectListID;
    int pos;
    LeaveIDNewSQL leaveIDNewSQL;
    OverTimeSQL overTimeSQL;
    OnDutySQL onDutySQL;
    WeekOffSQL weekOffSQL;
    LoanSQL loanSQL;
    ClaimSQL claimSQL;


    public HeaderAdapter(Context context, List<String> titleList, List<Integer> imageList, int pos, String serverDate, String usableDate,
                         String branchID, List<String> departmentID, ArrayList<Integer> projectListID,RecyclerView recyclerView){
        this.pos=pos;
        this.recyclerView = recyclerView;
        this.projectListID = projectListID;
        this.titleList =titleList;
        this.imageList = imageList;
        this.context = context;
        this.serverDate = serverDate;
        this.usableDate = usableDate;
        this.branchID = branchID;
        this.departmentID =departmentID;
        leaveIDNewSQL = new LeaveIDNewSQL(context);
        leaveIDNewSQL.getWritableDatabase();
        overTimeSQL = new OverTimeSQL(context);
        overTimeSQL.getWritableDatabase();
        onDutySQL = new OnDutySQL(context);
        onDutySQL.getWritableDatabase();
        weekOffSQL = new WeekOffSQL(context);
        weekOffSQL.getWritableDatabase();
        claimSQL = new ClaimSQL(context);
        claimSQL.getWritableDatabase();
        loanSQL = new LoanSQL(context);
        loanSQL.getWritableDatabase();
        recyclerView.smoothScrollToPosition(pos); // or recyclerView.scrollToPosition(position);

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.ad_leave_header_items,null);
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
        holder.head_icon.setImageResource(imageList.get(position));
        holder.title.setText(titleList.get(position));
        if(position==pos){
            holder.circle_background.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
        }else{
            holder.circle_background.setBackgroundTintList(context.getResources().getColorStateList(R.color.circle_bg));
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveIDNewSQL.DropTable();
                overTimeSQL.DropTable();
                onDutySQL.DropTable();
                weekOffSQL.DropTable();
                loanSQL.DropTable();
                claimSQL.DropTable();
                Intent intent = new Intent(context, AdminNewApprovals.class);
                intent.putExtra("position",position);
                intent.putExtra("branch",branchID);
                intent.putExtra("serverDate",serverDate);
                intent.putExtra("projectID",(Serializable) projectListID);
                intent.putExtra("usableDate",usableDate);
                intent.putExtra("department",(Serializable) departmentID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView head_icon;
        LinearLayout linearLayout;
        RelativeLayout circle_background;
        TextView title;
        public ProductViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.linearLayout);
            head_icon = view.findViewById(R.id.head_icon);
            circle_background = view.findViewById(R.id.circle_background);
            title = view.findViewById(R.id.title);

        }
    }
}
