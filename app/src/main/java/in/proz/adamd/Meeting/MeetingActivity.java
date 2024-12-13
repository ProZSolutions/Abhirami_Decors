package in.proz.adamd.Meeting;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import in.proz.adamd.Adapter.MeetingAdapter;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.MeetingEmpModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    List<String> getEmpList=new ArrayList<>();
    RecyclerView recyclerView;
    int check_st=0;
    List<CommonPojo> getEmployeeList;
    List<Integer> selectedList=new ArrayList<>();
   TextView header;
    CommonPojo commonPojo;
    CheckBox checkBox;
    Spinner multipleItemSelectionSpinner;
    RadioButton rdo_online,rdo_direct;
    EditText edt_other,ed_fromdate,ed_totime,ed_fromtime,edt_invite_by_mail,edt_meeting_link,edt_location,edt_meeting;
    LinearLayout other_layout,meeting_link_layout,location_layout,frame_layout,request_layout,reset_layout,listLayout;
    ImageView from_picker,from_time_picker,to_time_picker,back_arrow,frame_icon;
    TextView title,frame_tag,add_team,request_text,check_all_text;
    CommonClass commonClass = new CommonClass();
    Spinner select_for_spinner;
    List<String> selectList= new ArrayList<>();
    List<String> employeeNameList,employeeMailList;
    MKLoader loader;
    String str_from_date,str_from_time,str_to_time;
    LinearLayout apply_leave_layout;
    int commonBoolean=-1;
    TextView no_data;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_meeting);
        no_data = findViewById(R.id.no_data);
         Bundle b = getIntent().getExtras();
        if(b!=null){
            commonPojo = (CommonPojo) b.getSerializable("saved_details");

        }
        initView();


        selectList.add("Select Schedule");
        selectList.add("Client");
        selectList.add("Internal");
        selectList.add("Others");
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,selectList);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        select_for_spinner.setAdapter(ad);

        select_for_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(((TextView) select_for_spinner.getSelectedView())!=null) {
                    ((TextView) select_for_spinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }
                if(position!=0){
                    if(position==3){
                        other_layout.setVisibility(View.VISIBLE);
                    }else{
                        other_layout.setVisibility(View.GONE);
                        edt_other.setText(null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rdo_direct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rdo_online.setChecked(false);
                    location_layout.setVisibility(View.VISIBLE);
                    meeting_link_layout.setVisibility(View.GONE);
                }
            }
        });
        rdo_online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rdo_direct.setChecked(false);
                    location_layout.setVisibility(View.GONE);
                    meeting_link_layout.setVisibility(View.VISIBLE);
                }
            }
        });
