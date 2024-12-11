package com.android.gustyx.officedesk.data.repository

import com.android.gustyx.officedesk.data.dao.UserDao
import com.android.gustyx.officedesk.data.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }
}
