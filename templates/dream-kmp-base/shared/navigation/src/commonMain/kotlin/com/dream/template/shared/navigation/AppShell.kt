package com.dream.template.shared.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dream.template.shared.designsystem.AppTheme

@Composable
fun AppShell(
    sampleContent: @Composable () -> Unit
) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation(sampleContent = sampleContent)
        }
    }
}
