package com.example.music.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ActivityContainer2Binding
import com.example.music.presentation.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContainer2Binding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Layout-u binding vasitəsilə qururuq
        binding = ActivityContainer2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // SharedPreferences-dən tema seçimlərini yüklə
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        loadThemePreference()

        // NavHostFragment və NavController ilə işləyirik
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // BottomNavigationView NavController-ə bağlanır
        binding.bottomNavigationView.setupWithNavController(navController)

        // BottomNavigationView görünüşünü idarə edirik
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = when (destination.id) {
                R.id.nav_home,
                R.id.nav_search,
                R.id.nav_heart,
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    fun logout() {
        // SharedPreferences məlumatlarını təmizləyirik
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // LoginFragment-ə keçid
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        navController.navigate(R.id.loginFragment)
    }

    // Tema seçimlərini SharedPreferences-dən yükləmək
    private fun loadThemePreference() {
        val theme = sharedPreferences.getString("theme", "light")
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
