package com.riskitbiskit.animemangatrivia;

import android.content.Context;

import com.google.android.gms.ads.InterstitialAd;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AdModule {

    private final Context context;

    public AdModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    InterstitialAd providesInterstitialAd() {
        return new InterstitialAd(context);
    }




}
