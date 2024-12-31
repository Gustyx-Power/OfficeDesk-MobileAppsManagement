package com.android.gustyx.officedesk.data.repository

import android.util.Log
import com.android.gustyx.officedesk.data.dao.EmployeeDao
import com.android.gustyx.officedesk.data.entities.Employee

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    suspend fun getAllEmployees(): List<Employee> {
        return employeeDao.getAllEmployees()
    }

    suspend fun insert(employee: Employee) {
        employeeDao.insert(employee)
        Log.d("EmployeeRepository", "Employee inserted: $employee")
    }

    suspend fun delete(employee: Employee) {
        employeeDao.deleteEmployeeById(employee.id)
        Log.d("EmployeeRepository", "Employee deleted: $employee")
    }
}

