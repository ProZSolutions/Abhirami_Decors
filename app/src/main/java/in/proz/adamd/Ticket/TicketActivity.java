package in.proz.adamd.Ticket;


import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.proz.adamd.Adapter.TicketAdater;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.ModalClass.TicketModal;
import in.proz.adamd.R;

import in.proz.adamd.ModalClass.TicketDropDownModal;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.TicketDropDown;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinnerRequest,spinnerPriority;
    EditText  edt_issue_details;
    int  PICK_ONE=1;
    MultipartBody.Part body;

    private static final int RequestPermissionCode = 2000;
    private static final int PICK_IMAGE = 1000;
    LinearLayout request_layout, reset_layout, apply_leave_layout, listLayout;
    ImageView back_arrow;
    RecyclerView recyclerView;
    RelativeLayout file_picker_layout;
    TextView  title;
    CommonClass commonClass = new CommonClass();
    ProgressDialog progressDialog;
    MKLoader loader;

    TicketDropDown ticketDropDown;
    List<String> ticketNameList = new ArrayList<>();
    CommonPojo commonPojo;
    List<String> priorityList = new ArrayList<>();
    EditText edt_document;
    ImageView file_picker;
    LinearLayout frame_layout;
    ImageView frame_icon;
    TextView frame_tag;
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";

    LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text,no_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        no_data = findViewById(R.id.no_data);
         priorityList.add("Select");
        priorityList.add("Urgent");
        priorityList.add("High");
        priorityList.add("Medium");
        priorityList.add("Low");
        initView();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            commonPojo = (CommonPojo) b.getSerializable("saved_details");
            updateEditUI(commonPojo);
        }
        getList();
    }

    private void updateEditUI(CommonPojo commonPojo) {
        if (commonPojo!=null){

            if(!TextUtils.isEmpty(commonPojo.getType_name())){
                String pos =ticketDropDown.selectOnlyID(commonPojo.getType_name());
                spinnerRequest.setSelection(Integer.parseInt(pos));
            }
            /*no_of_quantity.setText(commonPojo.getQuantity());
            edt_configuration.setText(commonPojo.getConfiguration());*/
            edt_issue_details.setText(commonPojo.getDetails());
          //  edt_ticket_name.setText(commonPojo.getTicket_name());
            int pos = priorityList.indexOf(commonPojo.getPriority());
            spinnerPriority.setSelection(pos);
        }
    }

    private void initView() {
        CommonClass comm = new CommonClass();
        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(TicketActivity.this,online_layout,online_text,online_icon);

        requestMultiplePermissions();
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag = findViewById(R.id.frame_tag);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        edt_document = findViewById(R.id.edt_document);
        file_picker = findViewById(R.id.file_picker);
        edt_document.setOnClickListener(this);
        file_picker.setOnClickListener(this);
        file_picker_layout = findViewById(R.id.file_picker_layout);
        file_picker_layout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        ticketDropDown = new TicketDropDown(TicketActivity.this);
        ticketDropDown.getWritableDatabase();
        progressDialog = new ProgressDialog(TicketActivity.this);
        progressDialog.setCancelable(false);
        title=  findViewById(R.id.title);
        title.setText("Tickets");
       /* edt_ticket_name=findViewById(R.id.edt_ticket_name);
        edt_configuration=findViewById(R.id.edt_configuration);*/
        back_arrow = findViewById(R.id.back_arrow);
        spinnerRequest = findViewById(R.id.spinnerRequest);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,priorityList);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(ad);


       // no_of_quantity = findViewById(R.id.no_of_quantity);
        edt_issue_details = findViewById(R.id.edt_issue_details);
        request_layout = findViewById(R.id.request_layout);
        reset_layout = findViewById(R.id.reset_layout);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        listLayout = findViewById(R.id.listLayout);

        recyclerView = findViewById(R.id.recyclerView);
        //change_layout = findViewById(R.id.change_layout);
        back_arrow.setOnClickListener(this);

        reset_layout.setOnClickListener(this);
        request_layout.setOnClickListener(this);

       // change_layout.setOnClickListener(this);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        if(commonClass.isOnline(TicketActivity.this)) {
            getDropDownList();
        }
        spinnerRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinnerRequest.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) spinnerPriority.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackIntent();
    }

    private void callBackIntent() {
        Intent intent = new Intent(TicketActivity.this, DashboardNewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.edt_document:
                callPermissionInticative(PICK_ONE);
                break;
            case R.id.file_picker:
                callPermissionInticative(PICK_ONE);
                break;
            case R.id.file_picker_layout:
                callPermissionInticative(PICK_ONE);
                break;
            case R.id.back_arrow:
                callBackIntent();
                break;

            case R.id.reset_layout:
                callResetUI();
                break;
            case R.id.request_layout:
                if(spinnerRequest.getSelectedItemPosition()==0){
                    commonClass.showWarning(TicketActivity.this,"Please select ticket type");
                }else if(spinnerPriority.getSelectedItemPosition()==0){
                    commonClass.showWarning(TicketActivity.this,"Please select priority");
                }else if(TextUtils.isEmpty(edt_issue_details.getText().toString())){
                    commonClass.showWarning(TicketActivity.this,"Please enter issue details");
                }else{
                    if(body!=null){
                        callInsertRequest();
                    }else{
                        commonClass.showWarning(TicketActivity.this,"Please select Document");
                    }
                }
                break;

            case R.id.frame_layout:
               updateChangeLayout();
                break;

        }
    }

    private void updateChangeLayout() {
        if(apply_leave_layout.getVisibility()==View.VISIBLE){
            listLayout.setVisibility(View.VISIBLE);
            getList();
            apply_leave_layout.setVisibility(View.GONE);
            frame_tag.setText("Ticket Request");
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

        }else{
            listLayout.setVisibility(View.GONE);
            apply_leave_layout.setVisibility(View.VISIBLE);
            frame_tag.setText("Ticket List");
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));

        }
    }

    private void callInsertRequest() {
        String getID = ticketDropDown.selectOnlyID(spinnerRequest.getSelectedItem().toString());
      //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(TicketActivity.this)).create(ApiInterface.class);
        Call<CommonPojo> call = null;
        call = apiInterface.insertTicket(getID,spinnerPriority.getSelectedItem().toString(),edt_issue_details.getText().toString(),body);
        Log.d("TicketList"," request url "+call.request().url()+" is  "+getID);
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    Log.d("TicketList"," res[onse "+response.code());
                    if(response.code()==200){
                        Log.d("claim_url"," respone "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            commonClass.showSuccess(TicketActivity.this,response.body().getData());
                            callResetUI();
                            getList();
                           listLayout.setVisibility(View.VISIBLE);
                            apply_leave_layout.setVisibility(View.GONE);
                            frame_tag.setText("Ticket Request");
                        }else{
                            commonClass.showError(TicketActivity.this,response.body().getData());
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(TicketActivity.this,mError.getError());
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

                        commonClass.showError(TicketActivity.this,mError.getError());
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
                loader.setVisibility(View.GONE);
                commonClass.showError(TicketActivity.this,t.getMessage());
            }
        });

    }

    private void getList() {
       // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(TicketActivity.this)).create(ApiInterface.class);
        Call< TicketModal> call = apiInterface.getTicketList();
        Log.d("TicketList"," list url "+call.request().url());
        call.enqueue(new Callback<TicketModal>() {
            @Override
            public void onResponse(Call<TicketModal> call, Response<TicketModal> response) {
               // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("TicketList"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getGetTicketList().size()!=0){
                            no_data.setVisibility(View.GONE);
                            TicketAdater adater = new TicketAdater(TicketActivity.this,
                                    response.body().getGetTicketList(),recyclerView,loader);
                            recyclerView.setAdapter(adater);
                           // updateChangeLayout();
                        }else{
                            no_data.setVisibility(View.VISIBLE);
                        }
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<TicketModal> call, Throwable t) {
              //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("TicketList"," error "+t.getMessage());
            }
        });
    }

    public void  getDropDownList(){
        //progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(TicketActivity.this)).create(ApiInterface.class);
        Call<TicketDropDownModal> call = apiInterface.getTicketDropDownModal();
        Log.d("getTicket"," url "+call.request().url());
        call.enqueue(new Callback<TicketDropDownModal>() {
            @Override
            public void onResponse(Call<TicketDropDownModal> call, Response<TicketDropDownModal> response) {
              //  progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getTicket"," res code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
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

                            commonClass.showError(TicketActivity.this,mError.getError());
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

                        commonClass.showError(TicketActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TicketDropDownModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getTicket"," error "+t.getMessage());
            }
        });
    }

    private void updateAdapter() {
        ticketNameList = ticketDropDown.getAllNameList(null);
        ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ticketNameList);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerRequest.setAdapter(ad);

        if(commonPojo!=null){
            if(!TextUtils.isEmpty(commonPojo.getType_name())){
                int pos = ticketNameList.indexOf(commonPojo.getType_name());
                spinnerRequest.setSelection(pos);
            }
        }
    }

    private void callResetUI() {
        spinnerRequest.setSelection(0);
        edt_issue_details.setText(null);
        spinnerPriority.setSelection(0);
    }



    private void callPermissionInticative(int img_request) {
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Log.d("feedback_request"," if con 1 ");
                requestPermission(img_request,0);
            }else{
                Log.d("feedback_request","if con 2");
                pickImage1(img_request);



            }
        }else{
            if (checkPermission()) {
                Log.d("feedback_request","if con 3");
                pickImage1(img_request);


            } else {
                Log.d("feedback_request","if con 4");
                requestPermission(img_request,1);
            }
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(TicketActivity.this,
                READ_EXTERNAL_STORAGE);

        int check_write = ContextCompat.checkSelfPermission(TicketActivity.this,
                WRITE_EXTERNAL_STORAGE);
        int check_write1 = ContextCompat.checkSelfPermission(TicketActivity.this,
                MANAGE_DOCUMENTS);
        int check_write2 = ContextCompat.checkSelfPermission(TicketActivity.this,
                MANAGE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && check_write == PackageManager.PERMISSION_GRANTED
                && check_write1 == PackageManager.PERMISSION_GRANTED && check_write2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(TicketActivity.this)
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
        Log.d("feedback_request"," pick image request "+i);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  // set all MIME types

        startActivityForResult(intent, PICK_ONE);
    }
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("insp_details", " request code " + requestCode + " res " + resultCode + "  " + PICK_ONE + "  ok " + RESULT_OK);
        if (requestCode == PICK_ONE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Uri uri = data.getData();
                    //File file = new File(getPathFromUri(uri));
                    String path = getFilePathFromURI(TicketActivity.this,uri);
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
                }

            }
        }
    }
    // Method to get actual file path from URI



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
