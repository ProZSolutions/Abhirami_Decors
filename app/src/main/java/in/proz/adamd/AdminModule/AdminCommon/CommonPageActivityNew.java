package in.proz.adamd.AdminModule.AdminCommon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AdminModule.AdminAdapter.CommonPageAdapterNew;
import in.proz.adamd.AdminModule.AdminModals.InnerModal;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
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

public class CommonPageActivityNew extends AppCompatActivity implements View.OnClickListener {
    ImageView back_arrow,search;
    int pageNo=1;
    LinearLayout search_layout;
    CommonClass commonClass = new CommonClass();
    int api_hit_status;
    MKLoader loader;
    TextView no_data;
    ImageView clear ;
    String searchTextStr=null;
    String type,type_title,date_str,tag,server_str;
    GridLayoutManager manager;
    int status_value =0;
    List<String> departmentList = new ArrayList<>();
    TextView title,date,total_count;
    RecyclerView recyclerView;
    List<CommonPojo> commonPojoList=new ArrayList<>();
    CommonPageAdapterNew adapter;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    String request_type,branchID;
    EditText searchText;


    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_common_intent);
        Bundle b= getIntent().getExtras();
        if(b!=null){
            type = b.getString("type");
            status_value = b.getInt("status_value",0);
            type_title = b.getString("type_title");
            request_type = b.getString("request_type");
            branchID = b.getString("branch");
            date_str = b.getString("date_ui");
            server_str = b.getString("date_server");
            departmentList = (List<String>)b.getSerializable("department");


            if(TextUtils.isEmpty(branchID)){
                branchID ="ALL";
            }
            if(departmentList==null){
                departmentList.add("ALL");
            }else if(departmentList.size()==0){
                departmentList.add("ALL");
            }
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            if(TextUtils.isEmpty(date_str)){
                date_str=format1.format(new Date());
                server_str = format2.format(new Date());
            }
        }
        initView();
     }
    public void initView(){
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);

   /*     if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }else{
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/

        loader = findViewById(R.id.loader);
        no_data = findViewById(R.id.no_data);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);
        search = findViewById(R.id.search);
        search.setOnClickListener(this);
        searchText = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.recyclerView);
        total_count = findViewById(R.id.total_count);
        date = findViewById(R.id.date);
        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);
        back_arrow = findViewById(R.id.back_arrow);
        title = findViewById(R.id.title);
        title.setText(type_title);
        search_layout = findViewById(R.id.search_layout);
         back_arrow.setOnClickListener(this);
        date.setText("Date : "+date_str);

        LinearLayout total_layout = findViewById(R.id.total_layout);
        if(type.equals("1")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT; // or Gravity.START for RTL support
            total_layout.setLayoutParams(params);
            date.setVisibility(View.GONE);
            // Set gravity for the children of the LinearLayout
            total_layout.setGravity(Gravity.LEFT);
        }else{

            date.setVisibility(View.VISIBLE);

        }

        if(type.equals("1")){
            api_hit_status=1;
            tag="No.of Employees";
            title.setText("Employee List");
        }else if(type.equals("2")){
            api_hit_status=2;
            tag="No.of Present";
            title.setText("Present List");
        }else if(type.equals("3")){
            api_hit_status=3;
            tag="No.of Absent";
            title.setText("Absent List");
        }else if(type.equals("4")){
            api_hit_status=4;
            tag="No.of Leave";
            title.setText("Leave List");
        }else if(type.equals("5")){
            api_hit_status=5;
            tag="No.of Late";
            title.setText("Late Attendance List");
        }else if(type.equals("6")){
            api_hit_status=6;
            tag="No.of Permission";
            title.setText("Permission List");
        }
        else if(type.equals("7")){
            api_hit_status=7;
            tag="No.of Bio Punch";
            title.setText("Bio Punch List");
        }
        else if(type.equals("8")){
            api_hit_status=8;
            tag="No.of Mobile Punch ";
            title.setText("Mobile Punch List");
        }else if(type.equals("9")){
            api_hit_status=9;
            tag="No of On Duty";
            title.setText("OnDuty List");
        }

        adapter = new CommonPageAdapterNew(CommonPageActivityNew.this,
                commonPojoList,-1,recyclerView,loader);
        recyclerView.setAdapter(adapter);
        searchText.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("getTextChange"," change before "+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if(s!=null){
                    if(s.length()!=0){
                        searchTextStr = s.toString();
                        pageNo=1;
                        clear.setVisibility(View.VISIBLE);
                        getCustomerList();
                    }else{
                        searchText = null;
                        pageNo=1;
                        getCustomerList();
                        clear.setVisibility(View.GONE);
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("getTextChange"," change after "+s);
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
        if(commonClass.isOnline(CommonPageActivityNew.this)){
            getCustomerList();
        }else{
            commonClass.showInternetWarning(CommonPageActivityNew.this);
        }


    }

    private void getCustomerList() {

        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(getApplicationContext())).create(ApiInterface.class);
        Call<InnerModal> call=null;
        String requested_date=server_str;
      /*  if(status_value==0){
            requested_date=null;
        }*/


        if(departmentList.size()==1){
            if(departmentList.get(0).equals("ALL")){
                call = apiInterface.getCommonListString(requested_date,branchID,"ALL",request_type,
                        searchTextStr,String.valueOf(pageNo));
            }else{
                call = apiInterface.getCommonList(requested_date,branchID,departmentList,request_type,
                        searchTextStr,String.valueOf(pageNo));
            }
        }else if(departmentList.size()==0){
            call = apiInterface.getCommonListString(requested_date,branchID,"ALL",request_type,
                    searchTextStr,String.valueOf(pageNo));
        }else{
            call = apiInterface.getCommonList(requested_date,branchID,departmentList,request_type,
                    searchTextStr,String.valueOf(pageNo));
        }
      /*  call = apiInterface.getCommonList(server_str,branchID,departmentList,request_type,
                searchTextStr,String.valueOf(pageNo));*/
        Log.d("mainDropList"," get headers invoice "+call.request().headers()+
                " url "+call.request().url());
        call.enqueue(new Callback<InnerModal>() {
            @Override
            public void onResponse(Call<InnerModal> call, Response<InnerModal> response) {
                Log.d("customers"," response code invoice "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")) {
                            Log.d("customers"," size "+response.body().getOuterModal().getCommonPojoList().size());
                            if(response.body().getOuterModal()!=null)
                                if(response.body().getOuterModal().getCommonPojoList()!=null) {
                                    if (response.body().getOuterModal().getCommonPojoList().size() != 0) {
                                        total_count.setText(tag+" : "+response.body().getOuterModal().getTotal());
                                        no_data.setVisibility(View.GONE);

                                        if (pageNo == 1) {
                                            commonPojoList.clear();
                                        }
                                        for (int i = 0; i < response.body().getOuterModal().getCommonPojoList().size(); i++) {
                                            commonPojoList.add(response.body().getOuterModal().getCommonPojoList().get(i));
                                        }
                                        adapter.notifyDataSetChanged();

                                /*if(pageNo==1){
                                    adapter = new CustomerAdapter(AddCustomer.this,
                                            response.body().getAddClientModalList(),loader);
                                    recyclerView.setAdapter(adapter);
                                }else{
                                    adapter.notifyDataSetChanged();

                                }*/


                                    } else {
                                        if (pageNo == 1) {
                                            commonPojoList.clear();
                                            total_count.setText(tag+":0");
                                            no_data.setVisibility(View.VISIBLE);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }else{
                                    total_count.setText(tag+":0");
                                    no_data.setVisibility(View.VISIBLE);
                                }
                        }else{
                            total_count.setText(tag+":0");
                            no_data.setVisibility(View.VISIBLE);
                        }
                    }else{
                        total_count.setText(tag+":0");
                        no_data.setVisibility(View.VISIBLE);
                    }
                }else{
                    total_count.setText(tag+":0");

                    no_data.setVisibility(View.VISIBLE);
                }
                loader.setVisibility(View.GONE);


            }



            @Override
            public void onFailure(Call<InnerModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                Log.d("customers"," fail "+t.getMessage());
            }
        });


    }

    public void OnBackPRess(){
        Intent intent = new Intent(CommonPageActivityNew.this, AdminNewDashboard.class);
        intent.putExtra("usable",date_str);
        intent.putExtra("server",server_str);
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
         /*   case R.id.nabout_layout:
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
                search_layout.setVisibility(View.GONE);
                searchText.setText(null);
                pageNo=1;
                searchTextStr=null;
                getCustomerList();
                break;
        }
    }

}
