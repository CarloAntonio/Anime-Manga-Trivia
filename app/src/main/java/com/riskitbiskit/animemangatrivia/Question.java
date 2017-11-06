package com.riskitbiskit.animemangatrivia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("question")
    private String mQuestion;

    @SerializedName("correct_answers")
    private String mAnswer;

    @SerializedName("incorrect_answers")
    private List<String> mIncorrectAnswers;

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return mIncorrectAnswers;
    }
}
