package com.tolgakolek.mymovies.data.remote.service

import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.data.model.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("/?type=movie")
    suspend fun getSearchMovie(
        @Query(value = "apikey") apikey: String,
        @Query(value = "s") searchTitle: String,
        @Query(value = "page") page: Int
    ): Response<SearchResult>

    @GET("/?plot=full")
    suspend fun getMovieDetails(
        @Query(value = "apikey") apikey: String,
        @Query(value = "i") imdbId: String
    ): Response<MovieDetail>
}