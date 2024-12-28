package in.proz.adamd.AdminModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.tuyenmonkey.mkloader.MKLoader;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.proz.adamd.AdminFilterAdapter.BranchAdapter;
import in.proz.adamd.AdminFilterAdapter.DepartmentAdapter;
import in.proz.adamd.AdminFilterAdapter.StatusAdapter;
import in.proz.adamd.AdminModule.AdminApprovalModal.LeaveAppModal;
import in.proz.adamd.AdminModule.AdminApprovalModal.LeaveTopModal;
import in.proz.adamd.AdminModule.AdminCommon.CommonPageActivityNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.ClaimApprovalAdapterNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.LeaveApprovalAdapterNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.LoanApprovalAdapterNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.OnDutyAdapterNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.OvertimeAdapterNew;
import in.proz.adamd.AdminModule.AdminNewAdapter.WeekOffApprovalAdapterNew;
import in.proz.adamd.AdminModule.ApprovalsAdapter.HeaderNewAdapter;
import in.proz.adamd.AdminModule.ApprovalsAdapter.LoanApprovalAdapter;
import in.proz.adamd.AdminModule.SQLiteDB.BranchDB;
import in.proz.adamd.AdminModule.SQLiteDB.DepartmentDB;
import in.proz.adamd.BackgroundColorDecorator;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.KeystoneModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNewApprovals extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView,horizontal_rv;
    HeaderNewAdapter headeradapter;
    int position;
    int main_pending_count=0;
    List<KeystoneModal> branchMainList= new ArrayList<>();
    List<KeystoneModal> departmentMainList= new ArrayList<>();
    List<Integer> ImageList= new ArrayList<>();
    Integer[] activeList={R.drawable.leave_icon_active,R.drawable.loan_icon_active,
            R.drawable.claim_icon_active,R.drawable.onduty_icon_active,R.drawable.overtime_icon_active};
    Integer[] inactiveList={ R.drawable.leave_icon,R.drawable.loan_icon,
            R.drawable.claim_icon,R.drawable.onduty_icon,R.drawable.overtime_icon};
    Boolean isScrolling = false;
    String dash,dash1;
    int status_value=0;
    List<String> statusList = new ArrayList<>();
    int currentItems, totalItems, scrollOutItems;
    List<String> departmentNameList = new ArrayList<>();
    GridLayoutManager manager;

    HashMap<Integer,Object> dateHashmap=new HashMap<>();
    CommonClass commonClass = new CommonClass();
    ArrayList<Integer> projectListID = new ArrayList<>();
    ArrayList<Integer> projectListID1 = new ArrayList<>();

    List<String> selectedDeptID = new ArrayList<>();
    MaterialCalendarView custom_calendar;
    int pageNo=1;
    MKLoader loader;
    List<String> branchNameList = new ArrayList<>();
    boolean[] selectedProjects= new boolean[50];
    BranchDB branchDB;
    DepartmentDB departmentDB;

    String branchID,departmentID,serverDate,usableDate,selectedDept;
    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    String strBranchID,strBranchName,strDeptID,strDeptName,strStatusName;
    ImageView back_arrow;
    TextView title;
    List<LeaveAppModal> leaveAppModals = new ArrayList<>();
    private static final int SCROLL_AMOUNT = 250;

    //search list
 
    ImageView img1;
   // LeaveApprovalAdapter adapter;
    LeaveApprovalAdapterNew adapter;
    OvertimeAdapterNew overtimeAdapter;
    OnDutyAdapterNew onDutyAdapter;

    LoanApprovalAdapter approvalAdapter;
    WeekOffApprovalAdapterNew weekOffApprovalAdapter;
  //  ClaimApprovalAdapter claimApprovalAdapter;
    ClaimApprovalAdapterNew claimApprovalAdapter;
    LoanApprovalAdapterNew loanApprovalAdapter;

    TextView no_data;
    LinearLayout approve_layout;
    TextView approve,decline;
    CheckBox check_all;
    int checkpos=-1;
    //search list

    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;
    TextView dateText,pendingcount,texttag,filter_text;
    RelativeLayout tagLayout;
    int pending_request_count=0;
    TextView header_title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_admin_approvals);
       // updateBottomNavigation();
        dash = commonClass.getSharedPref(getApplicationContext(),"dash");
        Log.d("getEmployeeList"," dash "+dash);
        header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));



        Bundle b= getIntent().getExtras();
        if(b!=null){
            position = b.getInt("position");
            Log.d("posonduty"," position as "+position);
             dash1 = b.getString("dash1");
            branchID = b.getString("branch");
            serverDate = b.getString("serverDate");
            usableDate = b.getString("usableDate");
            selectedDeptID = (List<String>) b.getSerializable("department");
            projectListID1 = (ArrayList<Integer>)  b.getSerializable("projectID");
            Log.d("getDates"," usb "+usableDate+" ser "+serverDate);


            if(selectedDeptID==null){
                selectedDeptID = new ArrayList<>();
                selectedDeptID.add("ALL");
            }
            if(projectListID1==null){
                projectListID1 = new ArrayList<>();
                projectListID1.add(0);
            }
            if(TextUtils.isEmpty(branchID)){
                branchID = "ALL";
            }



//            if(TextUtils.isEmpty(serverDate)){
//                serverDate = serverDateFormat.format(new Date());
//                usableDate = usableDateFormat.format(new Date());
//            }
        }
        initView();
    }

    private void initView() {
        filter_text = findViewById(R.id.filter_text);
        pendingcount = findViewById(R.id.pendingcount);
        tagLayout = findViewById(R.id.tagLayout);
        texttag = findViewById(R.id.texttag);
        dateText = findViewById(R.id.dateText);
        if(TextUtils.isEmpty(dash)){
            //dateText.setText("Date : "+usableDate);
        }

        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
         nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
         nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        check_all = findViewById(R.id.check_all);


        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkpos=0;
                }else{
                    checkpos=-1;
                }
                updateNotificationChanged();
            }
        });
        approve_layout = findViewById(R.id.approve_layout);
        approve = findViewById(R.id.approve);
        decline = findViewById(R.id.decline);
        no_data = findViewById(R.id.no_data);
        loader = findViewById(R.id.loader);
        departmentDB = new DepartmentDB(AdminNewApprovals.this);
        departmentDB.getWritableDatabase();
        branchDB = new BranchDB(AdminNewApprovals.this);
        branchDB.getWritableDatabase();
         img1=findViewById(R.id.img1);
        img1.setOnClickListener(this);


        title = findViewById(R.id.title);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        horizontal_rv = findViewById(R.id.horizontal_rv);
        recyclerView = findViewById(R.id.recyclerView);
        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);


        switch (position){
            case 0:
                //leave
                texttag.setText("My Permission");
                adapter = new LeaveApprovalAdapterNew(AdminNewApprovals.this,
                        leaveAppModals,-1,recyclerView,loader,approve_layout,decline,approve
                        ,position,branchID,serverDate,usableDate,selectedDeptID,projectListID1,
                        check_all,checkpos,tagLayout,dash);
                recyclerView.setAdapter(adapter);


                break;
            case 1:
                //loan
                texttag.setText("My Loan");
                loanApprovalAdapter = new LoanApprovalAdapterNew(AdminNewApprovals.this,
                        leaveAppModals,-1,recyclerView,loader,approve_layout,decline,approve
                        ,position,branchID,serverDate,usableDate,selectedDeptID,projectListID1,check_all
                        ,checkpos,tagLayout,dash);
                recyclerView.setAdapter(loanApprovalAdapter);


                break;
            case 2:
                texttag.setText("My Claim");
                claimApprovalAdapter = new ClaimApprovalAdapterNew(AdminNewApprovals.this,
                        leaveAppModals,-1,recyclerView,loader,approve_layout,decline,approve
                        ,position,branchID,serverDate,usableDate,selectedDeptID,projectListID1
                        ,check_all,checkpos,tagLayout,dash);
                recyclerView.setAdapter(claimApprovalAdapter);


                //asset
                break;
            case 3:
                //ticket
                texttag.setText("My OnDuty");
                onDutyAdapter = new OnDutyAdapterNew(AdminNewApprovals.this,
                        leaveAppModals,-1,recyclerView,loader,approve_layout,decline,approve
                        ,position,branchID,serverDate,usableDate,selectedDeptID,projectListID1
                        ,check_all,checkpos,tagLayout,dash);
                recyclerView.setAdapter(onDutyAdapter);



                break;
            case 4:
                //claim

                tagLayout.setVisibility(View.GONE);
                texttag.setText("My Overtime");
                overtimeAdapter = new OvertimeAdapterNew(AdminNewApprovals.this,
                        leaveAppModals,-1,recyclerView,loader,approve_layout,decline,approve
                        ,position,branchID,serverDate,usableDate,selectedDeptID,projectListID1,check_all,
                        checkpos,dash);
                recyclerView.setAdapter(overtimeAdapter);
                break;


        }



        // horizontal RV
        updateTitle();
        updateAdapter();
        updateHorizontalRV(horizontal_rv,-1,"-1");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
               /* LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // Notify adapter to update UI
                adapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);

                */
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();

                // Notify adapter to update UI
                switch (position){
                    case 0:
                        //leave
                        adapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);

                        break;
                    case 1:
                        //loan
                        loanApprovalAdapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);


                        break;
                    case 2:
                        //asset
                        claimApprovalAdapter.notifyDataSetChanged();


                        break;
                    case 3:
                        onDutyAdapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);


                        //ticket
                        break;
                    case 4:
                        //claim
                        overtimeAdapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);


                        //claimApprovalAdapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition + 1);
                        break;

                }
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    pageNo+=1;
                    getEmployeeList();
                }
            }
        });
        if(commonClass.isOnline(AdminNewApprovals.this)){
            getEmployeeList();
        }else{
            commonClass.showInternetWarning(AdminNewApprovals.this);
        }



    }
