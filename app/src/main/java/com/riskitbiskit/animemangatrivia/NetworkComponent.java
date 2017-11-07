package com.riskitbiskit.animemangatrivia;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton @Component(modules = NetworkModule.class)
public interface NetworkComponent {

    Retrofit.Builder retrofitBuilder();

}
