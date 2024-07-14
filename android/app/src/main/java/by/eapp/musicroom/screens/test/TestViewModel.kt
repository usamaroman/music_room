package by.eapp.musicroom.screens.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.eapp.musicroom.domain.repo.StatusRepository
import by.eapp.musicroom.network.RegistrationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val statusRepository: StatusRepository
): ViewModel() {

    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state.asStateFlow()

    init {
         getStatus()
    }
    private fun getStatus() {
        viewModelScope.launch() {
        _state.value = statusRepository.getStatus().toString() }
    }
}