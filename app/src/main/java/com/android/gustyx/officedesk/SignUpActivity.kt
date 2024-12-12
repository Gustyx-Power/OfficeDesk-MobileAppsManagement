package com.android.gustyx.officedesk

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
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

        if (isRooted()) {
            showRootWarningAndExit()
            return
        }

        setContentView(R.layout.activity_signup)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val signUpButton = findViewById<ImageButton>(R.id.signup)
        val loginButton = findViewById<ImageButton>(R.id.login)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        loadLoginData(usernameEditText, passwordEditText, rememberMeCheckBox)

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
                if (validateUsername(username) && validatePassword(password)) {
                    if (isDeviceSecure()) {
                        if (isRooted()) {
                            showRootWarningAndExit()
                            return@setOnClickListener
                        } else {
                            biometricPrompt.authenticate(promptInfo)
                        }
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

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (isRooted()) {
                    showRootWarningAndExit()
                    return@setOnClickListener
                } else {
                    viewModel.getUser(username, password) { user ->
                        if (user != null) {
                            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                        }
                    }
                        saveLoginData(username, password, rememberMeCheckBox.isChecked)
                        viewModel.getUser(username, password) { user ->
                }
            } else {
                Toast.makeText(this, "Isi Username dan Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isDeviceSecure(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isDeviceSecure
    }

    private fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        val filePaths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su" , "/data/adb/ksud" ,
            "/data/adb/ksu" , "/data/adb/modules" , "/data/adb/zygiskksu" , "/storage/emulated/0/Android/data/me.weishu.kernelsu"
        )
        for (path in filePaths) {
            if (File(path).exists()) {
                return true
            }
        }
        return canExecuteCommand("/system/xbin/which su") || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su")
    }

    private fun canExecuteCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun showRootWarningAndExit() {
        Toast.makeText(this, "Perangkat telah di-root. Aplikasi akan ditutup.", Toast.LENGTH_SHORT).show()
        android.os.Handler().postDelayed({
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            exitProcess(0)
        }, 2000)
    }
}
