package in.proz.adamd.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Calendar;



public class cls_shared_data {

    public static String str_Attendance_Sync_Id;

    public static SharedPreferences sp_Auth, sp_Session;
    public static SharedPreferences.Editor editor, editor1;

    public static String s_Ipv4;
    public static String s_Mac;
    public static String s_Os_Ver;
    public static String s_Imei;
    public static String s_Auth_Key;
    public static String s_Session_Key;
    public static String s_Phone_No;

    public static String i_Edit_id;
    public static String i_Edit_Reason;
    public static String i_Edit_FileName;
    public static String i_Edit_Amount;
    public static String s_dt, s_dt1;
    public static String s_type;
    public static String s_leave_type;

    public static String s_UserName, s_Password;

    public static String s_Mode;

    public static String s_Open_Screen = "";

    public static double latitude, longitude;


//    public static String s_baseUrl="http://testapi.psprasand.com/";

    public static String s_baseUrl = "https://payroll.mettexlab.com/Index.php/";

    public static String s_url_dashboard = s_baseUrl + "dashboard-api/index";

    public static String s_url_login = s_baseUrl + "login-api/login/";
    public static String s_url_register = s_baseUrl + "login-api/register/";
    public static String s_url_Forgot_Password = s_baseUrl + "login-api/forgot/";
    public static String s_url_Check_Session = s_baseUrl + "login-api/sessionchk/";

    public static String s_url_claim_Create = s_baseUrl + "claim-api/create";
    public static String s_url_claim_Update = s_baseUrl + "claim-api/update?id=";
    public static String s_url_claim_List = s_baseUrl + "claim-api/index";

    public static String s_url_loan_Create = s_baseUrl + "loan-api/create";
    public static String s_url_loan_Update = s_baseUrl + "loan-api/update?id=";
    public static String s_url_loan_List = s_baseUrl + "loan-api/index";

    public static String s_url_leave_Create = s_baseUrl + "leave-api/create";
    public static String s_url_leave_Update = s_baseUrl + "leave-api/update/?id=";
    public static String s_url_leave_List = s_baseUrl + "leave-api/";

    public static String s_Url_Attendance_Punch_in = s_baseUrl + "attendance-api/punchin";
    public static String s_Url_Attendance_Punch_out = s_baseUrl + "attendance-api/punchout";

    public static String s_Url_Permission = s_baseUrl + "leave-api/permission";

    public static String s_Url_Image = "http://payroll.mettexlab.com";



    public static ArrayList<String> arr_Leave_Type = new ArrayList<>();
    public static ArrayList<String> arr_Leave_Day = new ArrayList<>();


    public static int socketTimeout = 30000;//30 seconds - change to what you want
    public static RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public static String s_Qry_Create_Attendance = "CREATE TABLE if not exists Attendance(Id  INTEGER PRIMARY KEY AUTOINCREMENT," +
            " DATE TEXT,IN_TIME TEXT,OUT_TIME TEXT,IN_LAT REAL(10,5),IN_LOG REAL(10,5),OUT_LAT REAL(10,5),OUT_LOG REAL(10,5),SYNC INT,SYNC_ID TEXT," +
            "In_Time_Stamp TEXT,Out_Time_Stamp TEXT)";

    public static String s_Qry_Drop_Attendance = "DROP TABLE IF EXISTS Attendance";

    public static String s_Qry_Create_Logs = "CREATE TABLE if not exists LOGS(Id  INTEGER PRIMARY KEY AUTOINCREMENT," +
            "DATE TEXT,LOG_TIME TEXT,LAT REAL(10,5),LOG REAL(10,5),SYNC int)";

    public static String s_Qry_Drop_Logs = "Drop table if exists Logs";

    public static String db_Name = "Database.db";
    public static int db_Ver = 2;

     public static SQLiteDatabase db;
    public static Cursor cur;



    public static void Create_Tbl(Context ctx) {
        try {
            System.out.println(cls_shared_data.s_Qry_Create_Attendance);
            db.execSQL(cls_shared_data.s_Qry_Create_Attendance);
            System.out.println(cls_shared_data.s_Qry_Create_Logs);
            db.execSQL(cls_shared_data.s_Qry_Create_Logs);
        } catch (Exception ex) {
            FancyToast.makeText(ctx, ex.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }
    }

    public static void Drop_Tbl(Context ctx) {
        try {
            System.out.println(cls_shared_data.s_Qry_Drop_Attendance);
            db.execSQL(cls_shared_data.s_Qry_Drop_Attendance);
            System.out.println(cls_shared_data.s_Qry_Drop_Logs);
            db.execSQL(cls_shared_data.s_Qry_Drop_Logs);
        } catch (Exception ex) {
            FancyToast.makeText(ctx, ex.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }
    }


    public static void get_AuthKey(Context ctx) {
        sp_Auth = ctx.getSharedPreferences("com.nimbuzsolutions.authkey", Context.MODE_PRIVATE);
        editor = sp_Auth.edit();
        s_Auth_Key = sp_Auth.getString("AuthKey", "");

    }

    public static void init_Session_Sp(Context ctx) {
        sp_Session = ctx.getSharedPreferences("com.nimbuzsolutions.Session", Context.MODE_PRIVATE);
        editor1 = sp_Session.edit();
        s_Session_Key = sp_Session.getString("sessionkey", "");
        s_UserName = sp_Session.getString("username", "");
    }

    public static void backGround_FancyToast(final Context context,
                                             final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    FancyToast.makeText(context, msg, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                }
            });
        }
    }

    public static void Init_Auth_Session(Context ctx) {
        cls_shared_data.init_Session_Sp(ctx);
        cls_shared_data.get_AuthKey(ctx);
    }

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String get_CurrentTime() {
        String delegate = "dd-MM-yyyy hh:mm:ss aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    public static void turnGPSOn(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.d("inline_error"," get provider "+provider);
        if(!TextUtils.isEmpty(provider)) {
            if (!provider.contains("gps")) { //if gps is enabled
                ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }

    }

    public static void turnGPSOff(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static AlertDialog.Builder dialog;
    public static AlertDialog alt;

    public static void close_Dialog() {
        alt.dismiss();
    }


}

