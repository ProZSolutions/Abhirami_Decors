package in.proz.adamd.DSR;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import in.proz.adamd.Adapter.DSRAdapter;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.AttendanceSQLite.AttendanceDSR;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.DSRMainModal;
import in.proz.adamd.ModalClass.DSRSubModal;
import in.proz.adamd.ModalClass.ProjectListModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.ProjectDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DSRActivity extends AppCompatActivity implements View.OnClickListener {
    TextView title,ed_fromdate;
    ImageView frame_icon,mike_1,mike_2,mike_3;
    LinearLayout update_layout,btnplanned_work;
    PDFView pdf_viewer;
    TextView frame_tag;
    LinearLayout spinner_layout;
    String view_wsr;
    TextView calendarSpinner;
    ImageView backward_date , forward_date;

    int pos=0;
    SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");

    ImageView back_arrow,from_picker;
    LinearLayout listLayout,apply_leave_layout,reset_layout,request_layout,frame_layout;
    List<String> projectIdList,projectURLList,projectNameList,listedProject,listedGit;
    ProjectDetails projectDetails ;
    String from_date_str;
    RecyclerView recyclerView;
    AttendanceDSR attendanceDSR;

    EditText edt_planned_work,edt_completed,edt_comments,edt_others;

    HashMap<String,List<String>> gitURLList;
    CommonClass commonClass = new CommonClass();
   // ProgressDialog progressDialog;
   // NachoTextView git_url_view;
    TextView project_name,git_name,submit_txt,dsr_txt;
    RelativeLayout gitnameRL,projectnameRL;
    DSRSubModal commonPojo;
    MKLoader loader;
     boolean[] selectedProjects;
    boolean[] selectedNames;
    String SendToID,SendToName;

    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    ArrayList<Integer> projectListID = new ArrayList<>();
    boolean[] selectedGit;
    LinearLayout other_layout;
    TextView date_text,change_layout;
    LinearLayout date_layout;
    ImageView download_pdf;
    String str_from,str_to;
    LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text,project_url_layout;
    int checkInTime;
    List<String> calendarWeekList = new ArrayList<>();

    Calendar calenderWeek = Calendar.getInstance();
    int currentWeek = calenderWeek.get(Calendar.WEEK_OF_YEAR);
    ArrayList<Integer> gitListID = new ArrayList<>();
    TextView no_data;
    MKLoader loader1;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_dsr);
         dsr_txt = findViewById(R.id.dsr_txt);
         loader1 = findViewById(R.id.loader1);

         attendanceDSR = new AttendanceDSR(DSRActivity.this);
         attendanceDSR.getWritableDatabase();

         no_data= findViewById(R.id.no_data);
        projectDetails = new ProjectDetails(DSRActivity.this);
        projectDetails.getWritableDatabase();
        Log.d("currentWeek"," current week "+currentWeek);

         initView();


          if(commonPojo!=null){

             submit_txt.setText("Update Work");
             dsr_txt.setText("Update Work ");
             apply_leave_layout.setVisibility(View.VISIBLE);
             listLayout.setVisibility(View.GONE);
             frame_tag.setText("Work List");
             Log.d("frame_tag"," call1");
             frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
         }
         Bundle b = getIntent().getExtras();
         if(b!=null){
             commonPojo = (DSRSubModal) b.getSerializable("edit_content");
             updateEditUI(commonPojo);
            // updateGIT();

         }
    }


    private void refreshGitNames(){
        String listStr="";
        Log.d("getGITLIST"," two listed git "+listedGit.size()+"  git list  "+gitListID.size());
        gitListID.clear();
       /* for(int i=0;i<listedGit.size();i++){
            if(!gitListID.contains(i)) {
                gitListID.add(i);
             }
        }*/



        List<String> GitSelName = new ArrayList<>();
        if(!TextUtils.isEmpty(git_name.getText().toString())){
            String[] split = git_name.getText().toString().split(",");
            for(int i= 0;i<split.length;i++){
                GitSelName.add(split[i]);
            }
        }
        Log.d("selectedGit"," url "+listedGit.size()+" gitlist "+GitSelName.size());

        for(int k=0;k<listedGit.size();k++){
            if(GitSelName.size()!=0){
                if(GitSelName.contains(listedGit.get(k))){
                    if(!gitListID.contains(k)) {
                        gitListID.add(k);
                    }
                }
            }
        }

    }
    private void updateGIT() {
        String listStr="";
        Log.d("getGITLIST","three listed git "+listedGit.size()+"  git list  "+gitListID.size());
        gitListID.clear();
        for(int i=0;i<listedGit.size();i++){
            if(!gitListID.contains(i)) {
                gitListID.add(i);
                listStr += listedGit.get(i) + ",";
            }
        }
        Log.d("gitList"," set1");
        git_name.setText(listStr);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_planned_work.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_planned_work.setText(str_rem);
            }
        }else if(requestCode ==2){
                 if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String str_rem = edt_completed.getText().toString();
                    if (TextUtils.isEmpty(str_rem)) {
                        str_rem = Objects.requireNonNull(result).get(0);
                    } else {
                        str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                    }
                    edt_completed.setText(str_rem);

            }
        }else if(requestCode ==3){
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_comments.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_comments.setText(str_rem);

            }
        }
    }
    private void recordVoiceToText( int i) {
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
       /* intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
         intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(DSRActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void projectAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DSRActivity.this);

        // set title
        builder.setTitle("Select Project Name");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = projectNameList.toArray(new String[0]);

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
                    projectListID.add(i);
                     Collections.sort(projectListID);
                } else {
                    projectListID.remove(Integer.valueOf(i));
                 }
                //updateGITLISt();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 StringBuilder stringBuilder = new StringBuilder();
                 for (int j = 0; j < projectListID.size(); j++) {
                     stringBuilder.append(stringArray[projectListID.get(j)]);
                     if (j != projectListID.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                // set text on textView
                project_name.setText(stringBuilder.toString());
                 if(stringBuilder!=null){
                     String str = stringBuilder.toString();
                     if(str.contains("Other")){
                         other_layout.setVisibility(View.VISIBLE);
                     }else{
                         if(commonPojo==null) {
                             edt_others.setText(null);
                         }
                         other_layout.setVisibility(View.GONE);
                     }
                 }
                  updateGITLISt();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });
   /*     builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (int j = 0; j < selectedLanguage.length; j++) {
                    // remove all selection
                    selectedLanguage[j] = false;
                     langList.clear();
                      project_name.setText("");
                }
            }
        });*/
        // show dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
    }
    public void gitAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DSRActivity.this);

        // set title
        builder.setTitle("Select GIT Name");

        // set dialog non cancelable
        Log.d("gitList"," insert array "+listedGit.size());
        builder.setCancelable(false);
        String[] stringArray = listedGit.toArray(new String[0]);
        Log.d("gitList"," string Array "+stringArray.length);
        selectedGit = new boolean[stringArray.length];
        Log.d("selectedGit"," selected git length "+selectedGit.length);


        Log.d("gitList"," gitname "+git_name.getText().toString());

        List<String> GitSelName = new ArrayList<>();
        if(!TextUtils.isEmpty(git_name.getText().toString())){
            String[] split = git_name.getText().toString().split(",");
            for(int i= 0;i<split.length;i++){
                GitSelName.add(split[i]);
            }
        }
        Log.d("selectedGit"," url "+listedGit.size()+" gitlist "+gitListID.size());

        for(int k=0;k<listedGit.size();k++){
           if(GitSelName.size()!=0){
               if(GitSelName.contains(listedGit.get(k))){
                   selectedGit[k]=true;
               }else{
                   selectedGit[k]=false;
               }
           }else {
               selectedGit[k]=false;
           }
        }



       /* Log.d("SelectedGit"," liksta size "+gitListID.size());
        if(gitListID.size()!=0) {
            for (int i = 0; i < gitListID.size(); i++) {
                Log.d("SelectedGit"," git valu "+gitListID.get(i));
                selectedGit[gitListID.get(i)] = true;
            }
        }*/

        builder.setMultiChoiceItems(stringArray, selectedGit, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                // check condition
                if (b) {
                    if(!gitListID.contains(i)){
                        gitListID.add(i);
                        Collections.sort(gitListID);
                    }

                } else {

                    if(gitListID.size()==1){
                        gitListID.remove(Integer.valueOf(i));
                        gitListID.add(0);
                    }else{
                        gitListID.remove(Integer.valueOf(i));
                    }
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder stringBuilder = new StringBuilder();
                Log.d("gitList"," size "+gitListID.size()+" array size "+stringArray.length+" listed git "+listedGit.size());
                for (int j = 0; j < gitListID.size(); j++) {
                    Log.d("gitList"," git value "+gitListID.get(j));
                   stringBuilder.append(stringArray[gitListID.get(j)]);
                    if (j != gitListID.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                // set text on textView
                Log.d("gitList"," set2");
                git_name.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });
   /*     builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (int j = 0; j < selectedLanguage.length; j++) {
                    // remove all selection
                    selectedLanguage[j] = false;
                     langList.clear();
                      project_name.setText("");
                }
            }
        });*/
        // show dialog
        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));    }

    public String getCalendarDate(int currentWeek){

        Log.d("getList"," current week "+currentWeek);
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.WEEK_OF_YEAR,currentWeek);

        //first day of week
        c1.set(Calendar.DAY_OF_WEEK, 2);

        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        str_from = year1+"-"+month1+"-"+day1;

        if(month1<=9){
            str_from = year1+"-0"+month1+"-"+day1;
        }if(day1<=9){
            str_from = year1+"-"+month1+"-0"+day1;

        }

        //last day of week
        c1.set(Calendar.DAY_OF_WEEK, 7);

        int year7 = c1.get(Calendar.YEAR);
        int month7 = c1.get(Calendar.MONTH)+1;
        int day7 = c1.get(Calendar.DAY_OF_MONTH);
        str_to = year7 +"-"+month7+"-"+day7;

        if(month7<=9){
            str_to = year7 +"-0"+month7+"-"+day7;
        }
        if(day7<=9){
            str_to = year7 +"-"+month7+"-0"+day7;

        }

        Log.d("currentWeek"," from "+str_from+" to "+str_to);



      /*  Log.d("currentWeek"," days before "+days);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        Date cdate= null;
        try {
            cdate = sdf.parse(currentDateandTime);
            Log.d("currentWeek"," current date obj "+cdate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar now2= Calendar.getInstance();
        now2.add(Calendar.DATE, -days);
        String beforedate=now2.get(Calendar.YEAR)+"-"+(now2.get(Calendar.MONTH) + 1)+"-"+now2.get(Calendar.DATE);
        Log.d("currentWeek"," before date "+beforedate);
        Date BeforeDate1= null;
        try {
            BeforeDate1 = sdf.parse(beforedate);
            Log.d("currentWeek"," before date obj  "+BeforeDate1);
        } catch (ParseException e) {
            Log.d("currentWeek"," erro "+e.getMessage());
            throw new RuntimeException(e);
        }*/
        String beforedate ="";
        return beforedate;

    }


    public void initView(){
        update_layout = findViewById(R.id.update_layout);
        btnplanned_work = findViewById(R.id.btnplanned_work);
        btnplanned_work.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
      //  nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);

        project_url_layout = findViewById(R.id.project_url_layout);

        backward_date = findViewById(R.id.backward_date);
        forward_date = findViewById(R.id.forward_date);
        calendarSpinner = findViewById(R.id.calendarSpinner);
        spinner_layout = findViewById(R.id.spinner_layout);
         getCalendarDate(currentWeek);
         forward_date.setOnClickListener(this);
         backward_date.setOnClickListener(this);

         spinner_layout.setOnClickListener(this);


         for(int i=currentWeek;i>0;i--){
             calendarWeekList.add("Calendar Week "+i);
         }
         calendarSpinner.setText("Calendar Week "+String.valueOf(currentWeek));
         SendToID="0";




        mike_1 = findViewById(R.id.mike_1);
        mike_1.setOnClickListener(this);
        mike_2 = findViewById(R.id.mike_2);
        mike_2.setOnClickListener(this);
        mike_3 = findViewById(R.id.mike_3);
        mike_3.setOnClickListener(this);
        /*CommonClass comm = new CommonClass();
        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(DSRActivity.this,online_layout,online_text,online_icon);*/
        date_layout = findViewById(R.id.date_layout);
        date_layout.setOnClickListener(this);
        download_pdf = findViewById(R.id.download_pdf);
        download_pdf.setOnClickListener(this);
        date_text = findViewById(R.id.date_text);
        date_text.setOnClickListener(this);
        Date date = new Date();
        String today =serverFormat.format(date);


         edt_others = findViewById(R.id.edt_others);
         other_layout = findViewById(R.id.oth_layout);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);
        git_name = findViewById(R.id.git_name);
        git_name.setOnClickListener(this);
        project_name = findViewById(R.id.project_name);
        project_name.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        projectnameRL = findViewById(R.id.projectnameRL);
        gitnameRL = findViewById(R.id.gitnameRL);
        recyclerView= findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager1=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager1);
         Log.d("DSRList"," token "+commonClass.getSharedPref(getApplicationContext(),"token")+" " +
                 "div "+commonClass.getDeviceID(DSRActivity.this));
        change_layout = findViewById(R.id.change_layout);
        listLayout= findViewById(R.id.listLayout);
        apply_leave_layout= findViewById(R.id.apply_leave_layout);
        reset_layout= findViewById(R.id.reset_layout);
        request_layout= findViewById(R.id.request_layout);
        submit_txt = findViewById(R.id.submit_txt);
        if(commonPojo!=null){
            update_layout.setVisibility(View.VISIBLE);
            submit_txt.setText("Update Work");
            dsr_txt.setText("Update Work ");
        }else{
            update_layout.setVisibility(View.GONE);
        }
        request_layout.setOnClickListener(this);
        reset_layout.setOnClickListener(this);
        change_layout.setOnClickListener(this);
         listedProject = new ArrayList<>();
         listedGit = new ArrayList<>();
        from_picker = findViewById(R.id.from_picker);
        from_picker.setOnClickListener(this);
        edt_planned_work = findViewById(R.id.edt_planned_work);
        edt_completed = findViewById(R.id.edt_completed);
        edt_comments = findViewById(R.id.edt_comments);
        ed_fromdate = findViewById(R.id.ed_fromdate);
        ed_fromdate.setOnClickListener(this);






        //   git_url_view = findViewById(R.id.git_url_view);
        //ed_fromdate.setOnClickListener(this);
        ed_fromdate.setText(displayFormat.format(new Date()));
        from_date_str = serverFormat.format(new Date());
        Log.d("projectDetails"," from date "+from_date_str);
       /* progressDialog = new ProgressDialog(DSRActivity.this);
        progressDialog.setCancelable(false);*/
        title = findViewById(R.id.header_title);
        title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        projectIdList = new ArrayList<>();
        projectNameList = new ArrayList<>();
        projectURLList = new ArrayList<>();
        //nachoTextView = findViewById(R.id.nacho_text_view);
      // nachoTextView.setThreshold(0);
          gitURLList = new HashMap<>();
         if(commonClass.isOnline(DSRActivity.this)){
            getProjectList();
            if(TextUtils.isEmpty(str_from) || TextUtils.isEmpty(str_to)){
               commonClass.showWarning(DSRActivity.this,"Please Select Date Range+");
            }else{
                getList();
            }

        }else{
             commonClass.showInternetWarning(DSRActivity.this);
         }
         /*project_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 updateGITLISt();             }
         });

         gitnameRL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 updateGITLISt();
             }
         });
         projectnameRL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 updateGITLISt();
             }
         });
         ed_fromdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 updateGITLISt();
             }
         });
        edt_completed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                updateGITLISt();
            }
        });
        edt_planned_work.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                updateGITLISt();
            }
        });
        edt_comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                updateGITLISt();
            }
        });*/

        if(commonPojo!=null){
           // updateEditUI(commonPojo);
        }




        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        Log.d("getCurrentDate"," time "+dateFormat.format(new Date())+
                " getcheckin time "+commonClass.getSharedPref(getApplicationContext(),"punchin_time"));

       /* if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"punchin_time"))){
            int punchin_time = Integer.parseInt(commonClass.getSharedPref(getApplicationContext(),"punchin_time"));
            int current_time = Integer.parseInt(dateFormat.format(new Date()));
            if(current_time<=punchin_time){
                edt_planned_work.setEnabled(true);
                project_name.setEnabled(true);
            }else{
                edt_planned_work.setEnabled(false);
                project_name.setEnabled(false);
            }
        }*/

    }
    private void callAlertDialog() {
        Log.d("callAlert"," size "+calendarWeekList.size());
        AlertDialog.Builder builder = new AlertDialog.Builder(DSRActivity.this);

        // set title
        builder.setTitle("Select Request");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = calendarWeekList.toArray(new String[0]);
        //   String[] stringArray = empNameList.toArray(new String[0]);

        selectedNames = new boolean[stringArray.length];
            if(TextUtils.isEmpty(SendToName)){
                SendToName="Calendar Week "+currentWeek;
            }
            for(int i=0;i<calendarWeekList.size();i++){
                if(SendToName.equals(calendarWeekList.get(i))){
                    SendToID=String.valueOf(i);
                    break;
                }
            }



        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendToID = String.valueOf(which);
                SendToName = calendarWeekList.get(which);
                calendarSpinner.setText(SendToName);
                //dialog.dismiss();
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
                dialog.dismiss();
                if(which==0){
                    forward_date.setEnabled(false);
                }else{
                    forward_date.setEnabled(true);
                }

                if (which==currentWeek-1){
                    backward_date.setEnabled(false);
                }else{
                    backward_date.setEnabled(true);
                }


                String week = extractNumbers(SendToName);
                int weeek =Integer.parseInt(week);


                getCalendarDate(weeek);
                getList();


            }
        });




        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
    }
    public static String extractNumbers(String input) {
        StringBuilder numbers = new StringBuilder();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            numbers.append(matcher.group());
        }

        return numbers.toString();
    }
    private void updateEditUI(DSRSubModal commonPojo) {
        frame_layout.setVisibility(View.GONE);
        change_layout.setVisibility(View.GONE);
         dsr_txt.setText("Update Work ");
        update_layout.setVisibility(View.VISIBLE);
         //from_date_str = commonPojo.getDate();
         String[] apl = commonPojo.getDate().split("-");
         int year = Integer.parseInt(apl[2]);
         if(year>2000){
             ed_fromdate.setText(commonPojo.getDate());
             from_date_str = apl[2]+"-"+apl[1]+"-"+apl[0];

         }else{
             ed_fromdate.setText(apl[2]+"-"+apl[1]+"-"+apl[0]);
             from_date_str = commonPojo.getDate();

         }


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
         String today_date = sdf1.format(new Date());
         if(!TextUtils.isEmpty(commonPojo.getCreated_at())){
             if(commonPojo.getCreated_at().startsWith(today_date)){
                 Log.d("comeTo"," true ");
                     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {



                     // Parse the input date-time string to an Instant
                     Instant inputDateTime = Instant.parse(commonPojo.getCreated_at());

                     // Get the current date-time
                     Instant currentDateTime = Instant.now();

                     // Calculate the difference in seconds
                     long secondsDifference = 0;
                         secondsDifference = ChronoUnit.SECONDS.between(inputDateTime, currentDateTime);


                     // Optionally convert to more readable formats
                     long days = secondsDifference / (24 * 3600);
                     long hours = (secondsDifference % (24 * 3600)) / 3600;
                     long minutes = (secondsDifference % 3600) / 60;
                     long seconds = secondsDifference % 60;
                         if(minutes<=60){
                             edt_planned_work.setEnabled(true);
                         }else{
                             edt_planned_work.setEnabled(false);
                         }
                     // Print the result
                     Log.d("comeTo","Difference: " + days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds.");
                     }





             }else{
                 Log.d("comeTo"," else g ");
                 edt_planned_work.setEnabled(false);
             }
         }else{
             Log.d("comeTo"," else 2 ");
             edt_planned_work.setEnabled(false);
         }





        if(!TextUtils.isEmpty(commonPojo.getOther_project_name())){
             edt_others.setText(commonPojo.getOther_project_name());
             other_layout.setVisibility(View.VISIBLE);
         }
      /*   if(commonPojo.getProject_name()!=null){
             if(commonPojo.getProject_name().size()!=0){
                 String listString = String.join(",", commonPojo.getProject_name());
                 project_name.setText(listString);
             }
         }*/
         if(!TextUtils.isEmpty(commonPojo.getP_name())){
             Log.d("getOtherProDet"," come to constion ");
           /*  List<String> sample = new ArrayList<>();
             sample.add(commonPojo.getP_name());*/

             project_name.setText(commonPojo.getP_name());

             if(!TextUtils.isEmpty(commonPojo.getOther_project_name())){
                 String pro_name="";
                 if(commonPojo.getP_name().contains(",")){
                     pro_name = commonPojo.getP_name().replace(","+commonPojo.getOther_project_name(),",Others");
                 }else{
                     pro_name = commonPojo.getP_name().replace( commonPojo.getOther_project_name(),"Others");
                 }
                 Log.d("getOtherProDet"," pro name "+pro_name+" pname  "+commonPojo.getP_name()+
                         " other project name "+commonPojo.getOther_project_name());
                 project_name.setText(pro_name);

             }
         }
         Log.d("getOtherProDet"," git url "+commonPojo.getGit_url());
        if(commonPojo.getGit_url()!=null){
            if(commonPojo.getGit_url().size()!=0){
                String listString = String.join(",", commonPojo.getGit_url());
                Log.d("SelectedGit"," list "+listString);
                Log.d("gitList"," set3");
                git_name.setText(listString);
            }
        }else{
            updateGITLISt();
        }



        if(projectNameList.size()==0){
            projectNameList.clear();
            projectNameList = projectDetails.getAllProjectList(null);
            projectNameList.add("Others");
            //Collections.sort(projectNameList);

        }

        if(!TextUtils.isEmpty(commonPojo.getP_name())){
            String value = commonPojo.getP_name().replace(", ",",");
            String[] split = value.split(",");
            for(int i=0;i<split.length;i++){
                int index = projectNameList.indexOf(split[i]);
                Log.d("splitWork"," value "+split[i]+"  index  "+index);
                if(index>=0){
                    projectListID.add(index);
                }else{
                    int index1 = projectNameList.indexOf("Others");
                    if(index1>=0){
                        projectListID.add(index1);
                    }
                }
            }
        }
        updateGITLISt();
        Log.d("getOtherProDet"," git name "+commonPojo.getGit_url());
        if(commonPojo.getGit_url()!=null){
            if(commonPojo.getGit_url().size()!=0){
                for(int i=0;i<commonPojo.getGit_url().size();i++){
                    int index = listedGit.indexOf(String.valueOf(commonPojo.getGit_url().get(i)));
                    Log.d("SelectedGit"," indes "+index);
                    if(index>=0){
                        if(!gitListID.contains(index)) {
                            gitListID.add(index);
                        }
                    }
                }
                Log.d("SelectedGit"," size "+gitListID.size());
            }
        }else{
            updateGIT();
        }



      /*  if(commonPojo!=null){
            if(commonPojo.getProject_name()!=null) {
                if (commonPojo.getProject_name().size() != 0) {

                    for (int i = 0; i < commonPojo.getProject_name().size(); i++) {
                        Log.d("projectName"," list as "+commonPojo.getProject_name().get(i));
                        int index= projectNameList.indexOf(commonPojo.getProject_name().get(i));
                        Log.d("projectName"," index as "+index);
                        if (index>=0) {
                            projectListID.add(i);
                        }
                    }
                    Collections.sort(projectIdList);
                    updateGITLISt();
                }
            }

            if(!TextUtils.isEmpty(commonPojo.getP_name())){
                String split []= commonPojo.getP_name().split(",");
                for (int k=0;k<split.length;k++){
                    int index= projectNameList.indexOf(split[k]);
                    Log.d("projectName"," index list as "+index);
                    if (index>=0) {
                        projectListID.add(index);
                    }

                }
                Collections.sort(projectIdList);
                updateGITLISt();
            }

        }*/



         edt_planned_work.setText(commonPojo.getPlanned_work());
          mike_1.setEnabled(false);
         edt_comments.setText(commonPojo.getComments());
         edt_completed.setText(commonPojo.getCompleted_activity());
        submit_txt.setText("Update Work");
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        frame_icon.setImageResource(R.drawable.calendar_icon_new);
        frame_tag.setText("Work List");
        Log.d("frame_tag","call2");
         //git_url_view.setText(commonPojo.getGit_url());
    }

    private void updateGITLISt() {
            listedGit.clear();
       //*- gitListID.clear();
         Log.d("getGITLIST"," size of selected "+projectListID.size()+" project name list "+
                 projectNameList.size());
        String str ="";
        listedGit.clear();
         for(int k=0;k<projectListID.size();k++){
             Log.d("getGITLIST"," get siz  "+projectListID.get(k)+" name  "+projectNameList.get(projectListID.get(k)));
             List<String> va = projectDetails.getAllGitList(projectNameList.get(projectListID.get(k)));
             Log.d("getGITLIST"," va size "+va.size());
             if(va.size()!=0){
                 for(int i1=0;i1<va.size();i1++){
                     listedGit.add(va.get(i1));
                     str = str+listedGit.get(i1)+",";
                 }
                 Log.d("getGITLIST"," git size "+listedGit.size());
             }
             if(listedGit.size()!=0){
                 Collections.sort(listedGit);
             }



         }




if(listedGit.size()==0){
    if(project_name.getText().equals("Others")) {
         project_url_layout.setText("Project URL");
    }else{
        project_url_layout.setText("Project URL*");
    }
    git_name.setText(null);

}else{
    //updateGIT();
    //git_name.setText(str);  //23-09-2024
    List<String> listArr= new ArrayList<>();
    Log.d("gitList"," gitname "+git_name.getText().toString()
    +" one listed git size "+listedGit.size());
    if(!TextUtils.isEmpty(git_name.getText().toString())){
        String[] split = git_name.getText().toString().split(",");
        Log.d("split"," len "+split.length);
        for(int i=0;i<split.length;i++){
            listArr.add(split[i]);
        }
        Log.d("gitList"," array size value "+listArr.size());
        List<String> my = new ArrayList<>();
        for(int m=0;m<listArr.size();m++){
            if(listedGit.contains(listArr.get(m))){
                my.add(listArr.get(m));
            }
        }
        Log.d("gitList"," my arr size "+my.size());
        if(my.size()!=0){
            Log.d("gitList"," set4");
            git_name.setText(String.join(",",my));
        }
    }
}
        refreshGitNames();






    }

    private void getProjectList() {
        //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(DSRActivity.this)).create(ApiInterface.class);
        Call<ProjectListModal> call = apiInterface.getProjectList();
        call.enqueue(new Callback<ProjectListModal>() {
            @Override
            public void onResponse(Call<ProjectListModal> call, Response<ProjectListModal> response) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getGetProjectList().size()!=0){
                            projectDetails.DropTable();
                            for(int i=0;i<response.body().getGetProjectList().size();i++){
                               /* projectIdList.add(response.body().getGetProjectList().get(i).getProject_id());
                                projectNameList.add(response.body().getGetProjectList().get(i).getProject_name());
                                gitURLList.put(response.body().getGetProjectList().get(i).getProject_id(),
                                        response.body().getGetProjectList().get(i).getGit_url());*/
                                String listString = String.join(",",  response.body().getGetProjectList().get(i).getGit_url());
                                Log.d("projectDetails"," insert git "+listString);
                                projectDetails.insertData(response.body().getGetProjectList().get(i).getProject_id(),
                                        response.body().getGetProjectList().get(i).getProject_name(),
                                        listString);
                            }
                            updateAdapter();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectListModal> call, Throwable t) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void updateAdapter() {
        projectNameList.clear();
        projectNameList = projectDetails.getAllProjectList(null);
        projectNameList.add("Others");
       // Collections.sort(projectNameList);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(DSRActivity.this, android.R.layout.simple_dropdown_item_1line,
                projectNameList);
        nachoTextView.setAdapter(adapter);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(apply_leave_layout.getVisibility()==View.VISIBLE){
            commonPojo = null;
            // if(apply_leave_layout.getVisibility()==View.VISIBLE){
            dsr_txt.setText("Work List");

            frame_tag.setText("Work List");
            apply_leave_layout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            frame_tag.setText("Work LIST");
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
            if(TextUtils.isEmpty(str_from) || TextUtils.isEmpty(str_to)){
                commonClass.showWarning(DSRActivity.this,"Please Select Date Range");
            }else{
                getList();
            }
        }else{
            callIntent();
        }

    }
    public void callIntent(){
        if(apply_leave_layout.getVisibility()==View.VISIBLE){
            dsr_txt.setText("Work List");
            change_layout.setVisibility(View.GONE);
            apply_leave_layout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);

        }else{
            Intent intent =new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }

    }
    class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdf_viewer.fromStream(inputStream).load();
        }
    }
    public void  showAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(DSRActivity.this);
        View view= LayoutInflater.from(DSRActivity.this).inflate(R.layout.pdf_viewer,null);
        pdf_viewer = view.findViewById(R.id.pdf_viewer);
        new RetrievePDFfromUrl().execute(view_wsr);


        builder.setView(view);
        final android.app.AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnplanned_work:
                callAlertDialogDSR();
                break;
            case R.id.spinner_layout:
                callAlertDialog();
                break;
            case R.id.nhome_layout:
                Intent intent1h = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1h);
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
                Intent intentlo = new Intent(getApplicationContext(), MapCurrentLocation.class);
                 startActivity(intentlo);
                break;
            /*case R.id.forward_date:
                String selected  = calendarSpinner.getText().toString();
                selected = selected.replace("Calendar Week ","");
                Integer pos  = Integer.parseInt(selected);
                Log.d("currentWeek"," for pos "+pos);
                if(pos!=currentWeek){
                    forward_date.setEnabled(true);
                    int mypos = pos+1;
                    if(TextUtils.isEmpty(SendToID)){
                        SendToID ="0";
                    }
                    int custom_pos = Integer.parseInt()-1;
                    calendarSpinner.setSelection(custom_pos);
                }else{
                    forward_date.setEnabled(false);
                }
                break;
            case R.id.backward_date:
                String selected1  = calendarSpinner.getSelectedItem().toString();
                selected1 = selected1.replace("Calendar Week ","");
                Integer pos1  = Integer.parseInt(selected1);
                Log.d("currentWeek"," back pos "+pos1);
                if(pos1!=0){
                    backward_date.setEnabled(true);
                    int mypos = pos1-1;
                    int custom_pos = calendarSpinner.getSelectedItemPosition()+1;
                    calendarSpinner.setSelection(custom_pos);
                }else{
                    backward_date.setEnabled(false);
                }
                break;*/
            case R.id.mike_1:
                recordVoiceToText(1);
                break;
            case R.id.mike_2:
                recordVoiceToText(2);
                break;
            case R.id.mike_3:
                recordVoiceToText(3);
                break;
            case R.id.change_layout:
               // callGeneratePDF();
                Intent intent = new Intent(getApplicationContext(),WSRLayout.class);
                startActivity(intent);
                break;
            case R.id.date_text:
                callView();
                break;
            case R.id.download_pdf:
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view_wsr));
                startActivity(browserIntent);*/
                callView();
                break;
            case R.id.date_layout:
                callView();
                           /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view_wsr));
                startActivity(browserIntent);*/
               // requestDate();
                break;
            case R.id.reset_layout:
                callReset();
                break;
            case R.id.request_layout:



                Log.d("projectDetails"," project size "+listedProject.size()+" git "+
                        listedGit.size()+" from date "+from_date_str);
                if(!TextUtils.isEmpty(ed_fromdate.getText().toString())){
                    String[] str = ed_fromdate.getText().toString().split("-");
                    from_date_str = str[2]+"-"+str[1]+"-"+str[0];
                }
                 if(TextUtils.isEmpty(from_date_str)) {
                     commonClass.showWarning(DSRActivity.this, "Select Date");
                 }else {
                     if(TextUtils.isEmpty(edt_planned_work.getText().toString())){
                         commonClass.showWarning(DSRActivity.this,"Enter Planned Work");
                     }else if(TextUtils.isEmpty(edt_completed.getText().toString())){
                             commonClass.showWarning(DSRActivity.this,"Enter Completed Work");
                         }else{
                             callUpdateMethod();
                         }
                     }





                break;
            case R.id.project_name:
                projectAlertDialog();
                break;
            case R.id.git_name:
                updateGITLISt();
                if(listedGit.size()!=0){
                    gitAlertDialog();
                }else{
                    git_name.setText(null);
                    commonClass.showWarning(DSRActivity.this,"No Git URL");
                }

                break;
            case R.id.back_arrow:
                callIntent();
                break;
            case R.id.ed_fromdate:
                datePicker(ed_fromdate,1);
                break;
            case R.id.from_picker:
                datePicker(ed_fromdate,1);
                break;
            case R.id.frame_layout:
                callUIUpdate();
                break;
        }
    }

    private void callView() {
        try {
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setDataAndType(Uri.parse(view_wsr), "application/pdf");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent1);
        } catch (Exception e) {
            // Error...
            commonClass.showWarning(DSRActivity.this,"No PDF Viewer App Available");
        }
    }

   /* private void callGeneratePDF() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(DSRActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.generatePDF();
        Log.d("generate"," url as "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                Log.d("generate"," res "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("generate"," status "+response.body().getStatus());
                        if(response.body().getStatus().contains("success")){
                            Log.d("generate"," file name "+response.body().getFilename());
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(response.body().getFilename())));


                        }else{
                            commonClass.showError(DSRActivity.this,response.body().getStatus());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(DSRActivity.this,mError.getError());
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

                        commonClass.showError(DSRActivity.this,mError.getError());
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
                Log.d("dropdownlist"," error"+t.getMessage());
                commonClass.showError(DSRActivity.this,t.getMessage());
            }
        });
    }*/

    private void requestDate() {
             Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Select a date range");
       /* builder.setCalendarConstraints(new  CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now()).build());*/
           /* builder.setCalendarConstraints(new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now()).build());*/

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
                str_from = sdf1.format(new Date(startDate));
                str_to = sdf1.format(new Date(endDate));

                // Creating the date range string
                String selectedDateRange = startDateString + " TO " + endDateString;



                // Displaying the selected date range in the TextView
                date_text.setText(selectedDateRange);
                getList();
            });

            // Showing the date picker dialog
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        }


    private void callUpdateMethod() {
        request_layout.setEnabled(false);

        //progressDialog.show();
        loader1.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(DSRActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call =null;
        if(commonPojo!=null){
            call = apiInterface.updateDSRList(from_date_str,edt_planned_work.getText().toString(),edt_completed.getText().toString(),
                    edt_comments.getText().toString(),
                    commonClass.getSharedPref(getApplicationContext(),"uuid"),commonPojo.getId(),
                    edt_others.getText().toString());

        }else{
          call   = apiInterface.insertDSRList(from_date_str,edt_planned_work.getText().toString(),edt_completed.getText().toString(),
                    edt_comments.getText().toString()
                  ,commonClass.getSharedPref(getApplicationContext(),"uuid"),edt_others.getText().toString());

        }
        Log.d("dsrInsert"," url "+call.request().url());
       call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                Log.d("dsrInsert"," respone "+response.code());
                request_layout.setEnabled(true);
               // progressDialog.dismiss();
                loader1.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().contains("success")){
                            commonClass.showSuccess(DSRActivity.this,response.body().getData());
                            long currentTimeMillis = System.currentTimeMillis();
                            long nextHourTimestamp = currentTimeMillis + 3600000;
                            commonClass.putSharedPref(getApplicationContext(),"insert_time",String.valueOf(nextHourTimestamp));
                            callReset();
                            callUIUpdate();
                            getList();
                        }else{
                            commonClass.showError(DSRActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(DSRActivity.this,mError.getError());
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

                        commonClass.showError(DSRActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                Log.d("dsrInsert"," error "+t.getMessage());
               // progressDialog.dismiss();
                request_layout.setEnabled(true);
                loader1.setVisibility(View.GONE);
                commonClass.showError(DSRActivity.this,t.getMessage());
            }
        });
    }

    private void callReset() {
         listedProject.clear();
         listedGit.clear();
         from_date_str=null;
         edt_others.setText(null);
        // ed_fromdate.setText(null);
         edt_completed.setText(null);
         edt_comments.setText(null);
         edt_planned_work.setText(null);
         project_name.setText(null);
         git_name.setText(null);
         gitListID.clear();
         projectListID.clear();

    }

    private void callUIUpdate() {
        commonPojo = null;
        if(apply_leave_layout.getVisibility()==View.VISIBLE){
        dsr_txt.setText("Work List");
            change_layout.setVisibility(View.GONE);
             apply_leave_layout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            frame_tag.setText("Add Work");
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
            if(TextUtils.isEmpty(str_from) || TextUtils.isEmpty(str_to)){
                commonClass.showWarning(DSRActivity.this,"Please Select Date Range+");
            }else{
                getList();
            }
         }else{
           // change_layout.setVisibility(View.VISIBLE);
            update_layout.setVisibility(View.GONE);
            dsr_txt.setText("Add Work");
            submit_txt.setText("Request");
            callReset();

            apply_leave_layout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
            frame_tag.setText("DSR List");
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));

        }
    }

    private void getList() {
         Log.d("getList"," dates "+str_from+" str to "+str_to+" dvic " +commonClass.getDeviceID(DSRActivity.this));
         // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(DSRActivity.this)).create(ApiInterface.class);
        Call<DSRMainModal> call  = apiInterface.getDSRList(str_from,str_to);
        Log.d("credentials"," from "+str_from+" to "+str_to+" ur l "+call.request().url()+
                " token "+commonClass.getSharedPref(getApplicationContext(),"token")+" device id "+
                commonClass.getDeviceID(DSRActivity.this));
        call.enqueue(new Callback<DSRMainModal>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DSRMainModal> call, Response<DSRMainModal> response) {
               // progressDialog.dismiss();
                Log.d("getDSR"," error "+response.code());
                // date format 10-12-2024
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().contains("success")){
                            if(!TextUtils.isEmpty(response.body().getWsr())){
                                view_wsr = response.body().getWsr();
                                date_layout.setVisibility(View.VISIBLE);
                            }else{
                                date_layout.setVisibility(View.GONE);
                            }
                            Log.d("currentWeek"," wsr "+response.body().getWsr());
                             if(response.body().getDsrSubModals().size()!=0){
                                 
                                 SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                 SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                                 String todat_date = df.format(new Date());

                                 String statis="0",sampledate=sdf1.format(new Date());
                                 for(int i=0;i<response.body().getDsrSubModals().size();i++){
                                     DSRSubModal modal = response.body().getDsrSubModals().get(i);                                          Log.d("getDSR"," get dat "+modal.getDate()+" today "+todat_date);
                                     Log.d("getDSR"," get dat "+modal.getDate()+" today "+todat_date);
                                    if(modal.getDate().equals(todat_date)){
                                          statis="1";
                                          break;
                                     }
                                 }
                                 if(statis.equals("1")){
                                     btnplanned_work.setVisibility(View.GONE);
                                 }else{
                                     callCalendarWeek();
                                     //btnplanned_work.setVisibility(View.VISIBLE);
                                 }
                                 
                                 no_data.setVisibility(View.GONE);
                                 DSRAdapter adapter =new DSRAdapter(DSRActivity.this,response.body().getDsrSubModals(),
                                         frame_layout);
                                 recyclerView.setAdapter(adapter);
                             }else{
                               //  btnplanned_work.setVisibility(View.VISIBLE);
                                 callCalendarWeek();

                                 no_data.setVisibility(View.VISIBLE);
                                 recyclerView.setAdapter(null);
                             }
                        }else{
                            commonClass.showError(DSRActivity.this,response.body().getStatus());
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(DSRActivity.this,mError.getError());
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

                        commonClass.showError(DSRActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DSRMainModal> call, Throwable t) {
                no_data.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                commonClass.showError(DSRActivity.this,t.getMessage());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callCalendarWeek() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Note: Month is 0-based (January = 0)
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month+=1;
        Log.d("getDSR"," sate "+year+"-"+month+"-"+day);

        LocalDate date = LocalDate.of(year, month, day);;
        int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
        Log.d("getDSR","week of yr "+weekOfYear);
        String rp = calendarSpinner.getText().toString().replace("Calendar Week ","");
        int rpi = Integer.parseInt(rp);
        Log.d("getDSR"," rpi "+rpi);
        if (weekOfYear == rpi) {
            btnplanned_work.setVisibility(View.VISIBLE);
        }else{
            btnplanned_work.setVisibility(View.GONE);
        }
    }

    private void datePicker(TextView edFromdate, int i) {
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
                             from_date_str =year + "-" + str_month + "-" + str_date;


                        ed_fromdate.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String currentDateandTime1 = sdf1.format(new Date());
        Log.d("getCurrentDate"," time as "+currentDateandTime+" min "+currentDateandTime1);
        int value = Integer.parseInt(currentDateandTime);
        int min = Integer.parseInt(currentDateandTime1);
         if((value>=0 &&value<=9) && (min>=0 && min<=59)){
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date date = cal.getTime();
            datePickerDialog.getDatePicker().setMinDate(date.getTime());

        }else{
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        Log.d("currentDateandTime"," as "+currentDateandTime);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
    private void callAlertDialogDSR() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.dsr_dialog_layout,null);
        TextView  skipp_laypput= view.findViewById(R.id.skipp_laypput);
        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        ImageView mike = view.findViewById(R.id.mike);

        MKLoader loader1 = view.findViewById(R.id.loader);
        TextView project_name = view.findViewById(R.id.project_name);
        LinearLayout oth_layout = view.findViewById(R.id.oth_layout);
        EditText edt_others = view.findViewById(R.id.edt_others);
        edt_planned_work = view.findViewById(R.id.edt_planned_work);
        RelativeLayout bottom_request_layout = view.findViewById(R.id.bottom_request_layout);

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            skipp_laypput.setVisibility(View.VISIBLE);

        }else{

            skipp_laypput.setVisibility(View.GONE);
        }





        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.setCancelable(false);
        mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVoiceToText(1);
            }
        });
        skipp_laypput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDashboard();
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                callDashboard();
            }
        });
        bottom_request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(commonClass.isOnline(DSRActivity.this)) {
                    bottom_request_layout.setEnabled(false);
                     //  mDialog.dismiss();
                    if(TextUtils.isEmpty(edt_planned_work.getText().toString())){
                        edt_planned_work.setText("திட்டமிட்ட பணி எதுவும் இல்லை");
                    }
                    callUpdateMethod(mDialog, project_name.getText().toString(), edt_others.getText().toString(),
                            edt_planned_work.getText().toString(), loader1, bottom_request_layout);
                }else{
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                    String date_format = date.format(new Date());
                    Log.d("checkinHeader"," date format "+date_format);
                    if(TextUtils.isEmpty(edt_planned_work.getText().toString())){
                        edt_planned_work.setText("திட்டமிட்ட பணி எதுவும் இல்லை");
                    }
                    attendanceDSR.insertBulk(date_format,project_name.getText().toString(),
                            edt_planned_work.getText().toString(),edt_others.getText().toString());
                    commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                    Log.d("CheckInCondition"," condition 7");
                    bottom_request_layout.setEnabled(false);
                     mDialog.dismiss();
                    callDashboard();
                    // checkInAttendance();
                }
            }
        });

    }
    private void callUpdateMethod(AlertDialog mDialog, String projectname, String othername, String plannedwork,
                                  MKLoader loader1, RelativeLayout bottom_request_layout) {
        loader1.setVisibility(View.VISIBLE);
        List<String> sProject = new ArrayList<>();
        String str_pro = projectname;
        if(!TextUtils.isEmpty(str_pro)){
            String[] split = str_pro.split(",");
            for (int i=0;i<split.length;i++){
                String projectName = projectDetails.getAllProjectIDList(split[i]);
                Log.d("attendance_list"," split id "+split[i]+" project namt "+projectName);
                if(TextUtils.isEmpty(projectName)){
                    sProject.add("others");
                }else {
                    sProject.add(projectName);
                }


            }
        }
        bottom_request_layout.setEnabled(false);
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String date_format = date.format(new Date());
        loader1.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(DSRActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.DSRInsertInAttendance(commonClass.getSharedPref(getApplicationContext(),"emp_id"),
                date_format,plannedwork,sProject,othername);
        Log.d("attendance_list"," call "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader1.setVisibility(View.GONE);
                Log.d("attendance_list"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==400){
                        //  mDialog.dismiss();

                        commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                    }
                    mDialog.dismiss();

                    if(response.code()==200){

                        if(response.body().getStatus().contains("success")){
                            commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                            Log.d("CheckInCondition"," condition 8");

                            bottom_request_layout.setEnabled(true);
                            //checkInAttendance();
                            commonClass.showSuccess(DSRActivity.this,response.body().getData());

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    //   mDialog.dismiss();
                                   callDashboard();
                                }
                            }, 1500);

                        }else{
                            //    mDialog.dismiss();

                            commonClass.showError(DSRActivity.this,response.body().getData());
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        //  mDialog.dismiss();

                        bottom_request_layout.setEnabled(true);
                        commonClass.showError(DSRActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DSRActivity.this);
                    } else{
                        Log.d("CheckInCondition"," condition 9");
                        //    mDialog.dismiss();

                        // checkInAttendance();
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(DSRActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }else{
                    Log.d("CheckInCondition"," condition 10");
                    // mDialog.dismiss();

                    //  checkInAttendance();
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(DSRActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                //checkInAttendance();
                loader1.setVisibility(View.GONE);
                // mDialog.dismiss();
                bottom_request_layout.setEnabled(true);
                commonClass.showError(DSRActivity.this,t.getMessage());
            }
        });
    }

    private void callDashboard() {
        getList();
    }


}
