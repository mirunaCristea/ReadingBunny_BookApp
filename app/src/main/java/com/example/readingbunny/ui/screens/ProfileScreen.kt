package com.example.readingbunny.ui.screens

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

@Composable
fun ProfileScreen(
    dailyGoalMinutes: Int,
    onDailyGoalChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
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
    }
}