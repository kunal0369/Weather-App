package com.example.infibeam.weather.ui.main;

import com.example.infibeam.weather.data.DataManager;
import com.example.infibeam.weather.data.model.WeatherData;
import com.example.infibeam.weather.injection.ConfigPersistent;
import com.example.infibeam.weather.ui.base.BasePresenter;
import com.example.infibeam.weather.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by kunal.chauhan on 10/26/2016.
 */

@ConfigPersistent
public class WeatherPresenter extends BasePresenter<WeatherMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public WeatherPresenter(DataManager dataManager){mDataManager = dataManager;}

    @Override
    public void attachView(WeatherMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if(mSubscription!=null)
            mSubscription.unsubscribe();
    }

    public void loadWeatherData(String lat, String lon, String app_id) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getWeatherData(lat,lon,app_id,"metric","35")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<WeatherData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was a problem loading the weather data");
                        getMvpView().onWeatherDataError();
                    }

                    @Override
                    public void onNext(WeatherData weatherData) {
                        Timber.d("Success in getting Weather data %s",weatherData.toString());

                        getMvpView().onWeatherDataSuccess(weatherData);
                    }
                });
    }
}
