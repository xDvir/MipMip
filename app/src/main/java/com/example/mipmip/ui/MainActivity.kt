package com.example.mipmip.ui


import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.mipmip.ui.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        setContent {
            Navigation()
        }
    }


}