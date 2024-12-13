package in.proz.adamd.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

 import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;

import in.proz.adamd.Adapter.ShiftAdapter;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.ShiftModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftActivity  extends AppCompatActivity implements View.OnClickListener {
    TextView title;
    ImageView back_arrow;
    TextView no_data;
    RecyclerView recyclerView;
   /* LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    ProgressDialog progressDialog;
    MKLoader loader;
    ImageView profile_icon;
    TextView profile_text;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    CommonClass commonClass=new CommonClass();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifthistory);
         initView();
    }
    public void initView(){
        profile_icon = findViewById(R.id.profile_icon);
        profile_text = findViewById(R.id.profile_text);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        no_data = findViewById(R.id.no_data);
        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);

        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        CommonClass comm = new CommonClass();
        TextView header_title = findViewById(R.id.header_title);
        profile_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        profile_text.setTextColor(getApplicationContext().getColor(R.color.black));
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));
/*
        if(!TextUtils.isEmpty(comm.getSharedPref(getApplicationContext(),"AdminEmpNo"))){
           // header_title.setText("Admin");
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
           // header_title.setText("Employee");
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/
      /*  online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(ShiftActivity.this,online_layout,online_text,online_icon);*/
        loader = findViewById(R.id.loader);
        progressDialog = new ProgressDialog(ShiftActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        title = findViewById(R.id.title);
        title.setText("Shift History");
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        getList();
    }

    private void getList() {
       // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(ShiftActivity.this,"token"),
                commonClass.getDeviceID(ShiftActivity.this)).create(ApiInterface.class);
        Call<ShiftModal> call = apiInterface.getShiftList();
        call.enqueue(new Callback<ShiftModal>() {
            @Override
            public void onResponse(Call<ShiftModal> call, Response<ShiftModal> response) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("atten-list"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            if(response.body().getCommonPojoList().size()!=0) {
                                no_data.setVisibility(View.GONE);
                                ShiftAdapter adapter = new ShiftAdapter(ShiftActivity.this
                                        , response.body().getCommonPojoList(), recyclerView);
                                recyclerView.setAdapter(adapter);
                            }else{no_data.setVisibility(View.VISIBLE);}
                        }else{
                            no_data.setVisibility(View.VISIBLE);
                            commonClass.showError(ShiftActivity.this,response.body().getStatus());
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(ShiftActivity.this,mError.getError());
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

                        commonClass.showError(ShiftActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ShiftModal> call, Throwable t) {
           // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                commonClass.showError(ShiftActivity.this,t.getMessage());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressCalled();
    }
    public void backPressCalled(){
        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        int id=  view.getId();
        switch (id){
            case R.id.nprofile_layout:
                Intent intentp=new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentp);
                break;
            case R.id.nhome_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intentabout1);
                break;
            /*case R.id.nabout_layout:
                Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentabout);
                break;*/
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent152 = new Intent(getApplicationContext(), MapCurrentLocation.class);
                startActivity(intent152);
                break;
            case R.id.back_arrow:
                backPressCalled();
                break;
        }
    }
}
