package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.music.data.model.response.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    _userProfile.value = profile
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseFirestore", "Profil məlumatları yüklənmədi", e)
            }
    }

    fun updateUserProfile(firstName: String, lastName: String, imageUri: Uri?, callback: (Boolean, String) -> Unit) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName
        )

        firestore.collection("users").document(userId).set(userData, SetOptions.merge())
            .addOnSuccessListener {
                if (imageUri != null) {
                    uploadProfileImage(userId, imageUri, callback)
                } else {
                    callback(true, "Profil məlumatları uğurla yeniləndi")
                }
            }
            .addOnFailureListener { e ->
                callback(false, "Məlumatları yeniləmək mümkün olmadı")
                Log.e("FirebaseFirestore", "Məlumatları yeniləmək mümkün olmadı", e)
            }
    }

    fun uploadProfileImage(userId: String, imageUri: Uri, callback: (Boolean, String) -> Unit) {
        val imageRef = storage.child("profile_images/$userId.jpg")

        try {
            val inputStream: InputStream? =
                getApplication<Application>().applicationContext.contentResolver.openInputStream(imageUri)
            val fileSizeInBytes = inputStream?.available() ?: 0
            val fileSizeInMB = fileSizeInBytes / (1024 * 1024) // MB-a çevirmək
            inputStream?.close()

            if (fileSizeInMB > 5) {
                callback(false, "Şəkilin həcmi çox böyükdür")
                return
            }
        } catch (e: Exception) {
            callback(false, "Şəkilin ölçüsü yoxlanmadı")
            return
        }

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection("users").document(userId)
                        .update("imageUrl", uri.toString())
                        .addOnSuccessListener {
                            _userProfile.value = _userProfile.value?.copy(imageUrl = uri.toString())
                            callback(true, "Şəkil uğurla yükləndi")
                        }
                        .addOnFailureListener { e ->
                            callback(false, "Firestore-a imageUrl yazmaq mümkün olmadı")
                        }
                }
            }
            .addOnFailureListener {
                callback(false, "Şəkili yükləmək mümkün olmadı")
            }
    }
}
1`