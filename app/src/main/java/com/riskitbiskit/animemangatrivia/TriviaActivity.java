package com.riskitbiskit.animemangatrivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TriviaActivity extends AppCompatActivity {

    //Constants
    public static final String QUESTION_NUMBER = "question_number";

    //Fields
    @BindView(R.id.question)
    TextView questionView;
    @BindView(R.id.answer1)
    TextView answerView1;
    @BindView(R.id.answer2)
    TextView answerView2;
    @BindView(R.id.answer3)
    TextView answerView3;
    @BindView(R.id.answer4)
    TextView answerView4;

    //Global variables
    List<Question.Results> mResults;
    String mQuestion;
    String mAnswer;
    List<String> mPossibleAnswers;
    int mQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        ButterKnife.bind(this);

        //Get intent from previous activity
        Intent intent = getIntent();

        //Check for question list
        if (intent.hasExtra(MainActivity.QUESTION_LIST)) {
            mResults = (List<Question.Results>) intent.getSerializableExtra(MainActivity.QUESTION_LIST);
        }

        //Check for placement in question list, if there is one
        if (intent.hasExtra(QUESTION_NUMBER)) {
            mQuestionNumber = intent.getIntExtra(QUESTION_NUMBER, 0);
        } else {
            mQuestionNumber = 0;
        }

        //Grab the specific question from the list based number
        Question.Results currentResult = mResults.get(mQuestionNumber);

        //Grab question, answer, and incorrect answers from current question
        mQuestion = formatText(currentResult.getQuestion());
        mAnswer = formatText(currentResult.getAnswer());
        mPossibleAnswers = currentResult.getIncorrectAnswers();

        //format possible answers
        for (int j = 0; j < mPossibleAnswers.size(); j++) {
            String formattedText = formatText(mPossibleAnswers.get(j));
            mPossibleAnswers.set(j, formattedText);
        }

        //Set question to question textview
        questionView.setText(mQuestion);

        //Add answer into the list of incorrect answers
        mPossibleAnswers.add(mAnswer);

        //Shuffle incorrect and correct answers
        Collections.shuffle(mPossibleAnswers);

        //Set answers to button
        answerView1.setText(mPossibleAnswers.get(0));
        answerView2.setText(mPossibleAnswers.get(1));
        answerView3.setText(mPossibleAnswers.get(2));
        answerView4.setText(mPossibleAnswers.get(3));

        //Set click listeners to each button
        setClickListener(answerView1);
        setClickListener(answerView2);
        setClickListener(answerView3);
        setClickListener(answerView4);

    }

    private void setClickListener(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view;

                //checks to see if text in button text is equal to the correct answer text
                if (textView.getText().equals(mAnswer)) {
                    Toast.makeText(getBaseContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    newQuestion();
                } else {
                    Toast.makeText(getBaseContext(), "Nope.", Toast.LENGTH_SHORT).show();
                    newQuestion();
                };
            }
        });
    }

    private void newQuestion() {
        mQuestionNumber++;
        Intent intent = new Intent(this, TriviaActivity.class);
        intent.putExtra(MainActivity.QUESTION_LIST, (Serializable) mResults);
        intent.putExtra(QUESTION_NUMBER, mQuestionNumber);
        finish();
        startActivity(intent);
    }

    private String formatText(String unformatedText) {

        //Format for apostrophes
        unformatedText = unformatedText.replaceAll("&#039;", "'");

        //Format for quotes
        return unformatedText.replaceAll("&quot;", "\"");
    }
}
