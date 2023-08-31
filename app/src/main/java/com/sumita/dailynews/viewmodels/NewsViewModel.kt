package com.sumita.dailynews.viewmodels

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.model.Article
import com.sumita.dailynews.utils.Resource
import com.sumita.dailynews.model.NewsResponse
import kotlinx.coroutines.launch
import retrofit2.Response

// Every time you need a dependency as parameter
// Like here, NewsRepository
// We create our own ViewModelProvider.Factory class to take the parameter

@RequiresApi(Build.VERSION_CODES.M)
class NewsViewModel(
    private val connectivityUtils: ConnectivityUtils,
    private val repo: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse:NewsResponse?=null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var serachNewsResponse:NewsResponse?=null

    init {
        getBreakingNews("in")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        if (connectivityUtils.isConnected()){
            breakingNews.postValue(Resource.Loading())
            val response = repo.getBreakingNews(countryCode, breakingNewsPage)
            Log.d("Response","$response")
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }else
            breakingNews.postValue(Resource.Error("No internet connection"))

    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun getSearchNews(searchQurey:String) = viewModelScope.launch {

        if (connectivityUtils.isConnected()){
            searchNews.postValue(Resource.Loading())
            val response = repo.getSearchNews(searchQurey, searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(response))
        }else
            searchNews.postValue(Resource.Error("No internet connection"))

    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    //Room function
    fun saveArticle(article: Article)=viewModelScope.launch {
        repo.saveArticle(article)
    }
    fun deleteArticles(article: Article)=viewModelScope.launch {
        repo.deleteArticles(article)
    }
    fun getAllArticles()=repo.getAllArticles()


}