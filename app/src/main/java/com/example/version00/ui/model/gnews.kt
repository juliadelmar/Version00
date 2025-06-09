package com.example.version00.ui.model

data class GNewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val image: String?,
    val publishedAt: String,
    val source: Source
)

data class Source(
    val name: String,
    val url: String
)
