package com.android.gustyx.officedesk.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.gustyx.officedesk.data.entities.Employee
import com.android.gustyx.officedesk.data.repository.EmployeeRepository
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> get() = _employees

    fun loadEmployees() {
        viewModelScope.launch {
            _employees.value = repository.getAllEmployees()
            Log.d("EmployeeViewModel", "Data karyawan dimuat: ${_employees.value}")
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.insert(employee)
            loadEmployees()
            Log.d("EmployeeViewModel", "Karyawan ditambahkan: $employee")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.delete(employee)
            loadEmployees()
            Log.d("EmployeeViewModel", "Karyawan dihapus: $employee")
        }
    }
}


class EmployeeViewModelFactory(private val repository: EmployeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
