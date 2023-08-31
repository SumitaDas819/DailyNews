package com.sumita.dailynews.viewmodels

import com.sumita.dailynews.api.RetrofitInstance
import com.sumita.dailynews.db.ArticleDao
import com.sumita.dailynews.model.Article

class NewsRepository(val db: ArticleDao) {

    suspend fun getBreakingNews(countryCode:String,pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
    suspend fun getSearchNews(searchQuery:String,pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
    suspend fun saveArticle(article:Article)=db.saveArticle(article)
    suspend fun deleteArticles(article: Article)=db.deleteArticles(article)
    fun getAllArticles()=db.getAllArticles()

}