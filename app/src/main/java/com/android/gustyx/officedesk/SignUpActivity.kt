package com.android.gustyx.officedesk

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.android.gustyx.officedesk.data.database.UserDatabase
import com.android.gustyx.officedesk.data.entities.User
import com.android.gustyx.officedesk.data.repository.UserRepository
import com.android.gustyx.officedesk.data.viewmodel.UserViewModel
import com.android.gustyx.officedesk.data.viewmodel.UserViewModelFactory
import java.io.File
import java.util.concurrent.Executor
import java.util.regex.Pattern
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(UserDatabase.getDatabase(this).userDao()))
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah perangkat di-root
        if (isRooted()) {
            showRootWarningAndExit()
            return
        }

        setContentView(R.layout.activity_signup)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Inisialisasi view
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signUpButton = findViewById<ImageButton>(R.id.signup)
        val loginButton = findViewById<ImageButton>(R.id.login)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)

        // Load data login jika Remember Me diaktifkan
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        loadLoginData(usernameEditText, passwordEditText, rememberMeCheckBox)

        // Inisialisasi Biometric Prompt
        setupBiometricPrompt(usernameEditText, passwordEditText)

        // Handle tombol Sign Up
        signUpButton.setOnClickListener {
            handleSignUp(usernameEditText, passwordEditText)
        }

        // Handle tombol Login
        loginButton.setOnClickListener {
            handleLogin(usernameEditText, passwordEditText, rememberMeCheckBox)
        }
    }

    private fun setupBiometricPrompt(usernameEditText: EditText, passwordEditText: EditText) {
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
    }

    private fun handleSignUp(usernameEditText: EditText, passwordEditText: EditText) {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (validateUsername(username) && validatePassword(password)) {
                if (isDeviceSecure()) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    Toast.makeText(this, "Setel keamanan layar perangkat terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Username atau Password tidak memenuhi kriteria.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Isi Username dan Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogin(usernameEditText: EditText, passwordEditText: EditText, rememberMeCheckBox: CheckBox) {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (validateUsername(username) && validatePassword(password)) {
                saveLoginData(username, password, rememberMeCheckBox.isChecked)
                viewModel.getUser(username, password) { user ->
                    if (user != null) {
                        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Username atau Password tidak memenuhi kriteria.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Isi Username dan Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDeviceSecure(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isDeviceSecure
    }

    private fun isRooted(): Boolean {
        val filePaths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
        )
        return filePaths.any { File(it).exists() }
    }

    private fun validateUsername(username: String): Boolean {
        return Pattern.compile("^[a-zA-Z0-9_]+$").matcher(username).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return Pattern.compile("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$").matcher(password).matches()
    }

    private fun loadLoginData(usernameEditText: EditText, passwordEditText: EditText, rememberMeCheckBox: CheckBox) {
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)
        if (isRemembered) {
            usernameEditText.setText(username)
            passwordEditText.setText(password)
            rememberMeCheckBox.isChecked = true
        }
    }

    private fun saveLoginData(username: String, password: String, isRemembered: Boolean) {
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            putBoolean("rememberMe", isRemembered)
            apply()
        }
    }

    private fun showRootWarningAndExit() {
        Toast.makeText(this, "Perangkat telah di-root. Aplikasi akan ditutup.", Toast.LENGTH_SHORT).show()
        finish()
        exitProcess(0)
    }
}