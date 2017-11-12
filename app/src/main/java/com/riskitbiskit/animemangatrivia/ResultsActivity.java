package com.riskitbiskit.animemangatrivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity {

    //Fields
    @BindView(R.id.total_correct)
    TextView totalCorrectTV;
    @BindView(R.id.total_incorrect)
    TextView totalIncorrectTV;
    @BindView(R.id.score_tv)
    TextView scoreTV;
    @BindView(R.id.result_greeting_tv)
    TextView resultGreetingTV;
    @BindView(R.id.done_bt)
    Button doneBt;
    @BindView(R.id.results_background)
    ImageView resultsBackground;

    //Global variables
    List<Question.Results> mResults;
    int mCorrect;
    int mIncorrect;
    double percent;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.am_image_1).into(resultsBackground);

        Intent intent = getIntent();
        pullFromIntent(intent);

        //Initialize shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Check if new score is a high score
        boolean isNewHighScore = checkIfNewHighScore();

        if (isNewHighScore) {
            //Save high score data
            saveData();
        }

        totalCorrectTV.setText(String.valueOf(mCorrect));
        totalIncorrectTV.setText(String.valueOf(mIncorrect));
        scoreTV.setText(mCorrect + "/" + mResults.size());

        percent = Double.parseDouble(String.valueOf(mCorrect)) / Double.parseDouble(String.valueOf(mResults.size()));

        RxView.clicks(doneBt)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    Intent newGameIntent = new Intent(getBaseContext(), MainActivity.class);
                    newGameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newGameIntent);
                });

        setupGreeting(percent);
    }

    private boolean checkIfNewHighScore() {

        boolean returnValue = false;

        if (mResults.size() == 10) {
            if (mSharedPreferences.getInt(MainActivity.SAVED_10, 0) < mCorrect) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        }

        if (mResults.size() == 20) {
            if (mSharedPreferences.getInt(MainActivity.SAVED_20, 0) < mCorrect) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        }

        if (mResults.size() == 30) {
            if (mSharedPreferences.getInt(MainActivity.SAVED_30, 0) < mCorrect) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        }

        if (mResults.size() == 40) {
            if (mSharedPreferences.getInt(MainActivity.SAVED_40, 0) < mCorrect) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        }

        return returnValue;
    }

    private void saveData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (mResults.size() == 10) {
            editor.putInt(MainActivity.SAVED_10, mCorrect);
        } else if (mResults.size() == 20) {
            editor.putInt(MainActivity.SAVED_20, mCorrect);
        } else if (mResults.size() == 30) {
            editor.putInt(MainActivity.SAVED_30, mCorrect);
        } else {
            editor.putInt(MainActivity.SAVED_40, mCorrect);
        }

        editor.apply();

    }

    private void setupGreeting(double percent) {
        if (percent == 1) {
            resultGreetingTV.setText("Perfect!");
        } else if (percent > .80 && percent < 1) {
            resultGreetingTV.setText("Great Job!");
        } else if (percent > .50 && percent < .80) {
            resultGreetingTV.setText("Nice!");
        } else {
            resultGreetingTV.setText("Try Again");
        }
    }

    private void pullFromIntent(Intent intent) {
        //Check for question list
        if (intent.hasExtra(MainActivity.QUESTION_LIST)) {
            mResults = (List<Question.Results>) intent.getSerializableExtra(MainActivity.QUESTION_LIST);
        }

        //Check for number of questions answered correctly
        if (intent.hasExtra(TriviaActivity.NUMBER_CORRECT)) {
            mCorrect = intent.getIntExtra(TriviaActivity.NUMBER_CORRECT, 0);
        } else {
            mCorrect = 0;
        }

        //Check for number of questions answered incorrectly
        if (intent.hasExtra(TriviaActivity.NUMBER_INCORRECT)) {
            mIncorrect = intent.getIntExtra(TriviaActivity.NUMBER_INCORRECT, 0);
        } else {
            mIncorrect = 0;
        }
    }
}
