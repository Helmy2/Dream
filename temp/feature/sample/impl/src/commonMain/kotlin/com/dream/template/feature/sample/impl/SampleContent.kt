package com.dream.template.feature.sample.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.shared.core.strings.Res
import com.dream.template.shared.core.strings.sample_add
import com.dream.template.shared.core.strings.sample_baseline_label
import com.dream.template.shared.core.strings.sample_cancel
import com.dream.template.shared.core.strings.sample_category_architecture
import com.dream.template.shared.core.strings.sample_category_contract
import com.dream.template.shared.core.strings.sample_category_preview
import com.dream.template.shared.core.strings.sample_description_label
import com.dream.template.shared.core.strings.sample_detail_title
import com.dream.template.shared.core.strings.sample_edit
import com.dream.template.shared.core.strings.sample_empty_body
import com.dream.template.shared.core.strings.sample_empty_title
import com.dream.template.shared.core.strings.sample_error_body
import com.dream.template.shared.core.strings.sample_error_title
import com.dream.template.shared.core.strings.sample_loading_body
import com.dream.template.shared.core.strings.sample_loading_title
import com.dream.template.shared.core.strings.sample_occurred_row
import com.dream.template.shared.core.strings.sample_retry
import com.dream.template.shared.core.strings.sample_save
import com.dream.template.shared.core.strings.sample_seed_contract_first_description
import com.dream.template.shared.core.strings.sample_seed_contract_first_title
import com.dream.template.shared.core.strings.sample_seed_desktop_preview_description
import com.dream.template.shared.core.strings.sample_seed_desktop_preview_title
import com.dream.template.shared.core.strings.sample_seed_shared_architecture_description
import com.dream.template.shared.core.strings.sample_seed_shared_architecture_title
import com.dream.template.shared.core.strings.sample_subtitle
import com.dream.template.shared.core.strings.sample_title
import com.dream.template.shared.core.strings.sample_title_label
import com.dream.template.shared.core.strings.sample_validation_required
import com.dream.template.shared.designsystem.AppTheme
import com.dream.template.shared.designsystem.DreamTheme
import com.dream.template.shared.designsystem.components.Button
import com.dream.template.shared.designsystem.components.ButtonVariant
import com.dream.template.shared.designsystem.components.Card
import com.dream.template.shared.designsystem.components.CardVariant
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.designsystem.components.CircularProgressIndicator
import com.dream.template.shared.designsystem.components.ComponentSize
import com.dream.template.shared.designsystem.components.IconButton
import com.dream.template.shared.designsystem.components.ListItem
import com.dream.template.shared.designsystem.components.TextField
import com.dream.template.shared.designsystem.components.TopAppBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun SampleContent(
    state: SampleState,
    onAction: (SampleAction) -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(Res.string.sample_title),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Button(
                        text = stringResource(Res.string.sample_add),
                        onClick = { onAction(SampleAction.StartCreate) },
                        variant = ButtonVariant.Tertiary,
                    )
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = DreamTheme.spacing.lg, vertical = DreamTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)) {
                    Text(
                        text = stringResource(Res.string.sample_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.sample_baseline_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            state.detailState?.let { detail ->
                item {
                    DetailCard(
                        detail = detail,
                        formState = state.formState,
                        isSaving = state.isSaving,
                        onAction = onAction,
                    )
                }
            }

            when (val contentState = state.contentState) {
                SampleContentState.Loading -> item { LoadingCard() }
                SampleContentState.Empty -> item { EmptyCard() }
                is SampleContentState.Content -> items(contentState.items) { item ->
                    SampleListItem(item = item, onClick = { onAction(SampleAction.SelectItem(item.id)) })
                }
                is SampleContentState.Error -> item {
                    Card(
                        title = stringResource(Res.string.sample_error_title),
                        body = contentState.error.messageText(),
                        variant = CardVariant.Filled,
                        accentColor = MaterialTheme.colorScheme.error,
                        actionLabel = stringResource(Res.string.sample_retry),
                        onAction = { onAction(SampleAction.Load(force = true)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        title = stringResource(Res.string.sample_loading_title),
        body = stringResource(Res.string.sample_loading_body),
        variant = CardVariant.Filled,
        leadingContent = {
            CircularProgressIndicator(
                size = ComponentSize.Small,
            )
        },
    )
}

@Composable
private fun EmptyCard() {
    Card(
        title = stringResource(Res.string.sample_empty_title),
        body = stringResource(Res.string.sample_empty_body),
        variant = CardVariant.Filled,
        accentColor = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun SampleListItem(
    item: SampleItemUi,
    onClick: () -> Unit,
) {
    Card(
        variant = CardVariant.Elevated,
        accentColor = MaterialTheme.colorScheme.primary,
        onClick = onClick,
    ) {
        ListItem(
            headline = item.title.asText(),
            supportingText = item.description.asText(),
            trailingContent = {
                Text(
                    text = item.category.label(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        )
    }
}

@Composable
private fun DetailCard(
    detail: SampleDetailState,
    formState: SampleFormState,
    isSaving: Boolean,
    onAction: (SampleAction) -> Unit,
) {
    Card(
        title = stringResource(Res.string.sample_detail_title),
        variant = CardVariant.Outlined,
    ) {
        if (detail.isEditing) {
            SampleForm(
                formState = formState,
                isSaving = isSaving,
                onAction = onAction,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)) {
                Text(detail.item.title.asText(), style = MaterialTheme.typography.titleLarge)
                Text(detail.item.description.asText(), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = stringResource(Res.string.sample_occurred_row, detail.item.occurredLabel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = detail.item.category.label(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Button(
                    text = stringResource(Res.string.sample_edit),
                    onClick = { onAction(SampleAction.StartEdit) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SampleForm(
    formState: SampleFormState,
    isSaving: Boolean,
    onAction: (SampleAction) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val wide = maxWidth >= 560.dp
        Column(
            modifier = Modifier.widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md),
        ) {
            if (wide) {
                Row(horizontalArrangement = Arrangement.spacedBy(DreamTheme.spacing.md)) {
                    TitleField(formState, onAction, Modifier.weight(1f))
                    DescriptionField(formState, onAction, Modifier.weight(1f))
                }
            } else {
                TitleField(formState, onAction, Modifier.fillMaxWidth())
                DescriptionField(formState, onAction, Modifier.fillMaxWidth())
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm),
            ) {
                SampleCategory.entries.forEach { category ->
                    AssistChip(
                        onClick = { onAction(SampleAction.CategoryChanged(category)) },
                        label = { Text(category.label()) },
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(DreamTheme.spacing.sm)) {
                Button(
                    text = stringResource(Res.string.sample_save),
                    onClick = { onAction(SampleAction.Save) },
                    enabled = !isSaving,
                )
                Button(
                    text = stringResource(Res.string.sample_cancel),
                    onClick = { onAction(SampleAction.CancelEdit) },
                    variant = ButtonVariant.Tertiary,
                )
            }
        }
    }
}

@Composable
private fun TitleField(
    formState: SampleFormState,
    onAction: (SampleAction) -> Unit,
    modifier: Modifier,
) {
    TextField(
        value = formState.title.asText(),
        onValueChange = { onAction(SampleAction.TitleChanged(it)) },
        placeholder = stringResource(Res.string.sample_title_label),
        isError = formState.titleError,
        supportingText = if (formState.titleError) stringResource(Res.string.sample_validation_required) else null,
        singleLine = true,
        modifier = modifier,
    )
}

@Composable
private fun DescriptionField(
    formState: SampleFormState,
    onAction: (SampleAction) -> Unit,
    modifier: Modifier,
) {
    TextField(
        value = formState.description.asText(),
        onValueChange = { onAction(SampleAction.DescriptionChanged(it)) },
        placeholder = stringResource(Res.string.sample_description_label),
        isError = formState.descriptionError,
        supportingText = if (formState.descriptionError) stringResource(Res.string.sample_validation_required) else null,
        singleLine = false,
        minLines = 2,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
}

@Composable
private fun SampleCategory.label(): String = when (this) {
    SampleCategory.Contract -> stringResource(Res.string.sample_category_contract)
    SampleCategory.Architecture -> stringResource(Res.string.sample_category_architecture)
    SampleCategory.Preview -> stringResource(Res.string.sample_category_preview)
}

@Composable
private fun SampleTextUi.asText(): String = when (this) {
    is SampleTextUi.Raw -> value
    is SampleTextUi.Resource -> resource.asText()
}

@Composable
private fun SampleTextResource.asText(): String = when (this) {
    SampleTextResource.SeedContractFirstTitle -> stringResource(Res.string.sample_seed_contract_first_title)
    SampleTextResource.SeedContractFirstDescription -> stringResource(Res.string.sample_seed_contract_first_description)
    SampleTextResource.SeedSharedArchitectureTitle -> stringResource(Res.string.sample_seed_shared_architecture_title)
    SampleTextResource.SeedSharedArchitectureDescription -> stringResource(Res.string.sample_seed_shared_architecture_description)
    SampleTextResource.SeedDesktopPreviewTitle -> stringResource(Res.string.sample_seed_desktop_preview_title)
    SampleTextResource.SeedDesktopPreviewDescription -> stringResource(Res.string.sample_seed_desktop_preview_description)
}

@Composable
private fun AppError.messageText(): String = when (this) {
    AppError.Unknown -> stringResource(Res.string.sample_error_body)
    is AppError.Message -> value
}

@PreviewLightDark
@Composable
private fun SampleContentPreview() {
    AppTheme {
        SampleContent(
            state = SampleState(
                contentState = SampleContentState.Content(
                    items = listOf(
                        SampleItemUi("one", SampleTextUi.Raw("Getting Started"), SampleTextUi.Raw("Explore editable details"), SampleCategory.Contract, "Jul 3, 2024"),
                        SampleItemUi("two", SampleTextUi.Raw("Your Progress"), SampleTextUi.Raw("Track update behavior"), SampleCategory.Architecture, "Jul 4, 2024"),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
