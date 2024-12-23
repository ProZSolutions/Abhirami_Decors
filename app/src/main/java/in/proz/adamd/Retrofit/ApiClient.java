package in.proz.adamd.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    public static Retrofit getApiClient()
    {
        retrofit=null;
        CommonClass commonClass=new CommonClass();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(600000, TimeUnit.SECONDS)
                .connectTimeout(600000, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit==null)
        {
            String BASE_URL = commonClass.commonURL();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).
                    client(okHttpClient).build();
        }
        return retrofit;
    }
   public static Retrofit getTokenRetrofit(final String token,final String device_id){
        retrofit=null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
              Log.d("auth_token"," aut "+token+" device "+device_id);
                Request request = chain.request();

                Request authenticatedRequest = request.newBuilder()
                        .header("Authorization", token)
                        .header("Accept","application/json")
                         .header("Device-ID",device_id)
                        .build();
                return chain.proceed(authenticatedRequest);
            }

        });
        CommonClass commonClass=new CommonClass();
        String BASE_URL = commonClass.commonURL();
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static Retrofit getTokenRetrofitRR(final String token,final String device_id){
        retrofit=null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d("auth_token"," aut "+token+" device "+device_id);
                Request request = chain.request();

                Request authenticatedRequest = request.newBuilder()
                        .header("Authorization", token)
                        .header("Accept","application/json")
                        .header("Device-ID",device_id)
                        .build();
                return chain.proceed(authenticatedRequest);
            }

        });
        CommonClass commonClass=new CommonClass();
        String BASE_URL = commonClass.commonURL();
        return new Retrofit.Builder().baseUrl("https://revathitraders.in/api/")
                .client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .build();
    }
   public static Retrofit getTokenWithoutAuth(){
        retrofit=null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                 Request request = chain.request();

                Request authenticatedRequest = request.newBuilder()
                         .header("Accept","application/json")
                         .build();
                return chain.proceed(authenticatedRequest);
            }

        });
        CommonClass commonClass=new CommonClass();
        String BASE_URL = commonClass.commonURL();
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}
