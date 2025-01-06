package com.example.music.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
                R.id.favoriteFragment,
                R.id.profileFragment -> true

                else -> false
            }
        }

        // SharedPreferences ilə istifadəçi girişini yoxlamaq
        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph).apply {
                setStartDestination(R.id.nav_home)
            }
            navController.graph = navGraph
        }

    }

    fun logout() {
        // SharedPreferences-u təmizləmək
        sharedPreferences.edit().clear().apply()

        // Login fragment-ə keçmək
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        navController.navigate(R.id.loginFragment)
    }

    private fun loadThemePreference() {
        val theme = sharedPreferences.getString("theme", "light") ?: "light"
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (theme == "dark") AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        // Yalnız tələb olunduqda mod dəyişdiririk
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

        // Yalnız lazım olduqda dil dəyişdiririk
        if (currentLocale != newLocale) {
            val config = resources.configuration
            config.setLocale(newLocale)
            createConfigurationContext(config)  // Yenilənmiş lokal tənzimləmələr
            recreate()  // Tətbiqi yenidən başladırıq
        }
    }

}
