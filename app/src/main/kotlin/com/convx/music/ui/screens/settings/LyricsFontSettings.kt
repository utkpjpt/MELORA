package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.ui.ios.compat.*
import com.convx.music.viewmodels.LyricsAppearanceViewModel

@Composable
fun LyricsFontSettings(viewModel: LyricsAppearanceViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Lyrics text size", style = AppTheme.typography.titleMedium)
        Text("Text size: ${state.size.toInt()} sp")
        Slider(value = state.size, onValueChange = viewModel::resize, onValueChangeFinished = viewModel::saveSize, valueRange = 18f..60f)
        Text("Your lyrics, your way", fontSize = state.size.sp, lineHeight = (state.size * 1.3f).sp)
        state.error?.let { Text(it, color = AppTheme.colorScheme.error) }
    }
}
