package com.igcaptiongenerator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igcaptiongenerator.data.model.CaptionResult
import com.igcaptiongenerator.data.repository.CaptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class CaptionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val result: CaptionResult? = null,
    val selectedTone: String = "funny",
    val selectedLanguage: String = "en",
    val hashtagCount: Int = 15
)

@HiltViewModel
class CaptionViewModel @Inject constructor(
    private val repository: CaptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptionUiState())
    val uiState: StateFlow<CaptionUiState> = _uiState

    val recentResults = repository.getRecentResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTone(tone: String) { _uiState.value = _uiState.value.copy(selectedTone = tone) }
    fun setLanguage(lang: String) { _uiState.value = _uiState.value.copy(selectedLanguage = lang) }
    fun setHashtagCount(count: Int) { _uiState.value = _uiState.value.copy(hashtagCount = count) }

    fun generate(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, result = null)
            try {
                val result = repository.generate(
                    imageFile = imageFile,
                    tone = _uiState.value.selectedTone,
                    language = _uiState.value.selectedLanguage,
                    hashtagCount = _uiState.value.hashtagCount
                )
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }
}
