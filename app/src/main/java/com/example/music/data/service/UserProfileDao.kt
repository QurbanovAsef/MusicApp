package com.example.music.data.service

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.music.data.model.response.UserProfile

@Dao
interface UserProfileDao {

    @Insert
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfile(id: Long): UserProfile?

    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): LiveData<List<UserProfile>>
}
