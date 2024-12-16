package in.proz.adamd.AdminModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.proz.adamd.AdminModule.AdminAdapter.WSRAdapter;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.ClaimModal;
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

public class AdminWSRLayout extends AppCompatActivity implements View.OnClickListener {
    ImageView back_arrow;
    RecyclerView recyclerView;
    List<String> calendarWeekList = new ArrayList<>();

    MKLoader loader;
    TextView no_data;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    Calendar calenderWeek = Calendar.getInstance();
    int currentWeek = calenderWeek.get(Calendar.WEEK_OF_YEAR);

    CommonClass commonClass = new CommonClass();
    boolean[] selectedNames;
    String SendToID,SendToName;
    LinearLayout search_layout,spinner_layout;
    TextView calendarSpinner;
    ImageView clear,search,backward_date,forward_date;
    EditText searchText;
    String str_search;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_war_layout);
        initView();
        searchText.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {
                     String s = editable.toString();
                         if (!s.equals(s.toUpperCase())) {
                            s = s.toUpperCase();
                            searchText.setText(s);
                            searchText.setSelection(searchText.length()); //fix reverse texting
                             str_search = s.toString();


                         }



            }

            @Override
            public void afterTextChanged(Editable s) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(s.length()!=0){
                            clear.setVisibility(View.VISIBLE);
                            getList();

                        }else{
                            searchText.setText(null);
                            clear.setVisibility(View.GONE);
                         }
                    }
                };
                handler.postDelayed(runnable, 300); // Adjust the delay as needed



            }
        });
    }
    private void callAlertDialog() {
        Log.d("callAlert"," size "+calendarWeekList.size());
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminWSRLayout.this);

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

             //   getCalendarDate(currentWeek-which);
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



    private void initView() {

        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
        //nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        //nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);


        spinner_layout = findViewById(R.id.spinner_layout);
        spinner_layout.setOnClickListener(this);
        forward_date = findViewById(R.id.forward_date);
        forward_date.setOnClickListener(this);
        backward_date = findViewById(R.id.backward_date);
        backward_date.setOnClickListener(this);
        calendarSpinner=findViewById(R.id.calendarSpinner);


        for(int i=(currentWeek);i>0;i--){
            calendarWeekList.add("Calendar Week "+i);
        }
        calendarSpinner.setText("Calendar Week "+String.valueOf(currentWeek));
        SendToID="0";



        search = findViewById(R.id.search);
        search.setOnClickListener(this);
         search_layout = findViewById(R.id.search_layout);
         clear = findViewById(R.id.clear);
         clear.setOnClickListener(this);
         searchText = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(manager);

        loader = findViewById(R.id.loader);
        no_data = findViewById(R.id.no_data);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        if(commonClass.isOnline(AdminWSRLayout.this)) {
            getList();
        }else{
            commonClass.showInternetWarning(AdminWSRLayout.this);
        }
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
    private void getList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AdminWSRLayout.this,"token"),
                commonClass.getDeviceID(AdminWSRLayout.this)).create(ApiInterface.class);
        Call<ClaimModal> call;

        String week = extractNumbers(calendarSpinner.getText().toString());
        str_search = searchText.getText().toString();
        if(TextUtils.isEmpty(str_search)){
            str_search="ALL";
        }


        call=apiInterface.getAdminWSRLIST(week,str_search);

        Log.d("leave_url"," url as "+call.request().url()+" week "+week+" search "+str_search);
        call.enqueue(new Callback<ClaimModal>() {
            @Override
            public void onResponse(Call<ClaimModal> call, Response<ClaimModal> response) {
                // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("leave_url"," res[onse "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            Log.d("leave_url","suze "+response.body().getGetClaimList().size());
                            if(response.body().getGetClaimList().size()!=0){
                             /*   apply_leave_layout.setVisibility(View.GONE);
                                listLayout.setVisibility(View.VISIBLE);
                                change_layout.setText("Apply Loan");*/
                                no_data.setVisibility(View.GONE);


                                    WSRAdapter adapter =new WSRAdapter(AdminWSRLayout.this,
                                            response.body().getGetClaimList(),0,recyclerView,loader);
                                    recyclerView.setAdapter(adapter);



                            }else{
                                recyclerView.setAdapter(null);
                                no_data.setVisibility(View.VISIBLE);
//                                apply_leave_layout.setVisibility(View.VISIBLE);
//                                listLayout.setVisibility(View.GONE);
                                //  commonClass.showError(LoanActivity.this,"No List Found..");
                            }
                        }else{
                            recyclerView.setAdapter(null);
                             commonClass.showError(AdminWSRLayout.this,response.body().getStatus());
                        }
                    }else{
                        recyclerView.setAdapter(null);
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AdminWSRLayout.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }else{
                    recyclerView.setAdapter(null);
                    no_data.setVisibility(View.VISIBLE);
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                        Log.d("thumbnail_url"," error "+mError.getError());
                        commonClass.showError(AdminWSRLayout.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ClaimModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                Log.d("claim_url"," on failure error "+t.getMessage());
                commonClass.showError(AdminWSRLayout.this,t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callIntent();
    }

    private void callIntent() {
        Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
        startActivity(intent);
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
            case R.id.spinner_layout:
                callAlertDialog();
                break;
          /*  case R.id.forward_date:
                recyclerView.setAdapter(null);
                String selected  = calendarSpinner.getSelectedItem().toString();
                selected = selected.replace("Calendar Week ","");
                Integer pos  = Integer.parseInt(selected);
                Log.d("currentWeek"," for pos "+pos);
                if(pos!=currentWeek){
                    forward_date.setEnabled(true);
                    int mypos = pos+1;
                    int custom_pos = calendarSpinner.getSelectedItemPosition()-1;
                    calendarSpinner.setSelection(custom_pos);
                }else{
                    forward_date.setEnabled(false);
                }
                break;
            case R.id.backward_date:
                recyclerView.setAdapter(null);
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
            case R.id.search:
                if(search_layout.getVisibility()==View.VISIBLE){
                    search_layout.setVisibility(View.GONE);
                }else{
                    search_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.clear:
                searchText.setText(null);
                str_search=searchText.getText().toString();
                search_layout.setVisibility(View.GONE);
                getList();
                break;
            case R.id.back_arrow:
                callIntent();
                break;
        }
    }
}
