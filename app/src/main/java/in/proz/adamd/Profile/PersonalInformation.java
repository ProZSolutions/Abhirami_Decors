package in.proz.adamd.Profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;
import com.tuyenmonkey.mkloader.model.Line;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.PersonalMainModal;
import in.proz.adamd.ModalClass.PersonalModal;
import in.proz.adamd.ModalClass.RequestDropDownModal;
import in.proz.adamd.NotesActivity.NotesActivity;
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

public class PersonalInformation extends AppCompatActivity implements View.OnClickListener {
    TextView title,gender,date_of_birth,blood_group,aadhar,address,pincode,name,employee_id,department,
            desingaiton,mobile,mail;
    String imageFileName , imageFileExt,dob_str;
    Bitmap bitmap;

    String emp_no;
    MultipartBody.Part body;
    MKLoader loader;
    ImageView edit_icon;
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";
    ProgressDialog progressDialog;
    LinearLayout nhome_layout,nabout_layout,nreports_layout,nlocation_layout,nprofile_layout;

    CommonClass commonClass=new CommonClass();
    CircleImageView profile_img ;
    PersonalModal personalModal;
    ImageView profile_icon;
    TextView profile_text;
    LinearLayout request_layout;
 /*   LinearLayout online_layout;
    ImageView online_icon,;
    TextView online_text;*/
    ImageView back_arrow;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
         initView();
    }
    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    static String mask(String input) {
        return input.replaceAll(".(?=.{4})", "X");
    }
    public void initView(){
        request_layout = findViewById(R.id.request_layout);
        request_layout.setOnClickListener(this);
        profile_icon = findViewById(R.id.profile_icon);
        profile_text = findViewById(R.id.profile_text);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);

        nhome_layout= findViewById(R.id.nhome_layout);
      //  nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);

        nhome_layout.setOnClickListener(this);
      //  nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

       /* if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            //header_title.setText("Admin");
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
            //header_title.setText("Employee");
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/
        profile_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        profile_text.setTextColor(getApplicationContext().getColor(R.color.black));
        edit_icon = findViewById(R.id.edit_icon);
        edit_icon.setOnClickListener(this);
        /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(PersonalInformation.this,online_layout,online_text,online_icon);*/
        loader = findViewById(R.id.loader);
        profile_img=findViewById(R.id.profile_img);
        progressDialog = new ProgressDialog(PersonalInformation.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        commonClass = new CommonClass();
        title = findViewById(R.id.title);
        title.setText("Personal Information");
        back_arrow = findViewById(R.id.back_arrow);
        gender = findViewById(R.id.gender);
        date_of_birth = findViewById(R.id.date_of_birth);
        blood_group = findViewById(R.id.blood_group);
        aadhar = findViewById(R.id.aadhar);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        name = findViewById(R.id.name);
        employee_id = findViewById(R.id.employee_id);
        department = findViewById(R.id.department);
        desingaiton = findViewById(R.id.desingaiton);
        mobile = findViewById(R.id.mobile);
        mail = findViewById(R.id.mail);
        back_arrow.setOnClickListener(this);
        profile_img.setOnClickListener(this);
        getList();
    }
    public void getList(){
        //progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(PersonalInformation.this,"token"),
                commonClass.getDeviceID(PersonalInformation.this)).create(ApiInterface.class);
        Call<PersonalMainModal> call = apiInterface.getUserProfile();
        Log.d("profile_index"," url as "+call.request().url()+"   "+commonClass.getDeviceID(PersonalInformation.this)+
                " token "+commonClass.getSharedPref(getApplicationContext(),"token"));
        call.enqueue(new Callback<PersonalMainModal>() {
            @Override
            public void onResponse(Call<PersonalMainModal> call, Response<PersonalMainModal> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("profile_index"," ode "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        personalModal = response.body().getPersonalModal();
                         Log.d("profile_index"," name "+response.body().getPersonalModal().getName());
                        if(!TextUtils.isEmpty(response.body().getPersonalModal().getName())){
                            emp_no = response.body().getPersonalModal().getEmp_no();
                            name.setText(passParameter(response.body().getPersonalModal().getName()));
                            employee_id.setText( passParameter(response.body().getPersonalModal().getEmp_no()));
                            department.setText( passParameter(response.body().getPersonalModal().getDepartment()));
                            desingaiton.setText( passParameter(response.body().getPersonalModal().getDesignation()));
                            mobile.setText(passParameter(response.body().getPersonalModal().getContact()));
                            mail.setText( passParameter(response.body().getPersonalModal().getComp_email()));
                            gender.setText(capitalizeFirstLetter(passParameter(response.body().getPersonalModal().getGender())));
                            date_of_birth.setText(passParameter(response.body().getPersonalModal().getDob()));
                            if(!TextUtils.isEmpty(response.body().getPersonalModal().getDob())){
                                if(!TextUtils.isEmpty(response.body().getPersonalModal().getDob())){
                                    String[] dt = response.body().getPersonalModal().getDob().split("-");
                                    date_of_birth.setText(dt[0]+"-"+dt[1]+"-"+dt[2]);
                                    if(dt[0]!=null){
                                        int value = Integer.parseInt(dt[0]);
                                        if(value>1900){
                                            date_of_birth.setText(dt[2]+"-"+dt[1]+"-"+dt[0]);
                                        }
                                    }
                                }
                            }
                            blood_group.setText(passParameter(response.body().getPersonalModal().getBloodgroup()));

                            if(response.body().getPersonalModal().getAadharPojo()!=null){
                               aadhar.setText(mask(passParameter(response.body().getPersonalModal().getAadharPojo().getAadharno())));
                            }
                            if(response.body().getPersonalModal().getAddressPojo()!=null){
                                address.setText(passParameter(response.body().getPersonalModal().getAddressPojo().getAddress()));
                            }
                            if(response.body().getPersonalModal().getAddressPojo()!=null){
                                pincode.setText(passParameter(response.body().getPersonalModal().getAddressPojo().getPincode()));
                            }
                            if(!TextUtils.isEmpty(response.body().getPersonalModal().getImage())){
                                Picasso.with(PersonalInformation.this).
                                        load(response.body().getPersonalModal().getImage()).into(profile_img);
                                commonClass.putSharedPref(getApplicationContext(),"image",response.body().getPersonalModal().getImage());
                            }
                            /*
                            */



                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(PersonalInformation.this,mError.getError());
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

                        commonClass.showError(PersonalInformation.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PersonalMainModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                commonClass.showError(PersonalInformation.this,t.getMessage());
            }
        });

    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imageFileName =commonClass.getFileNameFromUri(PersonalInformation.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(PersonalInformation.this,uri);


                        Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            profile_img.setImageBitmap(bitmap);
                            if(bitmap!=null) {
                                callAlertDialog(bitmap);
                            }
                        }catch (IOException e){

                        }
                    }
                }
            });

    private String passParameter(String name) {
         return  name;
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
            case R.id.request_layout:
                callUpdateAlert();
                break;
            case R.id.nprofile_layout:
                Intent intentp=new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentp);
                break;
            case R.id.back_arrow:
                backPressCalled();
                break;
            case R.id.nhome_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                intentabout1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentabout1);
                break;
          /*  case R.id.nabout_layout:
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
            case R.id.profile_img:
               // selectImage();
               // callCustom();
                //callSelect();
                callCustom1();
                break;
            case R.id.edit_icon:
                // selectImage();
                // callCustom();
                //callSelect();
                callCustom1();
                break;
        }
    }
    public void datePicker(TextView date_txt,int pos){
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

                            dob_str =year + "-" + str_month + "-" + str_date;

                        date_txt.setText(str_date + "-"
                                + str_month + "-" + year);



                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();

    }

    public void callUpdateAlert(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(PersonalInformation.this);
        View view= LayoutInflater.from(PersonalInformation.this).inflate(R.layout.update_profile,null);
        ImageView close = view.findViewById(R.id.close);
        TextView ed_fromdate = view.findViewById(R.id.ed_fromdate);
        ImageView from_picker = view.findViewById(R.id.from_picker);
        EditText blood_group = view.findViewById(R.id.blood_group);
        EditText address = view.findViewById(R.id.address);
        EditText pincode = view.findViewById(R.id.pincode);
        MKLoader mkLoader = view.findViewById(R.id.loader);
        LinearLayout approve = view.findViewById(R.id.approve);
        if(personalModal!=null) {
            dob_str = personalModal.getDob();
            if (!TextUtils.isEmpty(personalModal.getDob())) {
                String f =passParameter(personalModal.getDob());
                String[] f1 = f.split("-");
                ed_fromdate.setText(f1[2]+"-"+f1[1]+"-"+f1[0]);
            }
            if (!TextUtils.isEmpty(personalModal.getBloodgroup())) {
                blood_group.setText(personalModal.getBloodgroup());
            }
            if(personalModal.getAddressPojo()!=null) {
                if (!TextUtils.isEmpty(personalModal.getAddressPojo().getAddress())) {
                    address.setText(passParameter(personalModal.getAddressPojo().getAddress()));
                }
                if (!TextUtils.isEmpty(personalModal.getAddressPojo().getPincode())) {
                    pincode.setText(passParameter(personalModal.getAddressPojo().getPincode()));
                }
            }
        }
        from_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DOBON"," clicke ");
                datePicker(ed_fromdate,1);
            }
        });
        ed_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DOBON"," clicke ");
                datePicker(ed_fromdate,1);
            }
        });

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(dob_str)){
                    commonClass.showWarning(PersonalInformation.this,"Select DOB");
                }else if(TextUtils.isEmpty(blood_group.getText().toString())){
                    commonClass.showWarning(PersonalInformation.this,"Enter Blood Group");
                }else if(TextUtils.isEmpty(address.getText().toString())){
                    commonClass.showWarning(PersonalInformation.this,"Enter Address");
                }else if(TextUtils.isEmpty(pincode.getText().toString())){
                    commonClass.showWarning(PersonalInformation.this,"Enter Pincode");
                }else if(pincode.getText().toString().length()<6){
                    commonClass.showWarning(PersonalInformation.this,"Pincode should be 6 digit ");
                }else{
                    approve.setEnabled(false);
                    mkLoader.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                            commonClass.getDeviceID(PersonalInformation.this)).create(ApiInterface.class);
                    Call<CommonPojo> call = apiInterface.updateProfile(dob_str,blood_group.getText().toString(),
                            address.getText().toString(),pincode.getText().toString());
                    Log.d("dropdownlist"," urel as "+call.request().url());
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            //  progressDialog.dismiss();
                            mkLoader.setVisibility(View.GONE);
                            Log.d("dropdownlist"," code "+response.code());
                            if(response.isSuccessful()){
                                approve.setEnabled(true);
                                if(response.code()==200){
                                    Log.d("dropdownlist"," response "+response.body().getStatus());
                                    if(response.body().getStatus().equals("success")){
                                        commonClass.showSuccess(PersonalInformation.this,response.body().getStatus());
                                        mDialog.dismiss();
                                        getList();
                                    }else{
                                        commonClass.showError(PersonalInformation.this,response.body().getStatus());
                                    }
                                }else{
                                    Gson gson = new GsonBuilder().create();
                                    CommonPojo mError = new CommonPojo();
                                    try {
                                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                        commonClass.showError(PersonalInformation.this,mError.getError());
                                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        // handle failure to read error
                                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonPojo> call, Throwable t) {
                            // progressDialog.dismiss();
                            mkLoader.setVisibility(View.GONE);
                            approve.setEnabled(true);
                            Log.d("dropdownlist"," error"+t.getMessage());
                            commonClass.showError(PersonalInformation.this,t.getMessage());
                        }
                    });
                }
            }
        });


    }


    private void callCustom1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    private void callCustom() {

           /* Log.d("personal_img"," permi granted ");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);*/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  // Accept all MIME types
        startActivityForResult(intent, 10);

    }

    public String getPathFromURI(Uri contentUri){
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null   );
        if(cursor.moveToFirst()){
            int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(colum_index);
        }
        cursor.close();
        return  res;
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

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(photo_name, file.getName(), requestFile);
        Log.d("thumbnail_url"," body as "+body);
        return body;
    }
