package com.riskitbiskit.animemangatrivia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
                List<Question.Results> results = response.body().getResults();

                for (int i = 0; i < results.size(); i++) {
                    Question.Results currentResult = results.get(i);

                    String unformatedQuestion = currentResult.getQuestion();
                    String formattedQuestion = formatText(unformatedQuestion);
                    questionView.setText(formattedQuestion);

                    String unformattedAnswer = currentResult.getAnswer();
                    String formattedAnswer = formatText(unformattedAnswer);
                    answerView1.setText(formattedAnswer);

                    List<String> incorrectAns = currentResult.getIncorrectAnswers();
                    answerView2.setText(incorrectAns.get(0));
                    answerView3.setText(incorrectAns.get(1));
                    answerView4.setText(incorrectAns.get(2));
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e(LOG_TAG, "Error sending trivia request");
            }
        });
    }

    private String formatText(String unformatedText) {

        unformatedText = unformatedText.replaceAll("&#039;", "'");
        String formatedText = unformatedText.replaceAll("&quot;", "\"");

        return formatedText;
    }
}
