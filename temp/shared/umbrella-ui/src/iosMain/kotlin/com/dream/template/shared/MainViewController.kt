package com.dream.template.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.dream.template.shared.core.data.local.IosSampleDatabaseFactory
import com.dream.template.shared.app.DreamApp
import com.dream.template.shared.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin(sampleDatabaseFactory = IosSampleDatabaseFactory())
    DreamApp()
}