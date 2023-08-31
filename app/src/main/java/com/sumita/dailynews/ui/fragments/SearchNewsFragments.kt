package com.sumita.dailynews.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.databinding.FragmentSearchNewsBinding
import com.sumita.dailynews.db.ArticleDataBase
import com.sumita.dailynews.interfaces.OnArticleClickListener
import com.sumita.dailynews.model.Article
import com.sumita.dailynews.ui.adapter.NewsAdapter
import com.sumita.dailynews.utils.Constants
import com.sumita.dailynews.utils.Resource
import com.sumita.dailynews.viewmodels.NewsRepository
import com.sumita.dailynews.viewmodels.NewsViewModel
import com.sumita.dailynews.viewmodels.NewsViewModelProviderFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Create Fragments and Activities using the toolbar itself and not manually to avoid errors in the
// Android Studio
class SearchNewsFragments : Fragment(), OnArticleClickListener {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get(): FragmentSearchNewsBinding = _binding!!
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(requireContext(),this) }
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if(editable.toString().isNotEmpty()) {
                        newsViewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }

        val articleDao = ArticleDataBase.createDatabase(requireContext()).getArticleDao()
        val newsRepository = NewsRepository(articleDao)
        val connectivityUtils = ConnectivityUtils(requireContext())
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(connectivityUtils,newsRepository)
        newsViewModel =
            ViewModelProvider(this, newsViewModelProviderFactory)[NewsViewModel::class.java]

        newsViewModel.searchNews.observe(viewLifecycleOwner) { resource ->
            when (resource) {

                is Resource.Success -> {
                    hideProgressBar()
                    resource.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                       // val totalPage=it.totalResults / Constants.QUERY_PAGE_SIZE +2
                      //  isLastPage=newsViewModel.searchNewsPage==totalPage
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    resource.message?.let {
                        Log.d("Tag", "Error $it")
                        if (it=="No internet connection")
                            Toast.makeText(requireContext(),"No connection",Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

    }
    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
       // isLoading=false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
       // isLoading=true
    }

//    var isLoading=false
//    var isLastPage=false
//    var isScrolling=false
//
//    val onScrollListner=object : RecyclerView.OnScrollListener(){
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                isScrolling=true
//            }
//        }
//
//        @RequiresApi(Build.VERSION_CODES.M)
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val layoutmanage=recyclerView.layoutManager as LinearLayoutManager
//            val firstVisibleItemPosition=layoutmanage.findFirstVisibleItemPosition()
//            val visibleItemCount=layoutmanage.childCount
//            val totalItemCount=layoutmanage.itemCount
//
//            val isNotLoadingAndNotLastPage=!isLoading && !isLastPage
//            val isLastItem=firstVisibleItemPosition + visibleItemCount >= totalItemCount
//            val isNotAtBegninng=firstVisibleItemPosition >= 0
//            val isTotalMoreThenVisible=totalItemCount>= Constants.QUERY_PAGE_SIZE
//            val shouldPaginate=isNotLoadingAndNotLastPage && isLastItem && isNotAtBegninng &&
//                    isTotalMoreThenVisible && isScrolling
//
//            if (shouldPaginate){
//                newsViewModel.getSearchNews(binding.etSearch.text.toString())
//                isScrolling=false
//            }
//
//        }
//    }
    private fun setupRecyclerView() {
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            //addOnScrollListener(this@SearchNewsFragments.onScrollListner)
        }
    }
    override fun onClick(article: Article) {
        Toast.makeText(requireContext(), article.title, Toast.LENGTH_SHORT).show()
        val directions = SearchNewsFragmentsDirections.actionSearchNewsFragmentsToArticleFragments(article)
        findNavController().navigate(directions)
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}