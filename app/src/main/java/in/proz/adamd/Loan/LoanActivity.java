package in.proz.adamd.Loan;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.proz.adamd.Adapter.ClaimAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.AdminNewDashboard;
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
import in.proz.adamd.Retrofit.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back_arrow,from_picker;
    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;

    String[] mimeTypes = {"image/jpeg", "image/png","image/jpg"};
    int first_pos=0;

    CommonClass commonClass = new CommonClass();
    ImageView frame_icon,pick_icon;
    private static final int BUFFER_SIZE = 1024 * 2;

    TextView frame_tag;
    TextView title;
    int position;
    LinearLayout apply_leave_layout,listLayout,request_layout,reset_layout,frame_layout;
    EditText edt_amount,edt_reason,edt_duration;
     TextView ed_fromdate;
    //RelativeLayout bottom_request_layout;
    RecyclerView recyclerView;
    int REQUEST_ONE=100,PICK_ONE=1;
    private static final int PICK_IMAGE = 1000;
    Uri imageUri;
    String imagePathArray, fileTypeArray;


    MultipartBody.Part body;
    TextView request_text;
    String date_main,claim_type;
    ProgressDialog progressDialog;
    TextView header;
    Spinner spinnerType ;

    List<String> loanTypeList =new ArrayList<>();
    MKLoader loader;
    TextView header_title;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";


     String imageFileName , imageFileExt;
    Bitmap bitmap;
     ImageView view_image;


   /* LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    CommonPojo commonPojo;
    TextView no_data;
    int main_change=0;



    EditText document;
    RelativeLayout picker_layout;



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
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
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
                callPermissionInticative(PICK_ONE,REQUEST_ONE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_loan);
        title = findViewById(R.id.title);
        title.setText("Loan List");
          initView();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            claim_type = b.getString("claim_type");
            commonPojo = (CommonPojo) b.getSerializable("claimList");
            if(commonPojo!=null){
                updateUI(commonPojo);
            }
                 position=b.getInt("position");
            if(position!=0) {
                updateAdminUI();
            }

        }


    }

    private void updateUI(CommonPojo commonPojo) {
        title.setText("Edit Loan");
        ed_fromdate.setText(commonPojo.getDate());
        String[]  splt = commonPojo.getDate().split("-");
        date_main = splt[2]+"-"+splt[1]+"-"+splt[0];
        edt_reason.setText(commonPojo.getReason());
        edt_amount.setText(commonPojo.getAmount());
        if(!TextUtils.isEmpty(commonPojo.getFiles())) {
            view_image.setVisibility(View.VISIBLE);
            Picasso.with(this).load(commonPojo.getFiles()).into(view_image);
        }

        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        frame_icon.setImageResource(R.drawable.calendar_icon_new);
        frame_tag.setText("Loan List");
        request_text.setText("Update");
       // header.setText("Edit Loan");
    }

    public  void initView(){
        if(!commonClass.isOnline(LoanActivity.this)){
            commonClass.showInternetWarning(LoanActivity.this);
        }
        frame_tag = findViewById(R.id.frame_tag);
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);




        header_title = findViewById(R.id.header_title);

        pick_icon = findViewById(R.id.pick_icon);
        pick_icon.setOnClickListener(this);
        document = findViewById(R.id.document);
        document.setOnClickListener(this);
        picker_layout = findViewById(R.id.picker_layout);
        picker_layout.setOnClickListener(this);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        no_data = findViewById(R.id.no_data);
        request_text = findViewById(R.id.request_text);

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            request_text.setText("Submit");
        }else{
            request_text.setText("Request");
        }


        CommonClass comm = new CommonClass();
       /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(LoanActivity.this,online_layout,online_text,online_icon);*/

         view_image = findViewById(R.id.view_image);
         loader = findViewById(R.id.loader);
        loanTypeList.add("Select");
        loanTypeList.add("Full Payment");
        loanTypeList.add("Installment");
        edt_duration = findViewById(R.id.edt_duration);
        spinnerType = findViewById(R.id.spinner);
        ArrayAdapter ad2  = new ArrayAdapter(this,R.layout.spinner_drop_down,loanTypeList);
        ad2.setDropDownViewResource( R.layout.spinner_drop_down);
        spinnerType.setAdapter(ad2);
       // header = findViewById(R.id.header);
        progressDialog = new ProgressDialog(LoanActivity.this);
        progressDialog.setCancelable(false);
        from_picker=findViewById(R.id.from_picker);
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        back_arrow = findViewById(R.id.back_arrow);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        request_layout = findViewById(R.id.request_layout);
        listLayout = findViewById(R.id.listLayout);
        reset_layout = findViewById(R.id.reset_layout);
        reset_layout.setOnClickListener(this);
        ed_fromdate = findViewById(R.id.ed_fromdate);
        ed_fromdate.setOnClickListener(this);
        edt_amount = findViewById(R.id.edt_amount);
        edt_reason = findViewById(R.id.edt_reason);

        // change_layout = findViewById(R.id.change_layout);
       // bottom_request_layout = findViewById(R.id.bottom_request_layout);
        back_arrow.setOnClickListener(this);
         //change_layout.setOnClickListener(this);
        from_picker.setOnClickListener(this);
        request_layout.setOnClickListener(this);
        if(commonClass.isOnline(LoanActivity.this)){
            header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
               // header_title.setText("Admin");

                updateAdminUI();
            }else {
               // header_title.setText("Employee");

                if(commonPojo==null) {
                    if(commonClass.isOnline(LoanActivity.this)) {
                        getList();
                    }else{
                        commonClass.showInternetWarning(LoanActivity.this);
                    }
                }
            }
        }
        requestMultiplePermissions();


        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);

         if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            updateAdminUI();
        }
    }
    public void BackUI(){
        title.setText("Loan List");
        commonPojo =null;
        frame_tag.setText("Apply Loan");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
        apply_leave_layout.setVisibility(View.GONE);
        // bottom_request_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
        getList();
    }
    private void callDashboard() {
        if(commonPojo!=null){
            BackUI();
        }else {
            if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminEmpNo")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminRole")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminName"))) {
                if (position != 0 && main_change==1) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    }, 2500);

                } else {
                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callDashboard();
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
    public  void  callImageMethod(){
        if (Build.VERSION.SDK_INT >= 30) {
            pickImage2(PICK_IMAGE);
        }else {
            if (checkPermission()) {
                pickImage2(PICK_IMAGE);
            } else {
                requestPermission(0,0);
            }
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
            case R.id.picker_layout:
                callAlertPicker();
                break;
            case R.id.document:
                callAlertPicker();
                break;
            case R.id.pick_icon:
                callAlertPicker();
                break;



            case R.id.ed_fromdate:
                datePicker(ed_fromdate,0);
                break;
            case R.id.from_picker:
                datePicker(ed_fromdate,0);
                break;
            case R.id.back_arrow:
             callDashboard();
                break;
            case R.id.frame_layout:
                commonPojo= null;
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    first_pos=1;
                    title.setText("Loan List");
                    frame_tag.setText("Apply Loan");
                    title.setText("Apply Loan");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                    apply_leave_layout.setVisibility(View.GONE);
                   // bottom_request_layout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    getList();
                }else{
                    first_pos=0;
                    callReset();
                    title.setText("Loan Request");
                    view_image.setVisibility(View.GONE);
                    //header.setText("Apply Loan");
                    request_text.setText("Request");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                    frame_tag.setText("Loan List");
                    apply_leave_layout.setVisibility(View.VISIBLE);
                    //bottom_request_layout.setVisibility(View.VISIBLE);
                    listLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.request_layout:
                checkValues();
                break;
            case R.id.reset_layout:
                callReset();
                break;

        }
    }

    private void getList() {
      //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(LoanActivity.this,"token"),
                commonClass.getDeviceID(LoanActivity.this)).create(ApiInterface.class);
        Call<ClaimModal> call;

            call=apiInterface.getLoanList();

        Log.d("leave_url"," url as "+call.request().url());
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

                                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                    if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                                        if(first_pos==1){
                                            apply_leave_layout.setVisibility(View.GONE);
                                            listLayout.setVisibility(View.VISIBLE);
                                            frame_tag.setText("Apply Loan");
                                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                                            ClaimAdapter adapter =new ClaimAdapter(LoanActivity.this,
                                                    response.body().getGetClaimList(),recyclerView,"loan","loan",loader);
                                            recyclerView.setAdapter(adapter);
                                        }else{
                                            updateAdminUI();
                                        }
                                    }else{
                                        updateAdminUI();
                                    }

                                 }else{

                                    if(commonPojo==null){
                                        apply_leave_layout.setVisibility(View.GONE);
                                        listLayout.setVisibility(View.VISIBLE);
                                        frame_tag.setText("Apply Loan");
                                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                                        ClaimAdapter adapter =new ClaimAdapter(LoanActivity.this,
                                                response.body().getGetClaimList(),recyclerView,"loan","loan",loader);
                                        recyclerView.setAdapter(adapter);
                                    }else{
                                        apply_leave_layout.setVisibility(View.VISIBLE);
                                        listLayout.setVisibility(View.GONE);
                                        frame_tag.setText("Loan List");
                                        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                                    }



                                }


                              }else{
                                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){

                                    updateAdminUI();
                                }
                                no_data.setVisibility(View.VISIBLE);
