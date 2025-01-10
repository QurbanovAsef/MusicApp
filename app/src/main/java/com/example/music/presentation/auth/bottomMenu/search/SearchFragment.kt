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
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private val favoriteTrackViewModel: FavoriteTrackViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter
    private val searchHistory = mutableListOf<String>() // Search history storage
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
    }

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
        // "search" ikonasına klikləmə
        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            }
        }

        // Klaviaturadakı search ikonuna basıldığında da axtarış et
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            }
            true
        }

        // Klaviaturada hər dəyişiklikdə chip əlavə olunmasın
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                if (query.isEmpty()) {
                    binding.recyclerView.isVisible = false
                    binding.emptyStateTextView.isVisible = true
                    binding.emptyStateTextView.text = "Enter text to search"
                }
            }
        })
    }

    private fun searchSongs(query: String) {
        binding.progressBar.isVisible = true
        binding.emptyStateTextView.isVisible = false
        viewModel.searchSongs(query)
        addSearchChip(query)  // Yeni axtarış sözü əlavə et
    }

    private fun addSearchChip(query: String) {
        if (!searchHistory.contains(query)) {
            searchHistory.add(query)
            saveSearchHistory() // Yeni axtarışı saxla
        }
        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                removeSearchChip(query)
            }
            setOnClickListener {
                binding.searchEditText.setText(query) // Çipi seçərək təkrar axtarış
            }
        }
        binding.chipGroup.addView(chip)
    }

    private fun removeSearchChip(query: String) {
        searchHistory.remove(query)
        saveSearchHistory() // Yenilənmiş siyahını saxla
        binding.chipGroup.removeAllViews()
        for (item in searchHistory) {
            addSearchChip(item) // Qalan çipləri yenidən əlavə et
        }
    // SharedPreferences-də yadda saxla
        saveSearchHistory()
    }
    // Search tarixini SharedPreferences-ə saxlamaq
    private fun saveSearchHistory() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("history", searchHistory.toSet()) // Unikal məlumatlar saxla
        editor.apply()
    }



    // Search tarixini SharedPreferences-dən yükləmək
    private fun loadSearchHistory() {
        val savedHistory = sharedPreferences.getStringSet("history", emptySet())?.toMutableList()
        savedHistory?.let {
            searchHistory.clear()
            searchHistory.addAll(it)
            binding.chipGroup.removeAllViews() // Əvvəlki chip-ləri təmizlə
            for (query in searchHistory) {
                addSearchChip(query) // Hər bir query üçün chip əlavə et
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            binding.progressBar.isVisible = false
            if (response.isEmpty()) {
                binding.recyclerView.isVisible = false
                binding.emptyStateTextView.isVisible = true
                binding.emptyStateTextView.text = "No results found"
            } else {
                binding.recyclerView.isVisible = true
                binding.emptyStateTextView.isVisible = false
                searchAdapter.setItems(response)
            }
        }
    }
    override fun onPause() {
        super.onPause()
        saveSearchHistory() // Axtarışları saxla
    }

    override fun onResume() {
        super.onResume()
        loadSearchHistory() // Axtarış məlumatlarını yüklə
        binding.searchEditText.text.clear() // Input-u sıfırla
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