/*
    private void updateBottomNavigation() {
        MeowBottomNavigation bottomNavigation = findViewById(R.id.btm_nav);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.home__con));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.notepad_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.location_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.about_icon));

        //  bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.plogo));
        //bottomNavigation.setCount(ID_NOTIFICATION, "115");
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // Toast.makeText(DashboardActivity.this, "clicked item : " + model.getId(), Toast.LENGTH_SHORT).show();

                return null;
            }
        });
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                //switch and intent transition here

                //  Toast.makeText(DashboardActivity.this, "showing item : " + model.getId(), Toast.LENGTH_SHORT).show();
                switch (model.getId()){
                    case 1:
                        Intent intent1 = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intentabout);
                        break;
                    case 3:
                        Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                        startActivity(notes);
                        break;
                    case 4:
                        Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });
    }
*/
    private void updateAdapter() {
        selectedDept = "ALL";
        Log.d("SortingAlertDialog"," branch id "+branchID);
        if(TextUtils.isEmpty(branchID) && TextUtils.isEmpty(departmentID)){
            strStatusName="ALL";
            branchID="ALL";
            selectedDeptID.add("ALL");
            departmentNameList.add("ALL");
        }else {
            projectListID = projectListID1;
            if (!TextUtils.isEmpty(branchID)) {
                departmentNameList = departmentDB.SelectDepartmentList(branchID);
            }


/*
        if(branchDB.SelectDistrictList().size()!=0){
            departmentNameList = branchDB.SelectDistrictList();
        }*/
            if (departmentNameList.size() != 0) {
                // selectedProjects = new boolean[departmentNameList.size()];
                Log.d("SortingAlertDialog", " department " + departmentNameList.size() + " pro size " + selectedProjects.length
                        + " pro id " + projectListID.size());
                for (int i = 0; i < selectedProjects.length; i++) {
                    selectedProjects[i] = false;
                }
                if (projectListID.size() != 0) {

                    for (int i = 0; i < projectListID.size(); i++) {
                        selectedProjects[projectListID.get(i)] = true;
                    }
                }
            }
        }

    }

    public void updateTitle(){
        if(position==0){
            title.setText("Permission Approval List");

        }else if(position==1){
            title.setText("Loan Approval List");

        }else if(position==2){
            title.setText("Claim Approval List");

        }else if(position==3){
            title.setText("OnDuty Approval List");

        }else if(position==4){
            title.setText("Overtime Approval List");

        }

        statusList.add("ALL");
        statusList.add("Pending");
        statusList.add("Approved");
        statusList.add("Declined");
        statusList.add("Cancelled");
        statusList.add("Auto Cancelled");


    }


    private void updateHorizontalRV(RecyclerView horizontalRv,int p_value,String content) {

        Log.d("getEmployeeList"," checknbo 1 "+p_value);
        if(!content.equals("0")){
            Log.d("getEmployeeList"," count "+p_value);
            check_all.setVisibility(View.VISIBLE);
        }
        horizontalRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> titleList =new ArrayList<>();
        titleList.add("Permission");

        titleList.add("Loan");
        titleList.add("Claim");
        titleList.add("OnDuty");
        titleList.add("OverTime");

        if(p_value>=0){
            switch (p_value){
                case 0:
                    titleList.set(0,"Permission  - "+content);
                    break;
                case 1:
                    titleList.set(1,"Loan  - "+content);
                    break;
                case 2:
                    titleList.set(2,"Claim  - "+content);
                    break;
                case 3:
                    titleList.set(3,"OnDuty  - "+content);
                    break;
                case 4:
                    titleList.set(4,"OverTime  - "+content);
                    break;

            }
        }


        switch (position){
            case 0:
                updateIconList(0);
                break;
            case 1:
                updateIconList(1);
                break;
            case 2:
                updateIconList(2);
                break;
            case 3:
                updateIconList(3);
                break;
            case 4:
                updateIconList(4);
                break;

        }


        headeradapter = new HeaderNewAdapter(AdminNewApprovals.this,titleList,ImageList,position,serverDate,usableDate,
                branchID,selectedDeptID,projectListID,horizontal_rv);
        horizontal_rv.setAdapter(headeradapter);

    }
    public void updateIconList(int pos){
        for(int i=0;i<5;i++){
            if(pos==i){
                ImageList.add(i,activeList[i]);
            }else{
                ImageList.add(i,inactiveList[i]);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackPress();
    }

    public void callBackPress(){
        if(!TextUtils.isEmpty(dash)){
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(),AdminNewDashboard.class);
            startActivity(intent);
        }

    }
    @Override
    public void onClick(View v) {
        int id=  v.getId();
        switch (id){
           /* case R.id.search_layout:
                SortingAlertDialog();
                break;*/
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
            /*case R.id.nabout_layout:
                Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                intentabout.putExtra("admin","admin");
                startActivity(intentabout);
                break;*/
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                notes.putExtra("admin","admin");
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                intent.putExtra("admin","admin");
                startActivity(intent);
                break;
            case R.id.img1:
                SortingAlertDialog();
                break;
            case R.id.back_arrow:
                callBackPress();
                break;
          /*  case R.id.prev:
                horizontal_rv.smoothScrollBy(-SCROLL_AMOUNT, 0);
                break;
            case R.id.next:
                horizontal_rv.smoothScrollBy(SCROLL_AMOUNT, 0);
                break;*/
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SortingAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(AdminNewApprovals.this);
        View view= LayoutInflater.from(AdminNewApprovals.this).inflate(R.layout.activity_admin_sorting_white,null);
        RelativeLayout relBranch,relDept,relDate,relStatus;
        TextView branchName,departmentName,txtdate,status;
        relBranch = view.findViewById(R.id.relBranch);
        status = view.findViewById(R.id.status);
        relDate = view.findViewById(R.id.relDate);
        relStatus = view.findViewById(R.id.relStatus);
        relDept = view.findViewById(R.id.relDept);
        branchName = view.findViewById(R.id.branchName);
        departmentName = view.findViewById(R.id.departmentName);
        txtdate = view.findViewById(R.id.date);
        custom_calendar = view.findViewById(R.id.custom_calendar);
        ImageView close = view.findViewById(R.id.close);
        LinearLayout clear_filters = view.findViewById(R.id.clear_filters);
        LinearLayout status_layout = view.findViewById(R.id.status_layout);
        LinearLayout approve = view.findViewById(R.id.approve);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        LinearLayout branch_layout = view.findViewById(R.id.branch_layout);
        RecyclerView branchRV = view.findViewById(R.id.branchRV);
        RecyclerView statusRV = view.findViewById(R.id.statusRV);
        LinearLayout dept_layout = view.findViewById(R.id.dept_layout);
        RecyclerView dept_rv = view.findViewById(R.id.dept_rv);
        LinearLayoutManager manager=new GridLayoutManager(getApplicationContext(),1);
        LinearLayoutManager manager1=new GridLayoutManager(getApplicationContext(),1);
        LinearLayoutManager manager2=new GridLayoutManager(getApplicationContext(),1);
        branchRV.setLayoutManager(manager);
        dept_rv.setLayoutManager(manager1);
        statusRV.setLayoutManager(manager2);

        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();


        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"status"))){
            status.setText(commonClass.getSharedPref(getApplicationContext(),"status"));
        }else{
            status.setText("Status");
        }




         if(status_value==0) {
             txtdate.setText("Date");
        } else{
             if(!TextUtils.isEmpty(usableDate)){
                 txtdate.setText(usableDate);
             }else{

                 txtdate.setText("Date");
             }

        }
         if(TextUtils.isEmpty(serverDate)){
             serverDate = serverDateFormat.format(new Date());
             usableDate = usableDateFormat.format(new Date());
         }


        String[] splitDate1 = serverDate.split("-");
        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

        for(int i=1;i<=total_days;i++){
            if(status_value==0){
                dateHashmap.put(i,"normal");
            }else {

                if(!TextUtils.isEmpty(serverDate)) {
                    String[] splitDate = serverDate.split("-");
                    if (splitDate.length > 1) {
                        Log.d("indexValues", " i " + i + " split date " + splitDate[2] +
                                " integer " + Integer.parseInt(splitDate[2]));
                        if (i == Integer.parseInt(splitDate[2])) {
                           // dateHashmap.put(i, "leave");
                            dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                        } else {
                            dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                            //  dateHashmap.put(i, "normal");
                        }
                    } else {
                        dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                        //dateHashmap.put(i, "normal");
                    }
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
           // custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
            if(TextUtils.isEmpty(serverDate)){
                String d = serverDateFormat.format(new Date());
                calendar.setTime(format.parse(d));

            }else{
                calendar.setTime(format.parse(serverDate));

            }
   //         BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
            BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave, Color.parseColor(getApplicationContext().getString(R.string.n_org)));

            // Add decorators to the calendar
         //   custom_calendar.addDecorator(decorator1);
            custom_calendar.addDecorator(decorator2);
           // custom_calendar.setDate(calendar, dateHashmap);

        } catch (ParseException e) {
            Log.d("dateHashmap", " error " + e.getMessage());
            e.printStackTrace();
        }

        custom_calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date1, boolean selected) {
                // get string date
                if(dateHashmap!=null){
                    if(dateHashmap.size()!=0){
                        String sDate= commonClass.splitDate(date1.toString());
                        String sDate1= commonClass.splitYear(date1.toString());

                        usableDate = sDate;
                        try {
                            Date date = usableDateFormat.parse(usableDate);
                             txtdate.setText(usableDateFormat.format(date));
                            usableDate = usableDateFormat.format(date);
                            serverDate = serverDateFormat.format(date);
                            Log.d("getDates"," dates "+usableDate);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        serverDate = sDate1;
                        Log.d("getDates"," sdgbsg ");

                    }

                    status_value=1;
                    Set<CalendarDay> dateNormal = new HashSet<>();
                    Set<CalendarDay> dateLeave = new HashSet<>();
                    custom_calendar.removeDecorators();
                    String[] splitDate1 = serverDate.split("-");
                    int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

                    for (int i = 1; i <= total_days; i++) {
                        if(serverDate!=null) {
                            String[] splitDate = serverDate.split("-");
                            if (splitDate.length > 1) {
                                Log.d("indexValues", " i " + i + " split date " + splitDate[2] +
                                        " integer " + Integer.parseInt(splitDate[2]));
                                if (i == Integer.parseInt(splitDate[2])) {
                                   // dateHashmap.put(i, "leave");
                                    dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                                } else {
                                    dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                                    //dateHashmap.put(i, "normal");
                                }
                            } else {
                                dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                                // dateHashmap.put(i, "normal");
                            }
                        }
                    }

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
                        //custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
                        if(TextUtils.isEmpty(serverDate)){
                            String d = serverDateFormat.format(new Date());
                            calendar.setTime(format.parse(d));

                        }else{
                            calendar.setTime(format.parse(serverDate));

                        }
                        //custom_calendar.setDate(calendar, dateHashmap);
                       // BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
                        BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave, Color.parseColor(getApplicationContext().getString(R.string.n_org)));

                        // Add decorators to the calendar
                        //custom_calendar.addDecorator(decorator1);
                        custom_calendar.addDecorator(decorator2);


                    } catch (ParseException e) {
                        Log.d("dateHashmap", " error " + e.getMessage());
                        e.printStackTrace();
                    }


                }


            }
        });


        if(branchDB.SelectDistrictList().size()!=0){
            branchNameList = branchDB.SelectDistrictList();
            branchMainList= branchDB.selectAllDistrictDetails();
        }
        BranchAdapter adapter = new BranchAdapter(AdminNewApprovals.this,branchMainList,branchRV,0,branchName,departmentName);
        branchRV.setAdapter(adapter);


        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"branch_id"))){
            if(commonClass.getSharedPref(getApplicationContext(),"branch_id").equals("0")){
                branchName.setText("All Branch");
                if(!departmentNameList.contains("ALL")){
                    departmentNameList.add("ALL");
                }

            }else{
                int pos =Integer.parseInt(commonClass.getSharedPref(getApplicationContext(),"branch_id"));
                if(pos!=0){
                    String id=branchDB.selectDistrictId(branchNameList.get(pos));
                    departmentNameList =departmentDB.SelectDepartmentList(id);
                }
                String name = branchNameList.get(Integer.parseInt(commonClass.getSharedPref(getApplicationContext(),"branch_id")));
                branchName.setText(name);
            }

        }else{
            branchName.setText("Branch");
        }

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"department_id"))){
            List<String> idlist = Arrays.asList(commonClass.getSharedPref(this,"department_id").split(" , "));

            if(idlist.contains("0")){
                departmentName.setText("ALL");
            }else{
                List<String> departmentList=new ArrayList<>();
                for(int i=0;i<idlist.size();i++){
                    String index_v = departmentNameList.get(Integer.parseInt(idlist.get(i)));
                    departmentList.add(index_v);
                    Log.d("department_id"," selected dept "+index_v);
                }

                if(departmentList.size()!=0) {
                    String str = String.join(" , ", departmentList);
                    if (!TextUtils.isEmpty(str)) {
                        departmentName.setText(str);

                    } else {
                        departmentName.setText("Department");
                    }
                }else{
                    departmentName.setText("Department");
                }

            }

        }else{
            departmentName.setText("Department");
        }

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;





        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                pageNo=1;
                getEmployeeList();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        clear_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                status_value=0;
                selectedDeptID.clear();
                commonClass.putSharedPref(getApplicationContext(),"status",null);
                commonClass.putSharedPref(getApplicationContext(),"department_id",null);
                commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
                serverDate =null;
                usableDate=null;
                /*serverDate=serverDateFormat.format(new Date());
                usableDate=usableDateFormat.format(new Date());*/
                pageNo=1;
                getEmployeeList();
            }
        });

        relDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_layout.setVisibility(View.GONE);
                custom_calendar.setVisibility(View.GONE);
                branch_layout.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"branch_id"))){
                    int pos =Integer.parseInt(commonClass.getSharedPref(getApplicationContext(),"branch_id"));
                    if(pos!=0){
                        String id=branchDB.selectDistrictId(branchNameList.get(pos));
                        departmentMainList = departmentDB.SelectDepartmentListFull(id);
                    }else{
                        if(!departmentMainList.contains("ALL")){
                            departmentName.setText("ALL");
                            departmentMainList.add(new KeystoneModal("0","ALL"));
                        }
                    }

                } else{
                    if(!departmentMainList.contains("ALL")){
                        departmentName.setText("ALL");
                        departmentMainList.add(new KeystoneModal("0","ALL"));
                    }
                }
                DepartmentAdapter adapter1 = new DepartmentAdapter(AdminNewApprovals.this,departmentMainList,dept_rv,0,departmentName);
                dept_rv.setAdapter(adapter1);
                if(dept_layout.getVisibility()==View.VISIBLE){
                    dept_layout.setVisibility(View.GONE);
                }else{
                    dept_layout.setVisibility(View.VISIBLE);
                }


                /*projectAlertDialog("Select Department Name",departmentNameList,departmentName,1);
                mDialog.dismiss();*/
            }
        });
        relDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_layout.setVisibility(View.GONE);
                dept_layout.setVisibility(View.GONE);
                branch_layout.setVisibility(View.GONE);
                if(custom_calendar.getVisibility()==View.GONE) {
                    custom_calendar.setVisibility(View.VISIBLE);
                }else{
                    custom_calendar.setVisibility(View.GONE);
                }
            }
        });




        relBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_layout.setVisibility(View.GONE);
                custom_calendar.setVisibility(View.GONE);
                dept_layout.setVisibility(View.GONE);
                if(branch_layout.getVisibility()==View.VISIBLE){
                    branch_layout.setVisibility(View.GONE);
                }else{
                    branch_layout.setVisibility(View.VISIBLE);
                }
               /* callAlertDialog("Select Branch Name",branchNameList,branchName,0);
                mDialog.dismiss();*/
            }
        });

        relStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusAdapter adaptersta = new StatusAdapter(AdminNewApprovals.this,statusList,statusRV,0,status);
                statusRV.setAdapter(adaptersta);
                branch_layout.setVisibility(View.GONE);
                custom_calendar.setVisibility(View.GONE);
                dept_layout.setVisibility(View.GONE);
                if(status_layout.getVisibility()==View.VISIBLE){
                    status_layout.setVisibility(View.GONE);
                }else{
                    status_layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getEmployeeList() {
        Log.d("getEmployeeList"," positio "+position);
        /*if(TextUtils.isEmpty(serverDate)){
            serverDate = serverDateFormat.format(new Date());
        }*/
        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"status"))){
            strStatusName="ALL";
        }else{
            strStatusName=commonClass.getSharedPref(getApplicationContext(),"status");
            strStatusName = strStatusName.replace(" ","");
        }

        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"branch_id"))){
            branchID="ALL";
        } else{
            branchID = commonClass.getSharedPref(getApplicationContext(),"branch_id");
        }



        int size_var=0;
        String selectDept= null;
        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"department_id"))){
            selectedDept="ALL";
        }else{
            List<String> idlist = new ArrayList<>();
            idlist = Arrays.asList(commonClass.getSharedPref(getApplicationContext(), "department_id").split(" , "));
            if(idlist.contains("0")){
                selectedDept="ALL";
                size_var=1;
            }else{
                selectedDeptID.clear();
                for(int i=0;i<idlist.size();i++){
                    selectedDeptID.add(idlist.get(i));

                }
                size_var=0;
            }
        }








        String request_date = serverDate;
        Log.d("getEmployeeList"," dah "+dash+" status_value "+status_value);
        //if(!TextUtils.isEmpty(dash) && status_value==0){
            if(!TextUtils.isEmpty(dash) ){

                request_date=null;
        }



        Log.d("getEmployeeList"," index "+selectedDeptID.indexOf("ALL")+" size "+size_var+" dash "+
                dash+" status "+status_value);


        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(getApplicationContext())).create(ApiInterface.class);
        Call<LeaveTopModal> call=null;
        Log.d("getEmployeeList"," come true "+branchID+" name "+strStatusName+" date "+serverDate+
                "request date "+request_date+" dept id "+selectedDeptID.size()+" selected dept "+selectedDept+" id "
        +commonClass.getDeviceID(getApplicationContext()));


        if(selectedDeptID.size()!=0){
            if(selectedDeptID.size()==1){
                if(selectedDeptID.get(0).equals("ALL")){
                    selectedDeptID.clear();
                    selectedDept="ALL";
                }
            }
        }



        switch (position){
            case 0:
                if(size_var==0 && selectedDeptID.size()!=0){

                    call = apiInterface.getLeaveApprovalsArr(branchID,selectedDeptID,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }else{
                    Log.d("getEmployeeList"," come false ");
                    call = apiInterface.getLeaveApprovals(branchID,selectedDept,strStatusName,serverDate
                            ,String.valueOf(pageNo),request_date);
                }
                break;
            case 1:
                if(size_var==0 && selectedDeptID.size()!=0){
                    call = apiInterface.getLoanListArr(branchID,selectedDeptID,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }else{
                    call = apiInterface.getLoanList(branchID,selectedDept,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }
                break;
            case 2:
                if(size_var==0 && selectedDeptID.size()!=0){
                    call = apiInterface.getClaimListArr(branchID,selectedDeptID,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }else{
                    call = apiInterface.getClaimList(branchID,selectedDept,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }
                break;
            case 3:
                if(size_var==0 && selectedDeptID.size()!=0){
                    call = apiInterface.getOnDutyListArr(branchID,selectedDeptID,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);

                }else{
                    call = apiInterface.getOnDutyList(branchID,selectedDept,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);
                }
                break;
            case 4:
                if(size_var==0 && selectedDeptID.size()!=0){
                    call = apiInterface.getOverTimeApprovslsArr(branchID,selectedDeptID,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);

                }else{
                    call = apiInterface.getOverTimeApprovsls(branchID,selectedDept,strStatusName,serverDate,
                            String.valueOf(pageNo),request_date);

                }
                break;




        }
        if(call!=null) {
            Log.d("getEmployeeList", " url " + call.request().url()+" page no "+pageNo);
            call.enqueue(new Callback<LeaveTopModal>() {
                @Override
                public void onResponse(Call<LeaveTopModal> call, Response<LeaveTopModal> response) {
                    loader.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Log.d("getEmployeeList", " code " + response.code());
                        if (response.code() == 200) {
                            Log.d("getEmployeeList", " res " + response.body().getStatus()
                                    + " modal " + response.body().getLeaveInnerModal() + " data modal " +
                                    response.body().getLeaveInnerModal().getDataList().size());
                            if (response.body().getStatus().equals("success")) {
                                Log.d("getEmployeeList"," lay 1 page no"+pageNo);
                                if (response.body().getLeaveInnerModal().getDataList() != null) {
                                    Log.d("getEmployeeList"," lay 2 ");
                                    if (response.body().getLeaveInnerModal().getDataList().size() != 0) {
                                        Log.d("getEmployeeList"," lay 3 ");
                                        if (pageNo == 1) {

                                            if(!TextUtils.isEmpty(response.body().getLcount())){
                                                pending_request_count =Integer.parseInt(response.body().getLcount());
                                                main_pending_count+=Integer.parseInt(response.body().getLcount());

                                            }else{
                                                pending_request_count =0;
                                            }
                                            leaveAppModals.clear();
                                        }else{
                                            if(!TextUtils.isEmpty(response.body().getLcount())){
                                                main_pending_count+=Integer.parseInt(response.body().getLcount());

                                                pending_request_count+=Integer.parseInt(response.body().getLcount());
                                            }
                                        }

                                        pendingcount.setText(String.valueOf(main_pending_count));


                                        Log.d("AdminNewApprovals"," positio "+position+" pending  "+
                                                main_pending_count+"  get coinr "+response.body().getLcount());

                                        updateHorizontalRV(horizontal_rv,position,String.valueOf(main_pending_count));


                                        for (int i = 0; i < response.body().getLeaveInnerModal().getDataList().size(); i++) {
                                           response.body().getLeaveInnerModal().getDataList().get(i).setStatus( response.body().getLeaveInnerModal().getDataList().get(i).getStatus());
                                            leaveAppModals.add(response.body().getLeaveInnerModal().getDataList().get(i));
                                        }

                                        if(leaveAppModals.size()!=0){
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Collections.sort(leaveAppModals, new Comparator<LeaveAppModal >() {
                                                @Override
                                                public int compare( LeaveAppModal  a, LeaveAppModal  b) {
                                                    try {
                                                        Date dateA = sdf.parse(a.getFrom());
                                                        Date dateB = sdf.parse(b.getFrom());
                                                        return dateB.compareTo(dateA); // Descending order
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        return 0;
                                                    }
                                                }
                                            });                                        }
                                        no_data.setVisibility(View.GONE);
                                        updateNotificationChanged();

                                    } else {
                                        updateHorizontalRV(horizontal_rv,position,String.valueOf(main_pending_count));

                                        if (pageNo == 1) {
                                            Log.d("getEmployeeList", " come to else "+pageNo);
                                            Log.d("getEmployeeList"," checknbo 3");
                                            check_all.setVisibility(View.GONE);
                                            leaveAppModals.clear();
                                            pendingcount.setText(String.valueOf(main_pending_count));
                                            no_data.setVisibility(View.VISIBLE);
                                        }
                                        Log.d("getEmployeeList"," empty "+leaveAppModals.size());
                                        updateNotificationChanged();
                                    }
                                }
                            } else {
                                updateHorizontalRV(horizontal_rv,position,String.valueOf(main_pending_count));

                                if (pageNo == 1) {
                                    Log.d("getEmployeeList"," checknbo 2 ");
                                    check_all.setVisibility(View.GONE);
                                    pendingcount.setText(String.valueOf(main_pending_count));
                                    leaveAppModals.clear();
                                    no_data.setVisibility(View.VISIBLE);
                                }
                                updateNotificationChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<LeaveTopModal> call, Throwable t) {
                    loader.setVisibility(View.GONE);
                    Log.d("getEmployeeList", " error " + t.getMessage());
                }
            });
        }


    }



    private void updateNotificationChanged() {
        Log.d("updateNot"," pos "+position+" pos "+checkpos);
        switch (position){
            case 0:
                //claim
                adapter.notifyDataSetChanged();

                break;
            case 1:
                //leave
                loanApprovalAdapter.notifyDataSetChanged();
                break;
            case 2:
                //loan
                claimApprovalAdapter.notifyDataSetChanged();

                break;
            case 3:
                //asset
                onDutyAdapter.notifyDataSetChanged();

                break;
            case 4:
                //ticket
                overtimeAdapter.notifyDataSetChanged();

                break;

        }
    }

    public void projectAlertDialog(String title,List<String> empNameList,TextView send_to_text,int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewApprovals.this,R.style.CustomDialog);

        // set title
        builder.setTitle(title);
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = empNameList.toArray(new String[0]);

        //  selectedProjects = new boolean[stringArray.length];


        Log.d("projectListID"," size as "+projectListID.size());
       /* if(projectListID.size()!=0) {
            for (int i = 0; i < projectListID.size(); i++) {
                Log.d("projectListID"," selected id "+projectListID.get(i));
                selectedProjects[projectListID.get(i)] = true;
            }
        }*/


        String SendToID;
       // selectedDept="-1";

        if(TextUtils.isEmpty(strDeptName)){
            strDeptName="ALL";
        }
        if(TextUtils.isEmpty(strDeptID)){
            strDeptID="0";
        }
        Log.d("AdminDashbord"," dept name "+strDeptName+" id "+strDeptID);

            for (int i = 0; i < empNameList.size(); i++) {
                 if(Integer.parseInt(strDeptID)==i){
                    selectedDept = String.valueOf(i);
                     send_to_text.setText(empNameList.get(i));
                 }else{
                }
                selectedDept="0";
                send_to_text.setText("ALL");
            }

        SendToID = selectedDept;

        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(pos==0){
                    selectedDept="ALL";
                }else{

                    strDeptName = empNameList.get(which);
                    strDeptID = String.valueOf(which);
                    selectedDept = departmentDB.selectDepartmentID(strDeptName,strBranchID);
                    send_to_text.setText(strDeptName);
                }
                Log.d("AdminDashbord"," brch "+strBranchName+"  dept "+strDeptName+" see "+selectedDept+
                        " id "+strDeptID);

            }
        });



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                pageNo=1;
                getEmployeeList();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });


        final android.app.AlertDialog mDialog = builder.create();
        mDialog.create();
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));


    }

    private void callStatusAlertDialog(String title,List<String> empNameList,TextView send_to_text ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewApprovals.this,R.style.CustomDialog);

        // set title
        builder.setTitle(title);
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray ;
        stringArray= empNameList.toArray(new String[0]);



        //   String[] stringArray = empNameList.toArray(new String[0]);


        String SendToID;
        if(!TextUtils.isEmpty(strStatusName)) {
            for (int i = 0; i < empNameList.size(); i++) {
                if(empNameList.get(i).equals(strStatusName)){
                    strStatusName = String.valueOf(i);
                }else{
                }
            }
            send_to_text.setText(strStatusName);
        }
        SendToID = strStatusName;



        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                strStatusName = empNameList.get(which);
                send_to_text.setText(strStatusName);

                Log.d("AdminDashbord"," brch "+strStatusName+"  dept "+strStatusName);

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pageNo=1;
                getEmployeeList();
                dialog.dismiss();
            }
        });


        // builder.show();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
    }


    private void callAlertDialog(String title,List<String> empNameList,TextView send_to_text,int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewApprovals.this,R.style.CustomDialog);

        // set title
        builder.setTitle(title);
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray ;
        stringArray= empNameList.toArray(new String[0]);



        //   String[] stringArray = empNameList.toArray(new String[0]);


        String SendToID;
        if(pos==0){
            strBranchID="0";
            if(!TextUtils.isEmpty(strBranchName)) {
                for (int i = 0; i < empNameList.size(); i++) {
                    if(empNameList.get(i).equals(strBranchName)){
                        strBranchID = String.valueOf(i);
                    }else{
                    }
                }
                send_to_text.setText(strBranchName);
            }
            SendToID = strBranchID;
        }else{
            strDeptID="0";
            if(!TextUtils.isEmpty(strDeptName)) {
                for (int i = 0; i < empNameList.size(); i++) {
                    if(empNameList.get(i).equals(strDeptName)){
                        strDeptID = String.valueOf(i);
                    }else{
                    }
                }
                send_to_text.setText(strDeptName);
            }
            SendToID = strDeptID;
        }


        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(pos==0){

                    strBranchName = empNameList.get(which);
                    strBranchID = String.valueOf(which);
                    branchID = branchDB.selectDistrictId(strBranchName);
                    send_to_text.setText(strBranchName);
                    if(strBranchName.equals("ALL")){
                        departmentNameList.clear();
                        departmentNameList.add("ALL");
                    }else{
                        departmentNameList = departmentDB.SelectDepartmentList(branchID);
                    }

                }else{

                    strDeptName = empNameList.get(which);
                    strDeptID = String.valueOf(which);
                    departmentID = departmentDB.selectDepartmentID(strDeptName,strBranchID);
                    send_to_text.setText(strDeptName);
                }
                Log.d("AdminDashbord"," brch "+strBranchName+"  dept "+strDeptName);
                pageNo=1;
                getEmployeeList();
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pageNo=1;
                getEmployeeList();
                dialog.dismiss();
            }
        });


        //  builder.show();

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
          alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
    }

}
