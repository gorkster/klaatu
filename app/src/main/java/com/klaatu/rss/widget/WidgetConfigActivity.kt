package com.klaatu.rss.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.klaatu.rss.MainActivity
import com.klaatu.rss.data.FeedRepository
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Default to CANCELED in case the user backs out of initial widget placement
        setResult(
            RESULT_CANCELED,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
        )

        val initialConfig = WidgetPreferences.getConfig(this, appWidgetId)

        setContent {
            MaterialTheme {
                WidgetConfigScreen(
                    initialConfig = initialConfig,
                    onManageFeeds = {
                        startActivity(Intent(this, MainActivity::class.java))
                    },
                    onSave = { updatedConfig ->
                        WidgetPreferences.saveConfig(this, updatedConfig)
                        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
                        KlaatuWidget().update(this, glanceId)
                        setResult(
                            RESULT_OK,
                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
                        )
                        finish()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun WidgetConfigScreen(
    initialConfig: WidgetConfig,
    onManageFeeds: () -> Unit,
    onSave: suspend (WidgetConfig) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { FeedRepository(context) }
    val scope = rememberCoroutineScope()

    val feeds by repo.observeFeeds().collectAsState(initial = emptyList())
    val categories by repo.observeCategories().collectAsState(initial = emptyList())

    var selectedTheme by remember { mutableStateOf(initialConfig.theme) }
    var selectedCategories by remember { mutableStateOf(initialConfig.categories) }
    var selectedFeedIds by remember { mutableStateOf(initialConfig.feedIds) }
    var customLabel by remember { mutableStateOf(initialConfig.customLabel) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Widget", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onManageFeeds) {
                        Text("Manage OPML")
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        isSaving = true
                        scope.launch {
                            onSave(
                                WidgetConfig(
                                    appWidgetId = initialConfig.appWidgetId,
                                    theme = selectedTheme,
                                    categories = selectedCategories,
                                    feedIds = selectedFeedIds,
                                    customLabel = customLabel,
                                )
                            )
                        }
                    },
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Widget")
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text("Widget Header Label (optional)") },
                    placeholder = {
                        val auto = if (selectedCategories.isEmpty() && selectedFeedIds.isEmpty()) {
                            "All Feeds"
                        } else {
                            selectedCategories.sorted().joinToString(" + ").ifBlank { "Custom Feeds" }
                        }
                        Text(auto)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Text("Color Theme", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
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
                                .clickable { selectedTheme = preset }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(preset.headerBg),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(preset.displayName, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            item {
                Text("OPML Folders", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Leave all unchecked for All Feeds, or combine folders.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                if (categories.isEmpty()) {
                    Text(
                        "No OPML folders yet. Tap \"Manage OPML\" above to import feeds.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        FilterChip(
                            selected = selectedCategories.isEmpty() && selectedFeedIds.isEmpty(),
                            onClick = {
                                selectedCategories = emptySet()
                                selectedFeedIds = emptySet()
                            },
                            label = { Text("All Feeds") },
                        )
                        categories.forEach { cat ->
                            val isSelected = cat in selectedCategories
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategories = if (isSelected) {
                                        selectedCategories - cat
                                    } else {
                                        selectedCategories + cat
                                    }
                                },
                                label = { Text(cat) },
                            )
                        }
                    }
                }
            }

            if (feeds.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    Text("Individual Feeds (optional)", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Include specific feeds in addition to (or instead of) folders.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                items(feeds, key = { it.id }) { feed ->
                    val checked = feed.id in selectedFeedIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFeedIds = if (checked) {
                                    selectedFeedIds - feed.id
                                } else {
                                    selectedFeedIds + feed.id
                                }
                            }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { nowChecked ->
                                selectedFeedIds = if (nowChecked) {
                                    selectedFeedIds + feed.id
                                } else {
                                    selectedFeedIds - feed.id
                                }
                            },
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feed.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = feed.category,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
