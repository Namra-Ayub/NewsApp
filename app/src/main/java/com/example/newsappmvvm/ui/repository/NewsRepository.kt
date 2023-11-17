package com.example.newsappmvvm.ui.repository

import com.example.newsappmvvm.ui.api.ApiCallHandler
import com.example.newsappmvvm.ui.api.RetrofitInstance
import com.example.newsappmvvm.ui.db.ArticleDatabase
import com.example.newsappmvvm.ui.models.Article
import java.util.Locale.IsoCountryCode

class NewsRepository(
    val db: ArticleDatabase
): ApiCallHandler() {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        safeApiCall {
            RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
        }

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        safeApiCall {
            RetrofitInstance.api.searchForNews(searchQuery, pageNumber)
        }

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}