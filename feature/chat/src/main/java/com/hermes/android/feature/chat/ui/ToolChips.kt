package com.hermes.android.feature.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.feature.chat.viewmodel.ToolActivity

@Composable
fun ToolChipsRow(tools: List<ToolActivity>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = HermesSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
    ) {
        tools.forEach { tool ->
            val label = "${tool.toolName} · ${if (tool.phase == "finish") "done" else "running"}"
            AssistChip(
                onClick = { },
                label = { Text(label) },
                shape = RoundedCornerShape(HermesSpacing.ChipRadius),
            )
        }
    }
}
