package com.dream.template.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.shared.app.DreamApp
import com.dream.template.shared.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin(sampleDatabaseFactory = SampleDatabaseFactory())
    DreamApp()
}
