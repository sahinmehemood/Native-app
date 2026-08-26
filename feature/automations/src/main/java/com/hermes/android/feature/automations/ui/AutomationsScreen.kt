package com.hermes.android.feature.automations.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.data.ScheduledJob
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.feature.automations.viewmodel.AutomationsStatus
import com.hermes.android.feature.automations.viewmodel.AutomationsUiState
import com.hermes.android.feature.automations.viewmodel.JobRunState
import org.koin.androidx.compose.koinViewModel

@Composable
fun AutomationsRoute(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
) {
    val viewModel: AutomationsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    AutomationsScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onAdd = viewModel::showAdd,
        onEdit = viewModel::editJob,
        onDelete = viewModel::deleteJob,
        onRun = { job -> viewModel.runJob(job, onDispatched = onOpenSession) },
        onDismissEditor = viewModel::dismissEditor,
        onSaveJob = viewModel::saveJob,
        onClearRunError = viewModel::clearRunError,
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AutomationsScreen(
    state: AutomationsUiState,
    onNavigateUp: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (ScheduledJob) -> Unit,
    onDelete: (ScheduledJob) -> Unit,
    onRun: (ScheduledJob) -> Unit,
    onDismissEditor: () -> Unit,
    onSaveJob: (String, String, String, ScheduledJob?) -> Unit,
    onClearRunError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Automations") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Add automation")
                }
            },
        )
        when (state.status) {
            AutomationsStatus.Loading -> CircularProgressIndicator(Modifier.padding(HermesSpacing.Lg))
            AutomationsStatus.Empty -> EmptyState("No automations yet. Tap + to schedule a prompt that Hermes can run on demand.")
            AutomationsStatus.Ready -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Lg),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = HermesSpacing.Xxl),
                verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
            ) {
                items(state.jobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        runState = state.runStates[job.id] ?: JobRunState.Idle,
                        onRun = { onRun(job) },
                        onEdit = { onEdit(job) },
                        onDelete = { onDelete(job) },
                    )
                }
            }
        }
    }

    if (state.showAdd) {
        JobEditorDialog(
            editing = state.editing,
            onDismiss = onDismissEditor,
            onSave = { name, schedule, prompt -> onSaveJob(name, schedule, prompt, state.editing) },
        )
    }

    state.runError?.let { error ->
        AlertDialog(
            onDismissRequest = onClearRunError,
            confirmButton = { TextButton(onClick = onClearRunError) { Text("OK") } },
            title = { Text("Run failed") },
            text = { Text(error) },
        )
    }
}

@Composable
private fun JobCard(
    job: ScheduledJob,
    runState: JobRunState,
    onRun: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(job.name, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(HermesSpacing.Xs))
                    Text(job.schedule.ifBlank { "No schedule set" }, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (runState == JobRunState.Running) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = HermesSpacing.Sm))
                }
                IconButton(onClick = onEdit, enabled = runState != JobRunState.Running) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit automation")
                }
                IconButton(onClick = onDelete, enabled = runState != JobRunState.Running) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete automation")
                }
            }
            Spacer(Modifier.height(HermesSpacing.Xs))
            Text(job.prompt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(HermesSpacing.Sm))
            Button(onClick = onRun, enabled = runState != JobRunState.Running) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(HermesSpacing.Sm))
                Text("Run now")
            }
            if (runState == JobRunState.Success) {
                Spacer(Modifier.height(HermesSpacing.Xs))
                Text("Dispatched to a new Hermes session.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun JobEditorDialog(
    editing: ScheduledJob?,
    onDismiss: () -> Unit,
    onSave: (name: String, schedule: String, prompt: String) -> Unit,
) {
    var name by remember { mutableStateOf(editing?.name ?: "") }
    var schedule by remember { mutableStateOf(editing?.schedule ?: "") }
    var prompt by remember { mutableStateOf(editing?.prompt ?: "") }

    LaunchedEffect(editing) {
        name = editing?.name ?: ""
        schedule = editing?.schedule ?: ""
        prompt = editing?.prompt ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onSave(name, schedule, prompt) },
                enabled = name.isNotBlank() && prompt.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (editing == null) "New automation" else "Edit automation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm)) {
                Text(
                    "This is a device-local planner. “Run now” creates a Hermes session and sends the prompt — there is no server-side scheduler.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule (e.g. Daily 09:00)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Prompt") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        },
    )
}
