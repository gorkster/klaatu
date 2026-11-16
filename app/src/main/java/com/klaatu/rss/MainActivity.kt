package com.klaatu.rss

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.klaatu.rss.data.FeedRepository
import com.klaatu.rss.data.db.FeedEntity
import com.klaatu.rss.sync.FeedSyncWorker
import com.klaatu.rss.widget.KlaatuWidget
import com.klaatu.rss.widget.KlaatuWidgetReceiver
import com.klaatu.rss.widget.WidgetConfig
import com.klaatu.rss.widget.WidgetConfigActivity
import com.klaatu.rss.widget.WidgetPreferences
import com.klaatu.rss.widget.WidgetThemeMode
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val repo = remember { FeedRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val feeds by repo.observeFeeds().collectAsState(initial = emptyList())
    val articleCount by repo.observeTotalArticleCount().collectAsState(initial = 0)

    var activeWidgets by remember { mutableStateOf(loadActiveWidgets(context)) }
    var showAddFeedDialog by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                activeWidgets = loadActiveWidgets(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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

            // Active Widgets Section
            item {
                Text("Home Screen Widgets", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                if (activeWidgets.isEmpty()) {
                    Text(
                        "No widgets placed yet. Long-press your home screen → Widgets → Klaatu Feed to add one or more widgets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (activeWidgets.isNotEmpty()) {
                items(activeWidgets, key = { it.appWidgetId }) { widgetConfig ->
                    WidgetConfigRow(
                        config = widgetConfig,
                        onClick = {
                            val intent = Intent(context, WidgetConfigActivity::class.java).apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetConfig.appWidgetId)
                            }
                            context.startActivity(intent)
                        },
                    )
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

private fun loadActiveWidgets(context: android.content.Context): List<WidgetConfig> {
    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(ComponentName(context, KlaatuWidgetReceiver::class.java))
    return ids.map { WidgetPreferences.getConfig(context, it) }
}

@Composable
private fun WidgetConfigRow(
    config: WidgetConfig,
    onClick: () -> Unit,
) {
    val label = config.customLabel.ifBlank {
        when {
            config.categories.isEmpty() && config.feedIds.isEmpty() -> "All Feeds"
            config.categories.isNotEmpty() -> config.categories.sorted().joinToString(" + ")
            else -> "${config.feedIds.size} Feed(s)"
        }
    }
    val isDark = when (config.themeMode) {
        WidgetThemeMode.SYSTEM -> isSystemInDarkTheme()
        WidgetThemeMode.LIGHT -> false
        WidgetThemeMode.DARK -> true
    }
    val palette = config.theme.paletteFor(isDark)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(palette.headerBg),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(palette.cardBg),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Widget #${config.appWidgetId} · ${config.theme.displayName} (${config.themeMode.displayName})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Default.Settings,
            contentDescription = "Configure widget",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
