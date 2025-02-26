package com.example.music.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ActivityContainer2Binding
import com.example.music.presentation.viewmodel.SharedViewModel
import com.example.music.utils.AppConst.LANG_KEY_DEFAULT
import com.example.music.utils.AppConst.LANG_KEY_LANGUAGE
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES
import com.example.music.utils.LocaleUtil
import dagger.hilt.android.AndroidEntryPoint

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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = when (destination.id) {
                R.id.nav_home,
                R.id.nav_search,
                R.id.favoriteFragment,
                R.id.profileFragment -> true

                else -> false
            }
        }

        val sharedPreferences =
            getSharedPreferences(SHARED_KEY_PREFERENCES, android.content.Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph).apply {
                setStartDestination(R.id.nav_home)
            }
            navController.graph = navGraph
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences =
            newBase.getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)
        val lang =
            sharedPreferences.getString(LANG_KEY_LANGUAGE, LANG_KEY_DEFAULT) ?: LANG_KEY_DEFAULT
        applyOverrideConfiguration(LocaleUtil.getLocalizedConfiguration(lang))
        super.attachBaseContext(newBase)
    }

}
