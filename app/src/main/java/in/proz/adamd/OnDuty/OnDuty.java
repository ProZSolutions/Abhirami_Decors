package in.proz.adamd.OnDuty;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
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
import android.widget.Toast;

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

import in.proz.adamd.Adapter.OnDutyAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.DModal;
import in.proz.adamd.ModalClass.OnDutyMain;
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

public class OnDuty extends AppCompatActivity implements View.OnClickListener {
    Spinner spinnerLeave;
    ImageView mike;
    int position;
    int first_pos=0;
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
    SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    ImageView back_arrow;
    CommonClass commonClass=new CommonClass();
    MKLoader loader;
    TextView title;
    List<String> onDutyList=new ArrayList<>();
    LinearLayout online_layout;
    ImageView online_icon;
    OnDutyAdapter adapter;
     List<CommonPojo> getGetOnDutyList = new ArrayList<>();
    TextView online_text,header,request_text,no_data;
    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;

    int main_change=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onduty);
        no_data = findViewById(R.id.no_data);
    //    updateBottomNavigation();

        initView();

        Bundle b= getIntent().getExtras();
        if(b!=null){
            position= b.getInt("position");
            Log.d("posonduty","pos "+position);

            updateAdminUI();
        }
    }

    private void updateAdminUI() {
        title.setText("Onduty Request");
        apply_leave_layout.setVisibility(View.VISIBLE);
        frame_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
            if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                frame_layout.setVisibility(View.VISIBLE);
                frame_tag.setText("OnDuty List");
            }
        }
    }

    public void initView(){
        title = findViewById(R.id.title);
        title.setText("Onduty List");
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        request_text=findViewById(R.id.request_text);

        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        mike = findViewById(R.id.mike);
        mike.setOnClickListener(this);
        header=findViewById(R.id.title);
        CommonClass comm = new CommonClass();
        /*online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(OnDuty.this,online_layout,online_text,online_icon);*/


        edt_reason = findViewById(R.id.edt_reason);
        dropDownTable = new DropDownTableOD(OnDuty.this);
        dropDownTable.getWritableDatabase();
        recyclerView = findViewById(R.id.recyclerView);


        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);
        loader = findViewById(R.id.loader);


        adapter = new OnDutyAdapter(OnDuty.this,getGetOnDutyList,recyclerView,loader,position);
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




        getDropDownList();
         if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            updateAdminUI();
        }else {
             getList();
         }
    }
    private void getDropDownList() {
        // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(OnDuty.this)).create(ApiInterface.class);
        Call<DModal> call = apiInterface.getODDropDown();
        Log.d("dropdownlist"," urel as "+call.request().url());
        call.enqueue(new Callback<DModal>() {
            @Override
            public void onResponse(Call<DModal> call, Response<DModal> response) {
                //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("dropdownlist"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("dropdownlist"," response "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){

                            if(response.body().getGetDDropDownModal().size()!=0){
                                for(int i=0;i<response.body().getGetDDropDownModal().size();i++){
                                    CommonPojo commonPojo = response.body().getGetDDropDownModal().get(i);
                                    dropDownTable.insertData(commonPojo.getId(),commonPojo.getName(),"onduty");
                                }
                            }

                            updateList();
                        }else{
                            commonClass.showError(OnDuty.this,response.body().getStatus());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(OnDuty.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DModal> call, Throwable t) {
                // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("dropdownlist"," error"+t.getMessage());
                commonClass.showError(OnDuty.this,t.getMessage());
            }
        });
    }

    public void updateList() {
        onDutyList.clear();
        onDutyList = dropDownTable.getAllNameList("onduty");

        List<String> getList = new ArrayList<>();
        getList.add("On Duty");

         ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,getList);
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
        /*intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(OnDuty.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
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
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    first_pos=1;
                    frame_tag.setText("Apply OnDuty");
                    title.setText("Onduty List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                    apply_leave_layout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                     getList();
                 }else{
                    first_pos=0;
                    resetUI();
                    title.setText("Onduty Request");
                    frame_tag.setText("OnDuty List");
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
                resetUI();
                break;
            case R.id.selete_date:
                callDatePicker();
                break;
        }
    }

    private void resetUI() {
       // spinnerLeave.setSelection(0);
        edt_reason.setText(null);
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
            commonClass.showWarning(OnDuty.this,"Please select type");
        }else*/ if(TextUtils.isEmpty(selete_date.getText().toString())){
            commonClass.showWarning(OnDuty.this,"Please select date");
        }else if(TextUtils.isEmpty(edt_reason.getText().toString())){
            commonClass.showWarning(OnDuty.this,"Please enter reason");
        }else{
           // commonClass.showWarning(OnDuty.this,"suces ");
            callInsertMethod();
        }
    }
    private void callInsertMethod() {

        // progressDialog.show();
        request_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(OnDuty.this,"token"),
                commonClass.getDeviceID(OnDuty.this)).create(ApiInterface.class);
        Call<CommonPojo> call=null ;

        call = apiInterface.insertOnDuty(str_from_date,
                dropDownTable.selectOnlyID(spinnerLeave.getSelectedItem().toString(),"onduty"),edt_reason.getText().toString());


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
                            commonClass.showSuccess(OnDuty.this,response.body().getData());
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
                            commonClass.showError(OnDuty.this,response.body().getData());
                        }
                    }else{
                        Log.d("insertMethod"," res "+response.code());
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(OnDuty.this,mError.getError());
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

                        commonClass.showError(OnDuty.this,mError.getError());
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
                request_layout.setEnabled(true);
                Log.d("claim_url"," on failure error "+t.getMessage());
                commonClass.showError(OnDuty.this,t.getMessage());

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callIntent();
    }

    public void BackUI(){
        frame_tag.setText("Apply OnDuty");
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
                Log.d("posonduty","pos intent "+position);
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
            Intent intent = new Intent(getApplicationContext(), AttendanceActivity.class);
            startActivity(intent);
        }
    }

    public void getList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(OnDuty.this,"token"),
                commonClass.getDeviceID(OnDuty.this)).create(ApiInterface.class);
        Call<OnDutyMain> call = apiInterface.getOnDutyList(String.valueOf(pageNo));
        Log.d("insertMethod"," url "+call.request().url()+" auth "+
                commonClass.getSharedPref(OnDuty.this,"token")+" device  "+commonClass.getDeviceID(OnDuty.this));
        call.enqueue(new Callback<OnDutyMain>() {
            @Override
            public void onResponse(Call<OnDutyMain> call, Response<OnDutyMain> response) {
                loader.setVisibility(View.GONE);
                Log.d("insertMethod"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().contains("success")){
                            if(response.body().getGetOnDutyList()!=null){
                                if(response.body(). getGetOnDutyList().size()!=0){
                                    Log.d("insertMethod"," size as "+response.body(). getGetOnDutyList().size());
                                    if (pageNo == 1) {
                                        getGetOnDutyList.clear();
                                    }
                                    for (int i = 0; i < response.body() .getGetOnDutyList().size(); i++) {
                                        getGetOnDutyList.add(response.body() .getGetOnDutyList().get(i));
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
                                                frame_tag.setText("Apply OnDuty");
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
                                        frame_tag.setText("Apply OnDuty");
                                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                                    }





                                }else{
                                    if (pageNo == 1) {
                                        getGetOnDutyList.clear();
                                        no_data.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            if (pageNo == 1) {
                                getGetOnDutyList.clear();
                                no_data.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                            commonClass.showError(OnDuty.this,response.body().getStatus());

                        }
                    }else{
                        if (pageNo == 1) {
                            getGetOnDutyList.clear();
                            no_data.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(OnDuty.this,mError.getError());
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

                        commonClass.showError(OnDuty.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<OnDutyMain> call, Throwable t) {
                no_data.setVisibility(View.VISIBLE);
                Log.d("insertMethod"," error "+t.getMessage());
                loader.setVisibility(View.GONE);
            }
        });
    }
}
