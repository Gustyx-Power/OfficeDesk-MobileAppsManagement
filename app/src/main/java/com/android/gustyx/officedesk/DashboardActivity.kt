package com.android.gustyx.officedesk

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)

        // Ambil username dari SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Administrator")

        // Tampilkan username di TextView
        welcomeTextView.text = "Selamat Datang, $username!"
    }
}
