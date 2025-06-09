package com.example.version00.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.model.Article
import com.example.version00.ui.network.RetrofitClient2
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsViewModel : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> get() = _articles

    private val apiKey = "a445e9ec8755778dc2c7a2f98f6f3ea3"

    fun fetchNews() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient2.api.searchNews(apiKey = apiKey)
                _articles.value = response.articles
            } catch (e: Exception) {
                _articles.value = emptyList()
            }
        }
    }
}

