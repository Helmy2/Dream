package com.dream.template.shared.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeatureCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(DreamTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)
            ) {
                Spacer(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DreamAccentSun)
                        .height(10.dp)
                        .defaultMinSize(minWidth = 10.dp)
                )
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StateCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    leading: (@Composable (() -> Unit))? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(DreamTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md)
            ) {
                if (leading != null) {
                    leading()
                } else {
                    Spacer(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accentColor)
                            .height(14.dp)
                            .defaultMinSize(minWidth = 14.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.xs)) {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (actionLabel != null && onAction != null) {
                Button(onClick = onAction) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Composable
fun LoadingStateCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    StateCard(
        title = title,
        body = body,
        modifier = modifier,
        accentColor = DreamAccentSun,
        leading = {
            CircularProgressIndicator(
                modifier = Modifier.height(28.dp).defaultMinSize(minWidth = 28.dp),
                strokeWidth = 2.5.dp
            )
        }
    )
}
