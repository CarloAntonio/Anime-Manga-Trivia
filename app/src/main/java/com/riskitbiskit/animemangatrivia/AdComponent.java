package com.riskitbiskit.animemangatrivia;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = AdModule.class)
public interface AdComponent {
    InterstitialAd interstitialAd();

    AdRequest adRequest();

    //Not use, unsure how to implement
    AdListener adListener();
}
