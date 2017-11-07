package com.riskitbiskit.animemangatrivia;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //Constants
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String ROOT_URL = "https://opentdb.com/";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String QUESTION_LIST = "question_list";
    public static final String APP_ID = "ca-app-pub-9407172029768846~2697309241";

    //Views
    @BindView(R.id.new_game_button)
    Button newGameButton;
    @BindView(R.id.main_frame)
    ImageView background;

    //Fields (with dependency injections)
    @Inject Retrofit.Builder mBuilder;

    //Fields
    List<Question.Results> mResults;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Set-up background
        Glide.with(this).load(R.drawable.anime_manga_main).into(background);

        //Initialize Ads
        MobileAds.initialize(this, APP_ID);

        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(ROOT_URL))
                .build();

        mBuilder = networkComponent.retrofitBuilder();

        Retrofit retrofit = mBuilder.build();

        ApiClient apiClient = retrofit.create(ApiClient.class);
        Call<Question> call = apiClient.quizGetter(DIFFICULTY_MEDIUM);

        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                mResults = response.body().getResults();

                newGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TriviaActivity.class);
                        intent.putExtra(QUESTION_LIST, (Serializable) mResults);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e(LOG_TAG, "Error sending trivia request");
            }
        });
    }
}
