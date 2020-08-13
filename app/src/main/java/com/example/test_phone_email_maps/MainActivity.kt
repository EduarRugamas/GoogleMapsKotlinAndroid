package com.example.test_phone_email_maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test_phone_email_maps.Fragment.FragmentContacts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConfigInitial()


    }



    private fun ConfigInitial(){

        supportFragmentManager.beginTransaction().replace(R.id.FrameLayout, FragmentContacts()).commit()
    }

}