package com.mattyang.demos.Dagger;

import dagger.Component;

@Component(modules = CarModule.class)
public interface ManComponent {
    void injectMan(Man man);
}
