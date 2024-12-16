package in.proz.adamd.DSR;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.WSREmpList;
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

public class WSRLayout extends AppCompatActivity implements View.OnClickListener {
    CommonClass commonClass = new CommonClass();
    LinearLayout spinner_layout;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    EditText edt_blocking_factor,edt_next_week,edt_others;
    Spinner send_to_spinner;
    TextView send_to_text;
    TextView send_cc,header_title;
    MKLoader loader;
    List<String> empIDList,empNameList,tempEmpList,tempIDList,sendToIDList,sendToNameList;
    String SendToID,SendToName;
    TextView title;
    ArrayList<Integer> projectListID = new ArrayList<>();

    boolean[] selectedProjects,selectedNames;
    ImageView back_arrow;
    LinearLayout request_layout;
    ImageView online_icon;
    TextView online_text;
    LinearLayout online_layout;
    ImageView mike,mike1,mike2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_wsr);
        initView();
         send_to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if(send_to_spinner.getSelectedView()!=null) {
                   ((TextView) send_to_spinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void initView(){

        header_title = findViewById(R.id.header_title);
        //header_title.setText("Employee");
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);


        send_to_text = findViewById(R.id.send_to_text);
        spinner_layout = findViewById(R.id.spinner_layout);
        spinner_layout.setOnClickListener(this);
        mike = findViewById(R.id.mike);
        mike1 = findViewById(R.id.mike1);
        mike2 = findViewById(R.id.mike2);
        mike.setOnClickListener(this);
        mike1.setOnClickListener(this);
        mike2.setOnClickListener(this);
        empIDList = new ArrayList<>();
        sendToNameList = new ArrayList<>();
        sendToIDList = new ArrayList<>();
        tempEmpList = new ArrayList<>();
        tempIDList = new ArrayList<>();
        empNameList = new ArrayList<>();
        request_layout = findViewById(R.id.request_layout);
      /*  online_layout = findViewById(R.id.online_layout);
        online_icon = findViewById(R.id.online_icon);
        online_text = findViewById(R.id.online_text);
        commonClass.onlineStatusCheck(WSRLayout.this,online_layout,online_text,online_icon);*/
        edt_blocking_factor = findViewById(R.id.edt_blocking_factor);
        edt_next_week = findViewById(R.id.edt_next_week);
        edt_others = findViewById(R.id.edt_others);
        send_to_spinner = findViewById(R.id.send_to_spinner);
        send_cc = findViewById(R.id.send_cc);
        loader = findViewById(R.id.loader);
        back_arrow = findViewById(R.id.back_arrow);
        title = findViewById(R.id.title);
        title.setText("Send Mail");
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DSRActivity.class);
                startActivity(intent);
            }
        });
        send_cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(send_to_spinner.getSelectedItemPosition()==0){
                    commonClass.showWarning(WSRLayout.this,"Select Send To");
                }else{
                    projectAlertDialog();
                }
