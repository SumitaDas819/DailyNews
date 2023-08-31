package com.sumita.dailynews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sumita.dailynews.model.Article

@Database([Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDataBase : RoomDatabase(){

    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile   //to tell all the threads that the value has updated
        private var INSTANCE: ArticleDataBase?=null
        fun createDatabase(context: Context): ArticleDataBase {
            if (INSTANCE ==null){
                synchronized(this){
                    INSTANCE = Room.
                    databaseBuilder(context.applicationContext, ArticleDataBase::class.java,"article_db.db").build()
                }

            }
            return INSTANCE!!
        }
    }

}