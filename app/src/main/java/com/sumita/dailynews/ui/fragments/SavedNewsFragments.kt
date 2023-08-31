package com.sumita.dailynews.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sumita.dailynews.ConnectivityUtils
import com.sumita.dailynews.databinding.FragmentSavedNewsBinding
import com.sumita.dailynews.db.ArticleDataBase
import com.sumita.dailynews.interfaces.OnArticleClickListener
import com.sumita.dailynews.model.Article
import com.sumita.dailynews.ui.adapter.NewsAdapter
import com.sumita.dailynews.viewmodels.NewsRepository
import com.sumita.dailynews.viewmodels.NewsViewModel
import com.sumita.dailynews.viewmodels.NewsViewModelProviderFactory

class SavedNewsFragments : Fragment(), OnArticleClickListener {

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get(): FragmentSavedNewsBinding = _binding!!
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(requireContext(), this) }
    private lateinit var newsViewModel: NewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)

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
        newsViewModel.getAllArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currentItemPosition=viewHolder.adapterPosition
                val articleToDelete=newsAdapter.differ.currentList[currentItemPosition]
                newsViewModel.deleteArticles(articleToDelete)
                Snackbar.make(view,"Successfully deleted article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.saveArticle(articleToDelete)
                    }
                    show()
                }

            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun setupRecyclerView() {
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    override fun onClick(article: Article) {
        Toast.makeText(requireContext(), article.title, Toast.LENGTH_SHORT).show()
        val directions = SavedNewsFragmentsDirections.actionSavedNewsFragments2ToArticleFragments(article)
        findNavController().navigate(directions)
    }

}