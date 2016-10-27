package com.example.infibeam.weather.injection.module;

import android.app.Application;
import android.content.Context;

import com.example.infibeam.weather.data.remote.WeatherService;
import com.example.infibeam.weather.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    WeatherService provideWeatherService() {
        return WeatherService.Creator.newWeatherService();
    }

}
