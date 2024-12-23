package in.proz.adamd.AdminModule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
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
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.tuyenmonkey.mkloader.MKLoader;



import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.proz.adamd.AdminFilterAdapter.BranchAdapter;
import in.proz.adamd.AdminFilterAdapter.DepartmentAdapter;
import in.proz.adamd.AdminModule.AdminCommon.CommonPageActivityNew;
import in.proz.adamd.AdminModule.AdminModal.BranchGetModal;
import in.proz.adamd.AdminModule.AdminModal.BranchMainModal;
import in.proz.adamd.AdminModule.AdminModal.DashboardInnerModal;
import in.proz.adamd.AdminModule.SQLiteDB.BranchDB;
import in.proz.adamd.AdminModule.SQLiteDB.ClaimSQL;
import in.proz.adamd.AdminModule.SQLiteDB.DepartmentDB;
import in.proz.adamd.AdminModule.SQLiteDB.LoanSQL;
import in.proz.adamd.AdminModule.SQLiteDB.OnDutySQL;
import in.proz.adamd.AdminModule.SQLiteDB.OverTimeSQL;
import in.proz.adamd.AdminModule.SQLiteDB.WeekOffSQL;
import in.proz.adamd.BackgroundColorDecorator;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.KeystoneModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.AdminApiInterface;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.LeaveIDNewSQL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNewDashboard extends AppCompatActivity implements View.OnClickListener {
    CommonClass commonClass = new CommonClass();
    List<String> selectedDeptID = new ArrayList<>();
    SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMMM dd,yyyy");
    SimpleDateFormat sdfDisplay1 = new SimpleDateFormat("dd-MM-yyyy");

    MaterialCalendarView custom_calendar;
    List<String> branchNameList = new ArrayList<>();
    TextView tot_onduty;
    boolean[] selectedProjects;
    ArrayList<Integer> projectListID = new ArrayList<>();
    List<String> departmentNameList = new ArrayList<>();
    BranchDB branchDB;
    DepartmentDB departmentDB;
    DashboardInnerModal finalDashboardResponse;
    HashMap<Integer,Object> dateHashmap=new HashMap<>();

    String branchID,departmentID,serverDate,usableDate;
    LeaveIDNewSQL leaveIDNewSQL;
    OnDutySQL onDutySQL;
    OverTimeSQL overTimeSQL;
    ClaimSQL claimSQL;
    LoanSQL loanSQL;
    WeekOffSQL weekOffSQL;
    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    String strBranchID,strBranchName,strDeptID,strDeptName;
    RelativeLayout search_layout;

    MKLoader loader;
    TextView txt_leave,txt_duty,txt_claim,txt_assets,txt_tickets,txt_loan,
            tot_employee,tot_present,tot_absent,tot_leave,tot_late,tot_permission,tot_bio,tot_login;

    ImageView back_arrow;
    LinearLayout employee_layout,linear_present,linear_absent,linear_leave,linear_late,linear_permission,linear_bio,
            linear_mobile,lin_today_leave,linear_onduty,lin_today_onduty;
    TextView today_onleave ,today_onduty;

    LinearLayout leave_approval,onduty_approval,linear_loan,linear_tickets,asset_linear,layout_claim;
    ImageView img1,datePickerDisplay;
    TextView selectDisplayFormat;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;
    List<KeystoneModal> branchMainList= new ArrayList<>();
    List<KeystoneModal> departmentMainList= new ArrayList<>();
    LinearLayout linear_onduty_req ,linear_overtime_req;
    TextView tot_overtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_admin_dashboard);
        Bundle b= getIntent().getExtras();
        if(b!=null){
            usableDate = b.getString("usable");
            serverDate= b.getString("server");
            serverDate= b.getString("server");
        }
        initView();
     }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SelectDateDialog(AlertDialog mainAlert){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewDashboard.this);
        View view= LayoutInflater.from(AdminNewDashboard.this).inflate(R.layout.date_picker_calendar,null);
        ImageView closeWindow,clear;
        TextView selectedDate=view.findViewById(R.id.selectedDate);
        clear=view.findViewById(R.id.clear);
        closeWindow=view.findViewById(R.id.closeWindow);
        custom_calendar = view.findViewById(R.id.custom_calendar);

        /*HashMap<Object, Property> mapDescToProp = new HashMap<>();
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.leave_normal;
        propDefault.enable = false;
        mapDescToProp.put("normal", propDefault);
        propDefault.dateTextViewResource=R.id.text_view;
        Property propUnavailable = new Property();
        propUnavailable.layoutResource = R.layout.leave_active;
        propUnavailable.dateTextViewResource=R.id.text_view;
        mapDescToProp.put("leave", propUnavailable);
        custom_calendar.setMapDescToProp(mapDescToProp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            custom_calendar.setOutlineAmbientShadowColor(getApplicationContext().getResources().getColor(R.color.color_primary));
        }
*/


        custom_calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay newMonth) {
                String sDate = newMonth.getYear()
                        + "-" + newMonth.getMonth()
                        + "-" + newMonth.getDay();
                Set<CalendarDay> dateNormal = new HashSet<>();
                int total_days = commonClass.getDaysInMonth(newMonth.getYear(), newMonth.getMonth());

                for(int i=1;i<=total_days;i++){
                    dateNormal.add(CalendarDay.from(newMonth.getYear(), newMonth.getMonth(),newMonth.getDay()));

                    dateHashmap.put(i,"normal");
                }
                BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
                custom_calendar.addDecorator(decorator1);


            }
        });

        if(!TextUtils.isEmpty(usableDate)) {
            selectedDate.setText(usableDate);
        } else{
            serverDate = serverDateFormat.format(new Date());
            usableDate = usableDateFormat.format(new Date());
            selectedDate.setText(usableDate);
        }

        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();
        String[] splitDate1 = serverDate.split("-");
        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

        for(int i=1;i<=total_days;i++){
            String[] splitDate = serverDate.split("-");

            if(splitDate.length>1) {

                if (i == Integer.parseInt(splitDate[2])) {
                    dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));
                    //dateHashmap.put(i,"leave");
                }else{
                    dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                    //dateHashmap.put(i,"normal");
                }
            }else{
                dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));
                //dateHashmap.put(i,"normal");
            }
        }

        Calendar calendar=  Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.d("indexValues"," dashmap size "+dateHashmap.size()+" server date "+serverDate);
          //  custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
            calendar.setTime(format.parse(serverDate));
          //  custom_calendar.setDate(calendar,dateHashmap);
          //  BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
            BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave,  Color.parseColor(getApplicationContext().getString(R.string.n_org)));

            // Add decorators to the calendar
          //  custom_calendar.addDecorator(decorator1);
            custom_calendar.addDecorator(decorator2);

        } catch (ParseException e) {
            Log.d("dateHashmap"," error "+e.getMessage());
            e.printStackTrace();
        }




        builder.setView(view);

        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

   /*     Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity =  Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/


        mDialog.setCancelable(false);
        mDialog.create();
        if(!mDialog.isShowing()) {
            mDialog.show();
        }

        closeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if(mainAlert!=null){
                    if(mainAlert.isShowing()) {
                        mainAlert.dismiss();
                    }
                }

                getEmployeeList();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate.setText("Select Date");

                Calendar calendar=  Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                serverDate =format.format(new Date());
                usableDate=usableDateFormat.format(new Date());

                try {
                   // custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
                    calendar.setTime(format.parse(serverDate));
                   // custom_calendar.setDate(calendar,dateHashmap);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SortingAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(AdminNewDashboard.this);
        View view= LayoutInflater.from(AdminNewDashboard.this).inflate(R.layout.activity_admin_sorting,null);
        RelativeLayout relBranch,relDept,relDate;
        TextView branchName,departmentName,txtdate;
        relBranch = view.findViewById(R.id.relBranch);
        relDate = view.findViewById(R.id.relDate);
        relDept = view.findViewById(R.id.relDept);
        branchName = view.findViewById(R.id.branchName);
        departmentName = view.findViewById(R.id.departmentName);
        txtdate = view.findViewById(R.id.date);
        custom_calendar = view.findViewById(R.id.custom_calendar);
        ImageView close = view.findViewById(R.id.close);
        LinearLayout clear_filters = view.findViewById(R.id.clear_filters);
        LinearLayout approve = view.findViewById(R.id.approve);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        LinearLayout branch_layout = view.findViewById(R.id.branch_layout);
        RecyclerView branchRV = view.findViewById(R.id.branchRV);
         LinearLayout dept_layout = view.findViewById(R.id.dept_layout);
        RecyclerView dept_rv = view.findViewById(R.id.dept_rv);
        LinearLayoutManager manager=new GridLayoutManager(getApplicationContext(),1);
        LinearLayoutManager manager1=new GridLayoutManager(getApplicationContext(),1);
        branchRV.setLayoutManager(manager);
        dept_rv.setLayoutManager(manager1);


       /* HashMap<Object, Property> mapDescToProp = new HashMap<>();
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.leave_normal;
        propDefault.enable = false;
        mapDescToProp.put("normal", propDefault);
        propDefault.dateTextViewResource = R.id.text_view;
        Property propUnavailable = new Property();
        propUnavailable.layoutResource = R.layout.leave_active;
        propUnavailable.dateTextViewResource = R.id.text_view;
        mapDescToProp.put("leave", propUnavailable);
        custom_calendar.setMapDescToProp(mapDescToProp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            custom_calendar.setOutlineAmbientShadowColor(getApplicationContext().getResources().getColor(R.color.color_primary));
        }*/
        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();

        if (!TextUtils.isEmpty(usableDate)) {
            txtdate.setText(usableDate);
        } else {
            serverDate = serverDateFormat.format(new Date());
            usableDate = usableDateFormat.format(new Date());
            txtdate.setText(usableDate);
        }
        String[] splitDate1 = serverDate.split("-");

        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

        for (int i = 1; i <= total_days; i++) {
            String[] splitDate = serverDate.split("-");
            if (splitDate.length > 1) {
                Log.d("indexValues", " i " + i + " split date " + splitDate[2] +
                        " integer " + Integer.parseInt(splitDate[2]));
                if (i == Integer.parseInt(splitDate[2])) {
                  //  dateHashmap.put(i, "leave");
                    dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                } else {
                //    dateHashmap.put(i, "normal");
                    dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                }
            } else {
                dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                //dateHashmap.put(i, "normal");
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
           // custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
            calendar.setTime(format.parse(serverDate));
            //custom_calendar.setDate(calendar, dateHashmap);
           // BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
            BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave,  Color.parseColor(getApplicationContext().getString(R.string.n_org)));

            // Add decorators to the calendar
            //custom_calendar.addDecorator(decorator1);
            custom_calendar.addDecorator(decorator2);

        } catch (ParseException e) {
            Log.d("dateHashmap", " error " + e.getMessage());
            e.printStackTrace();
        }

        custom_calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date1, boolean selected) {

                String sDate=commonClass.splitDate(date1.toString());
                String sDate1 = commonClass.splitYear(date1.toString());
                 usableDate = sDate;
                 Log.d("getDatedList"," sdate "+sDate+" date1 "+sDate1);
                try {
                    Date date = usableDateFormat.parse(usableDate);
                    Log.d("getDatedList"," date "+date);
                    selectDisplayFormat.setText(sdfDisplay.format(date));
                    txtdate.setText(usableDateFormat.format(date));
                    usableDate = usableDateFormat.format(date);
                    serverDate = serverDateFormat.format(date);
                    Log.d("getDatedList"," usable format "+usableDate+" setver date "+serverDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                serverDate = sDate1;
                Set<CalendarDay> dateNormal = new HashSet<>();
                Set<CalendarDay> dateLeave = new HashSet<>();
                custom_calendar.removeDecorators();
                String[] splitDate1 = serverDate.split("-");
                int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

                for (int i = 1; i <= total_days; i++) {
                    String[] splitDate = serverDate.split("-");
                    if (splitDate.length > 1) {
                        Log.d("indexValues", " i " + i + " split date " + splitDate[2] +
                                " integer " + Integer.parseInt(splitDate[2]));
                        if (i == Integer.parseInt(splitDate[2])) {
                            dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                            // dateHashmap.put(i, "leave");
                        } else {
                            dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                          //  dateHashmap.put(i, "normal");
                        }
                    } else {
                        dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

                        // dateHashmap.put(i, "normal");
                    }
                }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
                  //  custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
                    calendar.setTime(format.parse(serverDate));
                    // custom_calendar.setDate(calendar, dateHashmap);
                  //  BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
                    BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave,  Color.parseColor(getApplicationContext().getString(R.string.n_org)));

                    // Add decorators to the calendar
                  //  custom_calendar.addDecorator(decorator1);
                    custom_calendar.addDecorator(decorator2);
                } catch (ParseException e) {
                    Log.d("dateHashmap", " error " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
/*
        custom_calendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                // get string date
                if(dateHashmap!=null){
                    if(dateHashmap.size()!=0){
                        String sDate=selectedDate.get(Calendar.DAY_OF_MONTH)
                                +"-" +(selectedDate.get(Calendar.MONTH)+1)
                                +"-" + selectedDate.get(Calendar.YEAR);
                        String sDate1=selectedDate.get(Calendar.YEAR)
                                +"-" +(selectedDate.get(Calendar.MONTH)+1)
                                +"-" + selectedDate.get(Calendar.DAY_OF_MONTH);
                        usableDate = sDate;
                        try {
                            Date date = usableDateFormat.parse(usableDate);
                            selectDisplayFormat.setText(sdfDisplay.format(date));
                            txtdate.setText(usableDateFormat.format(date));
                            usableDate = usableDateFormat.format(date);
                            serverDate = serverDateFormat.format(date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        serverDate = sDate1;

                    }



                    for (int i = 1; i <= 31; i++) {
                        String[] splitDate = serverDate.split("-");
                        if (splitDate.length > 1) {
                            Log.d("indexValues", " i " + i + " split date " + splitDate[2] +
                                    " integer " + Integer.parseInt(splitDate[2]));
                            if (i == Integer.parseInt(splitDate[2])) {
                                dateHashmap.put(i, "leave");
                            } else {
                                dateHashmap.put(i, "normal");
                            }
                        } else {
                            dateHashmap.put(i, "normal");
                        }
                    }

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
                        custom_calendar.getMonthYearTextView().setTextColor(R.color.black);
                        calendar.setTime(format.parse(serverDate));
                        custom_calendar.setDate(calendar, dateHashmap);

                    } catch (ParseException e) {
                        Log.d("dateHashmap", " error " + e.getMessage());
                        e.printStackTrace();
                    }


                }


            }
        });
*/


        if(branchDB.SelectDistrictList().size()!=0){
            branchNameList = branchDB.SelectDistrictList();
            branchMainList= branchDB.selectAllDistrictDetails();
        }
        BranchAdapter adapter = new BranchAdapter(AdminNewDashboard.this,branchMainList,branchRV,0,branchName,departmentName);
        branchRV.setAdapter(adapter);
        if(!TextUtils.isEmpty(usableDate)) {
            txtdate.setText(usableDate);
        } else{
            serverDate = serverDateFormat.format(new Date());
            usableDate = usableDateFormat.format(new Date());
            txtdate.setText(usableDate);
        }
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


        Log.d("department_id"," depat "+commonClass.getSharedPref(getApplicationContext(),"department_id"));
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
                commonClass.putSharedPref(getApplicationContext(),"department_id",null);
                commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
                serverDate=serverDateFormat.format(new Date());
                usableDate=usableDateFormat.format(new Date());
                selectDisplayFormat.setText(usableDate);
                getEmployeeList();
            }
        });

        relDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                DepartmentAdapter adapter1 = new DepartmentAdapter(AdminNewDashboard.this,departmentMainList,dept_rv,0,departmentName);
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
    }

    private String splitDate(String string) {
        String[] split = string.split("-");
        String mont= split[1];
        String date= split[2];
        if(Integer.parseInt(split[1])<=9){
            mont="0"+split[1];
        }
        if(Integer.parseInt(split[2])<=9){
            date="0"+split[2];
        }
        String rtn = split[0]+"-"+mont+"-"+date;
        return rtn;
    }

    public void datePicker(TextView editText) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date = String.valueOf(dayOfMonth);
                        String str_month = String.valueOf(monthOfYear + 1);
                        if (dayOfMonth <= 9) {
                            str_date = "0" + String.valueOf(dayOfMonth);
                        }
                        if ((monthOfYear + 1) <= 9) {
                            str_month = "0" + String.valueOf(monthOfYear + 1);
                        }

                        final Calendar c = Calendar.getInstance();
                        serverDate = year + "-" + str_month + "-" + str_date ;
                        usableDate = str_date+"-"+str_month+"-"+year;
                        editText.setText(usableDate);
                        getEmployeeList();


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getWindow().setGravity(Gravity.BOTTOM);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commonClass.putSharedPref(getApplicationContext(),"department_id",null);
        commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
    }

    private void initView() {
        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        tot_onduty= findViewById(R.id.tot_onduty);
        linear_onduty_req= findViewById(R.id.linear_onduty_req);
        linear_overtime_req= findViewById(R.id.linear_overtime_req);
        tot_overtime= findViewById(R.id.tot_overtime);
         linear_onduty_req.setOnClickListener(this);
        linear_overtime_req.setOnClickListener(this);
         commonClass.putSharedPref(getApplicationContext(),"dash",null);
        commonClass.putSharedPref(getApplicationContext(),"department_id",null);
        commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
        commonClass.putSharedPref(getApplicationContext(),"status",null);
        lin_today_leave= findViewById(R.id.lin_today_leave);
        lin_today_leave.setOnClickListener(this);
        linear_onduty= findViewById(R.id.linear_onduty);
        today_onduty= findViewById(R.id.today_onduty);
         today_onleave= findViewById(R.id.today_onleave);
        lin_today_onduty = findViewById(R.id.lin_today_onduty);
        lin_today_onduty.setOnClickListener(this);
        linear_onduty.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
      //  nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
       /* if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            nabout_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }else{
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }
*/

        datePickerDisplay = findViewById(R.id.datePickerDisplay);
        selectDisplayFormat = findViewById(R.id.selectedDateDisplay);


        Log.d("dateFormat"," date  "+usableDate);
        if(!TextUtils.isEmpty(usableDate)) {
            try {
                Date date = sdfDisplay1.parse(usableDate);
                Log.d("dateFormat"," date "+date);
                selectDisplayFormat.setText(sdfDisplay.format(date));
            } catch (ParseException e) {
                Log.d("dateFormat"," error "+e.getMessage());
                e.printStackTrace();
            }
        } else{
            serverDate = serverDateFormat.format(new Date());
            usableDate = usableDateFormat.format(new Date());
            try {
                Date date = sdfDisplay1.parse(usableDate);
                Log.d("dateFormat"," date "+date);
                selectDisplayFormat.setText(sdfDisplay.format(date));
            } catch (ParseException e) {
                Log.d("dateFormat"," error "+e.getMessage());
                e.printStackTrace();
            }
        }
        //selectDisplayFormat.setText(sdfDisplay.format(new Date()));
        datePickerDisplay.setOnClickListener(this);
        img1 = findViewById(R.id.img1);
        img1.setOnClickListener(this);
        linear_loan= findViewById(R.id.linear_loan);
        linear_loan.setOnClickListener(this);
        linear_tickets= findViewById(R.id.linear_tickets);
        linear_tickets.setOnClickListener(this);
        asset_linear= findViewById(R.id.asset_linear);
        asset_linear.setOnClickListener(this);
        layout_claim= findViewById(R.id.layout_claim);
        layout_claim.setOnClickListener(this);
        onduty_approval= findViewById(R.id.onduty_approval);
        onduty_approval.setOnClickListener(this);
        leaveIDNewSQL = new LeaveIDNewSQL(AdminNewDashboard.this);
        leaveIDNewSQL.getWritableDatabase();
        onDutySQL = new OnDutySQL(AdminNewDashboard.this);
        onDutySQL.getWritableDatabase();
        onDutySQL.DropTable();
        claimSQL = new ClaimSQL(AdminNewDashboard.this);
        claimSQL.getWritableDatabase();
        claimSQL.DropTable();
        weekOffSQL = new WeekOffSQL(AdminNewDashboard.this);
        weekOffSQL.getWritableDatabase();
        weekOffSQL.DropTable();
        loanSQL = new LoanSQL(AdminNewDashboard.this);
        loanSQL.getWritableDatabase();
        loanSQL.DropTable();

        leaveIDNewSQL.DropTable();
        overTimeSQL = new OverTimeSQL(AdminNewDashboard.this);
        overTimeSQL.getWritableDatabase();
        overTimeSQL.DropTable();
        leave_approval = findViewById(R.id.leave_approval);
        leave_approval.setOnClickListener(this);
       /* TextView clear_filters = findViewById(R.id.clear_filters);
        clear_filters.setOnClickListener(this);*/
        loader = findViewById(R.id.loader);
        linear_present=findViewById(R.id.linear_present);
        linear_absent=findViewById(R.id.linear_absent);
        linear_leave=findViewById(R.id.linear_leave);
        linear_late=findViewById(R.id.linear_late);
        linear_permission=findViewById(R.id.linear_permission);
        linear_bio=findViewById(R.id.linear_bio);
        linear_mobile=findViewById(R.id.linear_mobile);
        linear_present.setOnClickListener(this);
        linear_absent.setOnClickListener(this);
        linear_leave.setOnClickListener(this);
        linear_late.setOnClickListener(this);
        linear_permission.setOnClickListener(this);
        linear_bio.setOnClickListener(this);
        linear_mobile.setOnClickListener(this);


        employee_layout = findViewById(R.id.employee_layout);
        employee_layout.setOnClickListener(this);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        TextView title = findViewById(R.id.title);
        title.setText("Admin Summary");
        txt_leave=findViewById(R.id.txt_leave);
        txt_duty=findViewById(R.id.txt_duty);
        txt_claim=findViewById(R.id.txt_claim);
        txt_assets=findViewById(R.id.txt_assets);
        txt_tickets=findViewById(R.id.txt_tickets);
        txt_loan=findViewById(R.id.txt_loan);
        tot_employee=findViewById(R.id.tot_employee);
        tot_present=findViewById(R.id.tot_present);
        tot_absent=findViewById(R.id.tot_absent);
        tot_leave=findViewById(R.id.tot_leave);
        tot_late=findViewById(R.id.tot_late);
        tot_permission=findViewById(R.id.tot_permission);
        tot_bio=findViewById(R.id.tot_bio);
        tot_login=findViewById(R.id.tot_login);






        /*search_layout = findViewById(R.id.search_layout);
        search_layout.setOnClickListener(this);*/
        branchDB = new BranchDB(AdminNewDashboard.this);
        branchDB.getWritableDatabase();
        departmentDB = new DepartmentDB(AdminNewDashboard.this);
        departmentDB.getWritableDatabase();
        if(commonClass.isOnline(AdminNewDashboard.this)){
            getDropDownList();
            getEmployeeList();
        }else{
            commonClass.showInternetWarning(AdminNewDashboard.this);
        }

    }


    public void getEmployeeList(){
        if(TextUtils.isEmpty(serverDate)){
            serverDate = serverDateFormat.format(new Date());
        }



        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"branch_id"))){
            branchID="ALL";
        } else{
            branchID = commonClass.getSharedPref(getApplicationContext(),"branch_id");
        }

        String selectDept= null;
        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"department_id"))){
            selectDept="ALL";
        }else{
            List<String> idlist = new ArrayList<>();
            idlist = Arrays.asList(commonClass.getSharedPref(getApplicationContext(), "department_id").split(" , "));
             if(idlist.contains("0")){
                 selectDept="ALL";
             }else{
                 selectedDeptID.clear();
                 for(int i=0;i<idlist.size();i++){
                     selectedDeptID.add(idlist.get(i));
                 }
             }
        }


        Log.d("mainDropList"," branch id "+branchID+" dpet "+departmentID+" dept "+selectDept+"   date "+
                serverDate);
        loader.setVisibility(View.VISIBLE);
        AdminApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AdminNewDashboard.this,"token"),
                commonClass.getDeviceID(AdminNewDashboard.this)).create(AdminApiInterface.class);


        Call<DashboardInnerModal> call;
        if(!TextUtils.isEmpty(selectDept)){
            call = apiInterface.getAdminDashboardDetails(serverDate,branchID,selectDept);

        }else{
            call = apiInterface.getAdminDashboardDetails(serverDate,branchID,selectedDeptID);

        }




        Log.d("mainDropList"," emp list "+call.request().url()+" selected dept "+selectDept+"  branch id "+branchID+
                " server date "+serverDate+" sel "+selectedDeptID);
        call.enqueue(new Callback<DashboardInnerModal>() {
            @Override
            public void onResponse(Call<DashboardInnerModal> call, Response<DashboardInnerModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("mainDropList"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            finalDashboardResponse = response.body();
                            if(response.body().getDashboardModal().getTotal_employee_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_employee_details().getCount())){
                                    tot_employee.setText(response.body().getDashboardModal().getTotal_employee_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getTotal_present_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_present_details().getCount())){
                                    Log.d("mainDropList"," present "+response.body().getDashboardModal().getTotal_present_details().getCount());
                                    tot_present.setText(response.body().getDashboardModal().getTotal_present_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getTotal_absent_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_absent_details().getCount())){
                                    tot_absent.setText(response.body().getDashboardModal().getTotal_absent_details().getCount());
                                    Log.d("getAbsentList"," as "+response.body().getDashboardModal().getTotal_absent_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getTotal_leave_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_leave_details().getCount())){
                                    tot_leave.setText(response.body().getDashboardModal().getTotal_leave_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getToday_onduty_pencount()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getToday_onduty_pencount())){
                                    tot_onduty.setText(response.body().getDashboardModal().getToday_onduty_pencount());
                                }
                            }
                            if(response.body().getDashboardModal().getToday_overtime_pencount()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getToday_overtime_pencount())){
                                    tot_overtime.setText(response.body().getDashboardModal().getToday_overtime_pencount());
                                }
                            }
                            if(response.body().getDashboardModal().getTotal_late_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_late_details().getCount())){
                                    tot_late.setText(response.body().getDashboardModal().getTotal_late_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getTotal_permission_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getTotal_permission_details().getCount())){
                                    tot_permission.setText(response.body().getDashboardModal().getTotal_permission_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getBio_login_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getBio_login_details().getCount())){
                                    tot_bio.setText(response.body().getDashboardModal().getBio_login_details().getCount());
                                }
                            }
                            if(response.body().getDashboardModal().getMobile_login_details()!=null){
                                if(!TextUtils.isEmpty(response.body().getDashboardModal().getMobile_login_details().getCount())){
                                    tot_login.setText(response.body().getDashboardModal().getMobile_login_details().getCount());
                                }
                            }

                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_leave())){
                                txt_leave.setText(response.body().getDashboardModal().getReq_leave());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_onduty())){
                                txt_duty.setText(response.body().getDashboardModal().getReq_onduty());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_claim())){
                                txt_claim.setText(response.body().getDashboardModal().getReq_claim());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_assets())){
                                txt_assets.setText(response.body().getDashboardModal().getReq_assets());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_tickets())){
                                txt_tickets.setText(response.body().getDashboardModal().getReq_tickets());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getReq_loan())){
                                txt_loan.setText(response.body().getDashboardModal().getReq_loan());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getToday_leave_pencount())){
                                today_onleave.setText(response.body().getDashboardModal().getToday_leave_pencount());
                            }
                            if(!TextUtils.isEmpty(response.body().getDashboardModal().getToday_onduty_pencount())){
                                today_onduty.setText(response.body().getDashboardModal().getToday_onduty_pencount());
                            }







                        }
                    }
                    else{

                        tot_employee.setText("00");
                        tot_present.setText("00");
                        tot_absent.setText("00");
                        tot_leave.setText("00");
                        tot_late.setText("00");
                        tot_permission.setText("00");
                        tot_bio.setText("00");
                        tot_login.setText("00");
                        txt_leave.setText("00");
                        txt_duty.setText("00");
                        txt_claim.setText("00");
                        txt_assets.setText("00");
                        txt_tickets.setText("00");
                        txt_loan.setText("00");

                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardInnerModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("mainDropList"," eror  "+t.getMessage()+" t "+t.getMessage());
            }
        });
    }






    public void getDropDownList(){
        loader.setVisibility(View.VISIBLE);
        AdminApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AdminNewDashboard.this,"token"),
                commonClass.getDeviceID(AdminNewDashboard.this)).create(AdminApiInterface.class);
        Call<BranchGetModal> call = apiInterface.getSortingDropDown();
        Log.d("mainDropList"," url as "+call.request().url());
        call.enqueue(new Callback<BranchGetModal>() {
            @Override
            public void onResponse(Call<BranchGetModal> call, Response<BranchGetModal> response) {
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getStatus().startsWith("success")){
                        branchDB.DropTable();
                        departmentDB.DropTable();
                        if(response.body().getGetBranchDetails().size()!=0){

                            for(int i=0;i<response.body().getGetBranchDetails().size();i++){
                                BranchMainModal commonPojo = response.body().getGetBranchDetails().get(i);
                                branchDB.insertData(commonPojo.getBranch().getId(),commonPojo.getBranch().getName(),commonPojo.getBranch().getAddress(),
                                        commonPojo.getBranch().getLocation());
                                for(int j=0;j<commonPojo.getDepartments().size();j++){
                                    departmentDB.insertData(commonPojo.getDepartments().get(j).getId(),commonPojo.getDepartments().get(j).getName(),
                                            commonPojo.getDepartments().get(j).getBranch(),commonPojo.getDepartments().get(j).getRole(),
                                            commonPojo.getDepartments().get(j).getIncharge());
                                }
                            }

                            updateAdapter();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BranchGetModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void updateAdapter() {
        if(!departmentNameList.contains("ALL")){
            departmentNameList.add("ALL");
        }

        if(!branchNameList.contains("ALL")){
            branchNameList.add("ALL");
        }


        if(branchDB.SelectDistrictList().size()!=0){
            departmentNameList = branchDB.SelectDistrictList();
        }

    }
    public void OnBackPRess(){
        Intent intent = new Intent(AdminNewDashboard.this, DashboardNewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnBackPRess();
    }
    public void callCommonIntent(int pos){
        Intent intent = new Intent(getApplicationContext(),AdminNewApprovals.class);
        intent.putExtra("position",pos);
        intent.putExtra("branch",branchID);
        intent.putExtra("serverDate",serverDate);
        intent.putExtra("usableDate",usableDate);
        intent.putExtra("projectID",(Serializable) projectListID);
        intent.putExtra("department",(Serializable) selectedDeptID);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.linear_onduty_req:
                Intent intenton = new Intent(getApplicationContext(), OnDuty.class);
                startActivity(intenton);
                break;
            case R.id.linear_overtime_req:
                Intent intentot = new Intent(getApplicationContext(), OverTime.class);
                startActivity(intentot);
                break;

            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.linear_onduty:
                callIntentMethod("9","onduty","Onduty List","1");
                break;
            case R.id.lin_today_onduty:
                callMethodForApprove(5);
                break;
            case R.id.lin_today_leave:
                callMethodForApprove(0);
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
            case R.id.layout_claim:
                callCommonIntent(4);
                break;
            case R.id.asset_linear:
                callCommonIntent(2);
                break;
            case R.id.linear_tickets:
                callCommonIntent(3);
                break;
            case R.id.linear_loan:
                callCommonIntent(1);
                break;
            case R.id.onduty_approval:
                callCommonIntent(5);
                break;
            case R.id.leave_approval:
                callCommonIntent(0);
                break;
          /*  case R.id.clear_filters:
                branchID=null;
                departmentID=null;
                selectedDeptID.clear();
                projectListID.clear();
                serverDate=serverDateFormat.format(new Date());
                usableDate=usableDateFormat.format(new Date());
                getEmployeeList();
                break;*/
            case R.id.datePickerDisplay:
                SelectDateDialog(null);
                break;
          case R.id.img1:
                SortingAlertDialog();
                break;
            case R.id.back_arrow:
                OnBackPRess();
                break;
            case R.id.employee_layout:
                callIntentMethod("1","employee","Employee List","1");
                break;
            case R.id.linear_present:
                callIntentMethod("2","present","Present List","1");
                break;
            case R.id.linear_absent:
                callIntentMethod("3","absent","Absent List","1");
                break;
            case R.id.linear_leave:
                callIntentMethod("4","leave","Leave List","1");
                break;
            case R.id.linear_late:
                callIntentMethod("5","late","Late Attendance List","1");
                break;
            case R.id.linear_permission:
                callIntentMethod("6","permission","Permission List","1");
                break;
            case R.id.linear_bio:
                callIntentMethod("7","bio","Bio Punch List","1");
                break;
            case R.id.linear_mobile:
                callIntentMethod("8","mobile","Mobile Punch List","1");
                break;

        }
    }

    private void callMethodForApprove(int number) {
        commonClass.putSharedPref(getApplicationContext(),"dash",null);

        List<Integer> projectListID = new ArrayList<>();
        //projectListID.add(0);
        List<String> selectedDeptID = new ArrayList<>();
        //selectedDeptID.add("ALL");
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");



        Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
        intent.putExtra("position",number);
        intent.putExtra("branch","ALL");
        intent.putExtra("dash1","dash");
        intent.putExtra("serverDate",serverDate);
        intent.putExtra("usableDate",usableDate);
        intent.putExtra("projectID",(Serializable) projectListID);
        intent.putExtra("department",(Serializable) selectedDeptID) ;
        startActivity(intent);
    }

    private void callIntentMethod(String number, String employee,String title,String type) {
        int status_type=1;
        if(number.equals("4") && type.equals("1")){
            status_type=0;
        }else if(number.equals("9") && type.equals("1")){
            status_type=0;
        }


        Intent intent= new Intent(getApplicationContext(), CommonPageActivityNew.class);
        intent.putExtra("type",number);
        intent.putExtra("request_type",employee);
        intent.putExtra("type_title",title);
        intent.putExtra("status_value",status_type);
        intent.putExtra("branch",branchID);
        intent.putExtra("department", (Serializable) selectedDeptID);
        intent.putExtra("date_server",serverDate);
        intent.putExtra("date_ui",usableDate);
        startActivity(intent);
    }


}
