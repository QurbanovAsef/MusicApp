package com.example.music.presentation.auth.bottomMenu.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentSearchBinding
import com.example.music.presentation.adapter.SearchAdapter
import com.example.music.presentation.auth.bottomMenu.favorite.FavoriteTrackViewModel
import com.example.music.presentation.viewmodel.SharedViewModel
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private val favoriteTrackViewModel: FavoriteTrackViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter
    private val searchHistory = mutableListOf<String>()
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchFunctionality()
        loadSearchHistory()
        observeSearchResults()
        binding.emptyStateTextView.isVisible = true
        binding.emptyStateTextView.text = getString(R.string.search_suggestion)
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(
            onItemClick = { selectedTrack ->
                val bundle = Bundle().apply {
                    putParcelable("track", selectedTrack)
                }
                findNavController().navigate(R.id.action_nav_search_to_musicFragment, bundle)
            },
            onLikeDislike = { track -> favoriteTrackViewModel.toggleFavorite(track) }
        )
        binding.recyclerView.adapter = searchAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSearchFunctionality() {
        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            }
            true
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().isEmpty()) {
                    binding.recyclerView.isVisible = true
                    searchAdapter.setItems(viewModel.searchResults.value ?: emptyList())
                }
            }
        })
    }

    private fun searchSongs(query: String) {
        if (!searchHistory.contains(query)) {
            searchHistory.add(0, query)
            saveSearchHistory()
        }

        binding.progressBar.isVisible = true
        binding.emptyStateTextView.isVisible = false
        viewModel.searchSongs(query)
    }

    private fun saveSearchHistory() {
        sharedPreferences.edit()
            .putStringSet("history", searchHistory.toSet())
            .apply()
    }

    private fun loadSearchHistory() {
        val savedHistory = sharedPreferences.getStringSet("history", emptySet())?.toMutableList()
        savedHistory?.let {
            searchHistory.clear()
            searchHistory.addAll(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            binding.progressBar.isVisible = false

            if (binding.searchEditText.text.isEmpty()) {
                // Heç bir şey yazılmayıbsa, "Type something to search" göstər
                binding.recyclerView.isVisible = false
                binding.emptyStateTextView.isVisible = true
                binding.emptyStateTextView.text = getString(R.string.search_suggestion)
            } else if (response.isEmpty()) {
                // Axtarış var, amma nəticə tapılmayıbsa "No results found" göstər
                binding.recyclerView.isVisible = false
                binding.emptyStateTextView.isVisible = true
                binding.emptyStateTextView.text = getString(R.string.search_no_results)
            } else {
                // Axtarış nəticələri varsa, onları göstər
                binding.recyclerView.isVisible = true
                binding.emptyStateTextView.isVisible = false
                searchAdapter.setItems(response)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.text.clear() // Input-u sıfırla
        if (searchHistory.isNotEmpty()) {
            binding.recyclerView.isVisible = true
            searchAdapter.setItems(viewModel.searchResults.value ?: emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
