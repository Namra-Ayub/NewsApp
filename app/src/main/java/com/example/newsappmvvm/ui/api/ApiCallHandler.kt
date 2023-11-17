package com.example.newsappmvvm.ui.api

import com.example.newsappmvvm.ui.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class ApiCallHandler {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(apiCall.invoke())
            }catch (e: Exception){
                when(e){
                    is HttpException ->{
                        NetworkResult.Failure(false, e.response()?.code(), e.response()?.errorBody())
                    }
                    else -> {
                        NetworkResult.Failure(true, null, null)
                    }
                }
            }
        }
    }
}