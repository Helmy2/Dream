package com.dream.template.feature.sample.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import com.dream.template.shared.core.strings.Res
import com.dream.template.shared.core.strings.sample_baseline_label
import com.dream.template.shared.core.strings.sample_empty_body
import com.dream.template.shared.core.strings.sample_empty_title
import com.dream.template.shared.core.strings.sample_error_title
import com.dream.template.shared.core.strings.sample_loading_body
import com.dream.template.shared.core.strings.sample_loading_title
import com.dream.template.shared.core.strings.sample_retry
import com.dream.template.shared.core.strings.sample_subtitle
import com.dream.template.shared.core.strings.sample_title
import com.dream.template.shared.designsystem.DreamTheme
import com.dream.template.shared.designsystem.FeatureCard
import com.dream.template.shared.designsystem.LoadingStateCard
import com.dream.template.shared.designsystem.StateCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SampleScreen(
    viewModel: SampleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.onAction(SampleAction.Load())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DreamTheme.spacing.lg, vertical = DreamTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)) {
                Text(text = stringResource(Res.string.sample_title), style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = stringResource(Res.string.sample_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.sample_baseline_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        when (val contentState = state.contentState) {
            SampleContentState.Loading -> {
                item {
                    LoadingStateCard(
                        title = stringResource(Res.string.sample_loading_title),
                        body = stringResource(Res.string.sample_loading_body)
                    )
                }
            }

            SampleContentState.Empty -> {
                item {
                    StateCard(
                        title = stringResource(Res.string.sample_empty_title),
                        body = stringResource(Res.string.sample_empty_body),
                        accentColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            is SampleContentState.Content -> {
                items(contentState.items) { item ->
                    FeatureCard(title = item.title, body = item.description)
                }
            }

            is SampleContentState.Error -> {
                item {
                    StateCard(
                        title = stringResource(Res.string.sample_error_title),
                        body = contentState.message,
                        accentColor = MaterialTheme.colorScheme.error,
                        actionLabel = stringResource(Res.string.sample_retry),
                        onAction = { viewModel.onAction(SampleAction.Load(force = true)) }
                    )
                }
            }
        }
    }
}
