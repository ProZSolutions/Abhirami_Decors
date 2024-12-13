package in.proz.adamd.Request;

import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import in.proz.adamd.Adapter.AssetAdapter;
import in.proz.adamd.Adapter.TicketAdater;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.SubjectDropDown;
import in.proz.adamd.ModalClass.TicketDropDownModal;
import in.proz.adamd.ModalClass.TicketModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.Retrofit.FileUtils;
import in.proz.adamd.SQLiteDB.AssetDropDown;
import in.proz.adamd.SQLiteDB.TicketDropDown;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner ticspinnerRequest,ticspinnerPriority;
    EditText ticedt_issue_details;
     TextView re_text,tic_re_text;
    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;

    RelativeLayout picker_layout;
     EditText document;
     ImageView pick_icon;
    int  ticPICK_ONE=1;
    String imagePathArray, fileTypeArray;

    int REQUEST_ONE=100,PICK_ONE=1;
    private static final int PICK_IMAGE = 1000;
    MultipartBody.Part body;
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";



    private static final int RequestPermissionCode = 2000;
     LinearLayout ticrequest_layout, ticreset_layout, ticapply_leave_layout, ticlistLayout;
    ImageView back_arrow;
    RecyclerView ticrecyclerView;
     TextView title,header_title;
    CommonClass ticcommonClass = new CommonClass();
    ProgressDialog ticprogressDialog;
    MKLoader loader;

    TicketDropDown ticketDropDown;
    List<String> ticketNameList = new ArrayList<>();
    CommonPojo ticcommonPojo;
    List<String> priorityList = new ArrayList<>();
     LinearLayout ticframe_layout;
    ImageView ticframe_icon;
    String imageFileName , imageFileExt;
    Bitmap bitmap;
    TextView ticframe_tag;
    LinearLayout ticketFragment,assetFragment;

int position=-1;


    ///only header functinality
    LinearLayout asset_layout,ticket_layout;
    String[] mimeTypes = {"image/jpeg", "image/png","image/jpg"};

    ImageView ticket_icon,asset_icon;

     ImageView view_image;
     LinearLayout doc_upload_layout;


    TextView ticket_text,asset_text;
    RelativeLayout header_relative;
    //// only header functionality



    ///only asset functionality
    Spinner spinnerRequest,spinnerSubRequest,spinnerNameRequest;
    EditText no_of_quantity, edt_issue_details,edt_configuration;
    LinearLayout request_layout, reset_layout, apply_leave_layout, listLayout;
     RecyclerView recyclerView;
 //   TextView change_layout,header;
    CommonClass commonClass = new CommonClass();
    // ProgressDialog progressDialog;
    AssetDropDown assetDropDown;
    CommonPojo commonPojo;
     List<String> mainDropList,SubDropList,assetDropList;

 //   LinearLayout online_layout;
    Uri imageUri;
    int first_pos_ass =0,first_pos_tic=0;
/*    ImageView online_icon;
    TextView online_text;*/

    String request_type;

    //// only assetr functionalist
