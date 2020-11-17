package com.iat359.biteme

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.os.Bundle
import android.provider.MediaStore

class ReviewActivity : BaseActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val name = intent.getStringExtra("RECIPE_NAME")

        recipe_name.text = name

        camera_button.setOnClickListener {
            dispatchTakePictureIntent()
        }

        save_button.setOnClickListener{
            Intent(this, SwipeActivity::class.java).also {
                startActivity(it)
            }
        }

        cancel_button.setOnClickListener{
            finish()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }
}