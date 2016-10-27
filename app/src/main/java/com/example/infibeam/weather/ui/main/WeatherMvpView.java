package com.example.infibeam.weather.ui.main;

import com.example.infibeam.weather.data.model.WeatherData;
import com.example.infibeam.weather.ui.base.MvpView;

/**
 * Created by kunal.chauhan on 10/26/2016.
 */

public interface WeatherMvpView extends MvpView {

    void onWeatherDataSuccess(WeatherData weatherData);

    void onWeatherDataError();
}
