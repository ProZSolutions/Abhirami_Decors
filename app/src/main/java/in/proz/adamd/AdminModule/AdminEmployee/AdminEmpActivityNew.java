package in.proz.adamd.AdminModule.AdminEmployee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import in.proz.adamd.AdminFilterAdapter.BranchAdapter;
import in.proz.adamd.AdminFilterAdapter.DepartmentAdapter;
import in.proz.adamd.AdminModule.AdminAdapter.EmployeeAdapterNew;
import in.proz.adamd.AdminModule.AdminModal.BranchGetModal;
import in.proz.adamd.AdminModule.AdminModal.BranchMainModal;
import in.proz.adamd.AdminModule.AdminModals.InnerModal;
import in.proz.adamd.AdminModule.SQLiteDB.BranchDB;
import in.proz.adamd.AdminModule.SQLiteDB.DepartmentDB;
import in.proz.adamd.BackgroundColorDecorator;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.KeystoneModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.AdminApiInterface;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    AdminEmpActivityNew extends AppCompatActivity implements View.OnClickListener {
    ImageView back_arrow;
    int pageNo=1;
    MaterialCalendarView custom_calendar;
    List<KeystoneModal> branchMainList= new ArrayList<>();
    List<KeystoneModal> departmentMainList= new ArrayList<>();
    LinearLayout search_layout1,search_layout;
    CommonClass commonClass = new CommonClass();
    List<String> usertypeList=new ArrayList<>();

    int api_hit_status;
    MKLoader loader;
    TextView no_data;
    ImageView clear,img1 ;
    String searchTextStr=null;
    boolean[] selectedProjects;
    EditText edt_reason;

    String type,type_title,date_str,tag,server_str;
    GridLayoutManager manager;
    List<String> departmentList = new ArrayList<>();
    TextView title;
    RecyclerView recyclerView;
    List<CommonPojo> commonPojoList=new ArrayList<>();
    EmployeeAdapterNew adapter;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    String request_type,branchID,userType;
    EditText searchText;
    ImageView search;



    List<String> selectedDeptID = new ArrayList<>();
    List<String> branchNameList = new ArrayList<>();
    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    ArrayList<Integer> projectListID = new ArrayList<>();
    List<String> departmentNameList = new ArrayList<>();
    BranchDB branchDB;
    DepartmentDB departmentDB;
    String departmentID,serverDate,usableDate;

    String strBranchID,strBranchName,strDeptID,strDeptName,strUserID;
    TextView total_count;
    LinearLayout date_layout;
    HashMap<Integer,Object> dateHashmap=new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_common_layout_sample);
        Bundle b= getIntent().getExtras();

        initView();
     }
    public void initView(){

        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        total_count = findViewById(R.id.total_count);
        search = findViewById(R.id.search);
        search.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
      //  nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
      //  nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);



       /* if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/

        usertypeList.add("Active");
        usertypeList.add("Blocked");
        usertypeList.add("Relieved");
        branchDB = new BranchDB(   AdminEmpActivityNew.this);
        branchDB.getWritableDatabase();
        departmentDB = new DepartmentDB(   AdminEmpActivityNew.this);
        departmentDB.getWritableDatabase();
        img1 = findViewById(R.id.img1);
        img1.setVisibility(View.VISIBLE);
        img1.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        no_data = findViewById(R.id.no_data);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);
        searchText = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.recyclerView);

        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);
        back_arrow = findViewById(R.id.back_arrow);
        title = findViewById(R.id.title);
        title.setText("Employee List");
        search_layout1 = findViewById(R.id.search_layout1);
        search_layout = findViewById(R.id.search_layout);
          back_arrow.setOnClickListener(this);


        if(TextUtils.isEmpty(branchID)){
            branchID ="ALL";
        }
        if(departmentList==null){
            departmentList.add("ALL");
        }else if(departmentList.size()==0){
            departmentList.add("ALL");
        }

        if(TextUtils.isEmpty(userType)){
            userType="Active";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        if(TextUtils.isEmpty(date_str)){
            date_str=format1.format(new Date());
            server_str = format2.format(new Date());
        }
        if(commonClass.isOnline(AdminEmpActivityNew.this)) {
            getDropDownList();
        }

        adapter = new EmployeeAdapterNew(   AdminEmpActivityNew.this,
                commonPojoList,-1,recyclerView,loader);
        recyclerView.setAdapter(adapter);
        searchText.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() != 0) {
                            searchTextStr = s.toString();
                            pageNo = 1;
                            clear.setVisibility(View.VISIBLE);
                            getCustomerList();
                        } else {
                            searchTextStr = null;
                            pageNo = 1;
                            clear.setVisibility(View.GONE);
                            getCustomerList();
                        }
                    }
                };
                handler.postDelayed(runnable, 300); // Adjust the delay as needed

            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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
                    getCustomerList();
                }
            }
        });
        if(commonClass.isOnline(AdminEmpActivityNew.this)){
            getCustomerList();
        }else{
            commonClass.showInternetWarning(AdminEmpActivityNew.this);
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_reason.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_reason.setText(str_rem);
            }
        }
    }
    public void recordVoiceToText( EditText edt_reason) {
        this.edt_reason = edt_reason;
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        /*intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, 1);
        }
        catch (Exception e) {
            Toast.makeText(AdminEmpActivityNew.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void getCustomerList() {

        if(TextUtils.isEmpty(userType)){
            userType="Active";
        }

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
        Log.d("customers"," utype "+userType);
        String utype= userType.toLowerCase();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(getApplicationContext())).create(ApiInterface.class);
        Call<InnerModal> call =null;

        if(selectedDeptID.size()==0){
            call  = apiInterface.getEmployeeIndexAll(branchID,"ALL",utype,
                    searchTextStr,String.valueOf(pageNo));
        }else{
            if(selectedDeptID.size()==1){
                if(selectedDeptID.get(0).equals("ALL")){
                    call  = apiInterface.getEmployeeIndexAll(branchID,"ALL",utype,
                            searchTextStr,String.valueOf(pageNo));
                }else{
                    call  = apiInterface.getEmployeeIndex(branchID,selectedDeptID,utype,
                            searchTextStr,String.valueOf(pageNo));
                }
            }else{
                call  = apiInterface.getEmployeeIndex(branchID,selectedDeptID,utype,
                        searchTextStr,String.valueOf(pageNo));
            }

        }


        Log.d("customers"," get headers invoice "+call.request().headers()+
                " url "+call.request().url());
        call.enqueue(new Callback<InnerModal>() {
            @Override
            public void onResponse(Call<InnerModal> call, Response<InnerModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("customers"," response code invoice "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")) {
                            if(response.body().getOuterModal()!=null)
                                if(response.body().getOuterModal().getCommonPojoList()!=null) {
                                    if (response.body().getOuterModal().getCommonPojoList().size() != 0) {
                                        if (pageNo == 1) {
                                            commonPojoList.clear();
                                        }
                                        for (int i = 0; i < response.body().getOuterModal().getCommonPojoList().size(); i++) {
                                            commonPojoList.add(response.body().getOuterModal().getCommonPojoList().get(i));
                                        }
                                        adapter.notifyDataSetChanged();
                                        total_count.setText("Total Employee : "+String.valueOf(commonPojoList.size()));

                                    } else {
                                        if (pageNo == 1) {
                                            commonPojoList.clear();
                                            total_count.setText("Total Employee : 0");
                                            no_data.setVisibility(View.VISIBLE);
                                        }else{
                                            total_count.setText("Total Employee : "+String.valueOf(commonPojoList.size()));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                        }
                    }else{
                        if (pageNo == 1) {
                            commonPojoList.clear();
                            no_data.setVisibility(View.VISIBLE);
                            total_count.setText("Total Employee : 0");
                        }
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    if (pageNo == 1) {
                        commonPojoList.clear();
                        no_data.setVisibility(View.VISIBLE);
                        total_count.setText("Total Employee : 0");
                    }
                    adapter.notifyDataSetChanged();
                }

            }



            @Override
            public void onFailure(Call<InnerModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("customers"," fail "+t.getMessage());
                total_count.setText("Total Employee : 0");
            }
        });


    }

    public void OnBackPRess(){
        Intent intent = new Intent(   AdminEmpActivityNew.this, DashboardNewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnBackPRess();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
          /*  case R.id.nabout_layout:
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

            case R.id.back_arrow:
                OnBackPRess();
                break;
            case R.id.search:
                if(search_layout.getVisibility()==View.VISIBLE){
                    search_layout.setVisibility(View.GONE);
                 }else{
                    search_layout.setVisibility(View.VISIBLE);
                 }
                break;
            case R.id.clear:
                searchText.setText(null);
                pageNo=1;
                searchTextStr=null;
                search_layout.setVisibility(View.GONE);
                getCustomerList();
                break;
            case R.id.img1:
                SortingAlertDialog();
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SortingAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(AdminEmpActivityNew.this);
        View view= LayoutInflater.from(AdminEmpActivityNew.this).inflate(R.layout.activity_admin_sorting,null);
        RelativeLayout relBranch,relDept,relDate;
        TextView branchName,departmentName,txtdate;
        relBranch = view.findViewById(R.id.relBranch);
        relDate = view.findViewById(R.id.relDate);
        relDate.setVisibility(View.GONE);
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


   /*     HashMap<Object, Property> mapDescToProp = new HashMap<>();
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
        }
*/

        if (!TextUtils.isEmpty(usableDate)) {
            txtdate.setText(usableDate);
        } else {
            serverDate = serverDateFormat.format(new Date());
            usableDate = usableDateFormat.format(new Date());
            txtdate.setText(usableDate);
        }
        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();
        String[] splitDate1 = serverDate.split("-");

        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

        for (int i = 1; i <= total_days; i++) {
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

                //dateHashmap.put(i, "normal");
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.d("indexValues", " dashmap size " + dateHashmap.size() + " server date " + serverDate);
            //custom_calendar.getMonthYearTextView().setTextColor(getResources().getColor(R.color.black));
            calendar.setTime(format.parse(serverDate));
           // BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
            BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave, Color.parseColor(getApplicationContext().getString(R.string.n_org)) );

            // Add decorators to the calendar
           /// custom_calendar.addDecorator(decorator1);
            custom_calendar.addDecorator(decorator2);
            //custom_calendar.setDate(calendar, dateHashmap);

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
                try {
                    Date date = usableDateFormat.parse(usableDate);
                    //selectDisplayFormat.setText(sdfDisplay.format(date));
                    txtdate.setText(usableDateFormat.format(date));
                    usableDate = usableDateFormat.format(date);
                    serverDate = serverDateFormat.format(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                serverDate = sDate1;
                Set<CalendarDay> dateNormal = new HashSet<>();
                Set<CalendarDay> dateLeave = new HashSet<>();
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
        });


        if(branchDB.SelectDistrictList().size()!=0){
            branchNameList = branchDB.SelectDistrictList();
            branchMainList= branchDB.selectAllDistrictDetails();
        }
        BranchAdapter adapter = new BranchAdapter(AdminEmpActivityNew.this,branchMainList,branchRV,0,branchName,departmentName);
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
                    String str = String.join(",", departmentList);
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
                getCustomerList();
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
                selectedDeptID.clear();
                commonClass.putSharedPref(getApplicationContext(),"department_id",null);
                commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
                serverDate=serverDateFormat.format(new Date());
                usableDate=usableDateFormat.format(new Date());
                pageNo=1;
                getCustomerList();
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
                DepartmentAdapter adapter1 = new DepartmentAdapter(AdminEmpActivityNew.this,departmentMainList,dept_rv,0,departmentName);
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
    public void projectAlertDialog(String title,List<String> empNameList,TextView send_to_text,int pos){
        Log.d("SelectedProjectId"," department size "+projectListID.size());
        if(projectListID.size()!=0){
            send_to_text.setText(projectListID.size()+" Departments");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(   AdminEmpActivityNew.this,R.style.CustomDialog);

        // set title
        builder.setTitle(title);
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = empNameList.toArray(new String[0]);

        selectedProjects = new boolean[stringArray.length];


        if(projectListID.size()!=0) {
            for (int i = 0; i < projectListID.size(); i++) {
                selectedProjects[projectListID.get(i)] = true;
            }
        }

        builder.setMultiChoiceItems(stringArray, selectedProjects, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                // check condition
                if (b) {
                    if(i==0){
                        selectedDeptID.clear();
                        selectedDeptID.add("ALL");
                        dialogInterface.dismiss();
                        getCustomerList();
                    }else {
                        projectListID.add(i);
                        Collections.sort(projectListID);
                    }
                } else {
                    projectListID.remove(Integer.valueOf(i));
                }
                //updateGITLISt();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                StringBuilder stringBuilder = new StringBuilder();
                selectedDeptID.clear();
                departmentList.clear();
                for (int j = 0; j < projectListID.size(); j++) {
                    if (j != projectListID.size() - 1) {
                        Log.d("SelectedProjectId"," ad "+projectListID.get(j));
                        if(projectListID.get(j)==0){
                            selectedDeptID.add("ALL");
                        }else{

                            Log.d("SelectedProjectId"," selected id "+projectListID.get(j));
                            String dept = departmentNameList.get(projectListID.get(j));
                            selectedDeptID.add(departmentDB.selectDepartmentID(dept,strBranchID));
                            Log.d("SelectedProjectId"," Dept Name "+dept+" Dept ID "+
                                    departmentDB.selectDepartmentID(dept,strBranchID));

                        }

                    }
                    send_to_text.setText(projectListID.size()+" Departments");
                }
                pageNo=1;
                getCustomerList();
                // set text on textView

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog

                dialogInterface.dismiss();
            }
        });


        //builder.show();
        final android.app.AlertDialog mDialog = builder.create();

        mDialog.create();
        mDialog.show();


        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));

    }



    public void getDropDownList(){
        loader.setVisibility(View.VISIBLE);
        AdminApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(   AdminEmpActivityNew.this,"token"),
                commonClass.getDeviceID(   AdminEmpActivityNew.this)).create(AdminApiInterface.class);
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
        departmentNameList.add("ALL");
        branchNameList.add("ALL");

        if(branchDB.SelectDistrictList().size()!=0){
            departmentNameList = branchDB.SelectDistrictList();
        }

    }
    private void callAlertDialog(String title,List<String> empNameList,TextView send_to_text,int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(   AdminEmpActivityNew.this,R.style.CustomDialog);

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
            strUserID="0";
            if(!TextUtils.isEmpty(userType)) {
                for (int i = 0; i < empNameList.size(); i++) {
                    if(empNameList.get(i).equals(userType)){
                        strUserID = String.valueOf(i);
                    }else{
                    }
                }
                send_to_text.setText(userType);
            }
            SendToID = strUserID;
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

                    userType = empNameList.get(which);
                    strUserID = String.valueOf(which);
                    // departmentID = departmentDB.selectDepartmentID(strDeptName,strBranchID);
                    send_to_text.setText(userType);
                }
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

                Log.d("AdminDashbord"," brch "+strBranchName+"  dept "+userType);
                pageNo=1;
                getCustomerList();
                dialog.dismiss();
            }
        });




        // builder.show();
        final android.app.AlertDialog mDialog = builder.create();

        mDialog.create();
        mDialog.show();

        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
    }


/*    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        String sDate= newMonth.get(Calendar.YEAR)
                +"-" +(newMonth.get(Calendar.MONTH)+1)
                +"-" +newMonth.get(Calendar.DAY_OF_MONTH);
        custom_calendar.getMonthYearTextView().setTextColor(getResources().getColor(R.color.black));

        for(int i=1;i<=31;i++){
            dateHashmap.put(i,"normal");
        }




        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        for(int i=1;i<=31;i++){
            arr[0].put(i,"normal");
            dateHashmap.put(i,"normal");
        }

        return arr;
    }*/
}
