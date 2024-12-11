package com.android.gustyx.officedesk


import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signUpButton = findViewById<ImageButton>(R.id.signup)


        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {

                Toast.makeText(this, "Daftar Berhasil", Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(this, "Isi Username Dan Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
