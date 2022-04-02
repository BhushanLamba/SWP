package wts.com.newdesigntask.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient
{


    public static final String BASE_URL = "http://subapi.allcardfind.com/api/";
    public static final String BASE_URL_PLANS = "http://subapi.allcardfind.com/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    private Retrofit retrofitPlans;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(7000, TimeUnit.SECONDS)
                .readTimeout(7000, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitPlans = new Retrofit.Builder()
                .baseUrl(BASE_URL_PLANS).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public WebServiceInterface getApi() {
        return retrofit.create(WebServiceInterface.class);
    }

    public WebServiceInterface getApiPlans() {
        return retrofitPlans.create(WebServiceInterface.class);
    }

}
