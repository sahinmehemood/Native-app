package com.hermes.android.feature.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.feature.chat.viewmodel.PendingApproval

@Composable
fun ApprovalCard(
    approval: PendingApproval,
    onResolve: (String, String?) -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(HermesSpacing.Md)) {
            Text(approval.title ?: "Approval required", style = MaterialTheme.typography.titleMedium)
            if (approval.detail != null) {
                Text(
                    approval.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = HermesSpacing.Sm),
                horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                approval.choices.forEach { choice ->
                    Button(
                        onClick = { onResolve(choice, null) },
                        modifier = Modifier.height(HermesSpacing.TouchTarget),
                    ) {
                        Text(choice.replaceFirstChar { it.uppercase() })
                    }
                }
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.height(HermesSpacing.TouchTarget),
                ) { Text("Stop") }
            }
        }
    }
}
