package com.nightowlcrew.nudgie.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserSettingsRepository(
    private val prefs: SharedPreferences,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)

    fun edit(action: SharedPreferences.Editor.() -> Unit) {
        prefs.edit(action = action)
    }

    // Example: Saving the theme
    suspend fun saveAppTheme(themeName: String) {
        // 1. Always save locally for immediate UI updates
        prefs.edit { putString("app_theme", themeName) }
        
        // 2. If the user is authenticated (not anonymous), sync to Firestore
        val user = auth.currentUser
        if (user != null && !user.isAnonymous) {
            try {
                val data = mapOf("app_theme" to themeName)
                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                android.util.Log.e("FIRESTORE_SYNC", "Failed to sync theme", e)
            }
        }
    }

    suspend fun savePetName(petName: String) {
        prefs.edit { putString("pet_name", petName) }
        
        val user = auth.currentUser
        if (user != null && !user.isAnonymous) {
            try {
                val data = mapOf("pet_name" to petName)
                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                android.util.Log.e("FIRESTORE_SYNC", "Failed to sync pet name", e)
            }
        }
    }

    suspend fun savePetType(petType: String) {
        prefs.edit { putString("pet_type", petType) }
        val user = auth.currentUser
        if (user != null && !user.isAnonymous) {
            try {
                val data = mapOf("pet_type" to petType)
                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                android.util.Log.e("FIRESTORE_SYNC", "Failed to sync pet type", e)
            }
        }
    }

    suspend fun saveProfileUserName(name: String) {
        prefs.edit { putString("profile_user_name", name) }
        val user = auth.currentUser
        if (user != null && !user.isAnonymous) {
            try {
                val data = mapOf("profile_user_name" to name)
                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                android.util.Log.e("FIRESTORE_SYNC", "Failed to sync user name", e)
            }
        }
    }
}
