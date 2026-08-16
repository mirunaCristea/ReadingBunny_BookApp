package com.example.readingbunny.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog

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
    var pendingRestoreUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val restoreBackupLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {
                pendingRestoreUri = uri
            }
        }
    val exportBackupLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.CreateDocument(
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
                Text("Restore backup?")
            },

            text = {
                Text(
                    "Your current books, reading sessions, " +
                            "shelf layout and reading goal will be replaced."
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        pendingRestoreUri = null

                        onRestoreBackup(uri)
                    }
                ) {
                    Text("Restore")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        pendingRestoreUri = null
                    }
                ) {
                    Text("Cancel")
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
            text = "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Daily reading goal",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$dailyGoalMinutes minutes per day"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                15,
                30,
                45,
                60
            ).forEach { minutes ->

                FilterChip(
                    selected =
                        dailyGoalMinutes == minutes,

                    onClick = {
                        onDailyGoalChange(minutes)
                    },

                    label = {
                        Text("$minutes min")
                    }
                )
            }
        }

        Text(
            text = "My library",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            ProfileStatCard(
                value = totalBooks.toString(),
                label = "Books",
                modifier = Modifier.weight(1f)
            )

            ProfileStatCard(
                value = currentlyReadingBooks.toString(),
                label = "Reading",
                modifier = Modifier.weight(1f)
            )

            ProfileStatCard(
                value = finishedBooks.toString(),
                label = "Finished",
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFF3E7),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "Data & backup",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Export or restore your ReadingBunny data",
                fontSize = 13.sp,
                color = Color(0xFF74645E)
            )
            Button(
                onClick = {

                    exportBackupLauncher.launch(
                        "readingbunny-backup.json"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Export backup")

                backupMessage?.let { message ->

                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = Color(0xFF74645E)
                    )


                }
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

                Text("Restore backup")
            }

            backupMessage?.let { message ->

                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color(0xFF74645E)
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
                color = Color(0xFFFFF3E7),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {

        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF74645E)
        )
    }
}