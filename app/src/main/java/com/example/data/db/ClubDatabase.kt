package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Announcement
import com.example.data.model.GalleryPhoto
import com.example.data.model.Match
import com.example.data.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface ClubDao {
    // --- Players ---
    @Query("SELECT * FROM players ORDER BY number ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayer(id: Int)

    // --- Matches ---
    @Query("SELECT * FROM matches ORDER BY date ASC")
    fun getAllMatches(): Flow<List<Match>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun deleteMatch(id: Int)

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Int)

    // --- Gallery ---
    @Query("SELECT * FROM gallery_photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<GalleryPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: GalleryPhoto)

    @Query("DELETE FROM gallery_photos WHERE id = :id")
    suspend fun deletePhoto(id: Int)
}

@Database(
    entities = [Player::class, Match::class, Announcement::class, GalleryPhoto::class],
    version = 1,
    exportSchema = false
)
abstract class ClubDatabase : RoomDatabase() {
    abstract fun clubDao(): ClubDao

    companion object {
        @Volatile
        private var INSTANCE: ClubDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClubDatabase::class.java,
                    "club24_database"
                )
                .addCallback(ClubDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ClubDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.clubDao())
                }
            }
        }

        suspend fun populateDatabase(clubDao: ClubDao) {
            // 1. Pre-populate Players (Joueurs)
            val initialPlayers = listOf(
                Player(name = "Henri Pio", number = 10, position = "Pivot", goals = 12, matchesPlayed = 8, description = "Capitaine légendaire et finisseur redoutable."),
                Player(name = "Marc Noah", number = 1, position = "Gardien", goals = 0, matchesPlayed = 10, description = "Mur infranchissable du Club 24, réflexes d'acier."),
                Player(name = "Jean Ondoua", number = 5, position = "Défenseur", goals = 2, matchesPlayed = 9, description = "Roc défensif et relanceur propre."),
                Player(name = "Samuel Eto'o Junior", number = 7, position = "Ailier", goals = 8, matchesPlayed = 10, description = "Vitesse explosive et dribbles déstabilisants."),
                Player(name = "Pierre Bella", number = 8, position = "Ailier", goals = 4, matchesPlayed = 8, description = "Technicien hors pair, meneur de jeu créatif."),
                Player(name = "Yves Bilo'o", number = 14, position = "Défenseur", goals = 1, matchesPlayed = 6, description = "Défenseur combatif et guerrier sur le terrain."),
                Player(name = "Luc Abessolo", number = 9, position = "Pivot", goals = 6, matchesPlayed = 7, description = "Pivot physique excelant dos au but.")
            )
            for (player in initialPlayers) {
                clubDao.insertPlayer(player)
            }

            // 2. Pre-populate Matches
            val initialMatches = listOf(
                Match(date = "2026-07-20", time = "18:00", opponent = "Mbalmayo Futsal", location = "Gymnase d'Ebolowa", isFinished = false),
                Match(date = "2026-07-27", time = "19:30", opponent = "Yaoundé Express", location = "Stadium Municipal d'Ebolowa", isFinished = false),
                Match(date = "2026-07-10", time = "17:00", opponent = "Kribi Futsal", location = "Gymnase d'Ebolowa", ourScore = 4, opponentScore = 2, isFinished = true),
                Match(date = "2026-07-03", time = "18:00", opponent = "Sangmélima FC", location = "Terrain de Sangmélima", ourScore = 3, opponentScore = 3, isFinished = true)
            )
            for (match in initialMatches) {
                clubDao.insertMatch(match)
            }

            // 3. Pre-populate Announcements
            val initialAnnouncements = listOf(
                Announcement(
                    title = "Réunion Générale Extraordinaire",
                    content = "Tous les membres du Club 24 Futsal sont conviés à la réunion ce vendredi à 18h au siège du club. Présence obligatoire de tous les joueurs pour discuter du planning de la saison.",
                    type = "Réunion",
                    date = System.currentTimeMillis() - 1000 * 60 * 60 * 24 // 1 day ago
                ),
                Announcement(
                    title = "Convocation - Match contre Mbalmayo",
                    content = "Les joueurs convoqués pour le choc contre Mbalmayo Futsal le 20 Juillet sont priés de se présenter au Gymnase d'Ebolowa à 16h30 précises pour l'échauffement collectif.",
                    type = "Convocation",
                    date = System.currentTimeMillis() - 1000 * 60 * 60 * 4 // 4 hours ago
                ),
                Announcement(
                    title = "Entraînement Cardio & Tactique",
                    content = "La séance d'entraînement de ce mercredi se concentrera sur l'endurance et les transitions rapides d'attaque-défense. Amenez vos briques de cardio et votre concentration !",
                    type = "Information",
                    date = System.currentTimeMillis() - 1000 * 60 * 60 * 48 // 2 days ago
                )
            )
            for (announcement in initialAnnouncements) {
                clubDao.insertAnnouncement(announcement)
            }

            // 4. Pre-populate Gallery Photos
            val initialPhotos = listOf(
                GalleryPhoto(title = "Logo Officiel", description = "L'emblème officiel de notre club, symbole d'unité et de force.", drawableName = "img_club_logo"),
                GalleryPhoto(title = "Match de Préparation", description = "Nos joueurs en plein effort lors de la victoire 4-2 à domicile.", drawableName = "img_hero_banner")
            )
            for (photo in initialPhotos) {
                clubDao.insertPhoto(photo)
            }
        }
    }
}
