package com.dream.template.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dream.template.shared.app.DreamApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dream Template"
    ) {
        Column {
            DreamApp()
        }
    }
}