package com.tolgakolek.mymovies.data.model

data class MovieSearchUiState(
    val moviesSearch: SearchResult? = null,
    val movieTitle: String? = null,
    val isLoadData: Boolean = true,
    val page: Int = 1
)