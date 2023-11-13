package com.example.newsappmvvm.ui.repository

import com.example.newsappmvvm.ui.api.RetrofitInstance
import com.example.newsappmvvm.ui.db.ArticleDatabase
import java.util.Locale.IsoCountryCode

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
}