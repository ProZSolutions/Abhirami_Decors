package in.proz.adamd.Calendar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.tuyenmonkey.mkloader.MKLoader;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.proz.adamd.BackgroundColorDecorator;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.ModalClass.LeaveModal;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.CalendarModal;
import in.proz.adamd.ModalClass.CalendarSubClass;
import in.proz.adamd.NotesActivity.NotesActivity;
 import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.HolidayCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity implements   View.OnClickListener {
    MaterialCalendarView customCalendar;
    Spinner spinnerCalendar;
    HashMap<Integer,Object> dateHashmap,detailsHashmap;
    List<CalendarSubClass> getPojoList;
    HolidayCalendar holidayCalendar;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    CommonClass commonClass = new CommonClass();
    List<String> calendarList=new ArrayList<>();
   // ProgressDialog progressDialog ;
    TextView title,header_title;
    ImageView back_arrow;
    MKLoader loader;
 /*   LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initView();
    }

    private void initView() {
        if(!commonClass.isOnline(CalendarActivity.this)){
            commonClass.showInternetWarning(CalendarActivity.this);
        }
        nhome_layout= findViewById(R.id.nhome_layout);
      //  nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout.setOnClickListener(this);
        //nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);

        holidayCalendar = new HolidayCalendar(CalendarActivity.this);
        holidayCalendar.getWritableDatabase();
        header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        // header_title.setText("Employee");
        CommonClass comm = new CommonClass();

        loader = findViewById(R.id.loader);
         title = findViewById(R.id.title);
         title.setText("Calendar List");
        dateHashmap =new HashMap<>();
        detailsHashmap = new HashMap<>();
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        calendarList.add("Select");
        calendarList.add("DSR List");
        calendarList.add("Holiday List");
        spinnerCalendar = findViewById(R.id.spinnerCalendar);

        customCalendar=findViewById(R.id.custom_calendar);
        callDefaultUpdateCalendar(null);
        ArrayAdapter ad  = new ArrayAdapter(this,R.layout.spinner_drop_down,calendarList);
        ad.setDropDownViewResource( R.layout.spinner_drop_down);
        spinnerCalendar.setAdapter(ad);
        customCalendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay newMonth) {
                String sDate= newMonth.getYear()
                        +"-" +newMonth.getMonth()
                        +"-" +newMonth.getDay();
                 String month = String.valueOf(newMonth.getMonth());
                int mont =  newMonth.getMonth();
                String mon =String.valueOf(mont);



                if(mont<10){
                    mon ="0"+String.valueOf(mont);
                }
                if(commonClass.isOnline(CalendarActivity.this)){
                    callAPIHitMethod(mon,sDate, String.valueOf(newMonth.getYear()));
                }else{
                    if(spinnerCalendar.getSelectedItemPosition()==2){
                        getPojoList = holidayCalendar.getAllList();
                        SimpleDateFormat df21 = new SimpleDateFormat("yyyy-MM-dd");
                        String todayDate = df21.format(new Date());
                        updateCalendarData(month, getPojoList, todayDate);
                    }
                }
            }
        });
        customCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay selectedDate, boolean selected) {
                if(dateHashmap!=null){
                    if(dateHashmap.size()!=0){
                        String sDate=selectedDate.getDay()
                                +"/" +selectedDate.getMonth()
                                +"/" + selectedDate.getYear();
                        int date = selectedDate.getMonth();

                        if(dateHashmap.size()!=0){
                            if(dateHashmap.containsKey(date)){
                                String message = String.valueOf(detailsHashmap.get(date));
                                //commonClass.showAppToast(CalendarActivity.this,message);
                            }
                        }

                    }
                }

            }
        });


        spinnerCalendar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(((TextView) spinnerCalendar.getSelectedView())!=null) {
                    ((TextView) spinnerCalendar.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                }

                if(i!=0){
                    SimpleDateFormat df2 = new SimpleDateFormat("MM");
                    SimpleDateFormat df212 = new SimpleDateFormat("yyyy");
                    SimpleDateFormat df21 = new SimpleDateFormat("yyyy-MM-dd");
                    String todayDate = df21.format(new Date());

                    String month = df2.format(new Date());
                    String year = df212.format(new Date());
                    if(comm.isOnline(CalendarActivity.this)){
                        Log.d("calHit"," online change called ");
                        callAPIHitMethod(month,todayDate,year);
                    }else{
                        if(i==2){
                            getPojoList = holidayCalendar.getAllList();
                            Log.d("dropdownlist"," offl;ine "+getPojoList.size());
                            updateCalendarData(month, getPojoList, todayDate);

                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callDefaultUpdateCalendar(String todayDate) {
        Log.d("callDefault"," date "+todayDate);
        dateHashmap.clear();
        detailsHashmap.clear();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String date =  f.format(new Date());


        if(!TextUtils.isEmpty(todayDate)){
            date = todayDate;
        }

        Set<CalendarDay> dateNormal = new HashSet<>();
        Log.d("dateFormatL","date1 "+date);
        String splitDate[] = date.split("-");
        Log.d("dateFormatL","date4 "+date);
        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
        for(int i=1;i<=total_days;i++) {
            dateHashmap.put(i, "normal");
            detailsHashmap.put(i, "");
            dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[2])));

        }

        if(dateHashmap.size()!=0){

            Calendar calendar=  Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df21 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df211 = new SimpleDateFormat("dd");
            if(TextUtils.isEmpty(todayDate)) {
                todayDate = df21.format(new Date());
            }
            Log.d("callDefault"," date hashmap "+todayDate);

            try {
                 calendar.setTime(format.parse(todayDate));
               // customCalendar.setDate(calendar,dateHashmap);
                BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);

                // Add decorators to the calendar
                customCalendar.addDecorator(decorator1);

            } catch (ParseException e) {
                e.printStackTrace();
            }
//                                customCalendar.setDate(calendar,dateHashmap);
        }
    }

    private void callAPIHitMethod(String month,String todayDate,String year) {
        dateHashmap.clear();
        detailsHashmap.clear();
        customCalendar.removeDecorators();
        if(spinnerCalendar.getSelectedItemPosition()==0){
            callDefaultUpdateCalendar(todayDate);
            commonClass.showWarning(CalendarActivity.this,"Please select calendar type");
        }else {

            //progressDialog.6show();
            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                    commonClass.getDeviceID(CalendarActivity.this)).create(ApiInterface.class);
            Log.d("apiHit", " token " + commonClass.getSharedPref(getApplicationContext(), "token") + "  devive " +
                    commonClass.getDeviceID(CalendarActivity.this));
            Call<CalendarModal> call = null;
            Log.d("calHit"," item pos "+spinnerCalendar.getSelectedItemPosition());
            if (spinnerCalendar.getSelectedItemPosition() == 1) {
                String from_d= year+"-"+month+"-"+"01";
                String to_d= year+"-"+month+"-"+"30";
                Call<LeaveModal> call1 = apiInterface.getDSRLIST(from_d,to_d);
                Log.d("calHit"," url  "+call1.request().url());
                call1.enqueue(new Callback<LeaveModal>() {
                    @Override
                    public void onResponse(Call<LeaveModal> call, Response<LeaveModal> response) {
                        loader.setVisibility(View.GONE);
                        Log.d("calHit"," respne "+response.code());
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                Log.d("calHit"," ata "+response.body().getStatus()+"  size "+response.body().getDataLeaveList().size());
                                if (response.body().getStatus().equals("success")) {
                                    List<CommonPojo> pojo = response.body().getDataLeaveList();
                                    if(pojo!=null){
                                        if(pojo.size()!=0){
                                            updateDSRCalendar(month, pojo, todayDate);
                                        }
                                    }
                                } else {
                                    commonClass.showError(CalendarActivity.this, response.body().getStatus());
                                }
                            } else {
                                Gson gson = new GsonBuilder().create();
                                CommonPojo mError = new CommonPojo();
                                try {
                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                    commonClass.showError(CalendarActivity.this, mError.getError());
                                    //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    // handle failure to read error
                                    Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                }

                            }

                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                commonClass.showError(CalendarActivity.this, mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("thumbnail_url", " exp error  " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LeaveModal> call, Throwable t) {
                        loader.setVisibility(View.GONE);
                    }
                });
            }
            else if (spinnerCalendar.getSelectedItemPosition() == 2) {
                call = apiInterface.getHolidayCalendarList();
                Log.d("calHit"," url holiday "+call.request().url());
                call.enqueue(new Callback<CalendarModal>() {
                    @Override
                    public void onResponse(Call<CalendarModal> call, Response<CalendarModal> response) {
                        //progressDialog.dismiss();
                        Log.d("calHit"," response code "+response.code());

                        loader.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body().getStatus().equals("success")) {
                                    getPojoList = response.body().getCalendarlist();
                                    if(getPojoList!=null){
                                        if(getPojoList.size()!=0){
                                            holidayCalendar.deleteAll(CalendarActivity.this);
                                            for (int i=0;i<getPojoList.size();i++){
                                                holidayCalendar.insertData(getPojoList.get(i));
                                            }
                                        }
                                    }
                                    Log.d("selectedMonth"," pogo siz "+response.body().getCalendarlist());
                                    updateCalendarData(month, getPojoList, todayDate);
                                } else {
                                    commonClass.showError(CalendarActivity.this, response.body().getStatus());
                                }
                            } else {
                                Gson gson = new GsonBuilder().create();
                                CommonPojo mError = new CommonPojo();
                                try {
                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                    commonClass.showError(CalendarActivity.this, mError.getError());
                                    //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    // handle failure to read error
                                    Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                }

                            }

                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                commonClass.showError(CalendarActivity.this, mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("thumbnail_url", " exp error  " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CalendarModal> call, Throwable t) {
                        // progressDialog.dismiss();
                        loader.setVisibility(View.GONE);
                        commonClass.showError(CalendarActivity.this, t.getMessage());
                    }
                });

            }
           //  getPojoList.clear();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDSRCalendar(String month, List<CommonPojo> pojo, String todayDate) {
        //HashMap<Object, Property> mapDescToProp = new HashMap<>();
        dateHashmap.clear();
        detailsHashmap.clear();
       /* Property propDefault = new Property();
        propDefault.layoutResource = R.layout.leave_normal;
        propDefault.enable = false;
        mapDescToProp.put("normal", propDefault);
        propDefault.dateTextViewResource=R.id.text_view;
        Property propUnavailable = new Property();
        propUnavailable.layoutResource = R.layout.leave_green;
        propUnavailable.dateTextViewResource=R.id.text_view;
        mapDescToProp.put("leave", propUnavailable);
        customCalendar.setMapDescToProp(mapDescToProp);*/
        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();
        Log.d("selectedMonth"," omnth "+month+ " size "+pojo.size());
        for(int i=0;i<pojo.size();i++){
            if(!TextUtils.isEmpty(pojo.get(i).getDate())){
                String[] splitDate = pojo.get(i).getDate().split("-");
                Log.d("dateFormatL","date2 "+pojo.get(i).getDate());
                int value= Integer.parseInt(splitDate[0]);
                dateHashmap.put(value,"leave");
                dateLeave.add(CalendarDay.from(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]),Integer.parseInt( splitDate[0])));

            }
        }
        String[] splitDate1 = pojo.get(0).getDate().split("-");

        int total_days = commonClass.getDaysInMonth(Integer.parseInt(splitDate1[0]), Integer.parseInt(splitDate1[1]));

        for(int i=1;i<=total_days;i++){
            String[] splitDate = pojo.get(0).getDate().split("-");
            Log.d("dateFormatL","date3 "+pojo.get(0).getDate());
            if(!dateHashmap.containsKey(i)){
                dateHashmap.put(i,"normal");
                dateNormal.add(CalendarDay.from(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]),i));

            }
        }
        if(dateHashmap.size()!=0){
            Calendar calendar=  Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(format.parse(todayDate));
                //customCalendar.setDate(calendar,dateHashmap);
               // BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
                BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave,  Color.parseColor(getApplicationContext().getString(R.string.npresnet)));

                // Add decorators to the calendar
               // customCalendar.addDecorator(decorator1);
                customCalendar.addDecorator(decorator2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//                                customCalendar.setDate(calendar,dateHashmap);
        }
    }

     private static List<Date> getDates(String dateString1, String dateString2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCalendarData(String month, List<CalendarSubClass> getPojoList, String getDate) {
/*        HashMap<Object, Property> mapDescToProp = new HashMap<>();
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.leave_normal;
        propDefault.enable = false;
        mapDescToProp.put("normal", propDefault);
        propDefault.dateTextViewResource=R.id.text_view;
        Property propUnavailable = new Property();
        propUnavailable.layoutResource = R.layout.leave_green;
        propUnavailable.dateTextViewResource=R.id.text_view;
        mapDescToProp.put("leave", propUnavailable);
        customCalendar.setMapDescToProp(mapDescToProp);*/
        Set<CalendarDay> dateNormal = new HashSet<>();
        Set<CalendarDay> dateLeave = new HashSet<>();

        int yr=2025,mon=1;
        Log.d("selectedMonth"," omnth "+month);
        if(getPojoList!=null) {
            if (getPojoList.size() != 0) {
                Log.d("selectedMonth"," list size "+getPojoList.size());
                String regex = "-"+month+"-";
                for(int i=0;i<getPojoList.size();i++){
                    CalendarSubClass commonPojo = getPojoList.get(i);
                    if(commonPojo.getStart().contains(regex) || commonPojo.getEnd().contains(regex)){
                        List<Date> dates = getDates(commonPojo.getStart(), commonPojo.getEnd());
                        for(Date date:dates){
                            Log.d("selectedMonth"," dates as "+date);
                            SimpleDateFormat curFormater = new SimpleDateFormat("dd");
                            SimpleDateFormat curFormater1 = new SimpleDateFormat("MM");
                            SimpleDateFormat curFormater2 = new SimpleDateFormat("yyyy");
                            int fate =Integer.parseInt( curFormater.format(date));
                            mon =Integer.parseInt( curFormater1.format(date));
                            yr =Integer.parseInt( curFormater2.format(date));
                            Log.d("selectedMonth"," selected date "+fate);
                            dateLeave.add(CalendarDay.from(yr,mon,fate));

                            dateHashmap.put(fate,"leave");
                            detailsHashmap.put(fate,commonPojo.getTitle());

                        }
                    }
                }
            }
        }
        int total_days = commonClass.getDaysInMonth(yr, mon);
        Log.d("totdal_days_coubt"," as "+total_days);
        for(int i=1;i<=total_days;i++){
            if(!dateHashmap.containsKey(i)){
               // dateHashmap.put(i,"normal");
                dateLeave.add(CalendarDay.from(yr,mon,i));
            }
        }
        if(dateHashmap.size()!=0){
            Calendar calendar=  Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(format.parse(getDate));
              //  customCalendar.setDate(calendar,dateHashmap);
               // BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.gray_50);
                BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave,  Color.parseColor(getApplicationContext().getString(R.string.npresnet)));

                // Add decorators to the calendar
               // customCalendar.addDecorator(decorator1);
                customCalendar.addDecorator(decorator2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//                                customCalendar.setDate(calendar,dateHashmap);
        }
    }

/*    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
         String sDate= newMonth.get(Calendar.YEAR)
                +"-" +(newMonth.get(Calendar.MONTH)+1)
                +"-" +newMonth.get(Calendar.DAY_OF_MONTH);
        String format_date=newMonth.get(Calendar.YEAR)+"-"+(newMonth.get(Calendar.MONTH)+1)+"-01";
        String month = String.valueOf(newMonth.get(Calendar.MONTH))+1;
        int mont =  newMonth.get(Calendar.MONTH)+1;
        String mon =String.valueOf(mont);
        customCalendar.getMonthYearTextView().setTextColor(R.color.black);
        Log.d("onNavigation"," month name "+customCalendar.getMonthYearTextView().getText().toString());
        customCalendar.getMonthYearTextView().getText().toString();

        if(mont<10){
            mon ="0"+String.valueOf(mont);
        }
        if(commonClass.isOnline(CalendarActivity.this)){
            callAPIHitMethod(mon,sDate, String.valueOf(newMonth.get(Calendar.YEAR)));
        }else{
            if(spinnerCalendar.getSelectedItemPosition()==2){
                getPojoList = holidayCalendar.getAllList();
                SimpleDateFormat df21 = new SimpleDateFormat("yyyy-MM-dd");
                String todayDate = df21.format(new Date());
                updateCalendarData(month, getPojoList, todayDate);
            }
        }
        // updateCalendarData(mon,getPojoList,sDate);
        //getCalendarList(format_date,month);
       // dateHashmap.clear();
        //commonClass.showAppToast(CalendarActivity.this,"Navigation called ");

        Log.d("get_date"," on navigation called "+newMonth.MONTH+" date value as "+sDate);
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        switch(newMonth.get(Calendar.MONTH)) {
            case Calendar.AUGUST:
                arr[0] = new HashMap<>(); //This is the map linking a date to its description
                arr[0].put(3, "unavailable");
                arr[0].put(6, "holiday");
                arr[0].put(21, "unavailable");
                arr[0].put(24, "holiday");
                arr[1] = null; //Optional: This is the map linking a date to its tag.
                break;
            case Calendar.JUNE:
                arr[0] = new HashMap<>();
                arr[0].put(5, "unavailable");
                arr[0].put(10, "holiday");
                arr[0].put(19, "holiday");
                break;
        }
        return arr;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackIntent();
    }

    private void callBackIntent() {
        Intent intent =new Intent(getApplicationContext(), DashboardNewActivity.class);
        startActivity(intent);
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
        /*    case R.id.nabout_layout:
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
            case R.id.back_arrow:
                callBackIntent();
                break;
        }
    }
}
