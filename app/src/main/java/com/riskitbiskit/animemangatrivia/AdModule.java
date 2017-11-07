package com.riskitbiskit.animemangatrivia;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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

    @Provides @Singleton
    AdRequest providesAdRequestBuilder() {
        return new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
    }

    //Not use, unsure how to implement
    @Provides @Singleton
    AdListener providesAdListener() {
        return new AdListener();
    }


}
