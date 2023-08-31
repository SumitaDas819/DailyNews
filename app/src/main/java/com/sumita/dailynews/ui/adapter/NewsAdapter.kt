package com.sumita.dailynews.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumita.dailynews.databinding.ItemArticlePreviewBinding
import com.sumita.dailynews.interfaces.OnArticleClickListener
import com.sumita.dailynews.model.Article

class NewsAdapter(
    private val context: Context,
    private val onArticleClickListener: OnArticleClickListener
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // ViewHolder class
    inner class NewsViewHolder(val binding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    // Diff Callback
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    // onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
//        return NewsViewHolder(
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_article_preview, parent, false)
//        )

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArticlePreviewBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    // onBindViewHolder
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = differ.currentList[position]

        holder.binding.apply {
            Glide.with(context).load(currentArticle.urlToImage).into(ivArticleImage)
            tvSource.text = currentArticle.source?.name
            tvTitle.text = currentArticle.title
            tvDescription.text = currentArticle.description
            tvPublishedAt.text = currentArticle.publishedAt
            mainItemContainer.setOnClickListener {
                // TODO("Will be implemented later")
                onArticleClickListener.onClick(currentArticle)
            }
        }
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

     private var onItemClickListener: ((Article) -> Unit)? = null
     fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}