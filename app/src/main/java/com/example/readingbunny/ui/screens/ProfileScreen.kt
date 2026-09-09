package com.example.readingbunny.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.readingbunny.R

@Composable
fun ProfileScreen(
    dailyGoalMinutes: Int,
    totalBooks: Int,
    currentlyReadingBooks: Int,
    finishedBooks: Int,
    onDailyGoalChange: (Int) -> Unit,
    onExportBackup: (Uri) -> Unit,
    backupMessage: String?,
    onRestoreBackup: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val backupDefaultFilename =
        stringResource(R.string.backup_default_filename)
    var pendingRestoreUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val restoreBackupLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                pendingRestoreUri = uri
            }
        }

    val exportBackupLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(
                "application/json"
            )
        ) { uri ->
            if (uri != null) {
                onExportBackup(uri)
            }
        }

    pendingRestoreUri?.let { uri ->
        AlertDialog(
            onDismissRequest = {
                pendingRestoreUri = null
            },
            title = {
                Text(
                    stringResource(R.string.restore_backup_question)
                )
            },
            text = {
                Text(
                    stringResource(R.string.restore_backup_warning)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingRestoreUri = null
                        onRestoreBackup(uri)
                    }
                ) {
                    Text(
                        stringResource(R.string.restore_action)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingRestoreUri = null
                    }
                ) {
                    Text(
                        stringResource(R.string.cancel_action)
                    )
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.daily_reading_goal),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(
                R.string.minutes_per_day,
                dailyGoalMinutes
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                15,
                30,
                45,
                60
            ).forEach { minutes ->
                FilterChip(
                    selected = dailyGoalMinutes == minutes,
                    onClick = {
                        onDailyGoalChange(minutes)
                    },
                    label = {
                        Text(
                            stringResource(
                                R.string.minutes_short,
                                minutes
                            )
                        )
                    }
                )
            }
        }

        Text(
            text = stringResource(R.string.my_library),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatCard(
                value = totalBooks.toString(),
                label = stringResource(R.string.books_label),
                modifier = Modifier.weight(1f)
            )

            ProfileStatCard(
                value = currentlyReadingBooks.toString(),
                label = stringResource(R.string.reading_label),
                modifier = Modifier.weight(1f)
            )

            ProfileStatCard(
                value = finishedBooks.toString(),
                label = stringResource(R.string.finished_label),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.data_backup),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.data_backup_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = {
                    exportBackupLauncher.launch(
                        backupDefaultFilename
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.export_backup)
                )
            }

            OutlinedButton(
                onClick = {
                    restoreBackupLauncher.launch(
                        arrayOf(
                            "application/json"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.restore_backup)
                )
            }

            backupMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}