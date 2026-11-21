package com.dream.template.shared.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.NavKey
import com.dream.template.shared.core.strings.placeholder_body
import com.dream.template.shared.core.strings.placeholder_title
import com.dream.template.shared.core.strings.Res
import com.dream.template.shared.designsystem.DreamTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

/**
 * Start destination of the empty starter. The first real feature replaces it: point
 * `navigatorModule` at the feature's route and delete this file (see new-feature-checklist.md).
 */
@Serializable
data object HomeRoute : NavKey

// Plain Material3 Text: the design system has no text component yet, and this screen is temporary.
@Composable
fun PlaceholderHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DreamTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.placeholder_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.placeholder_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(KoinExperimentalAPI::class)
val placeholderHomeModule = module {
    navigation<HomeRoute> { PlaceholderHomeScreen() }
}
