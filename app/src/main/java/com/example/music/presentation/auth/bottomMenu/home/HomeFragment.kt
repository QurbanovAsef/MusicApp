package com.example.music.presentation.auth.bottomMenu.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidprojecttest1.databinding.FragmentHomeBinding
import com.example.music.presentation.auth.bottomMenu.home.fragmens.ArtistsFragment
import com.example.music.presentation.auth.bottomMenu.home.fragmens.NewsFragment
import com.example.music.presentation.auth.bottomMenu.home.fragmens.PodcastsFragment
import com.example.music.presentation.auth.bottomMenu.home.fragmens.VideoFragment
import com.example.music.presentation.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ViewPagerAdapter(this)
        adapter.addFragment(NewsFragment())
        adapter.addFragment(VideoFragment())
        adapter.addFragment(ArtistsFragment())
        adapter.addFragment(PodcastsFragment())
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Music"
                1 -> "Video"
                2 -> "Artists"
                3 -> "Podcasts"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

