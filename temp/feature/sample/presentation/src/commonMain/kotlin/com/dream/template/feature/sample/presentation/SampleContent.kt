package com.dream.template.feature.sample.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dream.template.feature.sample.domain.model.SampleItem
import androidx.compose.material3.CircularProgressIndicator
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
import com.dream.template.shared.designsystem.AppTheme
import com.dream.template.shared.designsystem.DreamTheme
import com.dream.template.shared.designsystem.components.Card
import com.dream.template.shared.designsystem.components.CardVariant
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleContent(
    state: SampleState,
    onAction: (SampleAction) -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.sample_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DreamTheme.spacing.lg, vertical = DreamTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)) {
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
                        Card(
                            title = stringResource(Res.string.sample_loading_title),
                            body = stringResource(Res.string.sample_loading_body),
                            variant = CardVariant.Filled,
                            leadingContent = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        )
                    }
                }

                SampleContentState.Empty -> {
                    item {
                        Card(
                            title = stringResource(Res.string.sample_empty_title),
                            body = stringResource(Res.string.sample_empty_body),
                            variant = CardVariant.Filled,
                            accentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                is SampleContentState.Content -> {
                    items(contentState.items) { item ->
                        Card(
                            title = item.title,
                            body = item.description,
                            variant = CardVariant.Elevated,
                            accentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is SampleContentState.Error -> {
                    item {
                        Card(
                            title = stringResource(Res.string.sample_error_title),
                            body = contentState.message,
                            variant = CardVariant.Filled,
                            accentColor = MaterialTheme.colorScheme.error,
                            actionLabel = stringResource(Res.string.sample_retry),
                            onAction = { onAction(SampleAction.Load(force = true)) }
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SampleContentPreview() {
    AppTheme {
        SampleContent(
            state = SampleState(
                contentState = SampleContentState.Content(
                    items = listOf(
                        SampleItem(title = "Getting Started", description = "Explore the features"),
                        SampleItem(title = "Your Progress", description = "Track your journey")
                    )
                )
            ),
            onAction = {}
        )
    }
}
