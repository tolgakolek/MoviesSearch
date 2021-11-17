package com.tolgakolek.mymovies.data.model

import com.squareup.moshi.Json

data class MovieDetail(
    @field:Json(name = "imdbID")
    var imdbID: String,

    var imdbRating: String,

    @field:Json(name = "Poster")
    var poster: String,

    @field:Json(name = "Plot")
    var plot: String,

    @field:Json(name = "Actors")
    var actors: String,

    @field:Json(name = "Genre")
    var genre: String,

    @field:Json(name = "Runtime")
    var runtime: String,

    @field:Json(name = "Released")
    var released: String,

    @field:Json(name = "Year")
    var year: String,

    @field:Json(name = "Title")
    var title: String
)
