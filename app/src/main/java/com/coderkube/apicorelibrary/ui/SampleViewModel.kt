package com.coderkube.apicorelibrary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderkube.apicorelibrary.data.SampleRepository
import com.coderkube.apicorelibrary.data.User
import com.example.apicore.init.ApiCore
import com.example.apicore.state.UiState
import com.example.apicore.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val repository: SampleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.asStateFlow()

    val networkStatusFlow = ApiCore.getNetworkStatusFlow()

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.getUsersWithRetry().toUiState()
        }
    }
}
