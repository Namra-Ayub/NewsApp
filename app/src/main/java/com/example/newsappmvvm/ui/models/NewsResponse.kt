package com.example.newsappmvvm.ui.models

import com.example.newsappmvvm.ui.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)