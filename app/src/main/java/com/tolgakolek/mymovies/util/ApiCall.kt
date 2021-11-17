package com.tolgakolek.mymovies.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.Response

suspend fun <T> apiCall(call: suspend () -> Response<T>): Flow<DataState<T>> {
    return flow<DataState<T>> {
        val response = call()
        if (response.isSuccessful)
            response.body()?.let {
                emit(DataState.Success(it))
            } ?: run {
                emit(DataState.Error(response.message()))
            }
        else {
            emit(DataState.Error(response.message()))
        }
    }
        .catch { emit(DataState.Error(it.message)) }
        .onStart { emit(DataState.Loading()) }
}