package com.example.infibeam.weather.injection.component;

import com.example.infibeam.weather.injection.PerActivity;
import com.example.infibeam.weather.injection.module.ActivityModule;
import com.example.infibeam.weather.ui.main.WeatherActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(WeatherActivity weatherActivity);

}
