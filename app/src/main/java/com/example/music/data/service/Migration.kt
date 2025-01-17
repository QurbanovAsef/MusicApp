package com.example.music.data.service

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Yeni cədvəl yaradılır
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_profiles` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `username` TEXT NOT NULL,
                `imageUri` TEXT
            )
            """
        )

        // İlkin məlumatlar əlavə edilir
        database.execSQL(
            """
            INSERT INTO `user_profiles` (`username`, `imageUri`) 
            VALUES ('default_user', 'default_image_uri')
            """
        )
    }
}