//
//    ActivityResultLauncher<Intent> pickImageActivityLancher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if(result.getResultCode()== Activity.RESULT_OK){
//                        Log.d("imagePicker"," action OK ");
//                        I5454ntent data = result.getData();
//                        Log.d("imagePicker"," get data "+data);
//                        Uri selected = data.getData();
//                        // Log.d("imagePicker"," uri "+getFileFromUri(selected));
//                        //   final String path = getPathFromURI(selected);
//                         final String path = getRealPathFromURI(selected);
//                     /*   String imagePathArray = FileUtils.getRealPath(PersonalInformation.this, selected);
//                        Log.d("imagePicker"," image ar "+imagePathArray);
//                        if (!TextUtils.isEmpty(imagePathArray)) {
//                            String path1 = FileUtils.compressImage(PersonalInformation.this, imagePathArray);
//                            Log.d("imagePicker","path "+path1);
//                            File mFile = new File(path1);
//                            Log.d("imagePicker"," file  "+mFile);
//                            String imageName = mFile.getName();
//                           String  fileTypeArray = getFileType(imagePathArray);
//                           Log.d("imagePicker"," file type "+fileTypeArray);
//                        }*/
//
//
//
//                      Log.d("imagePicker"," ptja "+path);
//                       if(path!=null){
//                            File f = new File(path);
//                            Log.d("imagePicker"," fole "+f);
//                            selected = Uri.fromFile(f);
//                            if(f!=null){
//                                callAlertDialog(f);
//                            }
//                        }
//                        profile_img.setImageURI(selected);
//                    }
//                }
//            });
    private String getRealPathFromURI(Uri contentURI) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
    private void selectImage() {
 /*       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);*/
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
      //  pickImageActivityLancher.launch(intent);
    }
    private File getFileFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return new File(filePath);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d("imagePicker","uri "+uri);
            Context context = PersonalInformation.this;
            String str_uri = String.valueOf(uri);
            String path= FileUtils.getRealPath(PersonalInformation.this,uri);
            /*if(str_uri.contains("/storage")){
                path = FileUtils.getRealPath(context, uri);
            }else{
                path = FileUtils.getRealPathFromURI(context, uri);
            }*/

            Log.d("imagePicker"," pat "+path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            profile_img.setImageBitmap(bitmap);
            if(path!=null){
                File f = new File(path);
                Log.d("imagePicker"," fole "+f);
                //selected = Uri.fromFile(f);
                if(f!=null){
                    callAlertDialogFile(f);
                }
            }

            /*File fil = new File(FileUtils.getRealPathFromURI(PersonalInformation.this, String.valueOf(uri)));
            Log.d("onPick"," file "+fil+" uri "+uri);
            if(fil!=null){
                callAlertDialog(fil);
            } */


            /*if (data != null) {
                Uri uri = data.getData();
                profile_img.setImageURI(uri);

              //  String path = getFilePathFromURI(PersonalInformation.this,uri);
                String path = RealPath.getPathFromURI(PersonalInformation.this,uri);
                String pdfname = String.valueOf(Calendar.getInstance().getTimeInMillis());
                File file1 = new File(path);
                String mimeType = this.getContentResolver().getType(uri);

                Log.d("getURIError"," file1 "+file1+" path "+path);
                RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file1);
                body = MultipartBody.Part.createFormData("file", file1.getName(), requestBody);
                RequestBody filename1 = RequestBody.create(MediaType.parse(mimeType), pdfname);
                if(body!=null){
                    callAlertDialog(file1);
                }

            }*/

        }else
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    File file = getFileFromUri(data.getData());
                    profile_img.setImageURI(data.getData());
                    if(file!=null){
                      //  callAlertDialog(file);
                    }
                    Log.d("selected_file"," as "+getFileFromUri(data.getData()));
                }
            }
        }
    }

    private void callAlertDialogFile(File f) {

        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.new_warning_dlg,null);
        TextView msg = view.findViewById(R.id.msg);
        TextView cancel_text = view.findViewById(R.id.cancel_text);
        cancel_text.setText("NO");

        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");

        msg.setText("Would You like to Upload Profile Image?");
        LinearLayout approve = view.findViewById(R.id.approve);
        approve.setVisibility(View.GONE);
        LinearLayout decline = view.findViewById(R.id.decline);
        decline.setVisibility(View.VISIBLE);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                uploadImageFile(f);
            }
        });


    }

    private void callAlertDialog(Bitmap bitmap) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.new_warning_dlg,null);
        TextView msg = view.findViewById(R.id.msg);
        TextView cancel_text = view.findViewById(R.id.cancel_text);
        cancel_text.setText("NO");

        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");

        msg.setText("Would You like to Upload Profile Image?");
        LinearLayout approve = view.findViewById(R.id.approve);
        approve.setVisibility(View.GONE);
        LinearLayout decline = view.findViewById(R.id.decline);
        decline.setVisibility(View.VISIBLE);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                uploadImage(bitmap);            }
        });



    }
    private void uploadImageFile(File file) {
      /*  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        final String base64image= Base64.encodeToString(bytes,Base64.DEFAULT);
        RequestBody requestBody = RequestBody.create(MediaType.parse(imageFileExt), bytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFileName, requestBody);*/
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Log.d("imagePicker"," image "+imagePart);
        if(imagePart!=null){
            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(PersonalInformation.this,"token"),
                    commonClass.getDeviceID(PersonalInformation.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.updateProfile(imagePart,personalModal.getId());
            Log.d("imagePicker"," call "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader.setVisibility(View.GONE);
                    Log.d("imagePicker"," respne "+response.code());
                    if(response.isSuccessful()) {
                        if (response.code() == 200) {
                            if(response.body().getStatus().equals("success")){
                                getList();
                                commonClass.showSuccess(PersonalInformation.this,"Image Updated Successfully...");
                            }else{
                                commonClass.showError(PersonalInformation.this,"Failed to save image");
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                commonClass.showError(PersonalInformation.this, mError.getError());
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
                            commonClass.showError(PersonalInformation.this,mError.getError());
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
                    commonClass.showError(PersonalInformation.this,t.getMessage());
                }
            });
        }else{
            commonClass.showWarning(PersonalInformation.this,"Image Not Picked");
        }
    }


    private void uploadImage(Bitmap bitmap) {
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG,10,byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            final String base64image= Base64.encodeToString(bytes,Base64.DEFAULT);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFileName, requestBody);
/*        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);*/
        Log.d("imagePicker"," image "+imagePart);
        if(imagePart!=null){
            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(PersonalInformation.this,"token"),
                    commonClass.getDeviceID(PersonalInformation.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.updateProfile(imagePart,personalModal.getId());
            Log.d("imagePicker"," call "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader.setVisibility(View.GONE);
                    Log.d("imagePicker"," respne "+response.code());
                    if(response.isSuccessful()) {
                        if (response.code() == 200) {
                            if(response.body().getStatus().equals("success")){
                                commonClass.showSuccess(PersonalInformation.this,"Image Updated Successfully...");
                            }else{
                                commonClass.showError(PersonalInformation.this,"Failed to save image");
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                 commonClass.showError(PersonalInformation.this, mError.getError());
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
                             commonClass.showError(PersonalInformation.this,mError.getError());
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
                    commonClass.showError(PersonalInformation.this,t.getMessage());
                }
            });
        }else{
            commonClass.showWarning(PersonalInformation.this,"Image Not Picked");
        }
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
