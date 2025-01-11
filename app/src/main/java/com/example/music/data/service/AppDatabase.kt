package com.example.music.data.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.UserProfile

@Database(entities = [FavoriteTrack::class, UserProfile::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun userProfileDao(): UserProfileDao // Yeni DAO əlavə edin

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database"
                ).addMigrations(MIGRATION_3_4)  // Migration-u əlavə edin
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
