package com.tolgakolek.mymovies.ui.moviesearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.mymovies.data.model.SearchResult
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
class MovieSearchViewModel @Inject constructor(private val movieRemoteDataSource: MovieRemoteDataSource) :
    ViewModel() {
    private val _viewState = MutableStateFlow(initialCreateViewState())
    val viewState = _viewState.asStateFlow()
    private var page = 1

    fun getMoviesSearch(searchTitle: String) {
        page = 1
        _viewState.value =
            _viewState.value.copy(movieTitle = searchTitle)
        viewModelScope.launch {
            movieRemoteDataSource.getSearchMovies(
                AppConstant.API_KEY,
                searchTitle,
                page
            ).collect {
                setMoviesSearch(it)
            }
        }
    }

    fun getMoreMovies() {
        viewModelScope.launch {
            movieRemoteDataSource.getSearchMovies(
                AppConstant.API_KEY,
                _viewState.value.movieTitle.toString(),
                page
            ).collect {
                setMoviesSearch(it)
            }
        }
    }

    private fun setMoviesSearch(searchResult: DataState<SearchResult>) {
        when (searchResult) {
            is DataState.Loading -> {
                _viewState.value =
                    _viewState.value.copy(isLoadData = false)
            }
            is DataState.Success -> {
                page++
                _viewState.value =
                    _viewState.value.copy(moviesSearch = searchResult.data, isLoadData = true)
            }
            is DataState.Error -> {
                _viewState.value =
                    _viewState.value.copy(isLoadData = true)
                Log.e("error", searchResult.message.toString())
            }
        }
    }

    private fun initialCreateViewState() = MovieSearchState()
}

data class MovieSearchState(
    val moviesSearch: SearchResult? = null,
    val movieTitle: String? = null,
    val isLoadData: Boolean = true
)