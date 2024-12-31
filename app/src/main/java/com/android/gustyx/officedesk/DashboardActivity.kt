package com.android.gustyx.officedesk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.gustyx.officedesk.data.database.EmployeeDatabase
import com.android.gustyx.officedesk.data.repository.EmployeeRepository
import com.android.gustyx.officedesk.data.viewmodel.EmployeeViewModel
import com.android.gustyx.officedesk.data.viewmodel.EmployeeViewModelFactory
import com.android.gustyx.officedesk.data.entities.Employee

class DashboardActivity : AppCompatActivity() {

    private val viewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(EmployeeRepository(EmployeeDatabase.getDatabase(this).employeeDao()))
    }
    private lateinit var employeeAdapter: EmployeeAdapter
    private var employeeRecyclerView: RecyclerView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        Log.d("DashboardActivity", "onCreate: Memulai inisialisasi komponen UI")

        // Inisialisasi UI components
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val btnAddData = findViewById<ImageButton>(R.id.addfeatures)
        employeeRecyclerView = findViewById(R.id.RecyclerViewEmployee)

        // Ambil username dari SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Administrator") ?: "Administrator"
        Log.d("DashboardActivity", "onCreate: Username - $username")


        welcomeTextView.text = "Selamat Datang, $username!"
        initializeRecyclerView()

        val addDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newEmployee = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra<Employee>("NEW_EMPLOYEE", Employee::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra("NEW_EMPLOYEE")
                }
                newEmployee?.let {
                    viewModel.addEmployee(it)
                }
            }
        }

        btnAddData.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            addDataLauncher.launch(intent)
        }


        viewModel.employees.observe(this, Observer { employees ->
            employees?.let {
                employeeAdapter.updateData(it)
                Log.d("DashboardActivity", "onCreate: Data karyawan diperbarui")
            }
        })

        viewModel.loadEmployees()
    }

    private fun initializeRecyclerView() {
        employeeAdapter = EmployeeAdapter(
            context = this,
            employees = mutableListOf(),
            onDeleteClick = { employee ->
                viewModel.deleteEmployee(employee)
            }
        )
        employeeRecyclerView?.layoutManager = LinearLayoutManager(this)
        employeeRecyclerView?.adapter = employeeAdapter
        Log.d("DashboardActivity", "initializeRecyclerView: RecyclerView diinisialisasi")
    }
}