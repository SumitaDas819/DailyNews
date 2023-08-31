package com.sumita.dailynews.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sumita.dailynews.ConnectivityUtils

class NewsViewModelProviderFactory(
    private val connectivityUtils: ConnectivityUtils,
    private val repo: NewsRepository

): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(connectivityUtils,repo) as T
    }
}