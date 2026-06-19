package com.dream.template.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.shared.app.DreamApp
import com.dream.template.shared.di.initKoin

fun main() = application {
    initKoin(sampleDatabaseFactory = SampleDatabaseFactory())

    Window(
        onCloseRequest = ::exitApplication,
        title = "Dream Template"
    ) {
        Column {
            DreamApp()
        }
    }
}