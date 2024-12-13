package in.proz.adamd.OverTime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.proz.adamd.Adapter.OverTimeAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.EmpDropDown;
import in.proz.adamd.ModalClass.OverTimeMain;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.DropDownTableOD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverTime extends AppCompatActivity implements View.OnClickListener {
    Spinner spinnerLeave;
    CommonPojo commonPojo;
    int first_pos=0;

    ImageView mike;
    String str_time_from,str_time_to;
    int position;
    DropDownTableOD dropDownTable;
    EditText selete_date,edt_reason;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    LinearLayout request_layout,reset_layout,frame_layout,apply_leave_layout,listLayout;
    RecyclerView recyclerView;
    GridLayoutManager manager;
    int pageNo=1;
    ImageView frame_icon;
    TextView frame_tag;
    String str_from_date;
    List<String> dropDownNames=new ArrayList<>();
    List<String> dropDownIds = new ArrayList<>();
    SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    ImageView back_arrow;
    CommonClass commonClass=new CommonClass();
    MKLoader loader;
    List<String> OverTimeList=new ArrayList<>();
    LinearLayout online_layout;
    ImageView online_icon;
    TextView title;
    OverTimeAdapter adapter;
    List<CommonPojo> getGetOverTimeList = new ArrayList<>();
    TextView online_text,header,request_text,no_data;
    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;

    int main_change=0;
    TextView edt_fromtime,edt_totime;
    ImageView totime_picker,fromtime_picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overtime);
        no_data = findViewById(R.id.no_data);
        //    updateBottomNavigation();
        Bundle b1= getIntent().getExtras();
        if(b1!=null){
            commonPojo = (CommonPojo) b1.getSerializable("overtime");

        }

        initView();

        Bundle b= getIntent().getExtras();
        if(b!=null){
            position= b.getInt("position");
            if(position>0){
                updateAdminUI();
            }
            Log.d("posOverTime","pos "+position);

        }
    }
    public String TimeFormatChange(String time){
        String ret_time="";
        if(time.contains("AM")){
            ret_time = time.replace(" AM","");
        }else{
            String temp_time =time.replace(" PM","");
            String[] time_arr = temp_time.split(":");
            int value = Integer.parseInt(time_arr[0])+12;
            ret_time = String.valueOf(value)+":"+time_arr[1];
        }
        return ret_time;
    }

    private void updateUIData() {
        title.setText("Update Overtime");
        edt_reason.setText(commonPojo.getReason());
        selete_date.setText(commonPojo.getDate());
        String[] date_arr = commonPojo.getDate().split("-");
        str_from_date=date_arr[2]+"-"+date_arr[1]+"-"+date_arr[0];
        Log.d("gettingCurr"," update UI");
        edt_fromtime.setText(commonPojo.getFrom());
        str_time_from=TimeFormatChange(commonPojo.getFrom());
        edt_totime.setText(commonPojo.getTo());
        str_time_to = TimeFormatChange(commonPojo.getTo());
        listLayout.setVisibility(View.GONE);
        apply_leave_layout.setVisibility(View.VISIBLE);
        frame_tag.setText("Overtime List");
        request_text.setText("Update");


    }

    private void updateAdminUI() {
        title.setText("Overtime Request");
        apply_leave_layout.setVisibility(View.VISIBLE);
        frame_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
            if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                frame_layout.setVisibility(View.VISIBLE);
                frame_tag.setText("Overtime List");
            }
        }
    }

    public void initView(){
        title = findViewById(R.id.title);
        edt_fromtime = findViewById(R.id.edt_fromtime);
        edt_totime = findViewById(R.id.edt_totime);
        edt_totime.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edt_fromtime.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        fromtime_picker = findViewById(R.id.fromtime_picker);
        totime_picker = findViewById(R.id.totime_picker);

        edt_fromtime.setOnClickListener(this);
        edt_totime.setOnClickListener(this);
        fromtime_picker.setOnClickListener(this);
        totime_picker.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        request_text=findViewById(R.id.request_text);




       /* SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat simpleDateFormatmin = new SimpleDateFormat("mm");
        SimpleDateFormat simpleDateFormatHR = new SimpleDateFormat("HH");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        Date againTime =new Date();
        againTime.setTime(System.currentTimeMillis()+(2*60*60*1000));
        str_time_from= simpleDateFormat.format(currentTime);
        str_time_to = simpleDateFormat.format(againTime);
        edt_fromtime.setText(getTime(str_time_from));
        edt_totime.setText(getTime(str_time_to));*/

        callDateTimeSetMethod();
        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        mike = findViewById(R.id.mike);
        mike.setOnClickListener(this);
        header=findViewById(R.id.title);
        CommonClass comm = new CommonClass();
        /*online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(OverTime.this,online_layout,online_text,online_icon);*/


        edt_reason = findViewById(R.id.edt_reason);
        dropDownTable = new DropDownTableOD(OverTime.this);
        dropDownTable.getWritableDatabase();
        recyclerView = findViewById(R.id.recyclerView);


        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);
        loader = findViewById(R.id.loader);


        adapter = new OverTimeAdapter(OverTime.this,getGetOverTimeList,recyclerView,loader,position);
        recyclerView.setAdapter(adapter);

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
                    getList();
                }
            }
        });


        spinnerLeave = findViewById(R.id.spinnerLeave);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);
        back_arrow= findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        request_layout = findViewById(R.id.request_layout);
        reset_layout = findViewById(R.id.reset_layout);
        request_layout.setOnClickListener(this);
        reset_layout.setOnClickListener(this);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        listLayout = findViewById(R.id.listLayout);
        selete_date = findViewById(R.id.selete_date);
        selete_date.setOnClickListener(this);
        spinnerLeave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(((TextView) spinnerLeave.getSelectedView())!=null) {
                    ((TextView) spinnerLeave.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        str_from_date=sdf.format(new Date());
        selete_date.setText(sdf1.format(new Date()));

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            updateAdminUI();
            // header_title.setText("Admin");
            request_text.setText("Submit");
        }else{
            request_text.setText("Request");
            // header_title.setText("Employee");
        }



        getOverTimeList();
         if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            updateAdminUI();
        }else {
             if(commonPojo!=null){
                 updateUIData();
             }else {
                 getList();
             }
        }
    }
    private String getTime(String string ) {
        String[] arr1=string.split(":");
        int rt=0;
        Integer hr = Integer.parseInt(arr1[0]);
        String str_date;
        if(hr>12){
            hr=hr-12;
            str_date = hr+":"+arr1[1]+" PM";

        }else{
            str_date = hr+":"+arr1[1]+" AM";
        }
        return str_date;
    }

    public void updateAdapter() {


        ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dropDownNames);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerLeave.setAdapter(ad);
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
    private void recordVoiceToText( int i) {
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(OverTime.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private void timePicker(TextView editText, int i) {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(OverTime.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
                        if(i==1){
                            str_time_from =str_fromdate;
                        }else{
                            str_time_to = str_fromdate;
                        }
                        Log.d("getSelecte"," date "+str_time_from+" to "+str_time_to);
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
                        editText.setText(sb);
                        //editText.setText(str_fromdate);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.edt_fromtime:
                timePicker(edt_fromtime,1);
                break;
            case R.id.edt_totime:
                timePicker(edt_totime,2);
                break;
            case R.id.fromtime_picker:
                timePicker(edt_fromtime,1);
                break;
            case R.id.totime_picker:
                timePicker(edt_totime,2);
                break;
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
            case R.id.nprofile_layout:
                Intent intentabout = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout);
                break;
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                startActivity(intent);
                break;
            case R.id.mike:
                recordVoiceToText(1);
                break;
            case R.id.frame_layout:
                commonPojo=null;
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    first_pos=1;
                    frame_tag.setText("Apply Overtime");
                    title.setText("Overtime List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                    apply_leave_layout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    getList();
                }else{
                    first_pos=0;
                    resetUI();
                    title.setText("Overtime Request");
                    frame_tag.setText("Overtime List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                    apply_leave_layout.setVisibility(View.VISIBLE);
                    listLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.back_arrow:
                callIntent();
                break;
            case R.id.request_layout:
                callCheckData();
                break;
            case R.id.reset_layout:
                commonPojo=null;
                resetUI();
                break;
            case R.id.selete_date:
                callDatePicker();
                break;
        }
    }

    private void resetUI() {
        // spinnerLeave.setSelection(0);
        Log.d("gettingCurr"," reset UI ");
        str_time_to=null;
        str_time_from =null;
        edt_totime.setText(null);
        edt_fromtime.setText(null);
        edt_reason.setText(null);
        spinnerLeave.setSelection(0);
        callDateTimeSetMethod();

        //  selete_date.setText(null);
    }

    public void callDatePicker(){
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
                        if((monthOfYear+1)<=9) {
                            str_month = "0" + String.valueOf(monthOfYear + 1);
                        }
                        str_from_date=year + "-" + str_month + "-" + str_date;

                        selete_date.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
        long min = System.currentTimeMillis() -604800000L;
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(min);
        long mill = System.currentTimeMillis() ;
        datePickerDialog.getDatePicker().setMaxDate(mill);
        datePickerDialog.show();

    }
    private void callCheckData() {
       /* if(spinnerLeave.getSelectedItem().toString().contains("Select")){
            commonClass.showWarning(OverTime.this,"Please select type");
        }else*/ if(TextUtils.isEmpty(selete_date.getText().toString())){
            commonClass.showWarning(OverTime.this,"Please select date");
        }else if(TextUtils.isEmpty(edt_fromtime.getText().toString())){
            commonClass.showWarning(OverTime.this,"Please Select From Time");
        }else if(TextUtils.isEmpty(edt_totime.getText().toString())){
            commonClass.showWarning(OverTime.this,"Please Select To Time");
        }else if(TextUtils.isEmpty(edt_reason.getText().toString())){
            commonClass.showWarning(OverTime.this,"Please enter reason");
        }else{
            // commonClass.showWarning(OverTime.this,"suces ");
            callInsertMethod();
        }
    }
    public void getOverTimeList(){
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(OverTime.this,"token"),
                commonClass.getDeviceID(OverTime.this)).create(ApiInterface.class);
        Call<EmpDropDown> call=apiInterface.getDropDown() ;
        call.enqueue(new Callback<EmpDropDown>() {
            @Override
            public void onResponse(Call<EmpDropDown> call, Response<EmpDropDown> response) {
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().contains("success")){
                            if(response.body().getGetData().size()!=0){
                                for(int i=0;i<response.body().getGetData().size();i++){
                                    dropDownIds.add(response.body().getGetData().get(i).getId());
                                    dropDownNames.add(response.body().getGetData().get(i).getName());
                                }
                                updateAdapter();
                            }
                        }else{

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EmpDropDown> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }


    private void callInsertMethod() {

        // progressDialog.show();
        request_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(OverTime.this,"token"),
                commonClass.getDeviceID(OverTime.this)).create(ApiInterface.class);
        Call<CommonPojo> call=null ;

        Log.d("getTimeValues"," start "+str_time_from+" to "+str_time_to);

        if(commonPojo!=null){
            call = apiInterface.updateOverTime(commonPojo.getId(),str_from_date,
                    dropDownIds.get(spinnerLeave.getSelectedItemPosition()),edt_reason.getText().toString(),str_time_from,str_time_to);


        }else{
            call = apiInterface.insertOverTime(str_from_date,
                    dropDownIds.get(spinnerLeave.getSelectedItemPosition()),edt_reason.getText().toString(),str_time_from,str_time_to);


        }

        Log.d("insertMethod"," url "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                if(response.isSuccessful()){
                    Log.d("insertMethod"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(OverTime.this,response.body().getData());
                            resetUI();
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                //updateAdminUI();
                                main_change=1;
                                callIntent();
                            }else {
                                getList();
                            }
                        }else{
                            commonClass.showError(OverTime.this,response.body().getData());
                        }
                    }else{
                        Log.d("insertMethod"," res "+response.code());
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(OverTime.this,mError.getError());
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

                        commonClass.showError(OverTime.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                request_layout.setEnabled(false);
                Log.d("claim_url"," on failure error "+t.getMessage());
                commonClass.showError(OverTime.this,t.getMessage());

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callIntent();
    }

    public void BackUI(){
        frame_tag.setText("Apply OverTime");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
        apply_leave_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
        getList();
    }
    private void callIntent() {
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            if(position!=0 && main_change==1){
                Log.d("posOverTime","pos intent "+position);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                }, 2500);

            }else{
                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent);
            }
        }else{
            if(apply_leave_layout.getVisibility()==View.VISIBLE){
                frame_tag.setText("Apply Overtime");
                title.setText("Overtime List");
                frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                apply_leave_layout.setVisibility(View.GONE);
                listLayout.setVisibility(View.VISIBLE);
                getList();
            }else{
                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent);
            }

        }
    }

    public void getList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(OverTime.this,"token"),
                commonClass.getDeviceID(OverTime.this)).create(ApiInterface.class);
        Call<OverTimeMain> call = apiInterface.getOverTimeList(String.valueOf(pageNo));
        Log.d("insertMethod"," url "+call.request().url());
        call.enqueue(new Callback<OverTimeMain>() {
            @Override
            public void onResponse(Call<OverTimeMain> call, Response<OverTimeMain> response) {
                loader.setVisibility(View.GONE);
                Log.d("insertMethod"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().contains("success")){
                            if(response.body().getRespons()!=null){
                                if(response.body().getRespons().size()!=0){
                                    Log.d("insertMethod"," size as "+response.body().getRespons().size());
                                    if (pageNo == 1) {
                                        getGetOverTimeList.clear();
                                    }
                                    for (int i = 0; i < response.body().getRespons().size(); i++) {
                                        getGetOverTimeList.add(response.body().getRespons().get(i));
                                    }



                                    if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                            !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                            !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                        if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                                            if(first_pos==1){
                                                adapter.notifyDataSetChanged();
                                                no_data.setVisibility(View.GONE);
                                                apply_leave_layout.setVisibility(View.GONE);
                                                listLayout.setVisibility(View.VISIBLE);
                                                frame_tag.setText("Apply OverTime");
                                                frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                                            }else{
                                                updateAdminUI();
                                            }
                                        }else{
                                            updateAdminUI();
                                        }

                                    }else{
                                        adapter.notifyDataSetChanged();
                                        no_data.setVisibility(View.GONE);
                                        apply_leave_layout.setVisibility(View.GONE);
                                        listLayout.setVisibility(View.VISIBLE);
                                        frame_tag.setText("Apply OverTime");
                                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                                    }


                                }else{
                                    if (pageNo == 1) {
                                        getGetOverTimeList.clear();
                                        no_data.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            if (pageNo == 1) {
                                getGetOverTimeList.clear();
                                no_data.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                            commonClass.showError(OverTime.this,response.body().getStatus());

                        }
                    }else{
                        if (pageNo == 1) {
                            getGetOverTimeList.clear();
                            no_data.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(OverTime.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }else{
                    no_data.setVisibility(View.VISIBLE);

                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(OverTime.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<OverTimeMain> call, Throwable t) {
                no_data.setVisibility(View.VISIBLE);

                loader.setVisibility(View.GONE);
            }
        });
    }
    public void timeFormatChange(int hourOfDay,int minute,TextView editText,int i){
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
            str_time_from =str_fromdate;
        }else{
            str_time_to = str_fromdate;
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
        Log.d("gettingCurr"," getting format "+str_fromdate+" sb "+sb);
        if(i==1){
            edt_fromtime.setText(sb);
        }else{
            edt_totime.setText(sb);
        }
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
         str_from_date = simpleDateFormat1.format(currentTime);
        str_time_from= simpleDateFormat.format(currentTime);
        str_time_to = simpleDateFormat.format(againTime);

        Log.d("gettingCurr"," from date "+str_time_from+" to "+str_time_to);
        timeFormatChange(Integer.parseInt(simpleDateFormatHR.format(new Date())),
                Integer.parseInt(simpleDateFormatmin.format(new Date())),edt_fromtime,1);
        timeFormatChange(Integer.parseInt(simpleDateFormatHR.format(againTime)),
                Integer.parseInt(simpleDateFormatmin.format(againTime)),edt_totime,2);

        /*ed_fromtime.setText(str_from_time);
        ed_totime.setText(str_to_time);*/
    }
}
