package com.example.newsappmvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.viewbinding.library.fragment.viewBinding
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.R
import com.example.newsappmvvm.databinding.FragmentSearchNewsBinding
import com.example.newsappmvvm.ui.NewsActivity
import com.example.newsappmvvm.ui.NewsViewModel
import com.example.newsappmvvm.ui.adapters.NewsAdapter
import com.example.newsappmvvm.ui.models.NewsResponse
import com.example.newsappmvvm.ui.util.Constants
import com.example.newsappmvvm.ui.util.NetworkResult
import com.example.newsappmvvm.ui.util.handleApiError

class SearchNewsFragment:Fragment() {

    private val binding: FragmentSearchNewsBinding by viewBinding()
    private lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter : NewsAdapter
    val TAG = "SearchNewsFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObserver()
    }

    private fun setupUI() {
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerview()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }


        binding.apply {
            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Do something when the "Done" button is clicked on the keyboard
                    searchNews()
                    true
                } else false
            }
        }
    }

    private fun setupObserver() {
        viewModel.searchNews.observe(viewLifecycleOwner) {
            hideProgressBar()
            when (it) {
                is NetworkResult.Success -> renderList(it.data)
                is NetworkResult.Loading -> showProgressBar()
                is NetworkResult.Failure -> handleApiError(it){ searchNews() }
            }
        }
    }

    private fun renderList(response: NewsResponse) {
        newsAdapter.differ.submitList(response.articles.toList())
        val totalPages = response.totalResults / Constants.QUERY_PAGE_SIZE + 2
        isLastPage= viewModel.breakingNewsPage == totalPages
        if (isLastPage){
            binding.rvSearchNews.setPadding(0, 0, 0, 0)
        }
    }

    private fun searchNews(){
        viewModel.refreshBreakingNewsList(binding.etSearch.text.toString().trim())
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            }

        }
    }





    private fun setupRecyclerview(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}