package com.example.music.presentation.auth.bottomMenu.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.util.Locale

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
    private val REQUEST_CODE_SPEECH_INPUT = 100

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
        setupVoiceSearch()
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
            searchSongs(query) // Boş olsa da işləməlidir
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            searchSongs(query) // Boş olsa da işləməlidir
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

    private fun setupVoiceSearch() {
        binding.voiceSearchIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_CODE_SPEECH_INPUT
                )
            } else {
                startVoiceRecognition()
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_now))

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.let {
                val spokenText = it[0]
                binding.searchEditText.setText(spokenText)
                searchSongs(spokenText)
            }
        }
    }

    private fun searchSongs(query: String) {
        if (query.isNotEmpty() && !searchHistory.contains(query)) {
            searchHistory.add(0, query)
            saveSearchHistory()
        }

        // Əvvəlki nəticələri təmizlə
        searchAdapter.setItems(emptyList())

        binding.recyclerView.isVisible = false
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
            binding.recyclerView.isVisible = response.isNotEmpty()
            binding.emptyStateTextView.isVisible = response.isEmpty()
            binding.emptyStateTextView.text = if (response.isEmpty()) getString(R.string.search_no_results) else ""
            searchAdapter.setItems(response)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.text.clear()
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
