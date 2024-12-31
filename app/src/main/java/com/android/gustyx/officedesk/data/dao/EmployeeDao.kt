package com.android.gustyx.officedesk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.gustyx.officedesk.data.entities.Employee

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employee: Employee)

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<Employee>

    @Query("DELETE FROM employees WHERE id = :employeeId")
    suspend fun deleteEmployeeById(employeeId: Int)
}
