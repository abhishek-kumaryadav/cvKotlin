package com.abhk943.cvkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {
    lateinit var _imageButton:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OpenCVLoader.initDebug()
        _imageButton=findViewById<ImageButton>(R.id.gotoWork)
        _imageButton.setOnClickListener{
            val intent =  Intent(this, WorkActivity::class.java)
            startActivity(intent)
        }
    }
}