package com.riskitbiskit.animemangatrivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TriviaActivity extends AppCompatActivity {

    //Constants
    public static final String QUESTION_NUMBER = "question_number";
    public static final String NUMBER_CORRECT = "number_correct";
    public static final String NUMBER_INCORRECT = "number_incorrect";
    public static final String INTERSTITIAL_APP_UNIT_ID = "ca-app-pub-9407172029768846/5620299135";
    public static final String TEST_INTERSTITIAL_APP_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    //Views
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
    @BindView(R.id.adView)
    AdView bannerAdView;
    @BindView(R.id.trivia_background)
    ImageView triviaBackground;

    //Fields (with dependency injections)
    @Inject InterstitialAd mInterstitialAd;
    @Inject AdRequest mAdRequest;

    //Fields
    List<Question.Results> mResults;
    String mQuestion;
    String mAnswer;
    List<String> mPossibleAnswers;
    int mQuestionNumber;
    int mCorrect;
    int mIncorrect;
    AdComponent mAdComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        ButterKnife.bind(this);

        //Setup Background Image
        Glide.with(this).load(R.drawable.am_image_2).into(triviaBackground);

        //Get intent from previous activity
        Intent intent = getIntent();

        //Grabs question list, question number, num of correct answers, and num of incorrect answers
        pullFromIntent(intent);

        //Create Ad Component
        mAdComponent = DaggerAdComponent.builder()
                .adModule(new AdModule(getApplicationContext()))
                .build();

        //Prep Interstitial Ad
        prepInterstitialAd();

        //Prep Banner Ad
        prepBannerAd();

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
        textView.setOnClickListener(view -> {
            TextView textView1 = (TextView) view;

            //checks to see if text in button text is equal to the correct answer text
            if (textView1.getText().equals(mAnswer)) {
                mCorrect++;
                newQuestion();
            } else {
                mIncorrect++;
                newQuestion();
            };
        });
    }

    private void newQuestion() {
        //Change to next question on list by adding 1 to count
        mQuestionNumber++;

        if (mQuestionNumber == mResults.size()) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                openResultsActivity();
            }
        } else {
            Intent intent = new Intent(this, TriviaActivity.class);
            intent.putExtra(MainActivity.QUESTION_LIST, (Serializable) mResults);
            intent.putExtra(QUESTION_NUMBER, mQuestionNumber);
            intent.putExtra(NUMBER_CORRECT, mCorrect);
            intent.putExtra(NUMBER_INCORRECT, mIncorrect);
            finish();
            startActivity(intent);
        }
    }

    private void openResultsActivity() {
        Intent intent = new Intent(this,ResultsActivity.class);
        intent.putExtra(MainActivity.QUESTION_LIST, (Serializable) mResults);
        intent.putExtra(NUMBER_CORRECT, mCorrect);
        intent.putExtra(NUMBER_INCORRECT, mIncorrect);
        finish();
        startActivity(intent);
    }

    //Format text method
    private String formatText(String unformatedText) {

        //Format for apostrophes
        unformatedText = unformatedText.replaceAll("&#039;", "'");

        unformatedText = unformatedText.replaceAll("&eacute;", "");

        //Format for quotes
        return unformatedText.replaceAll("&quot;", "\"");
    }

    //Grab data from intent method
    private void pullFromIntent(Intent intent) {
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

        //Check for number of questions answered correctly
        if (intent.hasExtra(NUMBER_CORRECT)) {
            mCorrect = intent.getIntExtra(NUMBER_CORRECT, 0);
        } else {
            mCorrect = 0;
        }

        //Check for number of questions answered incorrectly
        if (intent.hasExtra(NUMBER_INCORRECT)) {
            mIncorrect = intent.getIntExtra(NUMBER_INCORRECT, 0);
        } else {
            mIncorrect = 0;
        }
    }

    //Prep Interstitial Ad method
    private void prepInterstitialAd() {
        //Use dagger2 to grab an instance of Interstitial Ad
        mInterstitialAd = mAdComponent.interstitialAd();
        //TODO: Change before releasing for production
        //Set specific Ad unit id
        mInterstitialAd.setAdUnitId(INTERSTITIAL_APP_UNIT_ID);
        //Use dagger2 to get instance of AdRequest
        mInterstitialAd.loadAd(mAdComponent.adRequest());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                openResultsActivity();
            }
        });
    }

    //Prep Banner Ad method
    private void prepBannerAd() {
        //Use Dagger2 to grab instance of AdRequest
        mAdRequest = mAdComponent.adRequest();

        //load Ad into banner
        bannerAdView.loadAd(mAdRequest);
    }
}
