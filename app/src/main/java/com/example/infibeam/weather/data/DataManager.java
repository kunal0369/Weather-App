package com.example.infibeam.weather.data;

import com.example.infibeam.weather.data.local.DatabaseHelper;
import com.example.infibeam.weather.data.local.PreferencesHelper;
import com.example.infibeam.weather.data.model.WeatherData;
import com.example.infibeam.weather.data.remote.WeatherService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class DataManager {

    private final WeatherService mWeatherService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(WeatherService weatherService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mWeatherService = weatherService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<WeatherData> getWeatherData(String lat, String lon, String appid, String units, String cnt) {

        return mWeatherService.getWeatherData(lat, lon, appid, units, cnt)
                .flatMap(new Func1<Response<WeatherData>, Observable<WeatherData>>() {

                             @Override
                             public Observable<WeatherData> call(Response<WeatherData> weatherDataResponse) {

                                 //TODO: remove sensitive debug logs
                                 Timber.d("status code: %s", weatherDataResponse.code());
                                 Timber.d("body: %s", weatherDataResponse.body());
                                 Timber.d("error body: %s", weatherDataResponse.errorBody());
                                 Timber.d("message: %s", weatherDataResponse.message());

                                 try{
                                     switch (weatherDataResponse.code()){
                                         case 200:
                                             return Observable.just(weatherDataResponse.body());
                                         default:
                                             return Observable.empty();
                                     }
                                 } catch (Exception e){
                                     Timber.e(e, "error while getting weather data");
                                     return Observable.error(e);
                                 }
                             }
                         }
                );
    }
}