//                                apply_leave_layout.setVisibility(View.VISIBLE);
//                                listLayout.setVisibility(View.GONE);
                              //  commonClass.showError(LoanActivity.this,"No List Found..");
                            }
                        }else{
                            commonClass.showError(LoanActivity.this,response.body().getStatus());
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LoanActivity.this,mError.getError());
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
                        commonClass.showError(LoanActivity.this,mError.getError());
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
                commonClass.showError(LoanActivity.this,t.getMessage());
            }
        });
    }

    private void updateAdminUI() {
        title.setText("Loan Request");
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        frame_layout.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
            if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                frame_layout.setVisibility(View.VISIBLE);
                frame_tag.setText("Loan List");
            }
        }
    }

    private void checkValues() {
       /* if(spinnerType.getSelectedItemPosition()==0){
            commonClass.showAppToast(LoanActivity.this,"Select Payment Type");
        }else if(TextUtils.isEmpty(edt_duration.getText().toString())){
            commonClass.showAppToast(LoanActivity.this,"Enter Duration");
        }else*/
        if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
            commonClass.showWarning(LoanActivity.this,"Select Date");
        }else if(TextUtils.isEmpty(edt_amount.getText().toString())){
            commonClass.showWarning(LoanActivity.this,"Enter Amount");
        }else if(edt_amount.getText().toString().length()<4){
            commonClass.showWarning(LoanActivity.this,"Make sure entered amount is a 4-digit numbers");
        }else if(edt_amount.getText().toString().equals("0")){
            commonClass.showWarning(LoanActivity.this,"Enter Valid Amount");
        }else{
            int cont_amount =0;
            for(int i=0;i<edt_amount.getText().toString().length();i++){
                if(edt_amount.getText().toString().charAt(i)=='0'){
                    cont_amount+=1;
                }
            }
            if(cont_amount== edt_amount.getText().toString().length()){
                commonClass.showWarning(LoanActivity.this,"Enter Valid Amount");
            }else{
                if(TextUtils.isEmpty(edt_reason.getText().toString())){
                    commonClass.showWarning(LoanActivity.this,"Enter Reason");
                }else {
                    callRetrofit();
                }
            }
        }


    }

    private void callRetrofit() {
       // progressDialog.show();

        request_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(LoanActivity.this,"token"),
                commonClass.getDeviceID(LoanActivity.this)).create(ApiInterface.class);
       String leave_type = spinnerType.getSelectedItem().toString().toLowerCase();
       leave_type = leave_type.replace(" ","");
       
        Call<CommonPojo>    call =null;

        if(commonPojo!=null){
            if(body!=null){
                call  = apiInterface.updateLoanRequest(leave_type,date_main,edt_amount.getText().toString(),
                        edt_reason.getText().toString(),body,edt_duration.getText().toString(),commonPojo.getId());
            }else{
                call= apiInterface.updateLoanRequestWithoutImage(leave_type,date_main,edt_amount.getText().toString(),
                        edt_reason.getText().toString(),edt_duration.getText().toString(),commonPojo.getId());
            }
        }else{
            if(body!=null){
                call  = apiInterface.insertLoanRequest(leave_type,date_main,edt_amount.getText().toString(),
                        edt_reason.getText().toString(),body,edt_duration.getText().toString());
            }else{
                call= apiInterface.insertLoanRequestWithoutImage(leave_type,date_main,edt_amount.getText().toString(),
                        edt_reason.getText().toString(),edt_duration.getText().toString());
            }

        }

        
        Log.d("claim_url"," url as "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                if(response.isSuccessful()){
                    Log.d("claim_url"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(LoanActivity.this,response.body().getData());
                            frame_tag.setText("Apply Loan");
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                           // apply_leave_layout.setVisibility(View.GONE);
                            //bottom_request_layout.setVisibility(View.GONE);
                            //listLayout.setVisibility(View.VISIBLE);
                            callReset();
                            commonPojo =null;
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                main_change=1;
                                callDashboard();
                               // updateAdminUI();
                            }else {

                                getList();
                            }
                        }else{
                            commonClass.showError(LoanActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(LoanActivity.this,mError.getError());
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

                        commonClass.showError(LoanActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
              //  progressDialog.dismiss();
                request_layout.setEnabled(true);
                loader.setVisibility(View.GONE);
                Log.d("claim_url"," error "+t.getMessage());
                commonClass.showError(LoanActivity.this,t.getMessage());
            }
        });
    }
    private void callCustom1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imageFileName =commonClass.getFileNameFromUri(LoanActivity.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(LoanActivity.this,uri);


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

    private void callPermissionInticative(int img_request, int permission_request) {
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Log.d("feedback_request"," if con 1 ");
                requestPermission(permission_request,0);
            }else{
                Log.d("feedback_request","if con 2");
                //pickImage1(img_request);
                callCustom1();
            }
        }else{
            if (checkPermission()) {
                Log.d("feedback_request","if con 3");
                //pickImage1(img_request);
                callCustom1();


            } else {
                Log.d("feedback_request","if con 4");
                requestPermission(permission_request,1);
            }
        }
    }
    private void pickImage1(int i) {
        Log.d("feedback_request"," pick image request "+i);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  // Accept all MIME types

        startActivityForResult(intent, REQUEST_ONE);
    }
    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(LoanActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if(!Environment.isExternalStorageManager()){
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }else{
                                pickImage1(PICK_ONE);

                            }
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                             PermissionToken token) {
                        Log.d("feedback_request"," permission not granded ");
                        /* ... */}
                }).check();
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
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoanActivity.this,
                READ_EXTERNAL_STORAGE);
        int result1=ContextCompat.checkSelfPermission(LoanActivity.this,
                WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED;
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ONE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                File file = new File(getPathFromUri(uri));
                String path = getFilePathFromURI(LoanActivity.this,uri);
                String pdfname = String.valueOf(Calendar.getInstance().getTimeInMillis());
                File file1 = new File(path);
                String mimeType = this.getContentResolver().getType(uri);

                Log.d("getURIError"," file1 "+file1+" path "+path);
                RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file1);
                body = MultipartBody.Part.createFormData("file", file1.getName(), requestBody);
                RequestBody filename1 = RequestBody.create(MediaType.parse(mimeType), pdfname);
                if(data.getData()!=null){
                    String filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
                    edt_document.setText(filename);
                }
                Log.d("getURIError"," uri "+uri+" mime "+mimeType+" bosy  "+body);
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ONE && resultCode == RESULT_OK) {
            if (data != null) {
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
        }else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (imageUri != null) {
                    document.setText("Choose Image to upload");
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


    public void datePicker(TextView editText,int status){

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
                        date_main = year+"-"+str_month+"-"+str_date;
                        editText.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void callReset() {
        body=null;
        title.setText("Loan List");
        edt_duration.setText(null);
        spinnerType.setSelection(0);
        ed_fromdate.setText(null);
        edt_amount.setText(null);
        edt_reason.setText(null);
         document.setText("Choose Image to upload");
        view_image.setImageDrawable(null);
        view_image.setVisibility(View.GONE);
    }


    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            // create folder if not exists

            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.d("getURIError"," permission granded ");
                            //Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            Log.d("getURIError"," permission denied ");

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.d("getURIError"," error");
                      //  Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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


}
