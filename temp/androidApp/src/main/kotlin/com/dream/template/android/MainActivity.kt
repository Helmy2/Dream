package com.dream.template.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.shared.app.DreamApp
import com.dream.template.shared.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin(sampleDatabaseFactory = SampleDatabaseFactory(applicationContext))
        enableEdgeToEdge()
        setContent {
            DreamApp()
        }
    }
}
