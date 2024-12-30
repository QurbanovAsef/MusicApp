//package com.example.music.presentation.auth.bottomMenu.home.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.music.data.model.response.RadioStationResponse
//import com.example.music.data.retrofit.RetrofitInstance
//import kotlinx.coroutines.launch
//
//class RadioViewModel : ViewModel() {
//    private val _radioStations = MutableLiveData<List<RadioStationResponse>>()
//    val radioStations: LiveData<List<RadioStationResponse>> get() = _radioStations
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> get() = _error
//
//    fun fetchRadioStations() {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getRadioStations()
//                if (response.isSuccessful) {
//                    _radioStations.value = response.body()
//                } else {
//                    _error.value = "API error: ${response.message()}"
//                }
//            } catch (e: Exception) {
//                _error.value = "Network error: ${e.message}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//}
