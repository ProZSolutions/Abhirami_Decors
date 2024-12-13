package in.proz.adamd.AdminModule;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.tuyenmonkey.mkloader.MKLoader;

import in.proz.adamd.R;

import in.proz.adamd.Retrofit.CommonClass;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class PDFViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    MKLoader loader;
    private PdfDownloader pdfDownloader;
    String pdf_url,type;
    ImageView back_arrow;
    String  pdf_name;
    File pdfFile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdfviewer);
        pdfView = findViewById(R.id.pdfView);
        pdfDownloader = new PdfDownloader();
        loader = findViewById(R.id.loader);
        Bundle b= getIntent().getExtras();
        if(b!=null){
            pdf_url = b.getString("pdf_url");
            type = b.getString("type");
        }
        back_arrow = findViewById(R.id.back_arrow);
         back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loader.setVisibility(View.VISIBLE);

        pdf_name = String.valueOf(new Date().getTime())+".pdf";
        Log.d("PDFCon"," url "+pdf_url+" name "+pdf_name);
        pdfFile = new File(getFilesDir(), pdf_name);
        pdfDownloader.downloadPdf(pdf_url, pdfFile);
       // pdfView.postDelayed(() -> pdfView.fromFile(pdfFile).load(), 5000);

     }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
     }

    public class PdfDownloader {


        private OkHttpClient client = new OkHttpClient();

        public void downloadPdf(String url, File file) {
            Log.d("PDFCon"," download"+url+" file "+file);
             Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("PDFCon"," errror "+e.getMessage());
                    updateError(e.getMessage());
                      e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                     if (response.isSuccessful() && response.body() != null) {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(response.body().bytes());
                        fos.close();
                         Log.d("PDFCon"," res "+response.body());
                         updatePDF();

                     }
                }
            });
        }
    }

    public void updateError(String message){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                loader.setVisibility(View.GONE);
                CommonClass commonClass = new CommonClass();
                commonClass.showError(PDFViewerActivity.this,message);

            }
        });

    }
    private void updatePDF() {
        pdfView.postDelayed(() -> pdfView.fromFile(pdfFile).load(), 2000);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                loader.setVisibility(View.GONE);


            }
        });
    }


}
