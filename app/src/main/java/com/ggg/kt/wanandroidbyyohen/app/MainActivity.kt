package com.ggg.kt.wanandroidbyyohen.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.extension.applyStatusBarPadding
import com.ggg.kt.wanandroidbyyohen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applyStatusBarPadding()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        setupBottomNavVisibility(navController)
    }

    private fun setupBottomNavVisibility(navController: NavController) {
        val topLevelDestinations = setOf(
            R.id.home_fragment,
            R.id.square_fragment,
            R.id.project_fragment,
            R.id.navigation_fragment,
            R.id.mine_fragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = if(destination.id in topLevelDestinations) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}