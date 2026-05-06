package com.ggg.kt.wanandroidbyyohen.app

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.extension.setSystemBarsLight
import com.ggg.kt.wanandroidbyyohen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val topLevelDestinations = setOf(
        R.id.home_fragment,
        R.id.square_fragment,
        R.id.project_fragment,
        R.id.navigation_fragment,
        R.id.mine_fragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemBarsLight(true)
        setupBottomNavInsets()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        setupBottomNavVisibility(navController)
        setupBackNavigation(navController)
    }

    private fun setupBottomNavInsets() {
        val initialBottomMargin =
            (binding.bottomNav.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val initialPaddingBottom = binding.bottomNav.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = initialBottomMargin + bottomInset
            }
            view.updatePadding(bottom = initialPaddingBottom)

            insets
        }
        ViewCompat.requestApplyInsets(binding.bottomNav)
    }

    private fun setupBottomNavVisibility(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = if (destination.id in topLevelDestinations) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setupBackNavigation(navController: NavController) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id in topLevelDestinations) {
                    finish()
                    return
                }

                if (!navController.popBackStack()) {
                    finish()
                }
            }
        })
    }
}
