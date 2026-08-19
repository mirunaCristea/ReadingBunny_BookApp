package com.example.readingbunny.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingStatus
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.readingbunny.ui.theme.DarkBrown
import com.example.readingbunny.ui.theme.Terracotta


@Composable
fun BookScreen(
    books: List<Book>,
    onAddBookClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedFilter by rememberSaveable {
        mutableStateOf("All")
    }

    val visibleBooks = books.filter { book ->
        val matchesSearch =
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery,ignoreCase = true)


        val matchesFilter = when (selectedFilter) {
            "Unread" -> book.status == ReadingStatus.UNREAD
            "Reading" -> book.status == ReadingStatus.READING
            "Want to Read" -> book.status == ReadingStatus.WANT_TO_READ
            "Finished" -> book.status == ReadingStatus.FINISHED
            "DNF" -> book.status == ReadingStatus.DNF

            else -> true
        }

        matchesSearch && matchesFilter

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier =  Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Books",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )

            FilledIconButton(
                onClick =onAddBookClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Terracotta
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add book",
                    tint = White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search your books")
            },

            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },

            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "All",
                    "Reading",
                    "Want to Read",
                    "Unread",
                    "Finished",
                    "DNF"

                )
            filters.forEach {  filter ->

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                    },
                    label = {
                        Text(filter)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (visibleBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No books found",
                    color = Color(0xFF74645E)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = visibleBooks,
                    key = { book -> book.id }
                ) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            onBookClick(book)
                        })
                }
            }
        }

    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    val progress = if (book.totalPages > 0) {
        book.currentPage.toFloat() / book.totalPages.toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E7)

        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment =  Alignment.CenterVertically
        )
        {
            Box(
                modifier = Modifier
                    .width(82.dp)
                    .height(118.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Terracotta),
                contentAlignment = Alignment.Center
            ) {
                if (!book.coverUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "Cover ${book.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF382B27)
                )

                Text(
                    text = book.author,
                    color = Color(0xFF74645E)
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp),
                    color = Color(0xFFB85C48),
                    trackColor = Color(0xFFE8D8CC)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${book.currentPage} / ${book.totalPages} pages · ${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    color = Color(0xFF74645E)
                )
            }
        }

    }
}