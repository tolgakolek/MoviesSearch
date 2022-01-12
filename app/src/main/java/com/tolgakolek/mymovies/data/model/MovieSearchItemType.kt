package com.tolgakolek.mymovies.data.model

sealed class MovieSearchItemType {
    class LoadingType : MovieSearchItemType()
    class Item(val searchResult: SearchResult) : MovieSearchItemType()
}