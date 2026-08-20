package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hermes.android.presentation.state.SkillsUiState
import com.hermes.android.presentation.ui.components.HermesChip
import com.hermes.android.presentation.ui.components.HermesEmptyState
import com.hermes.android.presentation.ui.components.HermesListItem
import com.hermes.android.presentation.ui.components.HermesTextButton
import com.hermes.android.presentation.ui.components.HermesTextField
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.viewmodel.SkillsViewModel

@Composable
fun SkillsScreen(viewModel: SkillsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    val categories = remember(state) {
        (state as? SkillsUiState.Success)?.skills
            ?.map { it.category }
            ?.distinct()
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filtered = (state as? SkillsUiState.Success)?.skills?.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
            (query.isBlank() || it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true))
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            HermesTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Search skills",
                modifier = Modifier.fillMaxWidth().padding(HermesSpacing.Spacing12)
            )
            if (categories.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = HermesSpacing.Spacing12).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Spacing8)
                ) {
                    HermesChip("All", selected = selectedCategory == null, onClick = { selectedCategory = null })
                    categories.forEach { cat ->
                        HermesChip(cat, selected = selectedCategory == cat, onClick = { selectedCategory = cat })
                    }
                }
            }
            Spacer(Modifier.height(HermesSpacing.Spacing8))
            if (filtered.isEmpty()) {
                HermesEmptyState("No skills", "Try a different search or category.")
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Spacing12)) {
                    items(filtered, key = { it.id }) { skill ->
                        HermesListItem(
                            title = skill.name,
                            subtitle = skill.description,
                            trailing = {
                                HermesTextButton(
                                    text = if (skill.installed) "Installed" else "Install",
                                    onClick = { viewModel.toggle(skill.id) },
                                    color = if (skill.installed) HermesColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
