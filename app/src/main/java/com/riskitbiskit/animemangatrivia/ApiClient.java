package com.riskitbiskit.animemangatrivia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {

    @GET("api.php?amount=20&category=31&type=multiple")
    Call<Question> quizGetter(@Query("difficulty") String difficulty);

}
