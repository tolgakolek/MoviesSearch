package com.tolgakolek.mymovies.ui.moviesearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.mymovies.data.model.MovieSearchUiState
import com.tolgakolek.mymovies.data.model.SearchResult
import com.tolgakolek.mymovies.data.remote.source.MovieRemoteDataSource
import com.tolgakolek.mymovies.util.AppConstant
import com.tolgakolek.mymovies.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(private val movieRemoteDataSource: MovieRemoteDataSource) :
    ViewModel() {
    private val _viewState = MutableStateFlow(MovieSearchUiState())
    val viewState = _viewState.asStateFlow()

    fun getMoviesSearch(searchTitle: String) {
        _viewState.value =
            _viewState.value.copy(movieTitle = searchTitle, page = 1)
        viewModelScope.launch {
            movieRemoteDataSource.getSearchMovies(
                AppConstant.API_KEY,
                searchTitle,
                _viewState.value.page
            ).collect {
                setMoviesSearch(it,true)
            }
        }
    }

    fun getMoreMovies() {
        viewModelScope.launch {
            movieRemoteDataSource.getSearchMovies(
                AppConstant.API_KEY,
                _viewState.value.movieTitle.toString(),
                _viewState.value.page
            ).collect {
                setMoviesSearch(it,false)
            }
        }
    }

    private fun setMoviesSearch(searchResult: DataState<SearchResult>, isSearch: Boolean) {
        when (searchResult) {
            is DataState.Loading -> {
                _viewState.value =
                    _viewState.value.copy(isLoadData = false)
            }
            is DataState.Success -> {
                if(isSearch){
                    _viewState.value =
                        _viewState.value.copy(moviesSearch = searchResult.data)
                } else {
                    val movieSearch = searchResult.data
                    _viewState.value.moviesSearch?.search?.let {
                        movieSearch.search = it.plus(movieSearch.search).distinctBy {
                            it.imdbID
                        }
                    }
                    _viewState.value =
                        _viewState.value.copy(moviesSearch = movieSearch)
                }
                _viewState.value =
                    _viewState.value.copy(isLoadData = true, page = _viewState.value.page.plus(1))
            }
            is DataState.Error -> {
                _viewState.value =
                    _viewState.value.copy(isLoadData = true)
                Log.e("error", searchResult.message.toString())
            }
        }
    }
}