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
        val loginButton = findViewById<ImageButton>(R.id.login)

        // Konfigurasi BiometricPrompt
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Autentikasi error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val username = usernameEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val newUser = User(username = username, password = password)
                        viewModel.insert(newUser)
                        Toast.makeText(applicationContext, "Daftar Berhasil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Autentikasi gagal", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verifikasi Biometrik")
            .setSubtitle("Gunakan sidik jari atau face unlock untuk verifikasi")
            .setNegativeButtonText("Batalkan")
            .build()

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
