package in.proz.adamd.Retrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import com.google.android.gms.maps.model.Dash;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.YearMonth;

import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.LoginActivity;
import in.proz.adamd.R;

public class CommonClass {
    public static int socketTimeout = 30000;//30 seconds - change to what you want
    public static RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    public String commonURL(){
        // //every time change the version in about / copyright number when change
     //return  "https://payrolltesting.proz.in/api/";
        return "https://adams.proz.in/api/ ";
     //return  "https://payroll.proz.in/api/";
    }
    public boolean containsTamil(String text) {
        // Regular expression for Tamil characters
        String tamilRegex = "[\\u0B80-\\u0BFF]+";
        return text.matches(".*" + tamilRegex + ".*");
    }

    public boolean containsEnglish(String text) {
        // Regular expression for English characters (A-Z, a-z)
        String englishRegex = "[a-zA-Z]+";
        return text.matches(".*" + englishRegex + ".*");
    }
    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDaysInMonth(int year, int month) {
             YearMonth yearMonth = YearMonth.of(year, month);

            return yearMonth.lengthOfMonth(); // Returns the number of days in the month

    }
    public String splitDate(String string) {
        Log.d("splitString"," date "+string);
        string = string.replace("}","");
        string = string.replace("{","");
        string = string.replace("CalendarDay","");
        Log.d("splitString"," af date "+string);
        String[] split = string.split("-");
        String mont= split[1];
        String date= split[2];
        if(Integer.parseInt(split[1])<=9){
            mont="0"+split[1];
        }
        if(Integer.parseInt(split[2])<=9){
            date="0"+split[2];
        }
        String rtn = date+"-"+mont+"-"+split[0];
        return rtn;
    }
    public String splitYear(String string) {
        Log.d("splitString"," date "+string);
        string = string.replace("}","");
        string = string.replace("{","");
        string = string.replace("CalendarDay","");
        Log.d("splitString","af  date "+string);
        String[] split = string.split("-");
        String mont= split[1];
        String date= split[2];
        if(Integer.parseInt(split[1])<=9){
            mont="0"+split[1];
        }
        if(Integer.parseInt(split[2])<=9){
            date="0"+split[2];
        }
        String rtn = split[0]+"-"+mont+"-"+date;
        return rtn;
    }
    public String registerURL(){return "login-api/register/";}
    public String loginURL(){return "login-api/login/";}
    public String forgotURL(){return "login-api/forgot/";}
    public String checkSession(){return "login-api/sessionchk/"; }
    public static String s_url_dashboard =  "dashboard-api/index";
    public static String s_url_claim_List =  "claim-api/index";
    public static String s_update_claim_list="claim-api/update?id=";
    public static String s_url_loan_Create =  "loan-api/create";
    public static String s_url_loan_Update = "loan-api/update?id=";
    public static String s_url_loan_List =  "loan-api/index";
    public static String s_url_claim_Create =  "claim-api/create";

    public static String s_url_leave_Create =  "leave-request";
    public static String s_url_leave_Update =  "leave-api/update/?id=";
    public static String s_url_leave_List =  "leave-api/";

    public static String s_Url_Attendance_Punch_in =  "attendance-api/punchin";
    public static String s_Url_Attendance_Punch_out =  "attendance-api/punchout";

    public static String s_Url_Permission =  "leave-api/permission";

    public static String s_Url_Image = "http://payroll.mettexlab.com";

    public static String ss_Url_Image = "https://payroll.mettexlab.com";

    public String getDeviceID(Context context){

            String ID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
       // String ID="8bdad70785414480";
        return ID;
    }
    public String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver contentResolver = context.getContentResolver();
            try {
                // Query the content resolver to get the filename
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }
    public String getFileTypeFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        // If the URI scheme is content
        if ("content".equals(uri.getScheme())) {
            // Use ContentResolver to get the MIME type
            return contentResolver.getType(uri);
        }

        // If the URI scheme is file
        if ("file".equals(uri.getScheme())) {
            // Extract the file extension from the URI path and get the MIME type
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        // For other URI schemes, return null or handle as needed
        return null;
    }

