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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.databinding.FragmentBreakingNewsBinding
import com.sumita.dailynews.db.ArticleDataBase
import com.sumita.dailynews.interfaces.OnArticleClickListener
import com.sumita.dailynews.model.Article
import com.sumita.dailynews.ui.adapter.NewsAdapter
import com.sumita.dailynews.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.sumita.dailynews.utils.Resource
import com.sumita.dailynews.viewmodels.NewsRepository
import com.sumita.dailynews.viewmodels.NewsViewModel
import com.sumita.dailynews.viewmodels.NewsViewModelProviderFactory

class BreakingNewsFragments : Fragment(), OnArticleClickListener {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get(): FragmentBreakingNewsBinding = _binding!!

    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(requireContext(), this) }
    private lateinit var newsViewModel: NewsViewModel
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onAvailable(network: Network) {
            // Internet connection is available, trigger the network call
            newsViewModel.getBreakingNews("in")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        val articleDao = ArticleDataBase.createDatabase(requireContext()).getArticleDao()
        val newsRepository = NewsRepository(articleDao)
        val connectivityUtils = ConnectivityUtils(requireContext())

        val newsViewModelProviderFactory = NewsViewModelProviderFactory(connectivityUtils,newsRepository)
        newsViewModel =
            ViewModelProvider(this, newsViewModelProviderFactory)[NewsViewModel::class.java]
        newsViewModel.breakingNews.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()
                    resource.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
//                        val totalPage=it.totalResults / QUERY_PAGE_SIZE +2
//                        isLastPage=newsViewModel.breakingNewsPage==totalPage

                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    resource.message?.let {
                        Log.d("Tag", "Error is-- $it")
                        if (it=="No internet connection"){
                            Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)


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
//    private val onScrollListner=object : RecyclerView.OnScrollListener(){
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                isScrolling=true
//            }
//        }
//
//        @RequiresApi(Build.VERSION_CODES.M)
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//            val visibleItemCount = layoutManager.childCount
//            val totalItemCount = layoutManager.itemCount
//
//            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
//            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
//            val isNotAtBeginning = firstVisibleItemPosition >= 0
//            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
//            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
//                    isTotalMoreThanVisible && isScrolling
//            Log.d("itemtag","shouldPaginate - $shouldPaginate")
//            if(shouldPaginate) {
//                newsViewModel.getBreakingNews("in")
//                isScrolling = false
//            }
//
//        }
//    }

    private fun setupRecyclerView() {
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
           // addOnScrollListener(this@BreakingNewsFragments.onScrollListner)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onClick(article: Article) {
        Toast.makeText(requireContext(), article.title, Toast.LENGTH_SHORT).show()
        val directions = BreakingNewsFragmentsDirections.actionBreakingNewsFragments3ToArticleFragments(article)
        findNavController().navigate(directions)
    }
}