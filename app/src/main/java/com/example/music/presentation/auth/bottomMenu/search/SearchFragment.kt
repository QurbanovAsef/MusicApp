package com.example.music.presentation.auth.bottomMenu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.databinding.FragmentSearchBinding
import com.example.music.presentation.adapter.SearchAdapter
import com.example.music.presentation.viewmodel.SharedViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        setupRecyclerView()

        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query) // Axtarışı işə salırıq
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { songs ->
            if (songs.isEmpty()) {
                binding.emptyStateTextView.visibility = View.VISIBLE
            } else {
                binding.emptyStateTextView.visibility = View.GONE
                searchAdapter.setItems(songs)
            }
        }
    }

    private fun searchSongs(query: String) {
        viewModel.searchSongs(query) // ViewModel vasitəsilə axtarış sorğusunu göndəririk
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { song ->
            viewModel.addFavorite(song)
        }
        binding.recyclerView.adapter = searchAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
