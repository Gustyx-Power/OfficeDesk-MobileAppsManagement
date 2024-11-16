package com.android.gustyx.officedesk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream

class LoginActivity : AppCompatActivity() {

    private lateinit var imageViewLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imageViewLogo = findViewById(R.id.imageViewLogo)
        loadImageFromAssets("drawable/texthome.png")
    }

    private fun loadImageFromAssets(fileName: String) {
        try {
            val inputStream: InputStream = assets.open(fileName)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewLogo.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

    }
}