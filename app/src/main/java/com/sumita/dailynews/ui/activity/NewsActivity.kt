package com.sumita.dailynews.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.databinding.ActivityNewsBinding
import com.sumita.dailynews.db.ArticleDataBase
import com.sumita.dailynews.viewmodels.NewsRepository
import com.sumita.dailynews.viewmodels.NewsViewModel
import com.sumita.dailynews.viewmodels.NewsViewModelProviderFactory

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleDao= ArticleDataBase.createDatabase(applicationContext).getArticleDao()
        val newsRepository = NewsRepository(articleDao)

        // ViewModel instantiation and declaration along with NewsViewModelProviderFactory
        val connectivityUtils=ConnectivityUtils(this)
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(connectivityUtils,newsRepository)
        viewModel = ViewModelProvider(this, newsViewModelProviderFactory)[NewsViewModel::class.java]

        // Missed these two lines
        // 1. Declare and find NavHostFragment
        // 2. Find the navController using the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment
        val navController = navHostFragment.navController

        // Also, use the navController directly instead of finding it from anywhere else
        binding.bottomNavigationView.setupWithNavController(navController)

    }
}