package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.ClubDatabase
import com.example.data.model.Announcement
import com.example.data.model.GalleryPhoto
import com.example.data.model.Match
import com.example.data.model.Player
import com.example.data.repository.ClubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ClubTab {
    ACCUEIL,
    JOUEURS,
    MATCHS,
    ANNONCES,
    GALERIE,
    CONTACT
}

class ClubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClubRepository

    init {
        val database = ClubDatabase.getDatabase(application, viewModelScope)
        repository = ClubRepository(database.clubDao())
    }

    // --- Active Tab State ---
    private val _activeTab = MutableStateFlow(ClubTab.ACCUEIL)
    val activeTab: StateFlow<ClubTab> = _activeTab.asStateFlow()

    fun selectTab(tab: ClubTab) {
        _activeTab.value = tab
    }

    // --- Streams from Repository ---
    val players: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val matches: StateFlow<List<Match>> = repository.allMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val announcements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val photos: StateFlow<List<GalleryPhoto>> = repository.allPhotos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Database Modification Methods ---

    // Players
    fun addPlayer(name: String, number: Int, position: String, description: String? = null, goals: Int = 0, matchesPlayed: Int = 0) {
        viewModelScope.launch {
            repository.insertPlayer(
                Player(
                    name = name,
                    number = number,
                    position = position,
                    description = description,
                    goals = goals,
                    matchesPlayed = matchesPlayed
                )
            )
        }
    }

    fun deletePlayer(id: Int) {
        viewModelScope.launch {
            repository.deletePlayer(id)
        }
    }

    // Matches
    fun addMatch(date: String, time: String, opponent: String, location: String, ourScore: Int? = null, opponentScore: Int? = null, isFinished: Boolean = false) {
        viewModelScope.launch {
            repository.insertMatch(
                Match(
                    date = date,
                    time = time,
                    opponent = opponent,
                    location = location,
                    ourScore = ourScore,
                    opponentScore = opponentScore,
                    isFinished = isFinished
                )
            )
        }
    }

    fun deleteMatch(id: Int) {
        viewModelScope.launch {
            repository.deleteMatch(id)
        }
    }

    // Announcements
    fun addAnnouncement(title: String, content: String, type: String) {
        viewModelScope.launch {
            repository.insertAnnouncement(
                Announcement(
                    title = title,
                    content = content,
                    type = type,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteAnnouncement(id: Int) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
        }
    }

    // Gallery Photos
    fun addPhoto(title: String, description: String, localUri: String? = null, drawableName: String? = null) {
        viewModelScope.launch {
            repository.insertPhoto(
                GalleryPhoto(
                    title = title,
                    description = description,
                    localUri = localUri,
                    drawableName = drawableName,
                    isLocal = localUri != null,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deletePhoto(id: Int) {
        viewModelScope.launch {
            repository.deletePhoto(id)
        }
    }
}

class ClubViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClubViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClubViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
