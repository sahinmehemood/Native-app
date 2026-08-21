package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.ui.theme.HermesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HermesTheme { HermesShell() } }
    }
}

private data class PreviewMessage(val author: String, val body: String, val isUser: Boolean)

@Composable
private fun HermesShell() {
    val messages = remember {
        listOf(
            PreviewMessage("Hermes", "Ready when you are. Connect a Hermes gateway to begin.", false),
        )
    }
    var draft by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = HermesColors.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item { ConnectionBanner() }
                items(messages) { message -> MessageCard(message) }
            }
            Composer(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {}) { Icon(Icons.Outlined.Menu, "Open sessions") }
        Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
            Text("Hermes", color = HermesColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("Android client", color = HermesColors.textMuted, fontSize = 12.sp)
        }
        IconButton(onClick = {}) { Icon(Icons.Outlined.Settings, "Settings", tint = HermesColors.textSecondary) }
        IconButton(onClick = {}) { Icon(Icons.Outlined.MoreHoriz, "More actions", tint = HermesColors.textSecondary) }
    }
}

@Composable
private fun ConnectionBanner() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(HermesColors.surface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(HermesColors.warning))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Gateway not connected", color = HermesColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Connect your Hermes host to start an agent session.", color = HermesColors.textMuted, fontSize = 12.sp)
        }
        IconButton(onClick = {}) { Icon(Icons.Outlined.Add, "Add gateway", tint = HermesColors.accent) }
    }
}

@Composable
private fun MessageCard(message: PreviewMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
        Column(modifier = Modifier.fillMaxWidth(0.92f)) {
            Text(message.author, color = HermesColors.accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(5.dp))
            Text(
                message.body,
                modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(HermesColors.surface).padding(15.dp),
                color = HermesColors.textPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun Composer(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Message Hermes", color = HermesColors.textMuted) },
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = HermesColors.surface,
                unfocusedContainerColor = HermesColors.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = HermesColors.textPrimary,
                unfocusedTextColor = HermesColors.textPrimary,
            ),
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = {},
            modifier = Modifier.size(52.dp).clip(CircleShape).background(HermesColors.accent),
        ) { Icon(Icons.Outlined.ArrowUpward, "Send", tint = HermesColors.background) }
    }
}

private object HermesColors {
    val background = Color(0xFF0B0D0F)
    val surface = Color(0xFF15191D)
    val textPrimary = Color(0xFFF2F3F4)
    val textSecondary = Color(0xFFB4BBC2)
    val textMuted = Color(0xFF78818A)
    val accent = Color(0xFFD9A441)
    val warning = Color(0xFFE0A34B)
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0D0F)
@Composable
private fun HermesShellPreview() { HermesTheme { HermesShell() } }

