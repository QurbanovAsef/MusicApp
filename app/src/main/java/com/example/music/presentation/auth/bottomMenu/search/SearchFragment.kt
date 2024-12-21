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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query) // Axtarışı işə salırıq
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { songs ->
            binding.progressBar.isVisible = false

            if (songs.isEmpty()) {
                Toast.makeText(requireContext(), "Songs not found", Toast.LENGTH_SHORT).show()
                binding.emptyStateTextView.visibility = View.VISIBLE
            } else {
                binding.emptyStateTextView.visibility = View.GONE
                searchAdapter.setItems(songs)
            }
        }
    }

    private fun searchSongs(query: String) {
        binding.progressBar.isVisible = true
        viewModel.searchSongs(query) // ViewModel vasitəsilə axtarış sorğusunu göndəririk
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { show ->
            show.tracks?.let {
                viewModel.setPlayerTracks(show.tracks)
                findNavController().navigate(R.id.musicFragment)
            }
        }
        binding.recyclerView.adapter = searchAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
