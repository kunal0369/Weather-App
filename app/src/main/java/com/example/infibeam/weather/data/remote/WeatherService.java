package com.example.infibeam.weather.data.remote;

import com.example.infibeam.weather.BuildConfig;
import com.example.infibeam.weather.data.model.WeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherService {

    String ENDPOINT = "http://api.openweathermap.org/data/2.5/forecast/";

    String QUERY_LAT = "lat";
    String QUERY_LONG = "lon";
    String QUERY_UNITS = "units";
    String QUERY_APPID = "APPID";
    String QUERY_CNT = "cnt";

    @GET(ENDPOINT)
    Observable<Response<WeatherData>> getWeatherData(@Query(QUERY_LAT) String lat,
                                                     @Query(QUERY_LONG) String lon,
                                                     @Query(QUERY_APPID) String appId,
                                                     @Query(QUERY_UNITS) String units
                                                     ,@Query(QUERY_CNT) String cnt);


    /******** Helper class that sets up a new services *******/
    class Creator {

        public static WeatherService newWeatherService() {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherService.class);
        }
    }
}
