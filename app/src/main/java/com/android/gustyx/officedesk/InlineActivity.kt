package com.android.gustyx.officedesk

import android.content.Intent
import android.widget.ImageButton
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InlineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline)

        val buttonLogin: ImageButton = findViewById(R.id.button_login
        )
        buttonLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

