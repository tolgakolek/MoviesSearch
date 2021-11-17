package com.tolgakolek.mymovies.data.remote.source

import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.data.remote.service.MovieService
import com.tolgakolek.mymovies.util.DataState
import com.tolgakolek.mymovies.util.apiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRemoteDataSourceImpl @Inject constructor(private val movieService: MovieService) :
    MovieRemoteDataSource {
    override suspend fun getSearchMovies(
        apikey: String,
        searchTitle: String,
        page: Int
    ): Flow<DataState<SearchResult>> {
        return apiCall { movieService.getSearchMovie(apikey, searchTitle, page) }
    }

    override suspend fun getMovieDetails(
        apikey: String,
        imdbId: String
    ): Flow<DataState<MovieDetail>> {
        return apiCall { movieService.getMovieDetails(apikey, imdbId) }
    }
}