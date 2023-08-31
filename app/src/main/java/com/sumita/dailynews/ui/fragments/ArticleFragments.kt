package com.sumita.dailynews.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.databinding.FragmentArticleBinding
import com.sumita.dailynews.db.ArticleDataBase
import com.sumita.dailynews.viewmodels.NewsRepository
import com.sumita.dailynews.viewmodels.NewsViewModel
import com.sumita.dailynews.viewmodels.NewsViewModelProviderFactory

class ArticleFragments : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get(): FragmentArticleBinding = _binding!!
    private lateinit var newsViewModel: NewsViewModel

    private val args by navArgs<ArticleFragmentsArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val articleTitle = args.Article.title
        Log.i("ARTICLE DATA", "ARTICLE DATA: $articleTitle")
        Log.d("ARTICLE URL", "ARTICLE URL IS -- : ${args.Article.url}")


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleDao = ArticleDataBase.createDatabase(requireContext()).getArticleDao()
        val newsRepository = NewsRepository(articleDao)
        val connectivityUtils=ConnectivityUtils(requireContext())
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(connectivityUtils,newsRepository)
        newsViewModel =
            ViewModelProvider(this, newsViewModelProviderFactory)[NewsViewModel::class.java]


        val webView = binding.webView
        webView.settings.javaScriptEnabled=true
        val articleUrl = args.Article.url
        if (!articleUrl.isNullOrEmpty()) {
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.articleInProgress.visibility=View.GONE
                    webView.visibility=View.VISIBLE
                    binding.fab.visibility=View.VISIBLE
                }
            }
            webView.loadUrl(articleUrl)
        } else Toast.makeText(requireContext(), "url is not valid", Toast.LENGTH_LONG).show()


        binding.fab.setOnClickListener {
//            if (connectivityUtils.isConnected()){
                newsViewModel.saveArticle(args.Article)
                Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_LONG).show()
//            } else Toast.makeText(requireContext(),"Can't save - no internet connection",Toast.LENGTH_SHORT).show()

        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}