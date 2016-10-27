package com.example.infibeam.weather.injection.component;

import android.app.Application;
import android.content.Context;

import com.example.infibeam.weather.data.DataManager;
import com.example.infibeam.weather.data.local.DatabaseHelper;
import com.example.infibeam.weather.data.local.PreferencesHelper;
import com.example.infibeam.weather.data.remote.WeatherService;
import com.example.infibeam.weather.injection.ApplicationContext;
import com.example.infibeam.weather.injection.module.ApplicationModule;
import com.example.infibeam.weather.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext Context context();
    Application application();
    WeatherService weatherService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();

}