*/

                if(TextUtils.isEmpty(SendToName)){
                    commonClass.showWarning(WSRLayout.this,"Select Send To");
                }else{
                    projectAlertDialog();
                }
            }
        });
        request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_blocking_factor.getText().toString())){
                    commonClass.showWarning(WSRLayout.this," Enter Blocking Factor");
                }else  if (TextUtils.isEmpty(edt_next_week.getText().toString())){
                    commonClass.showWarning(WSRLayout.this," Enter Next Week Plan");
                }else if(TextUtils.isEmpty(SendToName)){
                    commonClass.showWarning(WSRLayout.this," Select Send To ");
                }else if(TextUtils.isEmpty(send_cc.getText().toString())){
                    commonClass.showWarning(WSRLayout.this," Select Send CC");
                }else if(send_cc.getText().toString().equals("Send To")){
                    commonClass.showWarning(WSRLayout.this," Select Send CC");
                }else{
                    //String sendToMail = send_to_spinner.getSelectedItem().toString();
                    String sendToMail = SendToName;
                    int index = sendToNameList.indexOf(sendToMail);
                    String listSM = sendToIDList.get(index);
                    List<String> ToCCMail= new ArrayList<>();
                    String[] strList = send_cc.getText().toString().split(",");
                    for (int i=0;i<strList.length;i++){
                        int index1 = sendToNameList.indexOf(strList[i]);
                        String listSM1= sendToIDList.get(index1);
                        ToCCMail.add(listSM1);
                    }
                    request_layout.setEnabled(false);
                    loader.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                            commonClass.getDeviceID(WSRLayout.this)).create(ApiInterface.class);
                    Call<CommonPojo> call = apiInterface.generatePDF(edt_blocking_factor.getText().toString(),edt_next_week.getText().toString(),
                            edt_others.getText().toString(),listSM,ToCCMail);
                    Log.d("thumbnail_url"," ur "+call.request().url());
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            request_layout.setEnabled(true);
                            loader.setVisibility(View.GONE);
                            Log.d("thumbnail_url"," code "+response.code());
                            if(response.isSuccessful()){
                                if(response.code()==200){
                                    if(response.body().getStatus().equals("success")){
                                        commonClass.showSuccess(WSRLayout.this,response.body().getData());
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                                startActivity(intent);
                                            }
                                        }, 1500);
                                    }else{
                                        commonClass.showSuccess(WSRLayout.this,response.body().getData());
                                    }
                                }else{
                                    Gson gson = new GsonBuilder().create();
                                    CommonPojo mError = new CommonPojo();
                                    try {
                                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                        commonClass.showError(WSRLayout.this,mError.getError());
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

                                    commonClass.showError(WSRLayout.this,mError.getError());
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
                            request_layout.setEnabled(true);
                            commonClass.showError(WSRLayout.this,t.getMessage());
                        }
                    });



                }
            }
        });
       // callTempList();
        if(commonClass.isOnline(WSRLayout.this)) {
            getEmployeeList();
        }else{
            commonClass.showInternetWarning(WSRLayout.this);
        }

    }

     private void callAlertDialog() {
         AlertDialog.Builder builder = new AlertDialog.Builder(WSRLayout.this);

         // set title
         builder.setTitle("Select To Recipient");
         // set dialog non cancelable
         builder.setCancelable(false);
         String[] stringArray = empNameList.toArray(new String[0]);
         //   String[] stringArray = empNameList.toArray(new String[0]);

         selectedNames = new boolean[stringArray.length];

         SendToID="-1";
         if(!TextUtils.isEmpty(SendToName)) {
             for (int i = 0; i < empNameList.size(); i++) {
                 if(empNameList.get(i).equals(SendToName)){
                     SendToID = String.valueOf(i);
                  }else{
                  }
             }
             send_to_text.setText(SendToName);
         }

    builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

             @Override
             public void onClick(DialogInterface dialog, int which) {
                 SendToID = String.valueOf(which);
                 SendToName = empNameList.get(which);
                 Log.d("selectedItems"," send ti "+SendToID+" name "+SendToName);
                 send_to_text.setText(SendToName);
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
            }
        });

          AlertDialog mDialog = builder.create();
         mDialog.setCancelable(false);
         mDialog.create();
         mDialog.show();
         mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
         mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
    }


    public void projectAlertDialog(){
        tempIDList.clear();
        tempEmpList.clear();
        String value = SendToName;
       // String value = send_to_spinner.getSelectedItem().toString();
        int index = sendToNameList.indexOf(value);
        for(int i=0;i<sendToIDList.size();i++){
            Log.d("sendNameList","name as "+sendToNameList.get(i)+" spiner cvalue "+value);
            tempIDList.add(sendToIDList.get(i));
            tempEmpList.add(sendToNameList.get(i));
        }

        Log.d("arrSixe"," bfr "+tempEmpList.size()+" emp list "+empNameList.size());
        tempIDList.remove(index);
        tempEmpList.remove(index);
        Log.d("arrSixe"," after "+tempEmpList.size()+" emp list "+empNameList.size());

        AlertDialog.Builder builder = new AlertDialog.Builder(WSRLayout.this);

        // set title
        builder.setTitle("Select CC Names");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = tempEmpList.toArray(new String[0]);
     //   String[] stringArray = empNameList.toArray(new String[0]);

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
                send_cc.setText(stringBuilder.toString());
                if(stringBuilder!=null){
                    String str = stringBuilder.toString();

                }
             }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
    }

    public void getEmployeeList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(WSRLayout.this)).create(ApiInterface.class);
        Call<WSREmpList> call = apiInterface.getDSRSendTo();
        Log.d("getDet"," url "+call.request().url());
        call.enqueue(new Callback<WSREmpList>() {
            @Override
            public void onResponse(Call<WSREmpList> call, Response<WSREmpList> response) {
                loader.setVisibility(View.GONE);
                Log.d("getDet"," code "+response.code());
                if(response.isSuccessful()){
                    empIDList.clear();
                    empNameList.clear();
                    sendToIDList.clear();
                    sendToNameList.clear();
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            Log.d("getDet"," get det "+response.body().getTo_data()+" status "+response.body().getStatus());
                            //                                        updateSpinner();
                            if(response.body().getCc_data().size()!=0){
                                if(response.body().getCc_data()!=null) {
                                    Log.d("getDet","cc size "+response.body().getCc_data().size());
                                 //   empIDList.add(0,"-1");
                                //    empNameList.add(0,"Select To");
                                    for (int i = 0; i < response.body().getCc_data().size(); i++) {
                                        CommonPojo commonPojo = response.body().getCc_data().get(i);
                                        empIDList.add(commonPojo.getEmpid());
                                        empNameList.add(commonPojo.getEmpname()+"~"+commonPojo.getDesg_name());
                                    }
                                    String details = commonClass.getSharedPref(getApplicationContext(),"name")+"~"+
                                            commonClass.getSharedPref(getApplicationContext(),"designation");
                                    if(details!=null){
                                        int idex = empNameList.indexOf(details);
                                        Log.d("getIndex"," index "+idex+" details "+details);
                                        if(idex>=0){
                                            empIDList.remove(idex);
                                            empNameList.remove(idex);
                                        }
                                    }
                                }
                            }
                            if(response.body().getTo_data().size()!=0){
                                if(response.body().getTo_data()!=null) {
                                    Log.d("getDet","cc size "+response.body().getTo_data().size());
                                    for (int i = 0; i < response.body().getTo_data().size(); i++) {
                                        CommonPojo commonPojo = response.body().getTo_data().get(i);
                                        sendToIDList.add(commonPojo.getEmpid());
                                        sendToNameList.add(commonPojo.getEmpname()+"~"+commonPojo.getDesg_name());
                                    }
                                }
                            }
                            if(sendToIDList.size()!=0){
                                updateSpinner();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WSREmpList> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("getDet"," error "+t.getMessage());
            }
        });
    }

    private void updateSpinner() {
       /* ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,empNameList);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        send_to_spinner.setAdapter(ad);*/
        ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,empNameList);
        ad.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        send_to_spinner.setAdapter(ad);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),DSRActivity.class);
        startActivity(intent);
    }

     @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_blocking_factor.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_blocking_factor.setText(str_rem);
            }
        }else  if (requestCode == 2) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_next_week.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_next_week.setText(str_rem);
            }
        }else  if (requestCode == 3) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_others.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_others.setText(str_rem);
            }
        }
    }
    private void recordVoiceToText( int i) {
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
      /*  intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(WSRLayout.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        int id= v.getId();
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
            case R.id.spinner_layout:
                callAlertDialog();
                break;
            case R.id.mike:
                recordVoiceToText(1);
                break;
            case R.id.mike1:
                recordVoiceToText(2);
                break;
            case R.id.mike2:
                recordVoiceToText(3);
                break;
        }
    }
}
