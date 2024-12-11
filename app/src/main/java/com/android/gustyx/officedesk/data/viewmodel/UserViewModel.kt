package com.android.gustyx.officedesk.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.gustyx.officedesk.data.entities.User
import com.android.gustyx.officedesk.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    fun getUser(username: String, password: String, callback: (User?) -> Unit) = viewModelScope.launch {
        val user = repository.getUser(username, password)
        callback(user)
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