TextView no_data,no_data_tic;
   int main_change=0,main_change2=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_request_layout);
        no_data = findViewById(R.id.no_data);
        no_data_tic = findViewById(R.id.no_data_tic);
         priorityList.add("Select");
        priorityList.add("Urgent");
        priorityList.add("High");
        priorityList.add("Medium");
        priorityList.add("Low");
         initView();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            request_type = b.getString("request_type");
            position = b.getInt("position");
            if(request_type!= null){
                if(request_type.equals("asset")){
                    requestUpdateUI(1);
                    if(position>0){
                        requestAdminUpdateUI(1);
                    }
                }else{
                    requestUpdateUI(2);
                    if(position>0){
                        requestAdminUpdateUI(2);
                    }
                }
            }
            ticcommonPojo = (CommonPojo) b.getSerializable("saved_details");
            if(ticcommonPojo!=null){
                updateEditUI(ticcommonPojo);
            }
            commonPojo = (CommonPojo) b.getSerializable("saved_details_asset");
            if (commonPojo!=null) {
                updateEditUIAsset(commonPojo);
            }


        }

        getListTic();
    }


    private void updateEditUIAsset(CommonPojo commonPojo) {
        requestUpdateUI(1);
        re_text.setText("Update");
       // header.setText("Edit Asset Request");
        ticketFragment.setVisibility(View.GONE);
        assetFragment.setVisibility(View.VISIBLE);
        apply_leave_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);

        ticframe_tag.setText("Asset List");
        ticframe_icon.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.calendar_icon_new));
        if(commonPojo!=null){
            edt_configuration.setText(commonPojo.getConfiguration());
            no_of_quantity.setText(commonPojo.getQuantity());
            edt_issue_details.setText(commonPojo.getDetails());
            updateAssetAdapter();
           /* updateSubAdapter();
            updateAssetAdapter();*/
        }
    }
    private void updateAssetAdapterAsset() {
        Log.d("dropdownurl"," sub "+spinnerSubRequest.getSelectedItem().toString());
        assetDropList.clear();
        assetDropList = assetDropDown.selectAllAssetNames(spinnerRequest.getSelectedItem().toString(),
                spinnerSubRequest.getSelectedItem().toString());
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,assetDropList);
        ad.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerNameRequest.setAdapter(ad);
        if(commonPojo!=null) {
            if (!TextUtils.isEmpty(commonPojo.getAsset_name())) {
                int po = assetDropList.indexOf(commonPojo.getAsset_name());
                spinnerNameRequest.setSelection(po);
            }
        }
    }
         private void updateAssetAdapter() {
            mainDropList = assetDropDown.selectAllMainCat();
            Log.d("mainDropList"," list "+mainDropList.size());
            ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,mainDropList);
            ad.setDropDownViewResource(R.layout.spinner_drop_down);
            spinnerRequest.setAdapter(ad);
            if(commonPojo!=null) {
                if (!TextUtils.isEmpty(commonPojo.getCategory_name())) {
                    Log.d("mainDropList"," cat name "+commonPojo.getCategory_name());
                    for(int i=0;i<mainDropList.size();i++){
                        Log.d("mainDropList"," lisr "+mainDropList.get(i));
                    }
                    int po = mainDropList.indexOf(commonPojo.getCategory_name());
                    spinnerRequest.setSelection(po);
                }
            }
        }


    private void updateEditUI(CommonPojo ticcommonPojo) {
        if (ticcommonPojo!=null){
            requestUpdateUI(2);
            if(!TextUtils.isEmpty(ticcommonPojo.getImage())) {
                view_image.setVisibility(View.VISIBLE);
                Picasso.with(this).load(ticcommonPojo.getImage()).into(view_image);
            }
            tic_re_text.setText("Update");
           // ticheader.setText("Edit Ticket Request");
            ticketFragment.setVisibility(View.VISIBLE);
            assetFragment.setVisibility(View.GONE);
            ticapply_leave_layout.setVisibility(View.VISIBLE);
            ticlistLayout.setVisibility(View.GONE);
            ticframe_tag.setText("Ticket List");
            ticframe_icon.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.calendar_icon_new));
            if(!TextUtils.isEmpty(ticcommonPojo.getType_name())){
                String pos =ticketDropDown.selectOnlyID(ticcommonPojo.getType_name());
                ticspinnerRequest.setSelection(Integer.parseInt(pos));
            }
            /*no_of_quantity.setText(ticcommonPojo.getQuantity());
            edt_configuration.setText(ticcommonPojo.getConfiguration());*/
            ticedt_issue_details.setText(ticcommonPojo.getDetails());
            //  edt_ticket_name.setText(ticcommonPojo.getTicket_name());
            int pos = priorityList.indexOf(ticcommonPojo.getPriority());
            ticspinnerPriority.setSelection(pos);
        }
    }

    private void initView() {


        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);

        picker_layout = findViewById(R.id.picker_layout);
        document = findViewById(R.id.document);
        pick_icon = findViewById(R.id.pick_icon);
        pick_icon.setOnClickListener(this);
        picker_layout.setOnClickListener(this);
        document.setOnClickListener(this);
        doc_upload_layout = findViewById(R.id.doc_upload_layout);


 
        view_image=findViewById(R.id.view_image);



        tic_re_text= findViewById(R.id.tic_re_text);
        re_text = findViewById(R.id.re_text);
         /*CommonClass comm = new CommonClass();
        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(RequestActivity.this,online_layout,online_text,online_icon);*/

      /*  header = findViewById(R.id.header);
        ticheader = findViewById(R.id.ticheader);*/
        requestMultiplePermissions();
        ///only header layout
        header_relative = findViewById(R.id.header_relative);
        asset_layout=findViewById(R.id.asset_layout);
        ticket_layout=findViewById(R.id.ticket_layout);
        ticket_icon=findViewById(R.id.ticket_icon);
        asset_icon=findViewById(R.id.asset_icon);
        ticket_text=findViewById(R.id.ticket_text);
        asset_text=findViewById(R.id.asset_text);
        asset_layout.setOnClickListener(this);
        ticket_layout.setOnClickListener(this);
        // only header layout


        //asset layout
        edt_configuration = findViewById(R.id.edt_configuration);
        mainDropList = new ArrayList<>();
        SubDropList = new ArrayList<>();
        assetDropList = new ArrayList<>();
        assetDropDown = new AssetDropDown(RequestActivity.this);
        assetDropDown.getWritableDatabase();
        spinnerRequest = findViewById(R.id.spinnerRequest);
        spinnerSubRequest = findViewById(R.id.spinnerSubRequest);
        spinnerNameRequest = findViewById(R.id.spinnerNameRequest);
        no_of_quantity = findViewById(R.id.no_of_quantity);
        edt_issue_details = findViewById(R.id.edt_issue_details);
        request_layout = findViewById(R.id.request_layout);
        reset_layout = findViewById(R.id.reset_layout);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        listLayout = findViewById(R.id.listLayout);
        recyclerView = findViewById(R.id.recyclerView);
        reset_layout.setOnClickListener(this);
        request_layout.setOnClickListener(this);


        assetFragment = findViewById(R.id.assetFragment);
        ticketFragment = findViewById(R.id.ticketFragment);
        ticframe_icon = findViewById(R.id.frame_icon);
        ticframe_tag = findViewById(R.id.frame_tag);
        ticframe_layout = findViewById(R.id.frame_layout);
        ticframe_layout.setOnClickListener(this);


        loader = findViewById(R.id.loader);
        ticketDropDown = new TicketDropDown(RequestActivity.this);
        ticketDropDown.getWritableDatabase();
        ticprogressDialog = new ProgressDialog(RequestActivity.this);
        ticprogressDialog.setCancelable(false);
        title=  findViewById(R.id.title);
        header_title=  findViewById(R.id.header_title);
        title.setText("Assets");
       /* edt_ticket_name=findViewById(R.id.edt_ticket_name);
        edt_configuration=findViewById(R.id.edt_configuration);*/
        back_arrow = findViewById(R.id.back_arrow);
        ticspinnerRequest = findViewById(R.id.ticspinnerRequest);
        ticspinnerPriority = findViewById(R.id.ticspinnerPriority);
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,priorityList);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        ticspinnerPriority.setAdapter(ad);


        // no_of_quantity = findViewById(R.id.no_of_quantity);
        ticedt_issue_details = findViewById(R.id.ticedt_issue_details);
        ticrequest_layout = findViewById(R.id.ticrequest_layout);
        ticreset_layout = findViewById(R.id.ticreset_layout);
        ticapply_leave_layout = findViewById(R.id.ticapply_leave_layout);
        ticlistLayout = findViewById(R.id.ticlistLayout);

        ticrecyclerView = findViewById(R.id.ticrecyclerView);
        //change_layout = findViewById(R.id.change_layout);
        back_arrow.setOnClickListener(this);

        ticreset_layout.setOnClickListener(this);
        ticrequest_layout.setOnClickListener(this);


        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        if(commonClass.isOnline(RequestActivity.this)){
            getAllDropDown(1); //main
            getList();
        }
        spinnerRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinnerRequest.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                if(i!=0){
                    updateSubAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSubRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinnerSubRequest.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                Log.d("dropdownurl"," in selection "+spinnerSubRequest.getSelectedItem().toString());
                if(i!=0){
                    updateAssetAdapterAsset();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerNameRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinnerNameRequest.getSelectedView()).setTextColor(getResources().getColor(R.color.black));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // asser layout





        // change_layout.setOnClickListener(this);
        GridLayoutManager layoutManager1=new GridLayoutManager(getApplicationContext(),1);
        ticrecyclerView.setLayoutManager(layoutManager1);
        if(ticcommonClass.isOnline(RequestActivity.this)) {
            getDropDownList();
        }
        ticspinnerRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) ticspinnerRequest.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ticspinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) ticspinnerPriority.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));



        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            re_text.setText("Submit");
            tic_re_text.setText("Submit");
           // header_title.setText("Admin");
        }else{
            //header_title.setText("Employee");
         }
    }
    private void getAllDropDown(int status) {
        //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        ////catergort Drop Down
        Call<SubjectDropDown> call = apiInterface.getMainCatDropDown();
        Log.d("mainDropList"," call "+call.request().url());
        call.enqueue(new Callback<SubjectDropDown>() {
            @Override
            public void onResponse(Call<SubjectDropDown> call, Response<SubjectDropDown> response) {
                //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("mainDropList"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("mainDropList"," status "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            Log.d("mainDropList"," size "+response.body().getGetDropDownList().size());
                            if(response.body().getGetDropDownList().size()!=0){
                                assetDropDown.deleteAll(RequestActivity.this);
                                for (int i=0;i<response.body().getGetDropDownList().size();i++){
                                    CommonPojo commonPojo = response.body().getGetDropDownList().get(i);
                                    assetDropDown.insertData(commonPojo.getId(),commonPojo.getAsset_name(),
                                            commonPojo.getCat_id(),commonPojo.getCategory_name(),
                                            commonPojo.getSubcategory_id(),commonPojo.getSubcategory_name());
                                }
                                updateAssetAdapter();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SubjectDropDown> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("dropdownurl"," error "+t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackIntent();
    }


    public void changePosition(int pos){
        if(pos==1){
            commonPojo=null;
            title.setText("Asset  List");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                asset_layout.setBackground(getApplicationContext().getDrawable(R.drawable.reactangle_padding_with_border));
                asset_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                asset_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                asset_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }
            ticketFragment.setVisibility(View.GONE);
            assetFragment.setVisibility(View.VISIBLE);

            apply_leave_layout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            ticframe_tag.setText("Asset Request");
            ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
            getList();
        }else{
            ticcommonPojo=null;
            title.setText("Ticket List");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ticket_layout.setBackground(getApplicationContext().getDrawable(R.drawable.reactangle_padding_with_border));
                ticket_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                ticket_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                ticket_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }
            ticketFragment.setVisibility(View.VISIBLE);
            assetFragment.setVisibility(View.GONE);
            ticapply_leave_layout.setVisibility(View.GONE);
            ticlistLayout.setVisibility(View.VISIBLE);
            ticframe_tag.setText("Ticket Request");
            ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

            getListTic();
        }
    }

    private void callBackIntent() {
        if(ticcommonPojo!=null || commonPojo!=null){
            if(commonPojo!=null){
                changePosition(1);
            }else{
                changePosition(2);
            }
        }else {
            if (position > 0 && main_change==1) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                }, 2500);
            } else {
                Intent intent = new Intent(RequestActivity.this, DashboardNewActivity.class);
                startActivity(intent);
            }
        }

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
            case R.id.reset_layout:
                callResetUIAsset();
                break;
            case R.id.request_layout:
                if(spinnerRequest.getSelectedItemPosition()==0){
                    commonClass.showWarning(RequestActivity.this,"Please select asset category");
                }else if(spinnerSubRequest.getSelectedItemPosition()==0){
                    commonClass.showWarning(RequestActivity.this,"Please select asset sub category");
                }else if(spinnerNameRequest.getSelectedItemPosition()==0){
                    commonClass.showWarning(RequestActivity.this,"Please select asset name");
                }else if(TextUtils.isEmpty(no_of_quantity.getText().toString())){
                    commonClass.showWarning(RequestActivity.this,"Please enter no.of quantity");
                } else if(TextUtils.isEmpty(edt_issue_details.getText().toString())){
                    commonClass.showWarning(RequestActivity.this,"Please enter Description ");
                }else{
                    callInsertLayout();
                }
                break;
            case R.id.asset_layout:
                requestUpdateUI(1);
                break;
            case R.id.ticket_layout:
                requestUpdateUI(2);
                break;

            case R.id.back_arrow:
                callBackIntent();
                break;

            case R.id.ticreset_layout:
                callResetUI();
                break;
            case R.id.ticrequest_layout:
                if(ticspinnerRequest.getSelectedItemPosition()==0){
                    ticcommonClass.showWarning(RequestActivity.this,"Please select ticket type");
                }else if(ticspinnerPriority.getSelectedItemPosition()==0){
                    ticcommonClass.showWarning(RequestActivity.this,"Please select priority");
                }else if(TextUtils.isEmpty(ticedt_issue_details.getText().toString())){
                    ticcommonClass.showWarning(RequestActivity.this,"Please enter Description");
                }else{
                    if(ticcommonPojo!=null){
                        if(!TextUtils.isEmpty(ticcommonPojo.getImage())){
                            callInsertRequest();
                        }else {
                            if (body != null) {
                                callInsertRequest();
                            } else {
                                ticcommonClass.showWarning(RequestActivity.this, "Please select Image");
                            }
                        }
                    }else {
                        if (body != null) {
                            callInsertRequest();
                        } else {
                            ticcommonClass.showWarning(RequestActivity.this, "Please select Image");
                        }
                    }
                }
                break;

            case R.id.frame_layout:
                updateChangeLayout();
                break;

        }
    }


    private void requestAdminUpdateUI(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            header_relative.setBackground(getApplicationContext().getDrawable(R.drawable.color_shadow));
            asset_layout.setBackgroundResource(0);
            ticket_layout.setBackgroundResource(0);
            asset_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ticket_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ticket_text.setTextColor(getApplicationContext().getColor(R.color.black));
            asset_text.setTextColor(getApplicationContext().getColor(R.color.black));
            if (i == 1) {
                title.setText("Asset Request");
                ticketFragment.setVisibility(View.GONE);
                assetFragment.setVisibility(View.VISIBLE);
                apply_leave_layout.setVisibility(View.VISIBLE);
                listLayout.setVisibility(View.GONE);
                ticframe_layout.setVisibility(View.GONE);
                ticframe_tag.setText("Asset Request");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
                    if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                        ticframe_layout.setVisibility(View.VISIBLE);
                        ticframe_tag.setText("Asset List");
                    }
                }

            } else if (i == 2) {
                title.setText("Ticket Request");
                ticketFragment.setVisibility(View.VISIBLE);
                assetFragment.setVisibility(View.GONE);
                ticapply_leave_layout.setVisibility(View.VISIBLE);
                ticlistLayout.setVisibility(View.GONE);
                ticframe_layout.setVisibility(View.GONE);
                ticframe_tag.setText("Ticket Request");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
                    if (commonClass.getSharedPref(getApplicationContext(), "role_no").equals("70")) {
                        ticframe_layout.setVisibility(View.VISIBLE);
                        ticframe_tag.setText("Ticket List");
                    }
                }
            }
        }
    }

    private void requestUpdateUI(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            header_relative.setBackground(getApplicationContext().getDrawable(R.drawable.color_shadow));
            asset_layout.setBackgroundResource(0);
            ticket_layout.setBackgroundResource(0);
            asset_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ticket_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ticket_text.setTextColor(getApplicationContext().getColor(R.color.black));
            asset_text.setTextColor(getApplicationContext().getColor(R.color.black));
            if (i == 1) {
                title.setText("Asset List");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    asset_layout.setBackground(getApplicationContext().getDrawable(R.drawable.reactangle_padding_with_border));
                    asset_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                    asset_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                    asset_text.setTextColor(getApplicationContext().getColor(R.color.white));
                }
                ticketFragment.setVisibility(View.GONE);
                assetFragment.setVisibility(View.VISIBLE);

                apply_leave_layout.setVisibility(View.GONE);
                listLayout.setVisibility(View.VISIBLE);
                ticframe_tag.setText("Asset Request");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                getList();


            } else if (i == 2) {
                title.setText("Ticket List");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ticket_layout.setBackground(getApplicationContext().getDrawable(R.drawable.reactangle_padding_with_border));
                    ticket_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                    ticket_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                    ticket_text.setTextColor(getApplicationContext().getColor(R.color.white));
                }
                ticketFragment.setVisibility(View.VISIBLE);
                assetFragment.setVisibility(View.GONE);
                ticapply_leave_layout.setVisibility(View.GONE);
                ticlistLayout.setVisibility(View.VISIBLE);
                ticframe_tag.setText("Ticket Request");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                getListTic();
            }
        }
    }

    private void updateChangeLayout() {
        commonPojo = null;
        ticcommonPojo = null;
        if(ticketFragment.getVisibility()==View.VISIBLE){
            if(ticapply_leave_layout.getVisibility()==View.VISIBLE){
                ticlistLayout.setVisibility(View.VISIBLE);
                getListTic();
                title.setText("Ticket List");
                ticapply_leave_layout.setVisibility(View.GONE);
                ticframe_tag.setText("Ticket Request");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

            }else{
                title.setText("Ticket Request");
                callResetUI();
                view_image.setVisibility(View.GONE);
              //  ticheader.setText("Add Ticket");
                tic_re_text.setText("Request");
                ticlistLayout.setVisibility(View.GONE);
                ticapply_leave_layout.setVisibility(View.VISIBLE);
                ticframe_tag.setText("Ticket List");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));

            }
        }else{
            if(apply_leave_layout.getVisibility()==View.VISIBLE){
                listLayout.setVisibility(View.VISIBLE);
                apply_leave_layout.setVisibility(View.GONE);
                ticframe_tag.setText("Asset Request");
                title.setText("Asset List");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                getList();
            }else{
                callResetUIAsset();
                title.setText("Asset Request");
                //header.setText("Add Asset");
                re_text.setText("Request");
                listLayout.setVisibility(View.GONE);
                apply_leave_layout.setVisibility(View.VISIBLE);
                ticframe_tag.setText("Asset List");
                ticframe_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));

            }
        }


    }

    private void callInsertRequest() {
        String getID = ticketDropDown.selectOnlyID(ticspinnerRequest.getSelectedItem().toString());
        //  ticprogressDialog.show();
        ticrequest_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(ticcommonClass.getSharedPref(getApplicationContext(), "token"),
                ticcommonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call = null;
        if(ticcommonPojo!=null){
            if(body!=null){
                call = apiInterface.updateTicket(ticcommonPojo.getId(),getID,ticspinnerPriority.getSelectedItem().toString(),
                        ticedt_issue_details.getText().toString(),body);
            }else{
                call = apiInterface.updateWithoutTicket(ticcommonPojo.getId(),getID,ticspinnerPriority.getSelectedItem().toString(),
                        ticedt_issue_details.getText().toString());
            }
        }else{
            call = apiInterface.insertTicket(getID,ticspinnerPriority.getSelectedItem().toString(),
                    ticedt_issue_details.getText().toString(),body);
        }

        Log.d("TicketList"," request url "+call.request().url()+" is  "+getID);
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                //ticprogressDialog.dismiss();96
                loader.setVisibility(View.GONE);
                ticrequest_layout.setEnabled(true);
                if(response.isSuccessful()){
                    Log.d("TicketList"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            ticcommonClass.showSuccess(RequestActivity.this,response.body().getData());

                           /* getListTic();
                            ticlistLayout.setVisibility(View.VISIBLE);
                            ticapply_leave_layout.setVisibility(View.GONE);
                            ticframe_tag.setText("Ticket Request");*/
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))) {
                                callBackIntent();
                            }else{
                                callResetUI();
                                requestUpdateUI(2);
                            }

                        }else{
                            ticcommonClass.showError(RequestActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            ticcommonClass.showError(RequestActivity.this,mError.getError());
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

                        ticcommonClass.showError(RequestActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                // ticprogressDialog.dismiss();
                ticrequest_layout.setEnabled(true);
                loader.setVisibility(View.GONE);
                ticcommonClass.showError(RequestActivity.this,t.getMessage());
            }
        });

    }

    private void getListTic() {
        // ticprogressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(ticcommonClass.getSharedPref(getApplicationContext(), "token"),
                ticcommonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        Call<TicketModal> call = apiInterface.getTicketList();
        Log.d("TicketList"," list url "+call.request().url());
        call.enqueue(new Callback<TicketModal>() {
            @Override
            public void onResponse(Call<TicketModal> call, Response<TicketModal> response) {
                // ticprogressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("TicketList"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getGetTicketList().size()!=0){
                            TicketAdater adater = new TicketAdater(RequestActivity.this,
                                    response.body().getGetTicketList(),ticrecyclerView,loader);
                            ticrecyclerView.setAdapter(adater);
                            no_data_tic.setVisibility(View.GONE);

                            // updateChangeLayout();
                        }else{
                            no_data_tic.setVisibility(View.VISIBLE);
                        }
                    }else{
                        no_data_tic.setVisibility(View.VISIBLE);
                    }
                }else{
                    no_data_tic.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TicketModal> call, Throwable t) {
                //  ticprogressDialog.dismiss();
                loader.setVisibility(View.GONE);
                no_data_tic.setVisibility(View.VISIBLE);
                Log.d("TicketList"," error "+t.getMessage());
            }
        });
    }

    public void  getDropDownList(){
        //ticprogressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(ticcommonClass.getSharedPref(getApplicationContext(), "token"),
                ticcommonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        Call<TicketDropDownModal> call = apiInterface.getTicketDropDownModal();
        Log.d("getTicket"," url "+call.request().url());
        call.enqueue(new Callback<TicketDropDownModal>() {
            @Override
            public void onResponse(Call<TicketDropDownModal> call, Response<TicketDropDownModal> response) {
                //  ticprogressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getTicket"," res code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("getTicket"," list ase "+response.body().getGetTicketList().size());
                        if(response.body().getGetTicketList().size()!=0){
                            ticketDropDown.DropTable();
                            for(int i=0;i<response.body().getGetTicketList().size();i++){
                               /* projectIdList.add(response.body().getGetProjectList().get(i).getProject_id());
                                projectNameList.add(response.body().getGetProjectList().get(i).getProject_name());
                                gitURLList.put(response.body().getGetProjectList().get(i).getProject_id(),
                                        response.body().getGetProjectList().get(i).getGit_url());*/
                                ticketDropDown.insertData(response.body().getGetTicketList().get(i).getTicket_type_id(),
                                        response.body().getGetTicketList().get(i).getTicket_type());
                            }
                            updateAdapter();

                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            ticcommonClass.showError(RequestActivity.this,mError.getError());
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

                        ticcommonClass.showError(RequestActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TicketDropDownModal> call, Throwable t) {
                //ticprogressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getTicket"," error "+t.getMessage());
            }
        });
    }

    private void updateAdapter() {
        ticketNameList = ticketDropDown.getAllNameList(null);
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,ticketNameList);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        ticspinnerRequest.setAdapter(ad);

        if(ticcommonPojo!=null){
            if(!TextUtils.isEmpty(ticcommonPojo.getType_name())){
                int pos = ticketNameList.indexOf(ticcommonPojo.getType_name());
                ticspinnerRequest.setSelection(pos);
            }
        }
    }

    private void callResetUI() {
        ticspinnerRequest.setSelection(0);
        ticedt_issue_details.setText(null);
        ticspinnerPriority.setSelection(0);
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
                        imageFileName =commonClass.getFileNameFromUri(RequestActivity.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(RequestActivity.this,uri);


                        Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            view_image.setImageBitmap(bitmap);
                            view_image.setVisibility(View.VISIBLE);
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
               // pickImage1(img_request);
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
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(RequestActivity.this,
                READ_EXTERNAL_STORAGE);

        int check_write = ContextCompat.checkSelfPermission(RequestActivity.this,
                WRITE_EXTERNAL_STORAGE);
        int check_write1 = ContextCompat.checkSelfPermission(RequestActivity.this,
                MANAGE_DOCUMENTS);
        int check_write2 = ContextCompat.checkSelfPermission(RequestActivity.this,
                MANAGE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && check_write == PackageManager.PERMISSION_GRANTED
                && check_write1 == PackageManager.PERMISSION_GRANTED && check_write2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(RequestActivity.this)
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

    private void pickImage1(int i) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  // Accept all MIME types
        startActivityForResult(intent, ticPICK_ONE);
     }
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("insp_details", " request code " + requestCode + " res " + resultCode + "  " + ticPICK_ONE + "  ok " + RESULT_OK);
        if (requestCode == ticPICK_ONE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Uri uri = data.getData();

                    String path = getFilePathFromURI(RequestActivity.this,uri);
                    String pdfname = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    File file1 = new File(path);
                    String mimeType = this.getContentResolver().getType(uri);

                    Log.d("getURIError"," file1 "+file1+" path "+path);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file1);
                    body = MultipartBody.Part.createFormData("file", file1.getName(), requestBody);
                    RequestBody filename1 = RequestBody.create(MediaType.parse(mimeType), pdfname);
                    if(data.getData()!=null){
                        String filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
                        document.setText(filename);
                    }                }

            }
        }
        else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (imageUri != null) {
                    document.setText("Choose Image for upload");
                    view_image.setImageURI(imageUri);
                    view_image.setVisibility(View.VISIBLE);

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        Log.d("thumbnail_url", " image strm " + imageStream);
                        final Bitmap selectedImagebit = BitmapFactory.decodeStream(imageStream);
                        Log.d("thumbnail_url", "bitmao " + selectedImagebit);
                        Bitmap bitmap = Bitmap.createScaledBitmap(selectedImagebit, 10, 10, true);
                        imagePathArray = FileUtils.getRealPath(RequestActivity.this, imageUri);
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
                            String path = FileUtils.compressImage(RequestActivity.this, imagePathArray);
                            File mFile = new File(path);
                            document.setText(mFile.getName());
                            // txt_capturefile.setText(imageName);
                            fileTypeArray = getFileType(imagePathArray);
                            body = callMultiPartMethod(imagePathArray, fileTypeArray, "file");
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
    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (uri != null) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                    filePath = getDataColumn(contentUri, null, null);
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://media/external_primary"), Long.valueOf(documentId));
                    filePath = getDataColumn(contentUri, null, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                filePath = getDataColumn(uri, null, null);
            }
        }
        Log.d("getURIError"," file  path  "+filePath);
        return filePath;
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
    private void callInsertLayout() {
        // progressDialog.show();
        request_layout.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call=null;
        if(commonPojo!=null){
            Log.d("getCreset"," id "+commonPojo.getId()+" "+
                    assetDropDown.selectMainID(spinnerRequest.getSelectedItem().toString())
                    +" "+assetDropDown.selectSubCatID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString())+"  "+
                    assetDropDown.selectAssetID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString(),spinnerNameRequest.getSelectedItem().toString()  ));
            call= apiInterface.updateAssetCreate(commonPojo.getId(),assetDropDown.selectMainID(spinnerRequest.getSelectedItem().toString()),
                    assetDropDown.selectSubCatID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString()),
                    assetDropDown.selectAssetID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString(),spinnerNameRequest.getSelectedItem().toString()),
                    edt_configuration.getText().toString(),no_of_quantity.getText().toString(),edt_issue_details.getText().toString());
        }else{
            call= apiInterface.insertAssetCreate(assetDropDown.selectMainID(spinnerRequest.getSelectedItem().toString()),
                    assetDropDown.selectSubCatID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString()),
                    assetDropDown.selectAssetID(spinnerRequest.getSelectedItem().toString(),spinnerSubRequest.getSelectedItem().toString(),spinnerNameRequest.getSelectedItem().toString()),
                    edt_configuration.getText().toString(),no_of_quantity.getText().toString(),edt_issue_details.getText().toString());
        }
        Log.d("getAssetUrl"," url as "+call.request().url());

        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                request_layout.setEnabled(true);
                if(response.isSuccessful()){
                    Log.d("getAssetUrl"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(RequestActivity.this,response.body().getData());

                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                                    !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                                 main_change=1;
                                    callBackIntent();
                            }else{
                                callResetUIAsset();
                                requestUpdateUI(1);
                            }
                            /*getList();
                            listLayout.setVisibility(View.VISIBLE);
                            apply_leave_layout.setVisibility(View.GONE);
                            change_layout.setText("Asset Request");*/
                        }else{
                            commonClass.showError(RequestActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(RequestActivity.this,mError.getError());
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

                        commonClass.showError(RequestActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }


            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                // progressDialog.dismiss();
                request_layout.setEnabled(true);
                loader.setVisibility(View.GONE);
                commonClass.showError(RequestActivity.this,t.getMessage());
            }
        });
    }

    private void getList() {
        //progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(RequestActivity.this)).create(ApiInterface.class);
        Call<TicketModal> call = apiInterface.getAssetList();
        Log.d("getAssetUrl"," list url "+call.request().url());
        call.enqueue(new Callback<TicketModal>() {
            @Override
            public void onResponse(Call<TicketModal> call, Response<TicketModal> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getAssetUrl"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getGetTicketList().size()!=0){
                            AssetAdapter adapter = new AssetAdapter(RequestActivity.this,
                                    response.body().getGetTicketList(),-1,recyclerView,loader);
                            recyclerView.setAdapter(adapter);
                            no_data.setVisibility(View.GONE);

                            // updateChangeLayout();
                        }else{
                            no_data.setVisibility(View.VISIBLE);
                        }
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                    }
                }else{
                    no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TicketModal> call, Throwable t) {
                // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                Log.d("TicketList"," error "+t.getMessage());
            }
        });
    }

    private void updateSubAdapter() {
        SubDropList.clear();
        SubDropList = assetDropDown.selectSubCat(spinnerRequest.getSelectedItem().toString());
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,SubDropList);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        spinnerSubRequest.setAdapter(ad);
        if(commonPojo!=null) {
            if (!TextUtils.isEmpty(commonPojo.getSubcategory_name())) {
                int po = SubDropList.indexOf(commonPojo.getSubcategory_name());
                spinnerSubRequest.setSelection(po);
            }
        }
    }
    private void callResetUIAsset() {
        spinnerRequest.setSelection(0);
        spinnerNameRequest.setSelection(0);
        spinnerSubRequest.setSelection(0);
        no_of_quantity.setText(null);
        edt_issue_details.setText(null);
        edt_configuration.setText(null);
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
}
