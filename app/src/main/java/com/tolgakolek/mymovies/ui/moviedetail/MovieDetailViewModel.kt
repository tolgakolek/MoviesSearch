package com.tolgakolek.mymovies.ui.moviedetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.mymovies.data.model.MovieDetail
import com.tolgakolek.mymovies.data.model.MovieDetailUiState
import com.tolgakolek.mymovies.data.remote.source.MovieRemoteDataSource
import com.tolgakolek.mymovies.util.AppConstant
import com.tolgakolek.mymovies.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(private val movieRemoteDataSource: MovieRemoteDataSource) :
    ViewModel() {
    private val _viewState = MutableStateFlow(MovieDetailUiState())
    val viewState = _viewState.asStateFlow()

    fun getMovieDetail(imdbId: String) {
        viewModelScope.launch {
            movieRemoteDataSource.getMovieDetails(AppConstant.API_KEY, imdbId).collect {
                when (it) {
                    is DataState.Success -> {
                        _viewState.value =
                            _viewState.value.copy(movie = it.data)
                    }
                    is DataState.Error -> {
                        Log.e("error", it.message.toString())
                    }
                }
            }
        }
    }
}