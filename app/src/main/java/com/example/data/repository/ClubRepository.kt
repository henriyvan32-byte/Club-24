package com.example.data.repository

import com.example.data.db.ClubDao
import com.example.data.model.Announcement
import com.example.data.model.GalleryPhoto
import com.example.data.model.Match
import com.example.data.model.Player
import kotlinx.coroutines.flow.Flow

class ClubRepository(private val clubDao: ClubDao) {

    val allPlayers: Flow<List<Player>> = clubDao.getAllPlayers()
    val allMatches: Flow<List<Match>> = clubDao.getAllMatches()
    val allAnnouncements: Flow<List<Announcement>> = clubDao.getAllAnnouncements()
    val allPhotos: Flow<List<GalleryPhoto>> = clubDao.getAllPhotos()

    // --- Player actions ---
    suspend fun insertPlayer(player: Player) {
        clubDao.insertPlayer(player)
    }

    suspend fun deletePlayer(id: Int) {
        clubDao.deletePlayer(id)
    }

    // --- Match actions ---
    suspend fun insertMatch(match: Match) {
        clubDao.insertMatch(match)
    }

    suspend fun deleteMatch(id: Int) {
        clubDao.deleteMatch(id)
    }

    // --- Announcement actions ---
    suspend fun insertAnnouncement(announcement: Announcement) {
        clubDao.insertAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(id: Int) {
        clubDao.deleteAnnouncement(id)
    }

    // --- Gallery actions ---
    suspend fun insertPhoto(photo: GalleryPhoto) {
        clubDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(id: Int) {
        clubDao.deletePhoto(id)
    }
}
