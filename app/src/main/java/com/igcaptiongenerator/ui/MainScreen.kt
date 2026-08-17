package com.igcaptiongenerator.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.igcaptiongenerator.data.model.CaptionResult
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: CaptionViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val recentResults by viewModel.recentResults.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        topBar = { TopAppBar(title = { Text("IG Caption Generator") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick Image from Gallery")
                }
            }

            item {
                if (imageUri != null) {
                    AsyncImage(model = imageUri, contentDescription = "Selected", modifier = Modifier.height(200.dp).fillMaxWidth())
                }
            }

            item {
                Text("Tone", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("funny", "formal", "romantic", "motivational", "minimal").forEach { tone ->
                        FilterChip(
                            selected = uiState.selectedTone == tone,
                            onClick = { viewModel.setTone(tone) },
                            label = { Text(tone.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            item {
                Text("Language", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en", "fa", "both").forEach { lang ->
                        FilterChip(
                            selected = uiState.selectedLanguage == lang,
                            onClick = { viewModel.setLanguage(lang) },
                            label = { Text(lang.uppercase()) }
                        )
                    }
                }
            }

            item {
                Text("Hashtags: ${uiState.hashtagCount}", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = uiState.hashtagCount.toFloat(),
                    onValueChange = { viewModel.setHashtagCount(it.toInt()) },
                    valueRange = 5f..30f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                        imageUri?.let { uri ->
                            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            viewModel.generate(file)
                        }
                    },
                    enabled = imageUri != null && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("Generate Caption")
                }
            }

            item {
                uiState.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
            }

            item {
                uiState.result?.let { result ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(result.caption, style = MaterialTheme.typography.bodyLarge)
                            Text(result.hashtags.joinToString(" "), color = MaterialTheme.colorScheme.primary)
                            OutlinedButton(onClick = {
                                clipboard.setText(AnnotatedString("${result.caption}

${result.hashtags.joinToString(" ")}"))
                            }) { Text("Copy to Clipboard") }
                        }
                    }
                }
            }

            item { Text("Recent Results", style = MaterialTheme.typography.titleMedium) }
            items(recentResults) { result ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(result.caption, style = MaterialTheme.typography.bodyMedium)
                        Text(result.hashtags.joinToString(" "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
