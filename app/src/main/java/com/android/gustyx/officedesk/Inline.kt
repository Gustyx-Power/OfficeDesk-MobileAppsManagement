kotlin
package com.android.gustyx.officedesk

import android.content.Intent
import android.widget.ImageButton
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Inline : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonMasuk: ImageButton = findViewById(R.id.button_login
        )

        buttonMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
    }
}