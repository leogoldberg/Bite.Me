package com.iat359.biteme

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.os.Bundle

class ReviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val name = intent.getStringExtra("RECIPE_NAME")

        recipe_name.text = name
    }
}