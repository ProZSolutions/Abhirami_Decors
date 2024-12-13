package in.proz.adamd.Claim;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import in.proz.adamd.Adapter.ClaimAdapter;
import in.proz.adamd.AdminModule.AdminNewApprovals;
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

public class ClaimActivity extends AppCompatActivity implements View.OnClickListener {
     ImageView back_arrow,from_picker;
     CommonClass commonClass = new CommonClass();
    int first_pos=0;

    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    LinearLayout apply_leave_layout,listLayout,request_layout,reset_layout;
     EditText ed_fromdate,edt_amount,edt_reason;
 
   //  TextView change_layout;
     Uri imageUri;
    String imagePathArray, fileTypeArray;

    RecyclerView recyclerView;
    int REQUEST_ONE=100,PICK_ONE=1;
    MultipartBody.Part body;
    String date_main,claim_type;
    ProgressDialog progressDialog;
    TextView header;

     ImageView view_image;
    private static final int RequestPermissionCode = 2000;
    private static final int PICK_IMAGE = 1000;
     LinearLayout frame_layout;
     MKLoader loader;
    String[] mimeTypes = {"image/jpeg", "image/png","image/jpg"};

    ImageView frame_icon;
    TextView frame_tag;
    int main_change=0;
    int postition=-1;
    CommonPojo commonPojo;
    String imageFileName , imageFileExt;
    Bitmap bitmap;
/*    LinearLayout online_layout;
    ImageView online_icon;*/
    TextView request_text;
    TextView title,no_data,header_title;
    EditText document;
    ImageView pick_icon;
    RelativeLayout picker_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);
        header_title=findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        // header_title.setText("Employee");
         initView();

        title = findViewById(R.id.title);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            claim_type = b.getString("claim_type");
            commonPojo = (CommonPojo) b.getSerializable("claimList");
            if(commonPojo!=null){
                updateUI(commonPojo);
            }
                 postition=b.getInt("position");
            if(postition>0){
                updateAdminUI();
            }

        }
        Log.d("frame_tag"," get claim "+claim_type);
        if(!TextUtils.isEmpty(claim_type)){
            if(claim_type.equals("claim")){
                if(commonPojo!=null){
                    title.setText("Edit Claim");
                  //  header.setText("Edit Claim");
                    frame_tag.setText("Apply Claim");

                }else{
                    title.setText("Claim List");
                  //  header.setText("Apply Claim");
                    frame_tag.setText("Apply Claim");
                }

               // change_layout.setText("Claim List");
            }else{
                if(commonPojo!=null){
                    title.setText("Edit Advance Claim");
                 //   header.setText("Edit Advance Claim");
                    frame_tag.setText("Apply Advance Claim");
                }else{
                    title.setText("Advance Claim");
                    //header.setText("Apply Advance Claim");
                    frame_tag.setText("Apply Advance Claim");
                }

               // change_layout.setText("Advance Claim List");
            }
        }
      
        getList();
    }

    private void updateAdminUI() {
        title.setText("Claim Request");
        frame_tag.setText("Claim List");
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
         frame_layout.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
            if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                frame_layout.setVisibility(View.VISIBLE);
                frame_tag.setText("Claim List");
            }
        }
    }

    private void updateUI(CommonPojo commonPojo) {
        if(!TextUtils.isEmpty(commonPojo.getDate())){
            String[] spl = commonPojo.getDate().split("-");
            date_main = spl[2]+"-"+spl[1]+"-"+spl[0];
        }
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
        apply_leave_layout.setVisibility(View.VISIBLE);
         listLayout.setVisibility(View.GONE);
         ed_fromdate.setText(commonPojo.getDate());
         edt_reason.setText(commonPojo.getReason());
         edt_amount.setText(commonPojo.getAmount());
        request_text.setText("Update");
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        frame_icon.setImageResource(R.drawable.calendar_icon_new);
        Log.d("frame_tag"," claim in updte ui "+claim_type);
        if(claim_type.equals("claim")){
            frame_tag.setText("Claim List");
        }else{
            frame_tag.setText("Advanced Claim List");
        }

        if(!TextUtils.isEmpty(commonPojo.getFiles())) {
            view_image.setVisibility(View.VISIBLE);
            Picasso.with(this).load(commonPojo.getFiles()).into(view_image);
        }
    }

    public  void initView(){

        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        //nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        
        document = findViewById(R.id.document);
        pick_icon = findViewById(R.id.pick_icon);
        picker_layout = findViewById(R.id.picker_layout);
        document.setOnClickListener(this);
        pick_icon.setOnClickListener(this);
        picker_layout.setOnClickListener(this);
        no_data = findViewById(R.id.no_data);
        request_text = findViewById(R.id.request_text);

        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            request_text.setText("Submit");
          //  header_title.setText("Admin");
        }else{
            //header_title.setText("Employee");
            request_text.setText("Request");
        }


      /*   online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(ClaimActivity.this,online_layout,online_text,online_icon);*/


        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);
        loader = findViewById(R.id.loader);
    
         view_image=findViewById(R.id.view_image);
         view_image.setOnClickListener(this);
        listLayout = findViewById(R.id.listLayout);
     //   header = findViewById(R.id.title);
        progressDialog = new ProgressDialog(ClaimActivity.this);
        progressDialog.setCancelable(false);
        from_picker=findViewById(R.id.from_picker);
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        back_arrow = findViewById(R.id.back_arrow);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        request_layout = findViewById(R.id.request_layout);
        reset_layout = findViewById(R.id.reset_layout);
        reset_layout.setOnClickListener(this);
        ed_fromdate = findViewById(R.id.ed_fromdate);
        edt_amount = findViewById(R.id.edt_amount);
        edt_reason = findViewById(R.id.edt_reason);
      
         //change_layout = findViewById(R.id.change_layout);
          back_arrow.setOnClickListener(this);
        // change_layout.setOnClickListener(this);
        from_picker.setOnClickListener(this);
        request_layout.setOnClickListener(this);
     }
    private void callDashboard() {
        if(commonPojo!=null){
            BackUI();
        }else {
            if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminEmpNo")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminRole")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminName"))) {
                if (postition != 0 && main_change==1) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                            intent.putExtra("position", postition);
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
    private void pickImage2(int i) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Fromthe Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camintent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(camintent,PICK_IMAGE);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
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
            case R.id.picker_layout:
                callAlertPicker();
                break;
            case R.id.document:
                callAlertPicker();
                break;
            case R.id.pick_icon:
                callAlertPicker();
                break;


            case R.id.from_picker:
                datePicker(ed_fromdate,0);
                break;
            case R.id.back_arrow:
                callDashboard();
                break;
            case R.id.frame_layout:
                    changeUI();
                break;
            case R.id.request_layout:
                checkValues();
                break;
            case R.id.reset_layout:
                callReset();
                break;

        }
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
                callPermissionInticative(PICK_ONE,REQUEST_ONE);
            }
        });
    }


    private void BackUI(){
        commonPojo = null;
        if(!TextUtils.isEmpty(claim_type)){
            if(commonPojo!=null){
                if(claim_type.equals("claim")){
                    title.setText("Claim List");
                     frame_tag.setText("Edit Claim");
                }else{
                    title.setText("Advance Claim List");
                    frame_tag.setText("Edit Advance Claim");
                }
            }else{
                if(claim_type.equals("claim")){
                    title.setText("Claim List");
                    frame_tag.setText("Apply Claim");
                }else{
                    title.setText("Claim List");
                    frame_tag.setText("Apply Advance Claim");
                }
            }

        }
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

        apply_leave_layout.setVisibility(View.GONE);
        //bottom_request_layout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            updateAdminUI();
        }else{
            getList();
        }
    }
    private void changeUI() {
        commonPojo = null;
        if(apply_leave_layout.getVisibility()==View.VISIBLE){
            first_pos=1;
            if(!TextUtils.isEmpty(claim_type)){
                if(commonPojo!=null){
                    if(claim_type.equals("claim")){
                        title.setText("Claim List");
                        frame_tag.setText("Edit Claim");
                     }else{
                        title.setText("Advance Claim List");
                        frame_tag.setText("Edit Advance Claim");
                     }
                }else{
                    if(claim_type.equals("claim")){
                        title.setText("Claim List");
                        frame_tag.setText("Apply Claim");
                     }else{
                        title.setText("Advance Claim List");
                        frame_tag.setText("Apply Advance Claim");
                     }
                }

            }
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

            apply_leave_layout.setVisibility(View.GONE);
            //bottom_request_layout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                    getList();
                }else{
                    updateAdminUI();
                }

            }else{
                getList();
            }

        }else{
            first_pos=0;
            callReset();;
            view_image.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo"))){
                request_text.setText("Submit");
            }else{
                request_text.setText("Request");
            }
              if(!TextUtils.isEmpty(claim_type)){

                    if(claim_type.equals("claim")){
                        title.setText("Claim Request");
                        frame_tag.setText("Claim List");
                      //  header.setText("Apply Claim");
                        //title.setText("Apply Claim");

                    }else{
                       // header.setText("Apply Advance Claim");
                        frame_tag.setText("Advance Claim List");
                        title.setText("Advance Claim Request");
                    }


            }
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
            apply_leave_layout.setVisibility(View.VISIBLE);
            //bottom_request_layout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }

    private void checkValues() {
        if(TextUtils.isEmpty(ed_fromdate.getText().toString())){
            commonClass.showWarning(ClaimActivity.this,"Select Date");
        }else if(TextUtils.isEmpty(edt_amount.getText().toString())){
            commonClass.showWarning(ClaimActivity.this,"Enter Amount");
        }else if(edt_amount.getText().toString().length()<2){
            commonClass.showWarning(ClaimActivity.this,"Make sure entered amount is a 2-digit numbers");
        }else {
            if(commonPojo!=null){
                if(TextUtils.isEmpty(commonPojo.getFiles())){
                    if (body==null){
                        commonClass.showWarning(ClaimActivity.this,"Select Image/Capture Image");
                    }else{
                        callConditionalLogin();
                    }
                }else{
                    callConditionalLogin();
                }
            }else{
                if (body==null){
                    commonClass.showWarning(ClaimActivity.this,"Select Image/Capture Image");
                }else{
                    callConditionalLogin();
                }
            }
        }



    }

    private void callConditionalLogin() {
        int cont_amount =0;
        for(int i=0;i<edt_amount.getText().toString().length();i++){
            if(edt_amount.getText().toString().charAt(i)=='0'){
                cont_amount+=1;
            }
        }
        if(cont_amount==edt_amount.getText().toString().length()){
            commonClass.showWarning(ClaimActivity.this,"Enter Valid Amount");
        }else{
            if(TextUtils.isEmpty(edt_reason.getText().toString())){
                commonClass.showWarning(ClaimActivity.this,"Enter Reason");
            } else{
                if(commonPojo!=null){
                    if(!TextUtils.isEmpty(commonPojo.getFiles())){
                        callRetrofit();
                    }else{
                        if (body != null) {
                            callRetrofit();
                        } else {
                            commonClass.showWarning(ClaimActivity.this, "Upload/Capture an Image ");
                        }                    }
                }else {
                    if (body != null) {
                        callRetrofit();
                    } else {
                        commonClass.showWarning(ClaimActivity.this, "Upload/Capture an Image ");
                    }
                }



            }
        }
    }

    private void callRetrofit() {
       // progressDialog.show();
        request_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(ClaimActivity.this,"token"),
                commonClass.getDeviceID(ClaimActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call=null;
        if(commonPojo!=null) {

            if (!TextUtils.isEmpty(claim_type)) {
                if (claim_type.equals("claim")) {
                    Log.d("getList"," con 1 "+body);
                    if(body!=null) {
                        call = apiInterface.updateClaimRequest(date_main, edt_amount.getText().toString(),
                                edt_reason.getText().toString(), body, commonPojo.getId());
                    }else{
                        call= apiInterface.updateClaimWithoutImage(date_main, edt_amount.getText().toString(),
                                edt_reason.getText().toString(),  commonPojo.getId());
                    }

                } else {
                    Log.d("getList"," con 2 "+body);
                    if(body!=null) {
                        Log.d("getList"," con 21 ");
                        call = apiInterface.updateadvanceClaimRequest(date_main, edt_amount.getText().toString(),
                                edt_reason.getText().toString(), body, commonPojo.getId());
                    }else{
                        Log.d("getList"," con 21 ");
                        call = apiInterface.updateAdvancedWithoutImage(date_main, edt_amount.getText().toString(),
                                edt_reason.getText().toString(),  commonPojo.getId());
                    }

                }
            }
        }else {
            if (!TextUtils.isEmpty(claim_type)) {
                if (claim_type.equals("claim")) {
                    Log.d("getList"," con 3 ");
                    call = apiInterface.insertClaimRequest(date_main, edt_amount.getText().toString(),
                            edt_reason.getText().toString(), body);

                } else {
                    Log.d("getList"," con 4 ");
                    call = apiInterface.advanceClaimRequest(date_main, edt_amount.getText().toString(), edt_reason.getText().toString(), body);

                }
            }
        }
        Log.d("claim_url"," url as "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
               // progressDialog.dismiss();
                request_layout.setEnabled(true);
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    Log.d("claim_url"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(ClaimActivity.this,response.body().getData());
                            callReset();
                            commonPojo=null;
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
                                main_change=1;
                               callDashboard();
                            }else{
                                getList();
                            }
                            recyclerView.setAdapter(null);
                            listLayout.setVisibility(View.VISIBLE);
                            if(!TextUtils.isEmpty(claim_type)){
                                if(claim_type.equals("claim")){
                                    title.setText("Claim List");
                                    frame_tag.setText("Apply Claim");
                                }else{
                                    title.setText("Advanced Claim List");
                                    frame_tag.setText("Apply Advance Claim");
                                }
                            }
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                            apply_leave_layout.setVisibility(View.GONE);
                           // bottom_request_layout.setVisibility(View.GONE);
                        }else{
                            commonClass.showError(ClaimActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(ClaimActivity.this,mError.getError());
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

                        commonClass.showError(ClaimActivity.this,mError.getError());
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
                request_layout.setEnabled(true);

                loader.setVisibility(View.GONE);
                    Log.d("claim_url"," error "+t.getMessage());
                    commonClass.showError(ClaimActivity.this,t.getMessage());
            }
        });
    }

    private void getList() {
        loader.setVisibility(View.VISIBLE);
        //progressDialog.show();
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(ClaimActivity.this,"token"),
                commonClass.getDeviceID(ClaimActivity.this)).create(ApiInterface.class);
        Call<ClaimModal> call;
        if(claim_type.equals("claim")){
            call=apiInterface.getClaimList();
        }else{
            call=apiInterface.getAdvancedClaimList();
        }
        Log.d("leave_url"," url as "+call.request().url());
        call.enqueue(new Callback<ClaimModal>() {
            @Override
            public void onResponse(Call<ClaimModal> call, Response<ClaimModal> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("leave_url"," res[onse "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            Log.d("leave_url","suze "+response.body().getGetClaimList().size());
                            if(response.body().getGetClaimList().size()!=0){
                                no_data.setVisibility(View.GONE);

                                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                    if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                                        if(first_pos==1){
                                            apply_leave_layout.setVisibility(View.GONE);
                                            listLayout.setVisibility(View.VISIBLE);
                                            ClaimAdapter adapter =new ClaimAdapter(ClaimActivity.this,
                                                    response.body().getGetClaimList(),recyclerView,claim_type,claim_type,loader);
                                            recyclerView.setAdapter(adapter);
                                        }else{
                                            updateAdminUI();
                                        }
                                    }else{
                                        updateAdminUI();
                                    }

                                }else{
                                    ClaimAdapter adapter =new ClaimAdapter(ClaimActivity.this,
                                            response.body().getGetClaimList(),recyclerView,claim_type,claim_type,loader);
                                    recyclerView.setAdapter(adapter);
                                }






                            }else{
                                no_data.setVisibility(View.VISIBLE);

                            }
                        }else{
                            commonClass.showError(ClaimActivity.this,response.body().getStatus());
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(ClaimActivity.this,mError.getError());
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
                        commonClass.showError(ClaimActivity.this,mError.getError());
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
                commonClass.showError(ClaimActivity.this,t.getMessage());
            }
        });
    }

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
                        imageFileName =commonClass.getFileNameFromUri(ClaimActivity.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(ClaimActivity.this,uri);


                        Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            view_image.setImageBitmap(bitmap);
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

    private void pickImage1(int i) {
        Log.d("feedback_request"," pick image request "+i);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  // Accept all MIME types

        startActivityForResult(intent, REQUEST_ONE);
    }
    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(ClaimActivity.this)
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
                                pickImage2(PICK_ONE);

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
        int result = ContextCompat.checkSelfPermission(ClaimActivity.this,
                READ_EXTERNAL_STORAGE);
        int result1=ContextCompat.checkSelfPermission(ClaimActivity.this,
                WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED;
    }

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
                    document.setText("Choose Image for Upload");
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

   

    public void datePicker(EditText editText,int status){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        view.setMaxDate(System.currentTimeMillis());
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
        if(claim_type.equals("claim")){
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }else{
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }
    private void callReset() {
        body=null;
        ed_fromdate.setText(null);
        edt_amount.setText(null);
        edt_reason.setText(null);
        document.setText("Choose Image for Upload");
         view_image.setImageDrawable(null);
        view_image.setVisibility(View.GONE);
    }

 
}
