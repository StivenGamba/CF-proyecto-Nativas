package com.example.taller_3_fragments.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri

data class UserProfile(
    val email: String?,
    val displayName: String?,
    val photoUrl: Uri? // Usamos Uri para la URL de la foto
)

class UserViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    fun setUserProfile(email: String?, displayName: String?, photoUrlString: String?) {
        val photoUri = photoUrlString?.let { Uri.parse(it) }
        _userProfile.value = UserProfile(email, displayName, photoUri)
    }

    fun clearUserProfile() {
        _userProfile.value = null
    }
}