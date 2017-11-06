package com.riskitbiskit.animemangatrivia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("question")
    private String mQuestion;

    @SerializedName("correct_answers")
    private String mAnswer;

    @SerializedName("incorrect_answers")
    private List<IncorrectAnswer> mIncorrectAnswers;

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public List<IncorrectAnswer> getIncorrectAnswers() {
        return mIncorrectAnswers;
    }

    public class IncorrectAnswer {
        private String wrongAnswer1;
        private String wrongAnswer2;
        private String wrongAnswer3;

        public String getWrongAnswer1() {
            return wrongAnswer1;
        }

        public String getWrongAnswer2() {
            return wrongAnswer2;
        }

        public String getWrongAnswer3() {
            return wrongAnswer3;
        }
    }
}