    public void showDocumentImage(Activity context,String url,int type){
        //dialog.setContentView(R.layout.dlg_warning);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.document_layout,null);
        ImageView backarrow= view.findViewById(R.id.back_arrow);
        ImageView iv_zoomable = view.findViewById(R.id.iv_zoomable);
        WebView webview = view.findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Configure a WebViewClient to handle navigation events
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Return false to allow the WebView to handle the URL
                return false;
            }
        });

        // Configure a WebChromeClient (optional)
        webview.setWebChromeClient(new WebChromeClient() {});
        if(type==0){
            Picasso.with(context).load(url).into(iv_zoomable);
            webview.setVisibility(View.GONE);
            iv_zoomable.setVisibility(View.VISIBLE);
        }else{
            webview.loadUrl(url);
            webview.setVisibility(View.VISIBLE);
            iv_zoomable.setVisibility(View.GONE);
        }
         builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

    }
    public void showWarning(Activity context,String message){
         //dialog.setContentView(R.layout.dlg_warning);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.dlg_warning,null);

         TextView text = (TextView) view.findViewById(R.id.msg_text);
        text.setText(message);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.create();
        mDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (context != null && !context.isFinishing() && !context.isDestroyed()) {
                    if (mDialog != null) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();

                        }
                    }
                }
            }
        }, 2500);
    }

    public void showInternetWarning(Activity context){
        String message ="This Module Will Work When Internet Available";
        //dialog.setContentView(R.layout.dlg_warning);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.dlg_warning,null);

        TextView text = (TextView) view.findViewById(R.id.msg_text);
        TextView btn_1 = view.findViewById(R.id.btn_1);
        btn_1.setText("OK");
        text.setText(message);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.create();
        mDialog.show();
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, DashboardNewActivity.class);
                context.startActivity(intent);
            }
        });

    }
    public void showSuccess(Activity context,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.dlg_success,null);

        TextView text = (TextView) view.findViewById(R.id.msg_text);
        text.setText(message);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(context != null && !context.isFinishing() && !context.isDestroyed()) {

                    if (mDialog != null) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();

                        }
                    }

                }
            }
        }, 2500);
    }
    public void clearAlldata(Activity con){
        CommonClass commonClass = new CommonClass();
        commonClass.putSharedPref(con, "token", null);
        commonClass.putSharedPref(con, "punch_in", null);
        commonClass.putSharedPref(con, "sync_id", null);
        commonClass.putSharedPref(con, "members", null);
        commonClass.putSharedPref(con, "work_location", null);
        commonClass.putSharedPref(con, "dsr_date", null);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(con, LoginActivity.class);
                con.startActivity(intent);
            }
        }, 1500);

    }
    public void showError(Activity context,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.dlg_error,null);

        TextView text = (TextView) view.findViewById(R.id.msg_text);
        text.setText(message);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (context != null && !context.isFinishing() && !context.isDestroyed()) {
                    if (mDialog != null) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();

                        }
                    }
                }

            }
        }, 2500);
    }



 /*   public void showAppToast(Activity context,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.plocal_toast,null);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(message);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();

    }*/
    public void showServerToast(Activity context,String message){
        LayoutInflater inflater =  context.getLayoutInflater();
        View layout = inflater.inflate( R.layout.pserver_toast,
                (ViewGroup) context.findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    public void callMainIntent(Context context){
        Intent intent = new Intent(context, DashboardNewActivity.class);
        context.startActivity(intent);
    }

    public void putSharedPref(Context context,String tag,String value){
        SharedPreferences sp=context.getSharedPreferences(tag,0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(tag,value);
        editor.apply();
        editor.commit();
        Log.d("getEmployeeList"," putted ");
    }
    public String getSharedPref(Context context,String tag){
        SharedPreferences sp=context.getSharedPreferences(tag,0);
        return  sp.getString(tag,null);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void onlineStatusCheck(Context context, LinearLayout linearLayout, TextView textView, ImageView imageView) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.success_color));
            textView.setText("Online");
            imageView.setImageResource(R.drawable.online);
         } else {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.failure_color));
            textView.setText("Offline");
            imageView.setImageResource(R.drawable.offline);
         }
    }
    public void NetworkError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Problem");
        builder.setMessage("Internet connection is not Available.Please connect to Login");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#2874F0"));

            }
        });
        alertDialog.show();
    }
}
