package com.example.newsappmvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.R
import com.example.newsappmvvm.databinding.FragmentBreakingNewsBinding
import com.example.newsappmvvm.ui.NewsActivity
import com.example.newsappmvvm.ui.NewsViewModel
import com.example.newsappmvvm.ui.adapters.NewsAdapter
import com.example.newsappmvvm.ui.models.NewsResponse
import com.example.newsappmvvm.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsappmvvm.ui.util.NetworkResult
import com.example.newsappmvvm.ui.util.handleApiError


class BreakingNewsFragment: Fragment(){
    private val binding: FragmentBreakingNewsBinding by viewBinding()
    private lateinit var viewModel: NewsViewModel
    private var newsAdapter= NewsAdapter()

    val TAG = "BreakingNewsFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_breaking_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObserver()
    }

    private fun setupUI() {
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerview()
    }

    private fun setupObserver() {
        viewModel.breakingNews.observe(viewLifecycleOwner) {
            hideProgressBar()
            when (it) {
                is NetworkResult.Success -> renderList(it.data)
                is NetworkResult.Loading -> showProgressBar()
                is NetworkResult.Failure -> handleApiError(it){ breakingNews() }
            }
        }
    }

    private fun renderList(response: NewsResponse) {
        newsAdapter.differ.submitList(response.articles.toList())
        val totalPages = response.totalResults / QUERY_PAGE_SIZE + 2
        isLastPage= viewModel.breakingNewsPage == totalPages
        if (isLastPage){
            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
        }
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                breakingNews()
                isScrolling = false
            }

        }
    }

    private fun breakingNews(){
        viewModel.getBreakingNews("us")
    }


    private fun setupRecyclerview(){
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
        newsAdapter.setOnItemClickListener {
            val directions = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article = it)
            findNavController().navigate(directions)
        }
    }
}