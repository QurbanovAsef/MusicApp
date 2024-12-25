package com.example.music.presentation.auth.bottomMenu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentSearchBinding
import com.example.music.presentation.adapter.SearchAdapter
import com.example.music.presentation.viewmodel.SharedViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchFunctionality()
        observeSearchResults()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { exactShow ->
            exactShow.tracks?.let { trackStrings ->
                val trackResponses = viewModel.mapStringsToTrackResponses(trackStrings)
                val activeTrack = trackResponses.firstOrNull()
                viewModel.setPlayerTracks(trackResponses, activeTrack)
                navigateToMusicFragment()
            }
        }

        binding.recyclerView.adapter = searchAdapter

        // LayoutManager təyini
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
    }


    private fun setupSearchFunctionality() {
        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            binding.progressBar.isVisible = false
            if (response.isEmpty()) {
                binding.emptyStateTextView.isVisible = true
                Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
            } else {
                binding.emptyStateTextView.isVisible = false
                searchAdapter.setItems(response) // ExactShow obyektlərini adapterə göndəririk
            }
        }
    }

    private fun searchSongs(query: String) {
        binding.progressBar.isVisible = true
        binding.emptyStateTextView.isVisible = false
        viewModel.searchSongs(query) // ViewModel-ə axtarış sorğusunu göndəririk
    }

    private fun navigateToMusicFragment() {
        findNavController().navigate(R.id.musicFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
