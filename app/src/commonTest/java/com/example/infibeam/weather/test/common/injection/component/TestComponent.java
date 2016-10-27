package com.example.infibeam.weather.test.common.injection.component;

import com.example.infibeam.weather.injection.component.ApplicationComponent;
import com.example.infibeam.weather.test.common.injection.module.ApplicationTestModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
