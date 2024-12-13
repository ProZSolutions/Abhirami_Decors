package in.proz.adamd;

import android.content.Intent;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Request.RequestActivity;

public class DocumentView extends AppCompatActivity {
    ImageView backarrow,iv_zoomable;
    PDFView pdfView;
    WebView webview ;
    String url,type,intent_type;
    MKLoader loader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_layout);
        backarrow= findViewById(R.id.back_arrow);
        iv_zoomable = findViewById(R.id.iv_zoomable);
        pdfView = findViewById(R.id.pdfView);
        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        loader = findViewById(R.id.loader);

        // Configure a WebViewClient to handle navigation events
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Return false to allow the WebView to handle the URL
                return false;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

        });

        // Configure a WebChromeClient (optional)
        webview.setWebChromeClient(new WebChromeClient() {});
        Bundle b = getIntent().getExtras();
        if(b!=null){
            url = b.getString("url");
            type = b.getString("type");
            intent_type =  b.getString("intent_type");
        }
        if(type!=null){
            Log.d("loadURL"," url as "+url+" type "+type+" intent "+intent_type);
            if(type.equals("0")){
                loader.setVisibility(View.VISIBLE);
                Picasso.with(this).load(url).into(iv_zoomable, new Callback() {
                    @Override
                    public void onSuccess() {
                        loader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
                iv_zoomable.setVisibility(View.VISIBLE);
                webview.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
            }else{
                loader.setVisibility(View.VISIBLE);
                iv_zoomable.setVisibility(View.GONE);
                webview.setVisibility(View.GONE);
               // webview.loadUrl(url);
                pdfView.setVisibility(View.VISIBLE);
                FileLoader.with(DocumentView.this)
                        .load(url)
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest fileLoadRequest, FileResponse<File> fileResponse) {
                                File pdfFile=fileResponse.getBody();

                                pdfView.fromFile(pdfFile).defaultPage(0)
                                        .spacing(10)
                                        .onPageChange(new OnPageChangeListener() {
                                            @Override
                                            public void onPageChanged(int page, int pageCount) {

                                            }
                                        })
                                        .onPageScroll(new OnPageScrollListener() {
                                            @Override
                                            public void onPageScrolled(int page, float positionOffset) {

                                            }
                                        })
                                        .enableSwipe(true)
                                        .swipeHorizontal(false)
                                        .enableDoubletap(true)
                                         .scrollHandle(new DefaultScrollHandle(DocumentView.this))
                                        .onPageScroll(new OnPageScrollListener() {
                                            @Override
                                            public void onPageScrolled(int page, float positionOffset) {

                                            }
                                        })
                                        .load();
                                loader.setVisibility(View.GONE);

                             }

                            @Override
                            public void onError(FileLoadRequest fileLoadRequest, Throwable throwable) {
                                loader.setVisibility(View.GONE);
                            }
                        });
                //new RetrivePDFfromUrl().execute(url);

            }
        }
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              callBackPress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackPress();
    }

    private void callBackPress() {
        if(!TextUtils.isEmpty(intent_type)){
            Intent intent=null;
            if(intent_type.equals("request")){
                intent = new Intent(getApplicationContext(), Request.class);
            }else if(intent_type.equals("dsr")){
                intent = new Intent(getApplicationContext(), DSRActivity.class);
            }else if (intent_type.equals("ticket")){
                intent = new Intent(getApplicationContext(), RequestActivity.class);
                intent.putExtra("request_type","ticket");
            }else if (intent_type.equals("loan")){
                intent = new Intent(getApplicationContext(), LoanActivity.class);
            }else if(intent_type.contains("claim")){
                intent = new Intent(getApplicationContext(), ClaimActivity.class);
                intent.putExtra("claim_type",intent_type);
            }else if(intent_type.equals("Leave")){
                intent = new Intent(getApplicationContext(), LeaveActivity.class);

            }
            startActivity(intent);
        }
    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream).load();
        }
    }
}
