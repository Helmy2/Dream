package com.dream.template.feature.sample.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dream.template.shared.designsystem.FeatureCard

@Composable
fun SampleScreen(
    viewModel: SampleViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.load()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = state.title, style = MaterialTheme.typography.headlineMedium)
                Text(text = state.subtitle, style = MaterialTheme.typography.bodyLarge)
            }
        }

        when (val contentState = state.contentState) {
            SampleContentState.Loading -> {
                item {
                    FeatureCard(
                        title = "Preparing the starter project",
                        body = "Dream is loading the sample content and reusable architecture guidance."
                    )
                }
            }

            SampleContentState.Empty -> {
                item {
                    FeatureCard(
                        title = "No starter cards yet",
                        body = "The template is ready, but no sample content was returned. Add the first real feature next."
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
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureCard(
                            title = "Starter content failed to load",
                            body = contentState.message
                        )
                        Button(onClick = viewModel::load) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
