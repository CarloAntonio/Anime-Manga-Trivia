package com.riskitbiskit.animemangatrivia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {

    @GET("api.php?category=31&type=multiple")
    Call<Question> quizGetter(@Query("difficulty") String difficulty, @Query("amount") String questionSize);

}
