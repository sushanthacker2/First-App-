package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CreationItem
import com.example.data.CreationRepository
import com.example.data.GeminiService
import com.example.data.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StudioScreen {
    DASHBOARD,
    VISUAL,
    SCRIPTS,
    AUDIO,
    LIBRARY
}

class CreatorStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CreationRepository(database.creationDao())
    private val geminiService = GeminiService()
    val ttsManager = TtsManager(application)

    private val _currentScreen = MutableStateFlow(StudioScreen.DASHBOARD)
    val currentScreen: StateFlow<StudioScreen> = _currentScreen.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatusMessage = MutableStateFlow("")
    val generationStatusMessage: StateFlow<String> = _generationStatusMessage.asStateFlow()

    // Local NPU Mode & Telemetry
    private val _isLocalNpuOnly = MutableStateFlow(true)
    val isLocalNpuOnly: StateFlow<Boolean> = _isLocalNpuOnly.asStateFlow()

    private val _selectedAiModel = MutableStateFlow("HP Local Copilot NPU")
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    private val _npuTops = MutableStateFlow(45)
    val npuTops: StateFlow<Int> = _npuTops.asStateFlow()

    private val _npuLatencyMs = MutableStateFlow(18)
    val npuLatencyMs: StateFlow<Int> = _npuLatencyMs.asStateFlow()

    val creations: StateFlow<List<CreationItem>> = combine(
        repository.allCreations,
        _selectedCategory,
        _searchQuery
    ) { list, cat, query ->
        list.filter { item ->
            val matchCategory = cat == "ALL" || item.category.equals(cat, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.prompt.contains(query, ignoreCase = true) ||
                item.content.contains(query, ignoreCase = true) ||
                item.tags.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.preloadSamplesIfNeeded()
        }
    }

    fun setScreen(screen: StudioScreen) {
        _currentScreen.value = screen
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleNpuMode() {
        _isLocalNpuOnly.value = !_isLocalNpuOnly.value
        _selectedAiModel.value = if (_isLocalNpuOnly.value) "HP Local Copilot NPU" else "Gemini 3.5 Cloud Hybrid"
    }

    fun setAiModel(model: String) {
        _selectedAiModel.value = model
    }

    fun toggleFavorite(item: CreationItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, item.isFavorite)
        }
    }

    fun deleteCreation(item: CreationItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun generateAndSave(
        title: String,
        category: String,
        prompt: String,
        style: String,
        aspectRatio: String = "16:9",
        drawableName: String = "",
        onComplete: (CreationItem) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = if (_isLocalNpuOnly.value) {
                "Accelerating on HP NPU (45 TOPS)..."
            } else {
                "Processing with Copilot Hybrid Studio..."
            }

            val (generatedContent, wasLocal) = geminiService.generateCreativeContent(
                prompt = prompt,
                category = category,
                style = style,
                useLocalEngine = _isLocalNpuOnly.value
            )

            val itemTitle = if (title.isNotBlank()) title else prompt.take(30).trim() + "..."
            val tags = "$style,$category"

            val newItem = CreationItem(
                title = itemTitle,
                category = category,
                prompt = prompt,
                content = generatedContent,
                style = style,
                aspectRatio = aspectRatio,
                drawableName = drawableName,
                isFavorite = false,
                tags = tags
            )

            val newId = repository.insert(newItem)
            _isGenerating.value = false
            _generationStatusMessage.value = ""
            onComplete(newItem.copy(id = newId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
