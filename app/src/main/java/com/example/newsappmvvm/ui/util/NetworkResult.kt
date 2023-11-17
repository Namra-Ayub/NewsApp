package com.example.newsappmvvm.ui.util

import okhttp3.ResponseBody

sealed class NetworkResult<out T> {

    data class Success<out T>(val data: T): NetworkResult<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ): NetworkResult<Nothing>()
    class Loading<T> : NetworkResult<T>()

}