/*
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    check_all_text.setText("Uncheck All");
                    add_team.setText("All Selected");
                    commonBoolean=2;
                    List<String> sampleEmpList = new ArrayList<>();
                    if(getEmployeeList.size()!=0){
                        for (int k=0;k<getEmployeeList.size();k++){
                            sampleEmpList.add(getEmployeeList.get(k).getName());
                        }
                        if(sampleEmpList.size()!=0) {
                            String listString = String.join(",", sampleEmpList);
                            Log.d("listString"," str "+listString);
                            commonClass.putSharedPref(MeetingActivity.this,"members",listString);
                        }
                    }
                }else{
                    check_all_text.setText("Check All");
                    commonBoolean=1;
                    commonClass.putSharedPref(getApplicationContext(),"members",null);
                    add_team.setText("Select Members");
                }
                if(getEmployeeList.size()!=0){

                    updateAdapterSetSpinner(getEmployeeList);
                }else{
                    getEmployeeList();
                }
            }
        });
*/


    }

    private void updateUI(CommonPojo commonPojo) {
        Log.d("getEmployeeList"," update ui size "+getEmployeeList.size());
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        request_text.setText("Update");
        title.setText("Edit Meeting");
        if(!TextUtils.isEmpty(commonPojo.getType())){
            String upperString = commonPojo.getType().substring(0, 1).toUpperCase() + commonPojo.getType().substring(1).toLowerCase();
            int index = selectList.indexOf(upperString);
            if(index!=0){
                select_for_spinner.setSelection(index);
                if(upperString.contains("Other")){
                    other_layout.setVisibility(View.VISIBLE);
                    edt_other.setText(commonPojo.getOther_type());
                }
            }
        }
        edt_meeting.setText(commonPojo.getMeeting_name());
        if(!TextUtils.isEmpty(commonPojo.getFrom_date())){
            String[] split = commonPojo.getFrom_date().split(" ");
            if(split.length!=0){
                String[] spltDte= split[0].split("-");
                String[] spltTime= split[1].split(":");
                ed_fromdate.setText(spltDte[2]+"-"+spltDte[1]+"-"+spltDte[0]);
                str_from_date = spltDte[0]+"-"+spltDte[1]+"-"+spltDte[2];
                str_from_time = spltTime[0]+":"+spltTime[1];
                //ed_fromtime.setText(str_from_time);
                String[] spl = str_from_time.split(":");
                timeFormatChange(Integer.parseInt(spl[0]),Integer.parseInt(spl[1]),ed_fromtime,1);
            }
        }
        if(!TextUtils.isEmpty(commonPojo.getT_time())){
            String[] split = commonPojo.getT_time().split(" ");
            if(split.length!=0){
                 str_to_time = split[0];
                 String[] spl = str_to_time.split(":");
                 timeFormatChange(Integer.parseInt(spl[0]),Integer.parseInt(spl[1]),ed_totime,2);
               // ed_totime.setText(str_to_time);
            }
        }
        if(!TextUtils.isEmpty(commonPojo.getParticipate())){
            List<String> empList= Arrays.asList(commonPojo.getParticipate().split(","));
            selectedList.clear();
            for(int i=0;i<getEmployeeList.size();i++){
                if(empList.contains(getEmployeeList.get(i).getName())){
                    selectedList.add(i);
                }
            }
            if(selectedList.size()==0){
                add_team.setText("Add Team Members");
                checkBox.setChecked(false);
            }else if(selectedList.size()==getEmployeeList.size()){
                add_team.setText("All Members Selected");
                checkBox.setChecked(true);
            }else{
                add_team.setText(selectedList.size()+" Members Selected");
            }


         /*   if(empList.size()==0){
                add_team.setText("0 Selected");
            }else{
                if(getEmployeeList.size()!=0){
                    if(getEmployeeList.size()==empList.size()){
                        add_team.setText("All Selected");
                        check_all_text.setText("Uncheck All");
                        checkBox.setChecked(true);
                    }else{
                        add_team.setText(empList.size()+" Members  Selected");
                    }
                }else{
                    add_team.setText(empList.size()+" Members Selected");
                }
            }
            commonClass.putSharedPref(getApplicationContext(),"members",commonPojo.getParticipate());
            updateAdapterSetSpinnerWithList(empList);*/
        }
        edt_invite_by_mail.setText(commonPojo.getInvite_mail());
        if(!TextUtils.isEmpty(commonPojo.getLoaction_type())){
            if(commonPojo.getLoaction_type().equals("online")){
                meeting_link_layout.setVisibility(View.VISIBLE);
                rdo_online.setChecked(true);
                edt_meeting_link.setText(commonPojo.getMeeting_link());
            }else{
                rdo_direct.setChecked(true);
                location_layout.setVisibility(View.VISIBLE);
                edt_location.setText(commonPojo.getLocation_details());
            }

        }


    }

    private void initView() {
        TextView header_title  = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        /*if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }
*/

        employeeMailList = new ArrayList<>();
        employeeNameList = new ArrayList<>();
        checkBox = findViewById(R.id.checkBox);
        check_all_text = findViewById(R.id.check_all_text);
        getEmployeeList=new ArrayList<>();



        request_text = findViewById(R.id.request_text);
        request_text.setText("Submit");
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        frame_icon = findViewById(R.id.frame_icon);
         frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);

        frame_tag = findViewById(R.id.frame_tag);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        listLayout = findViewById(R.id.listLayout);
        edt_meeting = findViewById(R.id.edt_meeting);
        edt_meeting.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        select_for_spinner = findViewById(R.id.select_for_spinner);
        add_team = findViewById(R.id.add_team);
        multipleItemSelectionSpinner = findViewById(R.id.multipleItemSelectionSpinner);
        rdo_online = findViewById(R.id.rdo_online);
        rdo_direct = findViewById(R.id.rdo_direct);
        edt_other = findViewById(R.id.edt_other);
        ed_fromdate = findViewById(R.id.ed_fromdate);
        ed_totime = findViewById(R.id.ed_totime);
        ed_fromtime = findViewById(R.id.ed_fromtime);
        edt_invite_by_mail = findViewById(R.id.edt_invite_by_mail);
        edt_meeting_link = findViewById(R.id.edt_meeting_link);
        edt_location = findViewById(R.id.edt_location);
        other_layout = findViewById(R.id.other_layout);
        meeting_link_layout = findViewById(R.id.meeting_link_layout);
        location_layout = findViewById(R.id.location_layout);
         request_layout = findViewById(R.id.request_layout);
        request_layout.setOnClickListener(this);
        reset_layout = findViewById(R.id.reset_layout);
        reset_layout.setOnClickListener(this);
        from_picker = findViewById(R.id.from_picker);
        from_time_picker = findViewById(R.id.from_time_picker);
        to_time_picker = findViewById(R.id.to_time_picker);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
         title = findViewById(R.id.title);
         title.setText("Meeting List");
          loader = findViewById(R.id.loader);
        if(commonClass.isOnline(MeetingActivity.this)){
            getEmployeeList();
        }
        add_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //multipleItemSelectionSpinner.performClick();
                projectAlertDialog();
            }
        });
        callDateTimeSetMethod();

        from_picker.setOnClickListener(this);
        ed_fromtime.setOnClickListener(this);
        ed_fromdate.setOnClickListener(this);
        to_time_picker.setOnClickListener(this);
        ed_totime.setOnClickListener(this);
         from_time_picker.setOnClickListener(this);
         if(commonClass.isOnline(MeetingActivity.this)){
             if(getEmployeeList.size()==0){
                 getEmployeeList();
             }
             getList();
         }
         Log.d("getEmployeeList"," size in init "+getEmployeeList.size());


        if(commonPojo!=null){
            title.setText("Edit Meeting");
            updateUI(commonPojo);
        }



        ed_fromdate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        ed_totime.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edt_invite_by_mail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        ed_fromtime.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edt_meeting_link.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check_st==0) {
                    if (isChecked) {
                        check_all_text.setText("Uncheck all");
                        for (int i = 0; i < getEmployeeList.size(); i++) {
                            selectedList.add(i);
                        }
                        add_team.setText("All Members Selected");
                    } else {
                        check_all_text.setText("Check all");
                        add_team.setText("Add Team Members");
                        selectedList.clear();
                    }
                }
            }
        });
    }

    private void updateAdminUI() {
    }

    private void callDateTimeSetMethod() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat simpleDateFormatmin = new SimpleDateFormat("mm");
        SimpleDateFormat simpleDateFormatHR = new SimpleDateFormat("HH");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        Date againTime =new Date();
        againTime.setTime(System.currentTimeMillis()+(60*60*1000));
        ed_fromdate.setText(simpleDateFormat2.format(currentTime));
        str_from_date = simpleDateFormat1.format(currentTime);
        str_from_time= simpleDateFormat.format(currentTime);
        str_to_time = simpleDateFormat.format(againTime);

        timeFormatChange(Integer.parseInt(simpleDateFormatHR.format(new Date())),
                Integer.parseInt(simpleDateFormatmin.format(new Date())),ed_fromtime,1);
        timeFormatChange(Integer.parseInt(simpleDateFormatHR.format(againTime)),
                Integer.parseInt(simpleDateFormatmin.format(againTime)),ed_totime,2);

        /*ed_fromtime.setText(str_from_time);
        ed_totime.setText(str_to_time);*/
    }

    private void timePicker(EditText editText, int i) {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(MeetingActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        /*String min = String.valueOf(minute);
                        if(min.length()==1){
                            min="0"+min;
                        }

                        String hr = String.valueOf(hourOfDay);
                        Log.d("hoursday"," hr "+hr);
                        if(hr.length()==1){
                            if(hr.equals("0")){
                                hr="12";
                            }else{
                                hr="0"+hr;
                            }

                        }
                        String str_fromdate = " " + hr + ":" + min ;
                        if(i==1){
                            str_from_time =str_fromdate;
                        }else{
                            str_to_time = str_fromdate;
                        }
                        StringBuilder sb = new StringBuilder();
                        Log.d("hoursday"," get hours dat "+hourOfDay);
                        if(hourOfDay>=12){
                            if(hourOfDay==12){
                                sb.append(hourOfDay).append( ":" ).append(min).append(" PM");
                            }else{
                                String value = String.valueOf(hourOfDay-12);
                                if (value.length()==1){
                                    value="0"+value;
                                }
                                sb.append(value).append( ":" ).append(min).append(" PM");
                            }
                        }else{
                            if(hourOfDay==0){
                                hourOfDay = 12;
                                sb.append(hourOfDay).append( ":" ).append(min).append(" AM");

                            }else{
                                sb.append(hourOfDay).append( ":" ).append(min).append(" AM");

                            }
                        }
                      //  editText.setText(str_fromdate);
                        editText.setText(sb); */
                        timeFormatChange(hourOfDay,minute,editText,i);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }



    public void timeFormatChange(int hourOfDay,int minute,EditText editText,int i){
        String min = String.valueOf(minute);
        if(min.length()==1){
            min="0"+min;
        }

        String hr = String.valueOf(hourOfDay);
        Log.d("hoursday"," hr "+hr);
        if(hr.length()==1){
            if(hr.equals("0")){
                hr="12";
            }else{
                hr="0"+hr;
            }

        }
        String str_fromdate = " " + hr + ":" + min ;
        Log.d("selectedTime"," select "+str_fromdate);
        if(i==1){
            str_from_time =str_fromdate;
        }else{
            str_to_time = str_fromdate;
        }
        StringBuilder sb = new StringBuilder();
        Log.d("hoursday"," get hours dat "+hourOfDay);
        if(hourOfDay>=12){
            if(hourOfDay==12){
                sb.append(hourOfDay).append( ":" ).append(min).append(" PM");
            }else{
                String value = String.valueOf(hourOfDay-12);
                if (value.length()==1){
                    value="0"+value;
                }
                sb.append(value).append( ":" ).append(min).append(" PM");
            }
        }else{
            if(hourOfDay==0){
                hourOfDay = 12;
                sb.append(hourOfDay).append( ":" ).append(min).append(" AM");

            }else{
                sb.append(hourOfDay).append( ":" ).append(min).append(" AM");

            }
        }
        //  editText.setText(str_fromdate);
        editText.setText(sb);
    }
        public void selectDatePicker(){
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


                            String str_date=String.valueOf(dayOfMonth);
                            String str_month=String.valueOf(monthOfYear+1);
                            if(dayOfMonth<=9){
                                str_date="0"+String.valueOf(dayOfMonth);
                            }
                            if((monthOfYear+1)<=9){
                                str_month="0"+String.valueOf(monthOfYear+1);
                            }
                                 str_from_date =year + "-" + str_month + "-" + str_date;

                            ed_fromdate.setText(str_date + "-"
                                    + str_month + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.create();
            datePickerDialog.show();

        }
    private void getEmployeeList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(MeetingActivity.this)).create(ApiInterface.class);
        Call<MeetingEmpModal> call = apiInterface.getEmpDetails();
        Log.d("getEmployeeList"," ca;;  "+call.request().url());
        call.enqueue(new Callback<MeetingEmpModal>() {
            @Override
            public void onResponse(Call<MeetingEmpModal> call, Response<MeetingEmpModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("getEmployeeList"," codee "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            if(response.body().getGetData()!=null){
                                Log.d("getEmployeeList"," size as "+response.body().getGetData().size());
                                if(response.body().getGetData().size()!=0){
                                    getEmployeeList = response.body().getGetData();
                                    for(int i=0;i<getEmployeeList.size();i++){
                                        employeeNameList.add(getEmployeeList.get(i).getName());
                                        employeeMailList.add(getEmployeeList.get(i).getEmp_no());
                                    }
                                    updateAdapterSetSpinner(response.body().getGetData());
                                    if (commonPojo!=null){
                                        updateUI(commonPojo);
                                    }

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeetingEmpModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }
    private void updateAdapterSetSpinnerWithList( List<String> empList) {
        Log.d("empList"," size "+empList.size()+ "size "+getEmployeeList.size());
        for(int l=0;l<empList.size();l++){
            Log.d("empList"," saved "+empList.get(l));
        }
        List<KeyPairBoolData> getKeyList= new ArrayList<>();
        ArrayList<StateVO> listVOs = new ArrayList<>();
        StateVO stateVO1 = new StateVO();
     /*   stateVO1.setTitle("Select All");
        stateVO1.setSelected(false);
        listVOs.add(0,stateVO1);*/
        for (int i=0;i<getEmployeeList.size();i++){
            CommonPojo commonPojo = getEmployeeList.get(i);
            Log.d("empList"," co, "+commonPojo.getName()+"-"+commonPojo.getEmp_no());

            StateVO stateVO = new StateVO();
            stateVO.setTitle(commonPojo.getName());
           // stateVO.setTitle(commonPojo.getName()+"-"+commonPojo.getComp_email());
                 int index = empList.indexOf(commonPojo.getName());
                Log.d("empList"," index as "+index);
                if(index>=0){
                    stateVO.setSelected(true);
                }else{
                    stateVO.setSelected(false);
                }


                if(commonBoolean>1){
                    stateVO.setSelected(true);
                }else if(commonBoolean==1){
                    stateVO.setSelected(false);
                }

            listVOs.add(stateVO);
        }
        MyAdapter myAdapter = new MyAdapter(MeetingActivity.this, 0,
                listVOs,add_team,multipleItemSelectionSpinner,checkBox,check_all_text);
        multipleItemSelectionSpinner.setAdapter(myAdapter);



    }


    public void  callMultiSelect(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.multi_spinner,null);
        Spinner multipleItemSelectionSpinner = view.findViewById(R.id.multipleItemSelectionSpinner);
        TextView ok_btn = view.findViewById(R.id.ok_btn);
        TextView cancel_btn = view.findViewById(R.id.cancel_btn);

        builder.setView(view);
        final android.app.AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }
    private void updateAdapterSetSpinner(List<CommonPojo> getData) {
        List<KeyPairBoolData> getKeyList= new ArrayList<>();
        ArrayList<StateVO> listVOs = new ArrayList<>();
        StateVO stateVO1 = new StateVO();
     /*   stateVO1.setTitle("Select All");
        stateVO1.setSelected(false);
        listVOs.add(0,stateVO1);*/
        for (int i=0;i<getData.size();i++){
            CommonPojo commonPojo = getData.get(i);
            StateVO stateVO = new StateVO();
            //stateVO.setTitle(commonPojo.getName()+"-"+commonPojo.getComp_email());
            stateVO.setTitle(commonPojo.getName());
            if(commonBoolean>1){
                stateVO.setSelected(true);
            }else if(commonBoolean==1){
                stateVO.setSelected(false);
            }
            listVOs.add(stateVO);
        }
        MyAdapter myAdapter = new MyAdapter(MeetingActivity.this, 0,
                listVOs,add_team,multipleItemSelectionSpinner, checkBox, check_all_text);
        multipleItemSelectionSpinner.setAdapter(myAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackIntent();
    }
    public void EmployeeLIst(){
        commonPojo=null;
        title.setText("Meeting List");
        frame_tag.setText("Add Meeting");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
        apply_leave_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
        if(getEmployeeList.size()==0){
            getEmployeeList();
        }
        getList();
    }
    public void BackIntent(){
        if(commonPojo!=null){
            EmployeeLIst();
        }else{
            Intent intent =new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }
    }
    public void projectAlertDialog(){
            check_st=1;
        List<String> empList= new ArrayList<>();
        List<String> empIDList= new ArrayList<>();
        for(int i=0;i<getEmployeeList.size();i++){
          empList.add(getEmployeeList.get(i).getName());
          empIDList.add(String.valueOf(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MeetingActivity.this);

        // set title
        builder.setTitle("Select Members Names");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = empList.toArray(new String[0]);
        //   String[] stringArray = empNameList.toArray(new String[0]);

        boolean[] selectedProjects = new boolean[stringArray.length];
        Log.d("getSize"," size "+selectedList.size());

        if(selectedList.size()!=0) {
            for (int i = 0; i < selectedList.size(); i++) {
                selectedProjects[selectedList.get(i)] = true;
            }
        }


        builder.setMultiChoiceItems(stringArray, selectedProjects, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                // check condition

                if (b) {
                    check_st=1;
                    selectedList.add(i);
                    Collections.sort(selectedList);
                } else {
                    check_st=1;
                    checkBox.setChecked(false);
                    selectedList.remove(Integer.valueOf(i));
                }
                if(selectedList.size()==0){
                    add_team.setText("Add Team Members");
                    checkBox.setChecked(false);

                }else{
                    if(selectedList.size()==getEmployeeList.size()){
                        add_team.setText("All Members Selected");
                        checkBox.setChecked(true);
                    }else{
                        add_team.setText(selectedList.size()+" Members Selected");
                    }
                }
                //updateGITLISt();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                check_st=0;
                dialogInterface.dismiss();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                check_st=0;
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
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
       /*     case R.id.nabout_layout:
                Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentabout);
                break;*/
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                startActivity(intent);
                break;
            case R.id.back_arrow:
                BackIntent();
                break;
            case R.id.reset_layout:
                callReset();
                break;
            case R.id.frame_layout:
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    if(commonPojo!=null){
                        commonPojo= null;
                        callReset();
                        title.setText("Meeting Request");
                        request_text.setText("Submit");
                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                        frame_tag.setText("Meeting List");
                        apply_leave_layout.setVisibility(View.VISIBLE);
                        listLayout.setVisibility(View.GONE);
                    }else {
                        title.setText("Meeting List");
                        frame_tag.setText("Add Meeting");
                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                        apply_leave_layout.setVisibility(View.GONE);
                        listLayout.setVisibility(View.VISIBLE);
                        if(getEmployeeList.size()==0){
                            getEmployeeList();
                        }
                        getList();
                    }
                }else{
                    commonPojo= null;
                    callReset();
                     title.setText("Meeting Request");
                    request_text.setText("Submit");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                    frame_tag.setText("Meeting List");
                    apply_leave_layout.setVisibility(View.VISIBLE);
                     listLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.request_layout:
                if(select_for_spinner.getSelectedItemPosition()==0){
                    commonClass.showWarning(MeetingActivity.this,"Please select schedule for");
                }else if(TextUtils.isEmpty(edt_meeting.getText().toString())){
                    commonClass.showWarning(MeetingActivity.this,"Please Enter Meeting Name");
                }else if(select_for_spinner.getSelectedItemPosition()==3){
                    if(TextUtils.isEmpty(edt_other.getText().toString())){
                        commonClass.showWarning(MeetingActivity.this,"Please Enter Others");
                    }else{
                        checkMeetingCondition();
                    }
                }else{
                    checkMeetingCondition();
                }
                break;
            case R.id.ed_fromdate:
                selectDatePicker();
                break;
            case R.id.from_picker:
                selectDatePicker();
                break;
            case R.id.ed_fromtime:
                timePicker(ed_fromtime,1);
                break;
            case R.id.from_time_picker:
                timePicker(ed_fromtime,1);
                break;
            case R.id.ed_totime:
                timePicker(ed_totime,2);
                break;
            case R.id.to_time_picker:
                timePicker(ed_totime,2);
                break;

        }
    }

    public void     checkMeetingCondition() {
        if (TextUtils.isEmpty(edt_meeting.getText().toString())) {
            commonClass.showWarning(MeetingActivity.this, "Please Enter Meeting Name");
        } else if (TextUtils.isEmpty(ed_fromdate.getText().toString())) {
            commonClass.showWarning(MeetingActivity.this, "Please select date");
        } else if (TextUtils.isEmpty(ed_fromtime.getText().toString())) {
            commonClass.showWarning(MeetingActivity.this, "Please select from time");
        } else if (TextUtils.isEmpty(ed_totime.getText().toString())) {
            commonClass.showWarning(MeetingActivity.this, "Please select to time");
        } else if (selectedList.size()==0) {
            commonClass.showWarning(MeetingActivity.this, "Please select members");
        }/* else if (TextUtils.isEmpty(commonClass.getSharedPref(MeetingActivity.this, "members"))) {
            commonClass.showWarning(MeetingActivity.this, "Please select members");
        }*/   else {
            if (rdo_online.isChecked()) {
                if (TextUtils.isEmpty(edt_meeting_link.getText().toString())) {
                    commonClass.showWarning(MeetingActivity.this, "Please enter meeting link");
                } else {
                    insertMeetingDetails();
                }
            } else if (rdo_direct.isChecked()) {
                if (TextUtils.isEmpty(edt_location.getText().toString())) {
                    commonClass.showWarning(MeetingActivity.this, "Please enter location");
                } else {
                    insertMeetingDetails();
                }
            } else {
                commonClass.showWarning(MeetingActivity.this, "Please select Online/Direct");
            }
        }
    }
    public void  insertMeetingDetails(){
        request_layout.setEnabled(false);
        Log.d("employeeList"," member "+commonClass.getSharedPref(getApplicationContext(), "members")+
        "selected item list "+selectedList.size()+" mail list "+selectList.size() );
        List<String> employeeList=new ArrayList<>();
        List<String> employeeListMail=new ArrayList<>();
      /*  if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "members"))){
           employeeList = Arrays.asList(commonClass.getSharedPref(getApplicationContext(), "members").split(","));
           for(int k=0;k<getEmployeeList.size();k++){
               Log.d("employeeList"," name "+getEmployeeList.get(k).getName()+" mail "+
                       getEmployeeList.get(k).getComp_email());

               if(selectedList.contains(k)){
                   employeeListMail.add(getEmployeeList.get(k).getComp_email());

               }


             *//*  int index = employeeNameList.indexOf(employeeList.get(k));
               Log.d("employeeName"," index "+index);
               if(index>=0){
               }*//*
            }
        }*/


        for (int i=0;i<selectedList.size();i++){
            employeeListMail.add(getEmployeeList.get(selectedList.get(i)).getEmp_no());
         }


        request_layout.setEnabled(false);
        Log.d("employeeList"," meeting size "+employeeList.size());
        String loc ="";
        if(rdo_online.isChecked()){
            loc="online";
        }else{
            loc="direct";
        }
       loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(MeetingActivity.this,"token"),
                commonClass.getDeviceID(MeetingActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call = null;
        if(commonPojo!=null){
            call = apiInterface.meetingUpdate(commonPojo.getId(),select_for_spinner.getSelectedItem().toString().toLowerCase(),edt_other.getText().toString(),edt_meeting.getText().toString(),
                    str_from_date,str_from_time,str_to_time,edt_invite_by_mail.getText().toString(),employeeListMail,loc,
                    edt_meeting_link.getText().toString(),commonClass.getSharedPref(getApplicationContext(),"username"),edt_location.getText().toString());
        }else{
            call = apiInterface.meetingCreate(select_for_spinner.getSelectedItem().toString().toLowerCase(),edt_other.getText().toString(),edt_meeting.getText().toString(),
                    str_from_date,str_from_time,str_to_time,edt_invite_by_mail.getText().toString(),employeeListMail,loc,
                    edt_meeting_link.getText().toString(),commonClass.getSharedPref(getApplicationContext(),"username"),edt_location.getText().toString());
        }

        Log.d("insertMeeting"," enques "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                Log.d("insertMeeting"," response "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")) {
                            commonClass.showSuccess(MeetingActivity.this, response.body().getData());
                            if(getEmployeeList.size()==0){
                                getEmployeeList();
                            }
                            getList();
                            callReset();
                            apply_leave_layout.setVisibility(View.GONE);
                            listLayout.setVisibility(View.VISIBLE);
                            title.setText("Meeting List");
                            request_text.setText("Submit");
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                            frame_tag.setText("Meeting List");
                        }else{
                            commonClass.showSuccess(MeetingActivity.this, response.body().getStatus()+"\n"+response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(MeetingActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }else{
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(MeetingActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                    loader.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                    Log.d("insertMeeting"," error "+t.getMessage());
                    commonClass.showError(MeetingActivity.this,t.getMessage());
            }
        });

    }

    private void getList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(MeetingActivity.this)).create(ApiInterface.class);
        Call<MeetingModal> call = apiInterface.getMeetingList();
        Log.d("meetingURL"," url "+call.request().url());
        call.enqueue(new Callback<MeetingModal>() {
            @Override
            public void onResponse(Call<MeetingModal> call, Response<MeetingModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("meetingURL"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if (response.body().getGetData()!=null){
                            Log.d("meetingURL"," size "+response.body().getGetData().size());
                            if(response.body().getGetData().size()!=0){
                                no_data.setVisibility(View.GONE);
                                MeetingAdapter adapter = new MeetingAdapter(MeetingActivity.this,response.body().getData,
                                        recyclerView,loader,getEmployeeList);
                                recyclerView.setAdapter(adapter);
                            }else{
                                no_data.setVisibility(View.VISIBLE);
                            }
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                    }
                }else{
                    no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MeetingModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                Log.d("meetingURL"," error "+t.getMessage());
            }
        });

    }

    private void callReset() {
        select_for_spinner.setSelection(0);
        other_layout.setVisibility(View.GONE);
        edt_other.setText(null);
        edt_meeting.setText(null);
        ed_fromdate.setText(null);
        ed_fromtime.setText(null);
        edt_other.setText(null);
        add_team.setText("Select Members");
        commonClass.putSharedPref(getApplicationContext(),"members",null);
        edt_invite_by_mail.setText(null);
        rdo_online.setChecked(false);
        rdo_direct.setChecked(false);
        location_layout.setVisibility(View.GONE);
        edt_location.setText(null);
        meeting_link_layout.setVisibility(View.GONE);
        edt_meeting_link.setText(null);
        callDateTimeSetMethod();
        checkBox.setChecked(false);
        check_all_text.setText("Check All");
        commonBoolean=1;
        updateAdapterSetSpinner(getEmployeeList);

    }
}
