package com.android.gustyx.officedesk

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.gustyx.officedesk.data.entities.Employee
import java.util.Locale

class EmployeeAdapter(
    private val context: Context,
    private var employees: MutableList<Employee>,
    private val onDeleteClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    fun updateData(newEmployees: List<Employee>) {
        val oldSize = employees.size
        employees.clear()
        notifyItemRangeRemoved(0, oldSize) // Menggunakan notifyItemRangeRemoved untuk memberitahu penghapusan data lama

        employees.addAll(newEmployees)
        notifyItemRangeInserted(0, newEmployees.size) // Menggunakan notifyItemRangeInserted untuk memberitahu penambahan data baru
        Log.d("EmployeeAdapter", "Data adapter diperbarui: $newEmployees")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_dashboard, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]
        holder.nameTextView.text = employee.name
        holder.positionTextView.text = employee.position

        // Menggunakan String.format untuk mempertimbangkan pengaturan lokal
        holder.salaryTextView.text = String.format(Locale.getDefault(), "%.2f", employee.salary)
        holder.deleteButton.setOnClickListener {
            onDeleteClick(employee)
        }
    }

    override fun getItemCount(): Int = employees.size

    inner class EmployeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val positionTextView: TextView = view.findViewById(R.id.positionTextView)
        val salaryTextView: TextView = view.findViewById(R.id.salaryTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }
}

