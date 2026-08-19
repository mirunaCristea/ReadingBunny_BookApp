package com.example.readingbunny.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.DecorationType
import com.example.readingbunny.model.ShelfBookPosition
import com.example.readingbunny.model.ShelfDecoration
import com.example.readingbunny.ui.theme.BookSpineColors
import com.example.readingbunny.ui.theme.ShelfWood
import com.example.readingbunny.ui.theme.SoftCream
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.readingbunny.R
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.readingbunny.model.ReadingStatus
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.draw.clip

@Composable
fun BookshelfScreen(
    books: List<Book>,
    decorations: List<ShelfDecoration>,
    onBookClick: (Book) -> Unit,
    onAddDecoration: (DecorationType, Int, Int) -> Unit,
    onDeleteDecoration: (ShelfDecoration) -> Unit,
    bookPositions: List<ShelfBookPosition>,
    onMoveBook: (Int, Int, Int) -> Unit,
    onMoveDecoration: (ShelfDecoration, Int, Int) -> Unit
) {

    var isDecorating by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedDecoration by remember {
        mutableStateOf<DecorationType?>(null)
    }

    var selectedBookId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var selectedPlacedDecorationId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val selectedPlacedDecoration =
        decorations.firstOrNull {
            it.id == selectedPlacedDecorationId
        }


    val highestUsedShelf = maxOf(
        decorations.maxOfOrNull { it.shelfIndex } ?: -1,
        bookPositions.maxOfOrNull { it.shelfIndex } ?: -1
    )

    val shelfCount = maxOf(
        3,
        (books.size + 5) / 6,
        highestUsedShelf + 1
    )

    val usedSlots = mutableSetOf<Pair<Int, Int>>()

    decorations.forEach { decoration ->
        usedSlots.add(
            decoration.shelfIndex to decoration.slotIndex
        )
    }

    bookPositions.forEach { position ->
        usedSlots.add(
            position.shelfIndex to position.slotIndex
        )
    }

    val effectiveBookPositions =
        mutableMapOf<Int, Pair<Int, Int>>()

    bookPositions.forEach { position ->
        effectiveBookPositions[position.bookId] =
            position.shelfIndex to position.slotIndex
    }

    books
        .filter { book ->
            book.id !in effectiveBookPositions
        }
        .forEach { book ->


            var foundPosition: Pair<Int, Int>? = null

            for (shelfIndex in 0 until shelfCount) {
                for (slotIndex in 0 until 6) {

                    val position = shelfIndex to slotIndex

                    if (position !in usedSlots) {
                        foundPosition = position
                        break
                    }
                }

                if (foundPosition != null) {
                    break
                }
            }

            if (foundPosition != null) {
                effectiveBookPositions[book.id] = foundPosition
                usedSlots.add(foundPosition)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "My Bookshelf",
                fontSize = 26.sp
            )

            Button(
                onClick = {
                    isDecorating = !isDecorating

                    if (!isDecorating) {
                        selectedDecoration = null
                        selectedBookId = null
                        selectedPlacedDecorationId = null
                    }
                }
            ) {
                Text(
                    text = if (isDecorating) {
                        "Done"
                    } else {
                        "Decorate"
                    }
                )
            }
        }

        if (isDecorating) {

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = DecorationType.entries
                ) { decoration ->

                    val isSelected =
                        selectedDecoration == decoration

                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .background(
                                color = if (isSelected) {
                                    ShelfWood
                                } else {
                                    SoftCream
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedDecoration = decoration
                                selectedBookId = null
                                selectedPlacedDecorationId = null
                            }
                            .padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            )
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            DecorationArtwork(
                                decoration = decoration,
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(32.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = decoration.displayName,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    selectedBookId != null ->
                        "Choose an empty slot to move the book"

                    selectedPlacedDecoration != null ->
                        "Choose an empty slot to move the decoration"

                    selectedDecoration != null ->
                        "Choose an empty slot to place ${selectedDecoration!!.displayName}"

                    else ->
                        "Choose a decoration or tap a book to move it"
                },
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (
                selectedBookId != null ||
                selectedPlacedDecorationId != null
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {
                            selectedBookId = null
                            selectedPlacedDecorationId = null
                        }
                    ) {
                        Text("Cancel")
                    }

                    if (selectedPlacedDecoration != null) {
                        Button(
                            onClick = {
                                onDeleteDecoration(
                                    selectedPlacedDecoration
                                )

                                selectedPlacedDecorationId = null
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(shelfCount) { shelfIndex ->



                val shelfDecorations =
                    decorations.filter { decoration ->
                        decoration.shelfIndex == shelfIndex
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = SoftCream,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(
                            start = 14.dp,
                            end = 14.dp,
                            top = 18.dp
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        repeat(6) { slotIndex ->

                            val book = books.firstOrNull { currentBook ->
                                effectiveBookPositions[currentBook.id] ==
                                        (shelfIndex to slotIndex)
                            }

                            val decoration = shelfDecorations.firstOrNull { decoration ->
                                decoration.slotIndex == slotIndex
                            }

                            val isEmpty =
                                book == null && decoration == null

                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(170.dp)
                                    .clickable(
                                        enabled = isDecorating && isEmpty
                                    ) {

                                        when {
                                            selectedBookId != null -> {

                                                onMoveBook(
                                                    selectedBookId!!,
                                                    shelfIndex,
                                                    slotIndex
                                                )

                                                selectedBookId = null
                                            }

                                            selectedPlacedDecoration != null -> {
                                                onMoveDecoration(
                                                    selectedPlacedDecoration,
                                                    shelfIndex,
                                                    slotIndex
                                                )

                                                selectedPlacedDecorationId = null

                                            }
                                            selectedDecoration != null -> {

                                                onAddDecoration(
                                                    selectedDecoration!!,
                                                    shelfIndex,
                                                    slotIndex
                                                )
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.BottomCenter
                            ) {

                                when {

                                    book != null -> {

                                        BookSpine(
                                            book = book,
                                            isSelected = selectedBookId == book.id,
                                            onClick = {

                                                if (isDecorating) {

                                                    selectedBookId = book.id

                                                    selectedDecoration = null
                                                    selectedPlacedDecorationId = null
                                                } else {

                                                    onBookClick(book)
                                                }
                                            }
                                        )
                                    }

                                    decoration != null -> {

                                        DecorationItem(
                                            decoration = decoration.type,
                                            isDecorating = isDecorating,
                                            isSelected =
                                            selectedPlacedDecorationId == decoration.id,
                                            onClick = {

                                                selectedPlacedDecorationId = decoration.id

                                                selectedBookId = null
                                                selectedDecoration = null
                                            }

                                        )


                                    }

                                    isDecorating &&
                                            (selectedDecoration != null ||
                                                    selectedBookId != null ||
                                                    selectedPlacedDecorationId != null ) -> {

                                        Text(
                                            text = "+",
                                            fontSize = 22.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .background(
                                color = ShelfWood,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}


@Composable
fun DecorationItem(
    decoration: DecorationType,
    isDecorating: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(85.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = SoftCream,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = isDecorating,
                onClick = onClick
            ),
        contentAlignment = Alignment.BottomCenter
    ) {

        val artworkModifier =
            when (decoration) {
                DecorationType.PLANT ->
                    Modifier
                        .width(52.dp)
                        .height(80.dp)

                DecorationType.CANDLE ->
                    Modifier
                        .width(34.dp)
                        .height(72.dp)

                DecorationType.LAMP ->
                    Modifier
                        .width(50.dp)
                        .height(72.dp)

                DecorationType.PUMPKIN ->
                    Modifier
                        .width(50.dp)
                        .height(50.dp)

                DecorationType.FRAME ->
                    Modifier
                        .width(46.dp)
                        .height(55.dp)

                DecorationType.TEA_CUP ->
                    Modifier
                        .width(56.dp)
                        .height(52.dp)
            }

        DecorationArtwork(
            decoration = decoration,
            modifier = artworkModifier
        )
    }
}


@Composable
fun BookSpine(
    book: Book,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {

    val heights = listOf(
        125.dp,
        140.dp,
        150.dp,
        135.dp
    )

    val spineHeight =
        heights[book.id % heights.size]

    val spineColor =
        BookSpineColors[
            book.id % BookSpineColors.size
        ]

    val bookmarkShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(size.width / 2f, size.height * 0.72f)
        lineTo(0f, size.height)
        close()
    }

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(spineHeight)
            .background(
                color = spineColor,
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp
                )
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = ShelfWood,
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp
                )
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = book.title,
            modifier = Modifier
                .width(spineHeight - 16.dp)
                .rotate(-90f),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        if (book.status == ReadingStatus.WANT_TO_READ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 6.dp)
                    .width(11.dp)
                    .height(24.dp)
                    .clip(bookmarkShape)
                    .background(
                        color = SoftCream
                        )
                    )

        }
    }
}

private fun DecorationType.drawableRes(): Int? {
    return when (this) {
        DecorationType.PLANT ->
            R.drawable.decoration_plant_waterfall

        DecorationType.CANDLE ->
            R.drawable.decoration_candle

        DecorationType.LAMP ->
            R.drawable.decoration_lamp

        DecorationType.PUMPKIN ->
            R.drawable.decoration_pumpkin

        DecorationType.FRAME ->
            null

        DecorationType.TEA_CUP ->
            R.drawable.decoration_tea
    }
}

@Composable
private fun DecorationArtwork(
    decoration: DecorationType,
    modifier: Modifier = Modifier
) {

    val drawableRes =
        decoration.drawableRes()

    if (drawableRes != null) {

        Image(
            painter = painterResource(
                id = drawableRes
            ),
            contentDescription =
                decoration.displayName,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

    } else {

        Text(
            text = decoration.emoji,
            fontSize = 30.sp
        )
    }
}