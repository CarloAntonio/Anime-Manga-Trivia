package com.riskitbiskit.animemangatrivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

    //Global variables
    List<Question.Results> mResults;
    int mCorrect;
    int mIncorrect;
    double percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        pullFromIntent(intent);

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
