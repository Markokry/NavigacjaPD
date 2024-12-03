package com.example.mapnavigationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mapnavigationapp.view.AppNavigation
import com.example.mapnavigationapp.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = MapViewModel()

        setContent {
            AppNavigation(viewModel)
        }
    }
}