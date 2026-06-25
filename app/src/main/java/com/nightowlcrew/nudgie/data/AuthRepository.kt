package com.nightowlcrew.nudgie.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun signInAnonymously() {
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Links the current anonymous account with the provided credential (e.g., Google, Email).
     */
    suspend fun linkAccount(credential: AuthCredential) {
        auth.currentUser?.linkWithCredential(credential)?.await()
    }

    fun signOut() {
        auth.signOut()
    }

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    val isAnonymous: Boolean
        get() = auth.currentUser?.isAnonymous ?: true

    val userId: String?
        get() = auth.currentUser?.uid
}
