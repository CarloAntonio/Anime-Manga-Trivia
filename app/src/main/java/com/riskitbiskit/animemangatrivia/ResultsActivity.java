package com.riskitbiskit.animemangatrivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity {

    //Fields
    @BindView(R.id.total_correct)
    TextView totalCorrectTV;
    @BindView(R.id.total_incorrect)
    TextView totalIncorrectTV;

    //Global variables
    List<Question.Results> mResults;
    int mCorrect;
    int mIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        pullFromIntent(intent);

        totalCorrectTV.setText(mCorrect + "/" + mResults.size());
        totalIncorrectTV.setText(mIncorrect + "/" + mResults.size());
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
