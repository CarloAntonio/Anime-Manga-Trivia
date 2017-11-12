package com.riskitbiskit.animemangatrivia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.jakewharton.rxbinding2.view.RxView;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    //Constants
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String ROOT_URL = "https://opentdb.com/";
    public static final String DIFFICULTY_EASY = "Easy";
    public static final String DIFFICULTY_MEDIUM = "Medium";
    public static final String DIFFICULTY_HARD = "Hard";
    public static final String SAVED_10 = "saved_10";
    public static final String SAVED_20 = "saved_20";
    public static final String SAVED_30 = "saved_30";
    public static final String SAVED_40 = "saved_40";
    public static final String QUESTION_LIST = "question_list";
    public static final String APP_ID = "ca-app-pub-9407172029768846~2697309241";

    //Views
    @BindView(R.id.new_game_button)
    Button newGameButton;
    @BindView(R.id.main_frame)
    ImageView background;
    @BindView(R.id.spinner_difficulty)
    MaterialSpinner mMSDifficulty;
    @BindView(R.id.spinner_num_questions)
    MaterialSpinner mMSNumQuestions;
    @BindView(R.id.ten_highscore)
    TextView mTenView;
    @BindView(R.id.twenty_highscore)
    TextView mTwentyView;
    @BindView(R.id.thirty_highscore)
    TextView mThirtyView;
    @BindView(R.id.forty_highscore)
    TextView mFortyView;

    //Fields (with dependency injections)
    @Inject Retrofit.Builder mBuilder;

    //Fields
    List<Question.Results> mResults;
    Context context = this;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Set-up background
        Glide.with(this).load(R.drawable.anime_manga_main).into(background);

        //Initialize Ads
        MobileAds.initialize(this, APP_ID);

        //Initialize shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Setup high score
        setupHighScoreView();

        //Grab a reference of the network component
        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(ROOT_URL))
                .build();

        //initialize retrofit builder using network component
        mBuilder = networkComponent.retrofitBuilder();

        setupSpinner();

        //Make API Call and handle result
        RxView.clicks(newGameButton)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {

                    //Check to see if a spinner choice was made
                    if (mMSDifficulty.getSelectedItem() != null && mMSNumQuestions.getSelectedItem() != null) {

                        //Grab user difficulty level and format for API call
                        String difficulty = mMSDifficulty
                                .getSelectedItem()
                                .toString()
                                //API requires difficulty to be all lowercase
                                .toLowerCase();

                        //Gran user question size
                        String questionSize = mMSNumQuestions
                                .getSelectedItem()
                                .toString()
                                .toLowerCase();

                        Retrofit retrofit = mBuilder.build();

                        ApiClient apiClient = retrofit.create(ApiClient.class);
                        Call<Question> call = apiClient.quizGetter(difficulty, questionSize);

                        //Make Call
                        call.enqueue(new Callback<Question>() {
                            @Override
                            public void onResponse(Call<Question> call, Response<Question> response) {
                                mResults = response.body().getResults();

                                //Start TriviaActivity
                                Intent intent = new Intent(context, TriviaActivity.class);
                                intent.putExtra(QUESTION_LIST, (Serializable) mResults);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<Question> call, Throwable t) {
                                Log.e(LOG_TAG, "Error sending trivia request");
                            }
                        });
                    } else {
                        //Remind User to choose difficulty
                        handleToastOnMainThread();
                    }
                });
    }

    private void setupHighScoreView() {
        if (mSharedPreferences.contains(SAVED_10)) {
            mTenView.setText(String.valueOf(mSharedPreferences.getInt(SAVED_10, 0)));
        } else {
            mTenView.setText("0");
        }

        if (mSharedPreferences.contains(SAVED_20)) {
            mTwentyView.setText(String.valueOf(mSharedPreferences.getInt(SAVED_20, 0)));
        } else {
            mTwentyView.setText("0");
        }

        if (mSharedPreferences.contains(SAVED_30)) {
            mThirtyView.setText(String.valueOf(mSharedPreferences.getInt(SAVED_30, 0)));
        } else {
            mThirtyView.setText("0");
        }

        if (mSharedPreferences.contains(SAVED_40)) {
            mFortyView.setText(String.valueOf(mSharedPreferences.getInt(SAVED_40, 0)));
        } else {
            mFortyView.setText("0");
        }
    }

    private void setupSpinner() {

        //Create a string array for difficulty options
        String[] items = {DIFFICULTY_EASY, DIFFICULTY_MEDIUM, DIFFICULTY_HARD };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMSDifficulty.setAdapter(adapter);

        //Create a string array of question size
        int[] numQuestions = getResources().getIntArray(R.array.number_questions);
        Integer[] integerQuestions = ArrayUtils.toObject(numQuestions);
        ArrayAdapter<Integer> numQuestionsAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, integerQuestions);
        numQuestionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMSNumQuestions.setAdapter(numQuestionsAdapter);

    }

    private void handleToastOnMainThread() {

        Handler handler = new Handler(Looper.getMainLooper());

        if (mMSDifficulty.getSelectedItem() == null && mMSNumQuestions.getSelectedItem() == null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Choose Difficulty & Questions Size", Toast.LENGTH_SHORT).show();
                }
            };
            handler.post(runnable);

        } else if (mMSDifficulty.getSelectedItem() == null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Choose Difficulty", Toast.LENGTH_SHORT).show();
                }
            };
            handler.post(runnable);

        } else if (mMSNumQuestions.getSelectedItem() == null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Choose Question Size", Toast.LENGTH_SHORT).show();
                }
            };
            handler.post(runnable);
        }
    }
}
