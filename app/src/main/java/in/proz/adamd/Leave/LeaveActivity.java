package in.proz.adamd.Leave;

import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import id.yuana.chart.pie.PieChartView;
import in.proz.adamd.Adapter.LeaveAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.LeaveBalanceModal;
import in.proz.adamd.ModalClass.LeaveModal;
import in.proz.adamd.ModalClass.RequestDropDownModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.Retrofit.FileUtils;
import in.proz.adamd.SQLiteDB.LeaveDropDown;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    TextView request_text;
    int date_picker_type=0;
    LinearLayout nhome_layout, nprofile_layout ,nreports_layout,nlocation_layout;
    EditText document;
    RelativeLayout picker_layout;
    ImageView pick_icon;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
    String from_date_str,to_date_str;
    MultipartBody.Part body;

   // ProgressDialog progressDialog;
    String sessoin_selection="1";
    LeaveDropDown dropDownTable;
    RelativeLayout bottom_request_layout;
    String imagePathArray, fileTypeArray,str_time_from,str_time_to;


    LinearLayout apply_leave_layout,listLayout,apply_leave_request,reset_leave_request;
    RecyclerView recyclerView;
    CommonClass commonClass=new CommonClass();
     PieChartView pieChart,causal_chart,medical_chart,sick_chart;
     TextView total_leave_text,casual_leave_text,medical_leave_text,sick_leave_text;
    float[] leaveList= new float[2];
    int[] colorList = {R.color.color_primary,R.color.shade_2};
    List<String> leaveTypeListarr,typeListarr,alternativeListarr;
    TextView txt_medical,txt_casual,txt_sick;




    ////// insert leave optioms
    /***  leave_type , alter , type***/

    EditText edt_reason;
    TextView ed_fromdate,edt_todate,edt_fromtime,edt_totime;
    ImageView from_picker,to_picker,fromtime_picker,totime_picker,view_image,back_arrow;
    Spinner leaveTypeSpinner,spinnerLeaveMain,alternativespinner;
    LinearLayout alternativeLayout,linearfromdate,lineartodate,linearfromtime,lineartotime,session_layout ,frame_layout;
 SwitchCompat swOnOff;
    int  PICK_ONE=1;
    private static final int RequestPermissionCode = 2000;
    private static final int PICK_IMAGE = 1000;
     String doc_filepath="";
     Uri imageUri;

    public File file;
    public String FilePath;
    TextView total_count,sick_count,casual_count,medical_count;
    LinearLayout leave_type_layout,pieChart_layout ;
    TextView piechart_text;
    String imageFileName , imageFileExt;
    Bitmap bitmap;
  /*  LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/

     ImageView frame_icon;
    TextView frame_tag,change_layout;
    MKLoader loader;
    ProgressBar medical_bar,sick_bar,casual_bar;
    TextView no_data,header_title;
    int position=-1;
    int main_change=0,first_pos=0;

    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_leave);
        no_data = findViewById(R.id.no_data);
         title = findViewById(R.id.title);
        header_title = findViewById(R.id.header_title);
        title.setText("Leave List");
        Bundle b=getIntent().getExtras();

        initView();
        if (b!=null){
            position=b.getInt("position");
             updateAdminUI();
        }
         
     }

    private void updateAdminUI() {
         title.setText("Leave Request");
         frame_tag.setText("Leave List");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        bottom_request_layout.setVisibility(View.VISIBLE);
        frame_layout.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
            if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                frame_layout.setVisibility(View.VISIBLE);
                frame_tag.setText("Leave List");
             }
        }

    }

    public  void  initView(){
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);

        pick_icon = findViewById(R.id.pick_icon);
        pick_icon.setOnClickListener(this);
        document = findViewById(R.id.document);
        document.setOnClickListener(this);
        picker_layout = findViewById(R.id.picker_layout);
        picker_layout.setOnClickListener(this);
        request_text = findViewById(R.id.request_text);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo"))){
            request_text.setText("Submit");
        }else{
            request_text.setText("Request");
        }

        CommonClass comm = new CommonClass();


        medical_bar = findViewById(R.id.medical_bar);
        sick_bar = findViewById(R.id.sick_bar);
        casual_bar = findViewById(R.id.casual_bar);
        loader = findViewById(R.id.loader);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);
        txt_casual =findViewById(R.id.txt_casual);
        txt_sick =findViewById(R.id.txt_sick);
        txt_medical =findViewById(R.id.txt_medical);
      
         
         piechart_text =findViewById(R.id.piechart_text);
        piechart_text.setOnClickListener(this);
        pieChart_layout=findViewById(R.id.pieChart_layout);
         
        leave_type_layout = findViewById(R.id.leave_type_layout);
        swOnOff = findViewById(R.id.swOnOff);
        total_count= findViewById(R.id.total_count);
        sick_count= findViewById(R.id.sick_count);
        casual_count= findViewById(R.id.casual_count);
        medical_count= findViewById(R.id.medical_count);
        back_arrow= findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        apply_leave_request = findViewById(R.id.request_layout);
        reset_leave_request = findViewById(R.id.reset_layout);
        reset_leave_request.setOnClickListener(this);
        apply_leave_request.setOnClickListener(this);
        leaveTypeListarr = new ArrayList<>();
        alternativeListarr = new ArrayList<>();
        typeListarr = new ArrayList<>();
        dropDownTable = new LeaveDropDown(LeaveActivity.this);
        dropDownTable.getWritableDatabase();
        alternativeLayout = findViewById(R.id.alternativeLayout);
        alternativespinner = findViewById(R.id.alternativespinner);
        leaveTypeSpinner = findViewById(R.id.spinner);
       /* progressDialog = new ProgressDialog(LeaveActivity.this);
        progressDialog.setCancelable(false);*/
        bottom_request_layout = findViewById(R.id.bottom_request_layout);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
       /*  change_layout = findViewById(R.id.change_layout);
        change_layout.setVisibility(View.GONE);*/
        listLayout = findViewById(R.id.listLayout);
        recyclerView= findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager1=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager1);
        pieChart = findViewById(R.id.pieChart);
        causal_chart = findViewById(R.id.causal_chart);
        medical_chart = findViewById(R.id.medical_chart);
        sick_chart = findViewById(R.id.sick_chart);
         total_leave_text= findViewById(R.id.total_leave_text);
        casual_leave_text= findViewById(R.id.casual_leave_text);
        medical_leave_text= findViewById(R.id.medical_leave_text);
        sick_leave_text= findViewById(R.id.sick_leave_text);
        linearfromdate= findViewById(R.id.linearfromdate);
        lineartodate= findViewById(R.id.lineartodate);
        linearfromtime= findViewById(R.id.linearfromtime);
        lineartotime= findViewById(R.id.lineartotime);
        session_layout= findViewById(R.id.session_layout);


        spinnerLeaveMain = findViewById(R.id.spinnerLeave);
        ed_fromdate = findViewById(R.id.ed_fromdate);
        edt_todate = findViewById(R.id.edt_todate);
        edt_fromtime = findViewById(R.id.edt_fromtime);
        edt_totime = findViewById(R.id.edt_totime);
        edt_reason = findViewById(R.id.edt_reason);
         from_picker = findViewById(R.id.from_picker);
        fromtime_picker = findViewById(R.id.fromtime_picker);
        to_picker = findViewById(R.id.to_picker);
        totime_picker = findViewById(R.id.totime_picker);
        view_image = findViewById(R.id.view_image);

        callDateTimeSetMethod();
        ed_fromdate.setOnClickListener(this);
        edt_todate.setOnClickListener(this);
        edt_fromtime.setOnClickListener(this);
        edt_totime.setOnClickListener(this);
         from_picker.setOnClickListener(this);
        to_picker.setOnClickListener(this);
        fromtime_picker.setOnClickListener(this);
        totime_picker.setOnClickListener(this);
        view_image.setOnClickListener(this);
        updateList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        from_date_str = sdf1.format(new Date());
        ed_fromdate.setText(sdf.format(new Date()));

        if(commonClass.isOnline(LeaveActivity.this)){
            getDropDownList();
            getLeaveList();
          //  getList();
        }else{
            commonClass.showInternetWarning(LeaveActivity.this);
        }


        leaveTypeListarr = dropDownTable.getAllNameList("leave_type");
      //  typeListarr = dropDownTable.getAllNameList("type");
      //  alternativeListarr = dropDownTable.getAllNameList("alter");
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,leaveTypeListarr);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        spinnerLeaveMain.setAdapter(ad);


        if(position!=-1){
            spinnerLeaveMain.setSelection(2);
        }
         
 


        spinnerLeaveMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if( ((TextView) spinnerLeaveMain.getSelectedView())!=null) {
                    ((TextView) spinnerLeaveMain.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }
                Log.d("LeaveType"," spinne leave main "+spinnerLeaveMain.getSelectedItem().toString()+" postion "+i);
                resetOnlyContent();
                switch (i){
                    case 1:
                        date_picker_type=0;
                        lineartotime.setVisibility(View.GONE);
                        linearfromtime.setVisibility(View.GONE);
                        linearfromdate.setVisibility(View.VISIBLE);
                       // lineartodate.setVisibility(View.GONE);
                        session_layout.setVisibility(View.VISIBLE);
                        alternativeLayout.setVisibility(View.GONE);
                        leave_type_layout.setVisibility(View.GONE);
                        break;
                    case 2:
                        date_picker_type=2;
                        lineartotime.setVisibility(View.GONE);
                        linearfromtime.setVisibility(View.GONE);
                        linearfromdate.setVisibility(View.VISIBLE);
                        lineartodate.setVisibility(View.VISIBLE);
                        session_layout.setVisibility(View.GONE);
                        alternativeLayout.setVisibility(View.GONE);
                        leave_type_layout.setVisibility(View.GONE);
                        break;
                    case 3:
                        date_picker_type=2;
                        lineartotime.setVisibility(View.VISIBLE);
                        linearfromtime.setVisibility(View.VISIBLE);
                        linearfromdate.setVisibility(View.VISIBLE);
                        //lineartodate.setVisibility(View.GONE);
                        session_layout.setVisibility(View.GONE);
                        alternativeLayout.setVisibility(View.GONE);
                        leave_type_layout.setVisibility(View.GONE);
                        break;
                    case 4:
                        date_picker_type=0;
                        lineartotime.setVisibility(View.GONE);
                        linearfromtime.setVisibility(View.GONE);
                        linearfromdate.setVisibility(View.VISIBLE);
                       // lineartodate.setVisibility(View.VISIBLE);
                        session_layout.setVisibility(View.GONE);
                        alternativeLayout.setVisibility(View.VISIBLE);
                        leave_type_layout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      /*  ArrayAdapter ad1  = new ArrayAdapter(this,R.layout.spinner_drop_down,typeListarr);
        ad1.setDropDownViewResource(R.layout.spinner_drop_down);
        leaveTypeSpinner.setAdapter(ad1);

        ArrayAdapter ad2  = new ArrayAdapter(this,R.layout.spinner_drop_down,alternativeListarr);
        ad2.setDropDownViewResource( R.layout.spinner_drop_down);
        alternativespinner.setAdapter(ad2);*/
       /* leaveTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(((TextView) leaveTypeSpinner.getSelectedView())!=null) {
                    Log.d("LeaveType"," type "+leaveTypeSpinner.getSelectedItem());
                    ((TextView) leaveTypeSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        alternativespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(((TextView) alternativespinner.getSelectedView())!=null) {
                    ((TextView) alternativespinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sessoin_selection="1";
                 }else{
                    sessoin_selection="2";
                 }
            }
        });


        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

      /*  if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            Log.d("getLeave"," admin UI 3");

            updateAdminUI();
         } */

    }

    private void getDropDownList() {
       // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(LeaveActivity.this)).create(ApiInterface.class);
        Call<RequestDropDownModal> call = apiInterface.getRequestDropDown();
        Log.d("dropdownlist"," urel as "+call.request().url());
        call.enqueue(new Callback<RequestDropDownModal>() {
            @Override
            public void onResponse(Call<RequestDropDownModal> call, Response<RequestDropDownModal> response) {
              //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("dropdownlist"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("dropdownlist"," response "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){

                         /*   if(response.body().getModal().getAlterTypeList().size()!=0){
                                for(int i=0;i<response.body().getModal().getAlterTypeList().size();i++){
                                    CommonPojo commonPojo = response.body().getModal().getAlterTypeList().get(i);
                                    dropDownTable.insertData(commonPojo.getId(),commonPojo.getName(),"alter");
                                }
                             }*/
                            if(response.body().getModal().getLeaveTypeList().size()!=0){
                                for(int i=0;i<response.body().getModal().getLeaveTypeList().size();i++){
                                    CommonPojo commonPojo = response.body().getModal().getLeaveTypeList().get(i);
                                    dropDownTable.insertData(commonPojo.getId(),commonPojo.getName(),"leave_type");
                                }
                                dropDownTable.insertData("1","Leave","type");
                            }
                           /* if(response.body().getModal().getTypeList().size()!=0){
                                for(int i=0;i<response.body().getModal().getTypeList().size();i++){
                                    CommonPojo commonPojo = response.body().getModal().getTypeList().get(i);
                                    dropDownTable.insertData(commonPojo.getId(),commonPojo.getName(),"type");
                                }
                            }*/
                            updateList();
                        }else{
                            commonClass.showError(LeaveActivity.this,response.body().getStatus());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LeaveActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestDropDownModal> call, Throwable t) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("dropdownlist"," error"+t.getMessage());
                commonClass.showError(LeaveActivity.this,t.getMessage());
            }
        });
    }

    private void updateList() {
        leaveTypeListarr.clear();
        typeListarr.clear();
        alternativeListarr.clear();
        leaveTypeListarr = dropDownTable.getAllNameList("leave_type");
        typeListarr = dropDownTable.getAllNameList("type");
        alternativeListarr = dropDownTable.getAllNameList("alter");


        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,leaveTypeListarr);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        spinnerLeaveMain.setAdapter(ad);

        ArrayAdapter ad1  = new ArrayAdapter(this,R.layout.spinner_drop_down,typeListarr);
        ad1.setDropDownViewResource( R.layout.spinner_drop_down);
        leaveTypeSpinner.setAdapter(ad1);

        ArrayAdapter ad2  = new ArrayAdapter(this,R.layout.spinner_drop_down,alternativeListarr);
        ad2.setDropDownViewResource( R.layout.spinner_drop_down);
        alternativespinner.setAdapter(ad2);
    }

    public void getLeaveList() {
       // progressDialog.show();
        txt_sick.setVisibility(View.GONE);
        txt_casual.setVisibility(View.GONE);
        txt_medical.setVisibility(View.GONE);
        casual_bar.setVisibility(View.VISIBLE);
        medical_bar.setVisibility(View.VISIBLE);
        sick_bar.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(LeaveActivity.this)).create(ApiInterface.class);
        Call<LeaveBalanceModal> call = apiInterface.getLeaveBalance();
        call.enqueue(new Callback<LeaveBalanceModal>() {
            @Override
            public void onResponse(Call<LeaveBalanceModal> call, Response<LeaveBalanceModal> response) {
            //    progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                txt_sick.setVisibility(View.VISIBLE);
                txt_casual.setVisibility(View.VISIBLE);
                txt_medical.setVisibility(View.VISIBLE);
                casual_bar.setVisibility(View.GONE);
                medical_bar.setVisibility(View.GONE);
                sick_bar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){

                            txt_casual.setText(response.body().getData().getCasual_bal_leave());
                            txt_medical.setText(response.body().getData().getMedical_bal_leave());
                            txt_sick.setText(response.body().getData().getSick_bal_leave());

                            float  appliedLeaveDays=0;
                            if(!TextUtils.isEmpty(response.body().getData().getCasual_leave())){
                                leaveList[0]=Float.valueOf(response.body().getData().getCasual_leave());
                                leaveList[1]=Float.valueOf(response.body().getData().getCasual_bal_leave());
                                causal_chart.setDataPoints(leaveList);
                                causal_chart.setCenterColor(R.color.white);
                                causal_chart.setSliceColor(colorList);
                                String text = response.body().getData().getCasual_bal_leave()+"/"+
                                        response.body().getData().getCasual_leave();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    casual_count.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    casual_count.setText(Html.fromHtml(text));
                                }
                             //   appliedLeaveDays+=Float.valueOf(response.body().getData().getCasual_leave());
                            }
                            if(!TextUtils.isEmpty(response.body().getData().getMedical_leave())){
                                leaveList[0]=Float.valueOf(response.body().getData().getMedical_leave());
                                leaveList[1]=Float.valueOf(response.body().getData().getMedical_bal_leave());
                                medical_chart.setDataPoints(leaveList);
                                 medical_chart.setCenterColor(R.color.white);
                                medical_chart.setSliceColor(colorList);
                                String text = response.body().getData().getMedical_bal_leave()+"/"+
                                        response.body().getData().getMedical_leave();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    medical_count.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    medical_count.setText(Html.fromHtml(text));
                                }
                                //appliedLeaveDays+=Float.valueOf(response.body().getData().getMedical_leave());
                            }
                            if(!TextUtils.isEmpty(response.body().getData().getSick_leave())){
                                leaveList[0]=Float.valueOf(response.body().getData().getSick_leave());
                                leaveList[1]=Float.valueOf(response.body().getData().getSick_bal_leave());
                                sick_chart.setDataPoints(leaveList);
                                sick_chart.setCenterColor(R.color.white);
                                sick_chart.setSliceColor(colorList);
                                String text = response.body().getData().getSick_bal_leave()+"/"+
                                        response.body().getData().getSick_leave();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    sick_count.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    sick_count.setText(Html.fromHtml(text));
                                }
                              //  appliedLeaveDays+=Float.valueOf(response.body().getData().getSick_leave());
                            }
                            leaveList[0]=Float.valueOf(response.body().getData().getTotal_bal_leave());
                            leaveList[1]=Float.valueOf(response.body().getData().getTotal_leave());
                            pieChart.setDataPoints(leaveList);
                              pieChart.setCenterColor(R.color.white);
                            pieChart.setSliceColor(colorList);
                            String text =  response.body().getData().getTotal_bal_leave()+"/"+response.body().getData().getTotal_leave() ;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                total_count.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                total_count.setText(Html.fromHtml(text));
                            }

                        }else{
                            commonClass.showError(LeaveActivity.this,response.body().getStatus());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LeaveActivity.this,mError.getError());
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

                        commonClass.showError(LeaveActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaveBalanceModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                txt_sick.setVisibility(View.VISIBLE);
                txt_casual.setVisibility(View.VISIBLE);
                txt_medical.setVisibility(View.VISIBLE);
                casual_bar.setVisibility(View.GONE);
                medical_bar.setVisibility(View.GONE);
                sick_bar.setVisibility(View.GONE);
                commonClass.showError(LeaveActivity.this,t.getMessage());
            }
        });
    }

    private void setupPieChart(PieChart chart) {
             chart.setDrawHoleEnabled(true);
            chart.setUsePercentValues(true);
            chart.setEntryLabelTextSize(18);
            chart.setEntryLabelColor(Color.BLACK);
            chart.getDescription().setEnabled(false);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setTextSize(18f);
            l.setDrawInside(false);
            l.setEnabled(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callDashboard();
    }
    public void callAlertPicker(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.new_file_picker,null);
        ImageView close = view.findViewById(R.id.close);
        LinearLayout take_photo = view.findViewById(R.id.take_photo);
        LinearLayout choose_image = view.findViewById(R.id.choose_image);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                callImageMethod();
            }
        });
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                callPermissionInticative(PICK_ONE);
            }
        });
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
            case R.id.picker_layout:
                callAlertPicker();
                break;
            case R.id.document:
                callAlertPicker();
                break;
            case R.id.pick_icon:
                callAlertPicker();
                break;
          /*  case R.id.change_layout:
                Intent intent123 = new Intent(getApplicationContext(), OnDuty.class);
                startActivity(intent123);
                break;*/
            case R.id.piechart_text:
                if(pieChart_layout.getVisibility()==View.VISIBLE){
                    pieChart_layout.setVisibility(View.GONE);
                    piechart_text.setText("View Piechart");
                }else{
                    pieChart_layout.setVisibility(View.VISIBLE);
                    piechart_text.setText("Hide Piechart");
                }
                break;
           
            
          
            case R.id.back_arrow:
                callDashboard();
                    break;
            case R.id.request_layout:
                checkData();
                break;
            case R.id.reset_layout:
                callResetFunction();
                break;
            case R.id.frame_layout:
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    first_pos =1;
                    frame_tag.setText("Apply Leave");
                    title.setText("Leave List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                     apply_leave_layout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    bottom_request_layout.setVisibility(View.GONE);
                    getList();
                    getLeaveList();
                }else{
                    first_pos =0;
                    title.setText("Leave Request");
                    frame_tag.setText("Leave List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                     apply_leave_layout.setVisibility(View.VISIBLE);
                    listLayout.setVisibility(View.GONE);
                    bottom_request_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.view_image:
                break;
             
            case R.id.ed_fromdate:
                datePicker(ed_fromdate,1);
                break;
            case R.id.edt_todate:
                datePicker(edt_todate,2);
                break;
            case R.id.edt_fromtime:
                timePicker(edt_fromtime,2);
                break;
            case R.id.edt_totime:
                timePicker(edt_totime,2);
                break;
            case R.id.from_picker:
                datePicker(ed_fromdate,1);
                break;
            case R.id.fromtime_picker:
                timePicker(edt_fromtime,1);
                break;
            case R.id.to_picker:
                datePicker(edt_todate,2);
                break;
            case R.id.totime_picker:
                timePicker(edt_totime,2);
                break;
        }
    }

    private void timePicker(TextView editText, int i) {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(LeaveActivity.this,
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
                        if(i==1){
                            str_time_from =str_fromdate;
                            str_time_from =convertTo24HourFormat(sb.toString());
                        }else{
                            str_time_to = str_fromdate;
                            str_time_to = convertTo24HourFormat(sb.toString());
                        }



                        Log.d("getSelecte"," original "+hourOfDay+"  "+mHour+"  mon "+min+" mini "+minute+
                                "  hr "+mHour+" final "+convertTo24HourFormat(sb.toString()));
                        Log.d("getSelecte"," date "+str_time_from+" to "+str_time_to+" hr  "+
                                hourOfDay+" min   "+min);

                        //editText.setText(str_fromdate);
                    }
                }, mHour, mMinute, false);
          timePickerDialog.show();

    }
    public static String convertTo24HourFormat(String time12Hour) {
        try {
            // Define a SimpleDateFormat to parse the 12-hour AM/PM time
            SimpleDateFormat sdf12Hour = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

            // Parse the input time string to a Date object
            Date date = sdf12Hour.parse(time12Hour);

            // Define another SimpleDateFormat to format it into 24-hour format
            SimpleDateFormat sdf24Hour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            // Format the Date object into a 24-hour formatted string
            return sdf24Hour.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public  void  callImageMethod(){
        if (Build.VERSION.SDK_INT >= 30) {
            pickImage2(PICK_IMAGE);
        }else {
            if (checkPermission()) {
                pickImage2(PICK_IMAGE);
            } else {
                requestPermission();
            }
        }
    }

    private void pickImage2(int i) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Fromthe Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camintent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(camintent,PICK_IMAGE);
    }
    private void requestPermission() {

        final String[] permissions = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(LeaveActivity.this,permissions, RequestPermissionCode);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LeaveActivity.this,
                READ_EXTERNAL_STORAGE);

        int check_write = ContextCompat.checkSelfPermission(LeaveActivity.this,
                WRITE_EXTERNAL_STORAGE);
        int check_write1 = ContextCompat.checkSelfPermission(LeaveActivity.this,
                MANAGE_DOCUMENTS);
        int check_write2 = ContextCompat.checkSelfPermission(LeaveActivity.this,
                MANAGE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && check_write == PackageManager.PERMISSION_GRANTED
                && check_write1 == PackageManager.PERMISSION_GRANTED && check_write2 == PackageManager.PERMISSION_GRANTED;
    }

    private void callResetFunction() {
        spinnerLeaveMain.setSelection(0);
        leaveTypeSpinner.setSelection(0);
        alternativespinner.setSelection(0);
        edt_fromtime.setText(null);
        edt_totime.setText(null);
        edt_todate.setText(null);
        ed_fromdate.setText("");
        edt_reason.setText(null);
        document.setText("Choose Document");
        linearfromdate.setVisibility(View.GONE);
        lineartodate.setVisibility(View.GONE);
        linearfromtime.setVisibility(View.GONE);
        lineartotime.setVisibility(View.GONE);
        session_layout.setVisibility(View.GONE);
        alternativeLayout.setVisibility(View.GONE);
        body=null;
         view_image.setImageDrawable(null);
        view_image.setVisibility(View.GONE);

    }
    public void resetOnlyContent(){
        edt_fromtime.setText(null);
        edt_totime.setText(null);
        edt_todate.setText(null);
        ed_fromdate.setText("");
        edt_reason.setText(null);
        document.setText("Choose Image");

        body=null;
         view_image.setImageDrawable(null);
        view_image.setVisibility(View.GONE);
        callDateTimeSetMethod();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        from_date_str = sdf1.format(new Date());
        ed_fromdate.setText(sdf.format(new Date()));
        to_date_str = sdf1.format(new Date());
        edt_todate.setText(sdf.format(new Date()));
      }
    private void callCustom1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imageFileName =commonClass.getFileNameFromUri(LeaveActivity.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(LeaveActivity.this,uri);


                        Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            view_image.setImageBitmap(bitmap);
                            view_image.setVisibility(View.VISIBLE);
                            document.setText(imageFileName);
                            if(bitmap!=null) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,10,byteArrayOutputStream);
                                byte[] bytes = byteArrayOutputStream.toByteArray();
                                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
                                body = MultipartBody.Part.createFormData("file", imageFileName, requestBody);

                            }
                        }catch (IOException e){

                        }
                    }
                }
            });
    private void callPermissionInticative(int img_request) {
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Log.d("feedback_request"," if con 1 ");
                requestPermission(img_request,0);
            }else{
                Log.d("feedback_request","if con 2");
                //pickImage1(img_request);
                callCustom1();



            }
        }else{
            if (checkPermission()) {
                Log.d("feedback_request","if con 3");
               // pickImage1(img_request);
                callCustom1();


            } else {
                Log.d("feedback_request","if con 4");
                requestPermission(img_request,1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkData() {
        if(spinnerLeaveMain.getSelectedItemPosition()==0){
            commonClass.showWarning(LeaveActivity.this,"Please Select Leave Type");
        }else{
            if(spinnerLeaveMain.getSelectedItemPosition()==1){
                //half day
              //  leave_type_layout.setVisibility(View.VISIBLE);

                    if(sessoin_selection.equals("0")){
                        commonClass.showWarning(LeaveActivity.this,"Please Select Session");
                    }else if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select Date ");
                    }else if(TextUtils.isEmpty(edt_reason.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Enter Reason");
                    }else{
                        // file selection TextUtils.isEmpty(document.getText().toString())
                      /*  if(body==null){
                            commonClass.showAppToast(LeaveActivity.this,"Please select Document / Image");
                        }else{*/
                            callInsertMethod();
                        //}
                    }

            }
            else if(spinnerLeaveMain.getSelectedItemPosition()==2){
                //full day
                //leave_type_layout.setVisibility(View.VISIBLE);

                    if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select From Date");
                    }/*else if(TextUtils.isEmpty(edt_todate.getText().toString())){
                        commonClass.showAppToast(LeaveActivity.this,"Please Select To Date");
                    }*/else if(TextUtils.isEmpty(edt_reason.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Enter Reason");
                    }else{
                        // file selection
                      /*  if(body==null){
                            commonClass.showAppToast(LeaveActivity.this,"Please select Document/Image");
                        }else{*/
                            callInsertMethod();
                        //}
                    }
                }


            else if(spinnerLeaveMain.getSelectedItemPosition()==3){
              //  leave_type_layout.setVisibility(View.GONE);
                //permission
                long hrs_diff=0,mins_diff=0;

                /*if(!TextUtils.isEmpty(edt_fromtime.getText().toString()) && !TextUtils.isEmpty(edt_totime.getText().toString())){


                    Log.d("getTimeList"," from hrr "+departHRMin(edt_fromtime.getText().toString(),0)+" sec "+
                            departHRMin(edt_fromtime.getText().toString(),1)+" to "+departHRMin(edt_totime.getText().toString(),0)
                    +" "+departHRMin(edt_totime.getText().toString(),0));
                    LocalTime startTime = LocalTime.of(departHRMin(edt_fromtime.getText().toString(),0), departHRMin(edt_fromtime.getText().toString(),1)); // 8:30 AM
                    LocalTime endTime = LocalTime.of(departHRMin(edt_totime.getText().toString(),0), departHRMin(edt_totime.getText().toString(),1));  // 2:45 PM
                    Duration duration = Duration.between(startTime, endTime);

                    // Get the difference in hours
                    hrs_diff = duration.toHours();
                    mins_diff= duration.toMinutes()%60;
                    Log.d("getTimeList"," hrs diss "+hrs_diff+"   "+mins_diff);
                }*/

                    if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select From Date");
                    }else if(TextUtils.isEmpty(edt_fromtime.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select From Time");
                    }else if(TextUtils.isEmpty(edt_totime.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select To Time");
                    }/*else if((hrs_diff>2)){
                        commonClass.showWarning(LeaveActivity.this,"Permission can apply only for 2 hrs");
                    }else if((hrs_diff==2 && mins_diff!=0)){
                        commonClass.showWarning(LeaveActivity.this,"Permission can apply only for 2 hrs");
                    }*/else if(TextUtils.isEmpty(edt_reason.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Enter Reason");
                    }else{
                         callInsertMethod();
                    }

            }
            else if(spinnerLeaveMain.getSelectedItemPosition()==4){
                //alternative
               /* if(leaveTypeSpinner.getSelectedItemPosition()==0){
                    commonClass.showAppToast(LeaveActivity.this,"Please Select Type");
                }else{*/
                    if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Select From Date");
                    }/*else if(TextUtils.isEmpty(edt_todate.getText().toString())){
                        commonClass.showAppToast(LeaveActivity.this,"Please Select To Date");
                    }*/else if(TextUtils.isEmpty(edt_reason.getText().toString())){
                        commonClass.showWarning(LeaveActivity.this,"Please Enter Reason");
                    }else{
                        if(alternativespinner.getSelectedItemPosition()==0){
                            commonClass.showWarning(LeaveActivity.this,"Please Select Alternative Type");
                        }else{
                            //file selection
                           /* if(body==null){
                                commonClass.showAppToast(LeaveActivity.this,"Please select Document/Image");
                            }else{*/
                                callInsertMethod();
                            //}
                        }
                     }
                //}
            }
        }
    }

    private int departHRMin(String string, int i) {
        String[] arr = string.split(" ");
        String[] arr1=arr[0].split(":");
        int rt=0;
        if(i==0){
            // hr
            if(arr[1].equals("PM")){
                rt=Integer.parseInt(arr1[0])+12;
            }else{
                rt=Integer.parseInt(arr1[0]);
            }
        }else{
            //min
            rt=Integer.parseInt(arr1[1]);
        }
        return rt;
    }

    private void callInsertMethod() {

       // progressDialog.show();
        apply_leave_request.setEnabled(false);
         loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(LeaveActivity.this,"token"),
                commonClass.getDeviceID(LeaveActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call=null ;
        Log.d("claim_url"," body value "+body);

        if(body!=null){
            call= apiInterface.insertLeaveRequest(String.valueOf(leaveTypeSpinner.getSelectedItemPosition()),
                    String.valueOf(spinnerLeaveMain.getSelectedItemPosition()),
                    sessoin_selection,edt_reason.getText().toString(),body,from_date_str,to_date_str,
                    str_time_from,str_time_to,String.valueOf(alternativespinner.getSelectedItemPosition()));
        }else{
            call=  apiInterface.insertLeaveRequestWithoutMultipart(String.valueOf(leaveTypeSpinner.getSelectedItemPosition()),
                    String.valueOf(spinnerLeaveMain.getSelectedItemPosition()),
                    sessoin_selection,edt_reason.getText().toString(),from_date_str,to_date_str,
                    str_time_from,str_time_to,String.valueOf(alternativespinner.getSelectedItemPosition()));
        }
        Log.d("claim_url"," url "+call.request().url()+" from time "+str_time_from+"  to time "+
                str_time_to+" from date "+from_date_str+" to date "+to_date_str);

        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
              //  progressDialog.dismiss();
                apply_leave_request.setEnabled(true);
                loader.setVisibility(View.GONE);
                Log.d("claim_url"," res[onse "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(LeaveActivity.this,response.body().getData());
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                main_change=1;
                                callDashboard();
                            }else{
                                callResetFunction();
                                getList();
                                getLeaveList();
                            }

                        }else{
                            commonClass.showError(LeaveActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LeaveActivity.this,mError.getError());
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

                        commonClass.showError(LeaveActivity.this,mError.getError());
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
                apply_leave_request.setEnabled(true);
                Log.d("claim_url"," on failure error "+t.getMessage());
                commonClass.showError(LeaveActivity.this,t.getMessage());

            }
        });



    }

    private void getList() {
      //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(LeaveActivity.this,"token"),
                commonClass.getDeviceID(LeaveActivity.this)).create(ApiInterface.class);
        Call<LeaveModal> call =apiInterface.getLeaveList();
        Log.d("leave_url"," url as "+call.request().url()
        +" token "+commonClass.getSharedPref(getApplicationContext(),"token")+" device "+
                commonClass.getDeviceID(getApplicationContext()));
        call.enqueue(new Callback<LeaveModal>() {
            @Override
            public void onResponse(Call<LeaveModal> call, Response<LeaveModal> response) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("leave_url"," res[onse "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                         if(response.body().getStatus().equals("success")){
                             if(response.body().getDataLeaveList().size()!=0){
                                 no_data.setVisibility(View.GONE);

                                 if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                         !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                         !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){

                                     if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                                         if(first_pos==1){
                                             apply_leave_layout.setVisibility(View.GONE);
                                             listLayout.setVisibility(View.VISIBLE);
                                             frame_tag.setText("Apply Leave");
                                             frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                                             LeaveAdapter adapter =new LeaveAdapter(LeaveActivity.this,
                                                     response.body().getDataLeaveList(),recyclerView,loader);
                                             recyclerView.setAdapter(adapter);
                                         }else{
                                             updateAdminUI();
                                         }
                                     }else{
                                         updateAdminUI();
                                     }


                                 }else{
                                     apply_leave_layout.setVisibility(View.GONE);
                                     listLayout.setVisibility(View.VISIBLE);
                                     frame_tag.setText("Apply Leave");
                                     frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                                     LeaveAdapter adapter =new LeaveAdapter(LeaveActivity.this,
                                             response.body().getDataLeaveList(),recyclerView,loader);
                                     recyclerView.setAdapter(adapter);
                                 }



                             }else{
                                 no_data.setVisibility(View.VISIBLE);
                             }
                         }else{
                             commonClass.showError(LeaveActivity.this,response.body().getStatus());
                         }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LeaveActivity.this,mError.getError());
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
                        Log.d("thumbnail_url"," error "+mError.getError());
                        commonClass.showError(LeaveActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaveModal> call, Throwable t) {
                no_data.setVisibility(View.VISIBLE);
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("claim_url"," on failure error "+t.getMessage());
                commonClass.showError(LeaveActivity.this,t.getMessage());
            }
        });
    }

    public class UploadService {

        private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

        public void uploadImage(File file, String filename) throws IOException {

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = df.format(c);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();




            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE_PNG, file))
                    .addFormDataPart("type", String.valueOf(leaveTypeSpinner.getSelectedItemPosition()))
                    .addFormDataPart("leave_type", String.valueOf(spinnerLeaveMain.getSelectedItemPosition()))
                    .addFormDataPart("alt_type", String.valueOf(alternativespinner.getSelectedItemPosition()))
                    .addFormDataPart("from_time", edt_fromtime.getText().toString())
                    .addFormDataPart("to_time", edt_totime.getText().toString())
                    .addFormDataPart("from_date", ed_fromdate.getText().toString())
                    .addFormDataPart("to_date", edt_todate.getText().toString())
                    .addFormDataPart("from", ed_fromdate.getText().toString())
                    .addFormDataPart("to", edt_todate.getText().toString())
                    .addFormDataPart("reason", edt_reason.getText().toString())
                    .addFormDataPart("date", formattedDate)
                    .addFormDataPart("session", sessoin_selection)
                    .addFormDataPart("Authorization", commonClass.getSharedPref(getApplicationContext(),"token"))
                    .addFormDataPart("Device-ID",commonClass.getDeviceID(LeaveActivity.this))
                    .build();

            Request request;
          /*  if (!TextUtils.isEmpty(edit_id)) {
                request = new Request.Builder().url(commonClass.commonURL()+commonClass.s_url_leave_Update + edit_id)
                        .post(requestBody).build();
            } else {*/
                request = new Request.Builder().url(commonClass.commonURL()+commonClass.s_url_leave_Create)
                        .post(requestBody).build();
           // }
            /*request = new Request.Builder().url(commonClass.commonURL()+commonClass.s_url_leave_Create)
                    .post(requestBody).build();*/

            okhttp3.Call call = client.newCall(request);
          //  progressDialog.show();
            loader.setVisibility(View.VISIBLE);
             call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    String result = e.getLocalizedMessage();
                    System.out.println("Error from server " + result);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loader.setVisibility(View.GONE);
                            //progressDialog.dismiss();
                             Log.d("inline_error"," err "+result);
                             commonClass.showError(LeaveActivity.this,result);
                         }
                    });
                }

                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        System.out.println("Output from server " + result);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                               // progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    commonClass.showSuccess(LeaveActivity.this,obj.getString("status"));
                                    if (obj.getString("status").toLowerCase().equals("success")) {
                                         callResetFunction();
                                         updateCustomUI();
                                    }

                                } catch (Exception ex) {
                                    Log.d("inline_error"," ma "+ex.getMessage());
                                    //progressDialog.dismiss();
                                    loader.setVisibility(View.GONE);
                                    commonClass.showError(LeaveActivity.this,ex.getMessage());
                                 }

                            }


                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run()
                            {
                                loader.setVisibility(View.GONE);
                                //progressDialog.dismiss();
                            }
                        });
                        System.out.println("Output from server " + response.code());
                    }

                }
            });
        }

        //            .addFormDataPart("files", "", RequestBody.create(MEDIA_TYPE_PNG, ""))



    }
    private void updateCustomUI() {
        frame_tag.setText("Apply Leave");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
        bottom_request_layout.setVerticalGravity(View.GONE);
        apply_leave_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
        bottom_request_layout.setVisibility(View.GONE);
       getList();
        getLeaveList();
    }
    public void datePicker(TextView editText,int status) {

        Log.d("LeaveType"," leave spin pos "+leaveTypeSpinner.getSelectedItemPosition()+" type "+
                leaveTypeSpinner.getSelectedItem().toString()+" date pickert "+
                date_picker_type+"  spin type "+spinnerLeaveMain.getSelectedItem().toString()+" pos "+
                spinnerLeaveMain.getSelectedItemPosition());
        if (date_picker_type == 1){
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

            Calendar startDate1 = Calendar.getInstance();
            startDate1.add(Calendar.MONTH, 0); // Go back one month
            startDate1.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of that month
            startDate1.set(Calendar.HOUR_OF_DAY, 0); // Set time to midnight
            startDate1.set(Calendar.MINUTE, 0);
            startDate1.set(Calendar.SECOND, 0);
            startDate1.set(Calendar.MILLISECOND, 0);



            Calendar startDate2 = Calendar.getInstance();
            startDate2.add(Calendar.MONTH, 2); // Go forward two months
            startDate2.set(Calendar.DAY_OF_MONTH, 1) ;// Set to the first day of that month
            startDate2.add(Calendar.DAY_OF_MONTH, -1); // Go back one day to the last day of the previous month
            startDate2.set(Calendar.HOUR_OF_DAY, 23); // Set time to the end of the day
            startDate2.set(Calendar.MINUTE, 59);
            startDate2.set(Calendar.SECOND, 59);
            startDate2.set(Calendar.MILLISECOND, 999);


            long startMillis = startDate1.getTimeInMillis();
            long endMillis = startDate2.getTimeInMillis();
            constraintsBuilder.setStart(startMillis); // Set start date
            constraintsBuilder.setEnd(endMillis);




            builder.setCalendarConstraints(constraintsBuilder.build());
         // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            // Retrieving the selected start and end dates
            Long startDate = selection.first;
            Long endDate = selection.second;


            // Formating the selected dates as strings
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));
            from_date_str = sdf1.format(new Date(startDate));
            to_date_str = sdf1.format(new Date(endDate));

            // Creating the date range string
            String selectedDateRange = startDateString + " TO " + endDateString;

            // Displaying the selected date range in the TextView
            ed_fromdate.setText(selectedDateRange);
        });

        // Showing the date picker dialog
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
        else {
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
                        Log.d("date_picker_type","  "+date_picker_type);


                        String str_date=String.valueOf(dayOfMonth);
                        String str_month=String.valueOf(monthOfYear+1);
                        if(dayOfMonth<=9){
                            str_date="0"+String.valueOf(dayOfMonth);
                        }
                        if((monthOfYear+1)<=9){
                            str_month="0"+String.valueOf(monthOfYear+1);
                        }
                        if(status==1){
                            from_date_str =year + "-" + str_month + "-" + str_date;
                        }else {
                            to_date_str =year + "-" + str_month + "-" + str_date;
                        }

                        editText.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
            if(date_picker_type==2){
                 datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                long mill = System.currentTimeMillis() + 604800000L;
                datePickerDialog.getDatePicker().setMaxDate(mill);
            }else if(date_picker_type==0){
           if(leaveTypeSpinner.getSelectedItemPosition()==1 ||
                leaveTypeSpinner.getSelectedItemPosition()==2){

                       if(leaveTypeSpinner.getSelectedItemPosition()==1){
                          // long min = System.currentTimeMillis() -604800000L;
                           datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                           //datePickerDialog.getDatePicker().setMinDate(min);
                          /* long mill = System.currentTimeMillis() + (604800000L*16) ;
                           datePickerDialog.getDatePicker().setMaxDate(mill);*/
                       }else{
                           long min = System.currentTimeMillis() -604800000L;
                           //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                           datePickerDialog.getDatePicker().setMinDate(min);
                        //   long mill = System.currentTimeMillis() + (604800000L*16) ;
                           long mill = System.currentTimeMillis() + (604800000L) ;
                           datePickerDialog.getDatePicker().setMaxDate(mill);
                       }


                }else{
                    long min = System.currentTimeMillis() -(604800000L);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.getDatePicker().setMinDate(min);
                   // long mill = System.currentTimeMillis() + 604800000L;
                    long mill = System.currentTimeMillis() + (604800000L);
                    datePickerDialog.getDatePicker().setMaxDate(mill);
                }

            }
        datePickerDialog.show();
        }
    }

     private void callDashboard() {

        Log.d("getPosition"," as "+position);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                if(position>=0 && main_change==1){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                            intent.putExtra("position",0);
                            startActivity(intent);
                       }
                    }, 2500);

                }else{
                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    startActivity(intent);
                }
        }else{
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }

    }
    private void pickImage1(int i) {
        Log.d("feedback_request"," pick image request "+i);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  // set all MIME types

        startActivityForResult(intent, PICK_ONE);
    }
    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(LeaveActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_DOCUMENTS,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                        Log.d("feedback_request","permission granded ");
                        if(report.areAllPermissionsGranted()){
                            pickImage1(requestCode);

                        }else{
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if(!Environment.isExternalStorageManager()){

                            }else{

                            }
                        } */
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                             PermissionToken token) {
                        Log.d("feedback_request"," permission not granded ");
                        /* ... */}
                }).check();
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("insp_details"," request code "+requestCode+" res "+resultCode+"  "+PICK_ONE+"  ok "+RESULT_OK);
        if (requestCode == PICK_ONE ) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("insp_details"," data valuew as  "+data.getData());
                    document.setText("Capture Image");
                     view_image.setVisibility(View.GONE);
                    Uri uri = data.getData();
                    File file = new File(getPathFromUri(uri));
                    Log.d("getURIError"," file "+file);
                    String mimeType = this.getContentResolver().getType(uri);
                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                    body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    if(data.getData()!=null){
                        String filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);


                        document.setText(filename);


                    }
                    Log.d("getURIError"," uri "+uri+" mime "+mimeType+" bosy  "+body);
                }

            }
        }else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (imageUri != null) {
                    document.setText("Choose File");
                    view_image.setImageURI(imageUri);
                    view_image.setVisibility(View.VISIBLE);

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        Log.d("thumbnail_url", " image strm " + imageStream);
                        final Bitmap selectedImagebit = BitmapFactory.decodeStream(imageStream);
                        Log.d("thumbnail_url", "bitmao " + selectedImagebit);
                        Bitmap bitmap = Bitmap.createScaledBitmap(selectedImagebit, 10, 10, true);
                        imagePathArray = FileUtils.getRealPath(this, imageUri);
                        Log.d("thumbnail_url", " image pth " + imagePathArray);
                        if (!TextUtils.isEmpty(imagePathArray)) {
                            try {
                                Bitmap bmp = BitmapFactory.decodeFile(imagePathArray);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                byte[] bt = bos.toByteArray();
                                String encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
                                Log.d("thumbnail_url", " encoded string as " + encodeString);
                            } catch (Exception e) {
                                Log.d("thumbnail_url", " error encode " + e.getMessage());
                                e.printStackTrace();
                            }
                            String path = FileUtils.compressImage(this, imagePathArray);
                            File mFile = new File(path);
                            document.setText(mFile.getName());
                            // txt_capturefile.setText(imageName);
                            fileTypeArray = getFileType(imagePathArray);
                            body=callMultiPartMethod(imagePathArray, fileTypeArray, "file");
//                        FilePath = callMultiPartMethod(imagePathArray,fileTypeArray);
                            /* Log.d("thumbnail_url"," image "+imagePathArray+"  file "+fileTypeArray+*/
                            /*         " main multipart "+callMultiPartMethod(imagePathArray,fileTypeArray));*/
                        }

                    } catch (FileNotFoundException e) {
                        Log.d("thumbnail_url", " error " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }


    }
    private String getFileType(String imgpath) {
        if (!TextUtils.isEmpty(imgpath)) {
            return getContentResolver().getType(Uri.fromFile(new File(imgpath)));
        }
        return "";
    }
    private MultipartBody.Part callMultiPartMethod(String imagePathArray, String fileTypeArray,String photo_name) {
        MultipartBody.Part part = getMultiPart(imagePathArray, fileTypeArray,photo_name);
        Log.d("thumbnail_url"," multi part "+part);
        return part;
        //    RequestBody size = createPartFromString(""+parts.size());

    }
    public static String getMimeTypeNew(String url) {
        //String someFilepath = ;
        String type = null;
        String extension = url.substring(url.lastIndexOf("."));

        //type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("extension_inf", extension);
        return extension;


    }
    private MultipartBody.Part getMultiPart(String path, String fileType,String photo_name) {
        if (TextUtils.isEmpty(path))
            return null;
        if (TextUtils.isEmpty(fileType)) {
            //fileType = CommonUtil.getMimeType(path);

            fileType = getMimeTypeNew(path);
        }
        if (TextUtils.isEmpty(fileType)) {
            return null;
        }
        File file = new File(path);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(fileType),
                        file
                );
        Log.d("thumbnail_url"," file type "+fileType+" phoro name "+photo_name+" req  " +
                ""+requestFile+" photo type ");

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(photo_name, file.getName(), requestFile);
        Log.d("thumbnail_url"," body as "+body);
        return body;
    }
    private String getPathFromUri(Uri uri)   {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.d("getURIError"," exception 1 "+e.getMessage());
            throw new RuntimeException(e);
        }
        File file = new File(getCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.d("getURIError"," exception 2 "+e.getMessage());
            throw new RuntimeException(e);
        }
        byte[] buffer = new byte[1024];
        int length;
        while (true) {
            try {
                if (!((length = inputStream.read(buffer)) > 0)) break;
            } catch (IOException e) {
                Log.d("getURIError"," exception 3 "+e.getMessage());
                throw new RuntimeException(e);
            }
            try {
                outputStream.write(buffer, 0, length);
            } catch (IOException e) {
                Log.d("getURIError"," exception 4 "+e.getMessage());
                throw new RuntimeException(e);
            }
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            Log.d("getURIError"," exception 5 "+e.getMessage());
            throw new RuntimeException(e);
        }


        return file.getAbsolutePath();
    }
    private String getBrowseFilename(String filename){
        File apkStorage = new File(doc_filepath);

        if (!apkStorage.exists()) {
            apkStorage.mkdirs();
        }
        String uriSting = (apkStorage.getAbsolutePath() + "/" +filename);
        return uriSting;
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
