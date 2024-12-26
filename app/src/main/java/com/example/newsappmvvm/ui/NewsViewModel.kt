package com.example.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsappmvvm.ui.models.Article
import com.example.newsappmvvm.ui.models.NewsResponse
import com.example.newsappmvvm.ui.repository.NewsRepository
import com.example.newsappmvvm.ui.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository,
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<NetworkResult<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NetworkResult.Success<NewsResponse>? = null

    val searchNews: MutableLiveData<NetworkResult<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NetworkResult.Success<NewsResponse>? = null

    var isRecreated = false

    init {
        if (isRecreated){
            Log.d("NewsViewModel", "Recreated")
        }else {
            Log.d("NewsViewModel", "First time Created")
            isRecreated = true
        }

        getBreakingNews("us")
    }

    fun refreshBreakingNewsList(searchQuery: String){
        searchNewsResponse = null
        searchNewsPage = 1
        searchNews(searchQuery)
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            breakingNews.postValue(NetworkResult.Loading())
            newsRepository.getBreakingNews(countryCode, breakingNewsPage).let { resultResponse ->
                when (resultResponse) {
                    is NetworkResult.Success -> {
                        breakingNewsPage++
                        if (breakingNewsResponse == null) {
                            breakingNewsResponse = resultResponse
                        } else {
                            val oldArticles = breakingNewsResponse?.data?.articles
                            val newArticles = resultResponse.data.articles
                            oldArticles?.addAll(newArticles)
                        }
                        breakingNews.postValue(breakingNewsResponse ?: resultResponse)
                    }
                    else -> breakingNews.postValue(resultResponse)
                }
            }
        }
    }

    fun searchNews(searchQuery: String) {
        viewModelScope.launch {
            searchNews.postValue(NetworkResult.Loading())
            newsRepository.searchNews(searchQuery, searchNewsPage).let { resultResponse ->
                when (resultResponse) {
                    is NetworkResult.Success -> {
                        searchNewsPage++
                        if (searchNewsResponse == null) {
                            searchNewsResponse = resultResponse
                        } else {
                            val oldArticles = searchNewsResponse?.data?.articles
                            val newArticles = resultResponse.data.articles
                            oldArticles?.addAll(newArticles)
                        }
                        searchNews.postValue(searchNewsResponse ?: resultResponse)
                    }
                    else -> searchNews.postValue(resultResponse)
                }
            }
        }
    }


    /***
     * Database related functions
     */

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}