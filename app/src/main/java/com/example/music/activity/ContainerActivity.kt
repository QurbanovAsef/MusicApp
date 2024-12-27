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
import java.util.Locale

@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContainer2Binding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContainer2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // SharedPreferences-dən tema və dil seçimlərini yüklə
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        loadThemePreference()
        loadLanguagePreference()

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
                R.id.musicFragment,
                R.id.favoriteFragment,
                R.id.profileFragment -> true
                else -> false
            }
        }
    }

    fun logout() {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        navController.navigate(R.id.loginFragment)
    }

    private fun loadThemePreference() {
        val theme = sharedPreferences.getString("theme", "light") ?: "light"
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (theme == "dark") AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (currentMode != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    private fun loadLanguagePreference() {
        val language = sharedPreferences.getString("language", "English") ?: "English"
        val currentLocale = resources.configuration.locales[0]
        val newLocale = when (language) {
            "English" -> Locale("en")
            "Azərbaycan" -> Locale("az")
            else -> Locale("en")
        }

        if (currentLocale != newLocale) {
            val config = resources.configuration
            config.setLocale(newLocale)
            createConfigurationContext(config)
            recreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
