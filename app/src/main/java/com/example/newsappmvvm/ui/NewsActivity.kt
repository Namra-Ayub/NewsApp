package com.example.newsappmvvm.ui

import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsappmvvm.databinding.ActivityNewsBinding
import com.example.newsappmvvm.ui.db.ArticleDatabase
import com.example.newsappmvvm.ui.repository.NewsRepository


class NewsActivity : AppCompatActivity() {

    private val binding: ActivityNewsBinding by viewBinding()
    lateinit var viewModel: NewsViewModel
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            navController = fragmentContainerView.getFragment<NavHostFragment>().navController
            bottomNavigationView.setupWithNavController(navController)
        }


        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

    }
}
