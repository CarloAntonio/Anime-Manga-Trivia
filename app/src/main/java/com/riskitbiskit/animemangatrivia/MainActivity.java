package com.riskitbiskit.animemangatrivia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ApiClient apiClient = retrofit.create(ApiClient.class);
        Call<Question> call = apiClient.quizGetter(DIFFICULTY_MEDIUM);

        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                mResults = response.body().getResults();

                int i = 0;

                if (i < mResults.size()) {
                    Question.Results currentResult = mResults.get(i);

                    mQuestion = formatText(currentResult.getQuestion());
                    mAnswer = formatText(currentResult.getAnswer());
                    mPossibleAnswers = currentResult.getIncorrectAnswers();

                    //format possible answers
                    for (int j = 0; j < mPossibleAnswers.size(); j++) {
                        String formattedText = formatText(mPossibleAnswers.get(j));
                        mPossibleAnswers.set(j, formattedText);
                    }

                    questionView.setText(mQuestion);

                    mPossibleAnswers.add(mAnswer);

                    Collections.shuffle(mPossibleAnswers);
                    answerView2.setText(mPossibleAnswers.get(0));
                    answerView3.setText(mPossibleAnswers.get(1));
                    answerView4.setText(mPossibleAnswers.get(2));
                    answerView1.setText(mPossibleAnswers.get(3));

                    setClickListener(answerView1);
                    setClickListener(answerView2);
                    setClickListener(answerView3);
                    setClickListener(answerView4);
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e(LOG_TAG, "Error sending trivia request");
            }
        });
    }

    private void setClickListener(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view;

                //checks to see if text in textview is equal to the correct answer text
                if (textView.getText().equals(mAnswer)) {
                    Toast.makeText(getBaseContext(), "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Nope.", Toast.LENGTH_SHORT).show();
                };
            }
        });
    }

    private String formatText(String unformatedText) {

        unformatedText = unformatedText.replaceAll("&#039;", "'");
        String formatedText = unformatedText.replaceAll("&quot;", "\"");

        return formatedText;
    }
}
