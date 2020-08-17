package com.hr.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val galleryFragment:GalleryFragment by lazy { GalleryFragment() }
    val photoFragment:PhotoFragment by lazy { PhotoFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fl,galleryFragment).commit()

    }
}