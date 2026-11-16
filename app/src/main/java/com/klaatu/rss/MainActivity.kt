package com.klaatu.rss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.klaatu.rss.data.FeedRepository
import com.klaatu.rss.data.db.FeedEntity
import com.klaatu.rss.sync.FeedSyncWorker
import com.klaatu.rss.widget.KlaatuWidget
import com.klaatu.rss.widget.WidgetPreferences
import com.klaatu.rss.widget.WidgetThemePreset
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FeedSyncWorker.schedulePeriodic(this)
        setContent {
            MaterialTheme {
                KlaatuCompanionScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KlaatuCompanionScreen() {
    val context = LocalContext.current
    val repo = remember { FeedRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val feeds by repo.observeFeeds().collectAsState(initial = emptyList())
    val categories by repo.observeCategories().collectAsState(initial = emptyList())
    val articleCount by repo.observeTotalArticleCount().collectAsState(initial = 0)

    var selectedTheme by remember { mutableStateOf(WidgetPreferences.getTheme(context)) }
    var selectedCategory by remember { mutableStateOf(WidgetPreferences.getCategory(context)) }
    var showAddFeedDialog by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    val importOpmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val imported = context.contentResolver.openInputStream(uri)?.use { stream ->
                repo.importOpml(stream)
            } ?: 0
            isSyncing = true
            repo.syncAllFeeds()
            KlaatuWidget().updateAll(context)
            isSyncing = false
            snackbarHostState.showSnackbar("Imported $imported feeds")
        }
    }

    val exportOpmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/x-opml")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val xml = repo.exportOpml()
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(xml.toByteArray(Charsets.UTF_8))
            }
            snackbarHostState.showSnackbar("Exported OPML")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Klaatu", fontWeight = FontWeight.Bold)
                        Text(
                            "${feeds.size} feeds · $articleCount articles",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = !isSyncing,
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                val synced = repo.syncAllFeeds()
                                KlaatuWidget().updateAll(context)
                                isSyncing = false
                                snackbarHostState.showSnackbar("Synced $synced feeds")
                            }
                        },
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync feeds")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddFeedDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add feed")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // OPML Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { importOpmlLauncher.launch(arrayOf("*/*")) },
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Import OPML")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { exportOpmlLauncher.launch("klaatu-feeds.opml") },
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Export OPML")
                    }
                }
            }

            // Widget Theme Picker
            item {
                Text("Widget Theme", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    WidgetThemePreset.entries.forEach { preset ->
                        val isSelected = preset == selectedTheme
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(20.dp),
                                )
                                .clickable {
                                    selectedTheme = preset
                                    WidgetPreferences.setTheme(context, preset)
                                    scope.launch { KlaatuWidget().updateAll(context) }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(preset.headerBg)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(preset.displayName, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // Widget Category Filter
            if (categories.isNotEmpty()) {
                item {
                    Text("Widget Folder", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = {
                                selectedCategory = null
                                WidgetPreferences.setCategory(context, null)
                                scope.launch { KlaatuWidget().updateAll(context) }
                            },
                            label = { Text("All Feeds") },
                        )
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = cat
                                    WidgetPreferences.setCategory(context, cat)
                                    scope.launch { KlaatuWidget().updateAll(context) }
                                },
                                label = { Text(cat) },
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text("Subscriptions", style = MaterialTheme.typography.titleSmall)
            }

            if (feeds.isEmpty()) {
                item {
                    Text(
                        "No feeds yet. Import an OPML file or tap + to add an RSS/Atom feed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            } else {
                items(feeds, key = { it.id }) { feed ->
                    FeedRow(
                        feed = feed,
                        onDelete = {
                            scope.launch {
                                repo.deleteFeed(feed.id)
                                KlaatuWidget().updateAll(context)
                            }
                        },
                    )
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAddFeedDialog) {
        AddFeedDialog(
            onDismiss = { showAddFeedDialog = false },
            onAdd = { url, category ->
                showAddFeedDialog = false
                scope.launch {
                    val added = repo.addFeed(url, category)
                    if (added) {
                        isSyncing = true
                        repo.syncAllFeeds()
                        KlaatuWidget().updateAll(context)
                        isSyncing = false
                        snackbarHostState.showSnackbar("Feed added")
                    } else {
                        snackbarHostState.showSnackbar("Feed already exists or URL is invalid")
                    }
                }
            },
        )
    }
}

@Composable
private fun FeedRow(
    feed: FeedEntity,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feed.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${feed.category} · ${feed.xmlUrl}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove feed",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AddFeedDialog(
    onDismiss: () -> Unit,
    onAdd: (url: String, category: String) -> Unit,
) {
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add RSS / Atom Feed") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Feed URL") },
                    placeholder = { Text("https://9to5linux.com/feed") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Folder / Category (optional)") },
                    placeholder = { Text("Android + Linux") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = url.isNotBlank(),
                onClick = { onAdd(url, category) },
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
