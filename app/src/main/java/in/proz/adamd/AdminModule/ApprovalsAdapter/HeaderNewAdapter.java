package in.proz.adamd.AdminModule.ApprovalsAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.LeaveIDNewSQL;

public class HeaderNewAdapter  extends RecyclerView.Adapter<HeaderNewAdapter.ProductViewHolder>{
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


    public HeaderNewAdapter(Context context, List<String> titleList, List<Integer> imageList, int pos, String serverDate, String usableDate,
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
        View view=inflater.inflate(R.layout.new_ad_leave_header,null);
      /*  TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);*/

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.title.setText(titleList.get(position));
        if(position==pos){
              holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_primary));
        }else{
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.n_blue));
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
                CommonClass commonClass = new CommonClass();
                commonClass.putSharedPref(context,"dash","dash");
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

        LinearLayout linearLayout;
         TextView title;
        public ProductViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.linearLayout);
            title = view.findViewById(R.id.title);

        }
    }
}
