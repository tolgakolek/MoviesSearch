package com.tolgakolek.mymovies.data.model

import com.squareup.moshi.Json

data class SearchResult(
    @field:Json(name = "Search")
    var search: List<Movie>,
    val totalResults: Int
) {
    data class Movie(
        val imdbID: String,

        @field:Json(name = "Title")
        val title: String,

        @field:Json(name = "Year")
        val year: String,

        @field:Json(name = "Poster")
        val poster: String
    )
}
