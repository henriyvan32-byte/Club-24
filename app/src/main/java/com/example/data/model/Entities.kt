package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val number: Int,
    val position: String, // e.g. "Gardien", "Défenseur", "Ailier", "Pivot"
    val goals: Int = 0,
    val matchesPlayed: Int = 0,
    val description: String? = null
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,       // e.g. "2026-07-20"
    val time: String,       // e.g. "18:00"
    val opponent: String,
    val location: String,
    val ourScore: Int? = null,
    val opponentScore: Int? = null,
    val isFinished: Boolean = false
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(), // timestamp
    val type: String // "Réunion", "Convocation", "Information"
)

@Entity(tableName = "gallery_photos")
data class GalleryPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val drawableName: String? = null, // for local assets like img_club_logo, img_hero_banner
    val localUri: String? = null,     // for custom added photos
    val isLocal: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
