package com.android.gustyx.officedesk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.gustyx.officedesk.data.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?
}
