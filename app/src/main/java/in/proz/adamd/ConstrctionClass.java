package in.proz.adamd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tuyenmonkey.mkloader.MKLoader;

import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConstrctionClass extends AppCompatActivity {
    ImageView back_btn;
    TextView app_header,app_sub_header,go_back;
    LinearLayout download_app;
    MKLoader loader;
    String apk_url=null;
    ImageView imageview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plocal_toast);
        back_btn = findViewById(R.id.back_btn);
        imageview = findViewById(R.id.imageview);
        loader = findViewById(R.id.loader);
        app_header = findViewById(R.id.app_header);
        app_sub_header = findViewById(R.id.app_sub_header);
        download_app = findViewById(R.id.download_app);
        go_back = findViewById(R.id.go_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        getList();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            String header = b.getString("header");
            String sheader = b.getString("sheader");
            app_header.setText(header);
            app_sub_header.setText(sheader);
            if(header.contains("mismatch")){
                imageview.setImageResource(R.drawable.under_maintenance);
                download_app.setVisibility(View.VISIBLE);
            }else{
                imageview.setImageResource(R.drawable.construction);
                download_app.setVisibility(View.GONE);
            }
         }


        download_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonClass commonClass = new CommonClass();
                Log.d("appk_url"," url "+apk_url);
                if(!TextUtils.isEmpty(apk_url)){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(apk_url));
                    startActivity(intent);
                }else{
                    commonClass.showError(ConstrctionClass.this,"No APK Found");
                }
            }
        });

    }

    private void getList() {
        CommonClass commonClass = new CommonClass();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth().create(ApiInterface.class);
        Call<CommonPojo> call =apiInterface.getAppDownloadURL();
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getStatus().equals("success")){
                            apk_url = response.body().getData();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
        finishAffinity();
        System.exit(0);
        moveTaskToBack(true);
    }
}
