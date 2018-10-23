package com.mattyang.demos.Dagger;

import dagger.Module;
import dagger.Provides;

@Module
public class CarModule {

    @Provides
    public static Car provideCar(){
        return new Car();
    }
}
