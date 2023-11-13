package com.example.newsappmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsappmvvm.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.newsappmvvm.databinding.ActivityNewsBinding
import com.example.newsappmvvm.ui.db.ArticleDatabase
import com.example.newsappmvvm.ui.repository.NewsRepository
import android.viewbinding.library.activity.viewBinding


class NewsActivity : AppCompatActivity() {

    private val binding: ActivityNewsBinding by viewBinding()
    lateinit var viewModel: NewsViewModel
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.newsNav.getFragment<NavHostFragment>().navController

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)




//        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
//        val newsNavHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}
