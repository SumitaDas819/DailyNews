package com.sumita.dailynews.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.PrimaryKey

@Parcelize
data class ArticleSample(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
): Parcelable
