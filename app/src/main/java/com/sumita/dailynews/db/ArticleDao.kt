package com.sumita.dailynews.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sumita.dailynews.model.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(article: Article): Long

    @Query("select * from articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(article: Article)
}