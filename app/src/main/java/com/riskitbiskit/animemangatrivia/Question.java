package com.riskitbiskit.animemangatrivia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("results")
    private List<Results> mResults;

    public List<Results> getResults() {
        return mResults;
    }

    public class Results {
        @SerializedName("question")
        private String mQuestion;

        @SerializedName("correct_answer")
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
}
