package com.tolgakolek.mymovies.data.remote.source

import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.util.DataState
import kotlinx.coroutines.flow.Flow

interface MovieRemoteDataSource {
    suspend fun getSearchMovies(
        apikey: String,
        searchTitle: String,
        page: Int
    ): Flow<DataState<SearchResult>>

    suspend fun getMovieDetails(
        apikey: String,
        imdbId: String
    ): Flow<DataState<MovieDetail>>
}