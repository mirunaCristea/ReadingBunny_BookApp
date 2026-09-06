package com.example.readingbunny.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.readingbunny.R
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookshelfStyle
import com.example.readingbunny.model.DecorationType
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.model.ShelfBookPosition
import com.example.readingbunny.model.ShelfDecoration
import com.example.readingbunny.ui.components.bookshelf.BookshelfTokens
import com.example.readingbunny.ui.components.bookshelf.BookshelfTop
import com.example.readingbunny.ui.components.bookshelf.ShelfLevel
import com.example.readingbunny.ui.theme.BookSpineColors
import com.example.readingbunny.ui.theme.CozyBookshelfColors
import com.example.readingbunny.ui.theme.ForestBookshelfColors
import com.example.readingbunny.ui.theme.NightBookshelfColors


@Composable
fun BookshelfScreen(
    books: List<Book>,
    decorations: List<ShelfDecoration>,
    onBookClick: (Book) -> Unit,
    onAddDecoration: (DecorationType, Int, Int) -> Unit,
    onDeleteDecoration: (ShelfDecoration) -> Unit,
    bookPositions: List<ShelfBookPosition>,
    onMoveBook: (Int, Int, Int) -> Unit,
    onMoveDecoration: (ShelfDecoration, Int, Int) -> Unit,
    onUpdateDecorationTransform: (
        ShelfDecoration,
        Float,
        Float,
        Float,
        Float
    ) -> Unit,
    bookshelfStyle: BookshelfStyle,
    onBookshelfStyleChange: (BookshelfStyle) -> Unit,
) {

    var isDecorating by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedDecoration by remember {
        mutableStateOf<DecorationType?>(null)
    }

    var selectedPlacedDecorationId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val selectedPlacedDecoration =
        decorations.firstOrNull {
            it.id == selectedPlacedDecorationId
        }

    var draggingBookId by remember {
        mutableStateOf<Int?>(null)
    }

    var draggingDecorationId by remember {
        mutableStateOf<Int?>(null)
    }

    var dragOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    var dragStartCenter by remember {
        mutableStateOf<Offset?>(null)
    }

    val slotBounds = remember {
        mutableMapOf<Pair<Int, Int>, Rect>()
    }

    /*
     * Highest shelf that is already explicitly used.
     */
    val highestUsedShelf = maxOf(
        decorations.maxOfOrNull {
            it.shelfIndex
        } ?: -1,
        bookPositions.maxOfOrNull {
            it.shelfIndex
        } ?: -1
    )

    /*
     * Minimum 4 shelves so the bookshelf visually fills
     * most phone screens.
     *
     * Books + decorations are counted because both occupy
     * shelf slots.
     */
    val shelfCount = maxOf(
        4,
        (books.size + decorations.size + 5) / 6,
        highestUsedShelf + 1
    )

    /*
     * Slots already occupied by persisted objects.
     */
    val usedSlots =
        mutableSetOf<Pair<Int, Int>>()

    decorations.forEach { decoration ->
        usedSlots.add(
            decoration.shelfIndex to
                    decoration.slotIndex
        )
    }

    bookPositions.forEach { position ->
        usedSlots.add(
            position.shelfIndex to
                    position.slotIndex
        )
    }

    /*
     * Actual position used to render each book.
     *
     * Books which do not have a persisted position yet
     * receive the first available slot.
     */
    val effectiveBookPositions =
        mutableMapOf<Int, Pair<Int, Int>>()

    bookPositions.forEach { position ->
        effectiveBookPositions[position.bookId] =
            position.shelfIndex to
                    position.slotIndex
    }

    books
        .filter { book ->
            book.id !in effectiveBookPositions
        }
        .forEach { book ->

            var foundPosition:
                    Pair<Int, Int>? = null

            for (
            shelfIndex in
            0 until shelfCount
            ) {

                for (
                slotIndex in
                0 until 6
                ) {

                    val position =
                        shelfIndex to slotIndex

                    if (
                        position !in usedSlots
                    ) {
                        foundPosition =
                            position
                        break
                    }
                }

                if (
                    foundPosition != null
                ) {
                    break
                }
            }

            if (
                foundPosition != null
            ) {
                effectiveBookPositions[
                    book.id
                ] = foundPosition

                usedSlots.add(
                    foundPosition
                )
            }
        }

    val draggingBookShelfIndex =
        draggingBookId?.let { bookId ->
            effectiveBookPositions[
                bookId
            ]?.first
        }

    val draggingDecorationShelfIndex =
        draggingDecorationId?.let {
                decorationId ->

            decorations
                .firstOrNull {
                    it.id ==
                            decorationId
                }
                ?.shelfIndex
        }

    /*
     * Current bookshelf colors.
     */
    val shelfBackgroundColor =
        when (bookshelfStyle) {

            BookshelfStyle.COZY ->
                CozyBookshelfColors
                    .shelfBackground

            BookshelfStyle.FOREST ->
                ForestBookshelfColors
                    .shelfBackground

            BookshelfStyle.NIGHT ->
                NightBookshelfColors
                    .shelfBackground
        }

    val shelfWoodColor =
        when (bookshelfStyle) {

            BookshelfStyle.COZY ->
                CozyBookshelfColors.wood

            BookshelfStyle.FOREST ->
                ForestBookshelfColors.wood

            BookshelfStyle.NIGHT ->
                NightBookshelfColors.wood
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme
                    .colorScheme
                    .background
            )
    ) {

        /*
         * HEADER
         *
         * Header has its own padding.
         * The bookshelf underneath remains edge-to-edge.
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "My bookshelf",
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Button(
                onClick = {

                    isDecorating =
                        !isDecorating

                    if (!isDecorating) {

                        selectedDecoration =
                            null

                        selectedPlacedDecorationId =
                            null
                    }
                }
            ) {

                Text(
                    text =
                        if (isDecorating) {
                            "Done"
                        } else {
                            "Decorate"
                        }
                )
            }
        }

        /*
         * EMPTY STATE
         */
        if (
            books.isEmpty() &&
            !isDecorating
        ) {

            Text(
                text =
                    "Your bookshelf is waiting for its first book.",
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 4.dp
                ),
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }

        /*
         * DECORATION MODE CONTROLS
         */
        if (isDecorating) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Bookshelf style",
                    style =
                        MaterialTheme
                            .typography
                            .titleSmall
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    BookshelfStyle
                        .entries
                        .forEach { style ->

                            FilterChip(
                                selected =
                                    bookshelfStyle ==
                                            style,
                                onClick = {
                                    onBookshelfStyleChange(
                                        style
                                    )
                                },
                                label = {
                                    Text(
                                        style.displayName
                                    )
                                }
                            )
                        }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                LazyRow(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    items(
                        items =
                            DecorationType.entries
                    ) { decoration ->

                        val isSelected =
                            selectedDecoration ==
                                    decoration

                        Box(
                            modifier =
                                Modifier
                                    .width(90.dp)
                                    .background(
                                        color =
                                            if (
                                                isSelected
                                            ) {
                                                shelfWoodColor
                                            } else {
                                                MaterialTheme
                                                    .colorScheme
                                                    .surfaceVariant
                                            },
                                        shape =
                                            RoundedCornerShape(
                                                12.dp
                                            )
                                    )
                                    .clickable {

                                        selectedDecoration =
                                            if (
                                                selectedDecoration ==
                                                decoration
                                            ) {
                                                null
                                            } else {
                                                decoration
                                            }

                                        selectedPlacedDecorationId =
                                            null
                                    }
                                    .padding(
                                        horizontal =
                                            8.dp,
                                        vertical =
                                            8.dp
                                    )
                        ) {

                            Column(
                                horizontalAlignment =
                                    Alignment
                                        .CenterHorizontally,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                            ) {

                                DecorationArtwork(
                                    decoration =
                                        decoration,
                                    modifier =
                                        Modifier
                                            .width(
                                                32.dp
                                            )
                                            .height(
                                                32.dp
                                            )
                                )

                                Spacer(
                                    modifier =
                                        Modifier
                                            .height(
                                                4.dp
                                            )
                                )

                                Text(
                                    text =
                                        decoration
                                            .displayName,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    color =
                                        if (
                                            isSelected
                                        ) {
                                            MaterialTheme
                                                .colorScheme
                                                .onPrimary
                                        } else {
                                            MaterialTheme
                                                .colorScheme
                                                .onSurface
                                        },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        when {

                            selectedPlacedDecoration !=
                                    null ->

                                "Decoration selected"

                            selectedDecoration !=
                                    null ->

                                "Choose an empty slot to place ${selectedDecoration!!.displayName}"

                            draggingBookId !=
                                    null ->

                                "Drag the book to an empty slot"

                            draggingDecorationId !=
                                    null ->

                                "Drag the decoration to an empty slot"

                            else ->

                                "Choose a decoration or drag an item to move it"
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                if (
                    selectedPlacedDecoration !=
                    null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )

                    Row(
                        verticalAlignment =
                            Alignment
                                .CenterVertically,
                        horizontalArrangement =
                            Arrangement
                                .spacedBy(
                                    12.dp
                                )
                    ) {

                        Button(
                            onClick = {

                                val newScale =
                                    (
                                            selectedPlacedDecoration
                                                .scale -
                                                    0.1f
                                            )
                                        .coerceAtLeast(
                                            0.6f
                                        )

                                onUpdateDecorationTransform(
                                    selectedPlacedDecoration,
                                    newScale,
                                    selectedPlacedDecoration
                                        .rotation,
                                    selectedPlacedDecoration
                                        .offsetX,
                                    selectedPlacedDecoration
                                        .offsetY
                                )
                            }
                        ) {
                            Text("-")
                        }

                        Text(
                            text =
                                "${
                                    (
                                            selectedPlacedDecoration
                                                .scale *
                                                    100
                                            ).toInt()
                                }%"
                        )

                        Button(
                            onClick = {

                                val newScale =
                                    (
                                            selectedPlacedDecoration
                                                .scale +
                                                    0.1f
                                            )
                                        .coerceAtMost(
                                            1.5f
                                        )

                                onUpdateDecorationTransform(
                                    selectedPlacedDecoration,
                                    newScale,
                                    selectedPlacedDecoration
                                        .rotation,
                                    selectedPlacedDecoration
                                        .offsetX,
                                    selectedPlacedDecoration
                                        .offsetY
                                )
                            }
                        ) {
                            Text("+")
                        }
                    }

                    TextButton(
                        onClick = {

                            onUpdateDecorationTransform(
                                selectedPlacedDecoration,
                                1f,
                                selectedPlacedDecoration
                                    .rotation,
                                selectedPlacedDecoration
                                    .offsetX,
                                selectedPlacedDecoration
                                    .offsetY
                            )
                        }
                    ) {
                        Text(
                            "Reset size"
                        )
                    }

                    Button(
                        onClick = {

                            onDeleteDecoration(
                                selectedPlacedDecoration
                            )

                            selectedPlacedDecorationId =
                                null
                        }
                    ) {
                        Text(
                            "Delete decoration"
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        /*
         * BOOKSHELF
         *
         * No horizontal padding.
         * The bookshelf itself fills the screen.
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    shelfBackgroundColor
                )
        ) {

            /*
             * Top wooden border.
             */
            item {

                BookshelfTop(
                    color =
                        shelfWoodColor
                )
            }

            /*
             * Repeated shelf levels.
             */
            items(
                count = shelfCount
            ) { shelfIndex ->

                val shelfDecorations =
                    decorations.filter {
                            decoration ->

                        decoration
                            .shelfIndex ==
                                shelfIndex
                    }

                ShelfLevel(
                    woodColor =
                        shelfWoodColor,
                    backgroundColor =
                        shelfBackgroundColor,
                    modifier =
                        Modifier.zIndex(
                            if (
                                draggingBookShelfIndex ==
                                shelfIndex ||
                                draggingDecorationShelfIndex ==
                                shelfIndex
                            ) {
                                10f
                            } else {
                                0f
                            }
                        )
                ) {

                    /*
                     * The content area ends before
                     * the wooden plank.
                     */
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    BookshelfTokens
                                        .LevelHeight -
                                            BookshelfTokens
                                                .ShelfHeight
                                )
                                .padding(
                                    start =
                                        BookshelfTokens
                                            .ContentHorizontalPadding,
                                    end =
                                        BookshelfTokens
                                            .ContentHorizontalPadding,
                                    top =
                                        BookshelfTokens
                                            .ContentTopPadding
                                ),
                        verticalAlignment =
                            Alignment.Bottom,
                        horizontalArrangement =
                            Arrangement
                                .spacedBy(
                                    5.dp
                                )
                    ) {

                        /*
                         * Six equal slots per shelf.
                         */
                        repeat(6) { slotIndex ->

                            val slotPosition =
                                shelfIndex to
                                        slotIndex

                            androidx.compose.runtime
                                .DisposableEffect(
                                    slotPosition
                                ) {

                                    onDispose {
                                        slotBounds.remove(
                                            slotPosition
                                        )
                                    }
                                }

                            val book =
                                books.firstOrNull {
                                        currentBook ->

                                    effectiveBookPositions[
                                        currentBook.id
                                    ] ==
                                            slotPosition
                                }

                            val decoration =
                                shelfDecorations
                                    .firstOrNull {
                                            currentDecoration ->

                                        currentDecoration
                                            .slotIndex ==
                                                slotIndex
                                    }

                            val isEmpty =
                                book == null &&
                                        decoration == null

                            val isDraggingSomething =
                                draggingBookId !=
                                        null ||
                                        draggingDecorationId !=
                                        null

                            val isAvailableDropTarget =
                                isDraggingSomething &&
                                        isEmpty

                            /*
                             * IMPORTANT:
                             *
                             * weight(1f) gives all six slots
                             * equal width.
                             */
                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(
                                            color =
                                                if (
                                                    isAvailableDropTarget
                                                ) {
                                                    MaterialTheme
                                                        .colorScheme
                                                        .primary
                                                        .copy(
                                                            alpha =
                                                                0.08f
                                                        )
                                                } else {
                                                    MaterialTheme
                                                        .colorScheme
                                                        .surfaceVariant
                                                        .copy(
                                                            alpha =
                                                                0f
                                                        )
                                                },
                                            shape =
                                                RoundedCornerShape(
                                                    8.dp
                                                )
                                        )
                                        .onGloballyPositioned {
                                                coordinates ->

                                            slotBounds[
                                                slotPosition
                                            ] =
                                                coordinates
                                                    .boundsInRoot()
                                        }
                                        .clickable(
                                            enabled =
                                                isDecorating &&
                                                        isEmpty &&
                                                        selectedDecoration !=
                                                        null
                                        ) {

                                            selectedDecoration
                                                ?.let {
                                                        decorationType ->

                                                    onAddDecoration(
                                                        decorationType,
                                                        shelfIndex,
                                                        slotIndex
                                                    )
                                                }
                                        },
                                contentAlignment =
                                    Alignment
                                        .BottomCenter
                            ) {

                                when {

                                    /*
                                     * BOOK
                                     */
                                    book != null -> {

                                        val isDragging =
                                            draggingBookId ==
                                                    book.id

                                        BookSpine(
                                            book =
                                                book,
                                            modifier =
                                                Modifier
                                                    .zIndex(
                                                        if (
                                                            isDragging
                                                        ) {
                                                            1f
                                                        } else {
                                                            0f
                                                        }
                                                    )
                                                    .graphicsLayer {

                                                        if (
                                                            isDragging
                                                        ) {

                                                            translationX =
                                                                dragOffset.x

                                                            translationY =
                                                                dragOffset.y

                                                            scaleX =
                                                                1.05f

                                                            scaleY =
                                                                1.05f

                                                            alpha =
                                                                0.9f
                                                        }
                                                    }
                                                    .pointerInput(
                                                        isDecorating,
                                                        book.id,
                                                        bookPositions,
                                                        decorations
                                                    ) {

                                                        if (
                                                            isDecorating
                                                        ) {

                                                            detectDragGesturesAfterLongPress(

                                                                onDragStart = {

                                                                    draggingBookId =
                                                                        book.id

                                                                    draggingDecorationId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        slotBounds[
                                                                            slotPosition
                                                                        ]?.center

                                                                    selectedDecoration =
                                                                        null

                                                                    selectedPlacedDecorationId =
                                                                        null
                                                                },

                                                                onDrag = {
                                                                        change,
                                                                        dragAmount ->

                                                                    change.consume()

                                                                    dragOffset +=
                                                                        dragAmount
                                                                },

                                                                onDragCancel = {

                                                                    draggingBookId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        null
                                                                },

                                                                onDragEnd = {

                                                                    val startCenter =
                                                                        dragStartCenter

                                                                    if (
                                                                        startCenter !=
                                                                        null
                                                                    ) {

                                                                        val dropPoint =
                                                                            startCenter +
                                                                                    dragOffset

                                                                        val targetSlot =
                                                                            slotBounds
                                                                                .entries
                                                                                .firstOrNull {
                                                                                        entry ->

                                                                                    entry
                                                                                        .value
                                                                                        .contains(
                                                                                            dropPoint
                                                                                        )
                                                                                }
                                                                                ?.key

                                                                        if (
                                                                            targetSlot !=
                                                                            null
                                                                        ) {

                                                                            val targetShelfIndex =
                                                                                targetSlot
                                                                                    .first

                                                                            val targetSlotIndex =
                                                                                targetSlot
                                                                                    .second

                                                                            val targetBook =
                                                                                books
                                                                                    .firstOrNull {
                                                                                            currentBook ->

                                                                                        effectiveBookPositions[
                                                                                            currentBook.id
                                                                                        ] ==
                                                                                                targetSlot
                                                                                    }

                                                                            val targetDecoration =
                                                                                decorations
                                                                                    .firstOrNull {
                                                                                            currentDecoration ->

                                                                                        currentDecoration
                                                                                            .shelfIndex ==
                                                                                                targetShelfIndex &&
                                                                                                currentDecoration
                                                                                                    .slotIndex ==
                                                                                                targetSlotIndex
                                                                                    }

                                                                            val isTargetFree =
                                                                                (
                                                                                        targetBook ==
                                                                                                null ||
                                                                                                targetBook.id ==
                                                                                                book.id
                                                                                        ) &&
                                                                                        targetDecoration ==
                                                                                        null

                                                                            if (
                                                                                isTargetFree
                                                                            ) {

                                                                                onMoveBook(
                                                                                    book.id,
                                                                                    targetShelfIndex,
                                                                                    targetSlotIndex
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    draggingBookId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        null
                                                                }
                                                            )
                                                        }
                                                    },
                                            onClick = {

                                                if (
                                                    !isDecorating
                                                ) {
                                                    onBookClick(
                                                        book
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    /*
                                     * DECORATION
                                     */
                                    decoration != null -> {

                                        val isDraggingDecoration =
                                            draggingDecorationId ==
                                                    decoration.id

                                        DecorationItem(
                                            decoration =
                                                decoration,
                                            isDecorating =
                                                isDecorating,
                                            modifier =
                                                Modifier
                                                    .zIndex(
                                                        if (
                                                            isDraggingDecoration
                                                        ) {
                                                            1f
                                                        } else {
                                                            0f
                                                        }
                                                    )
                                                    .graphicsLayer {

                                                        if (
                                                            isDraggingDecoration
                                                        ) {

                                                            translationX =
                                                                dragOffset.x

                                                            translationY =
                                                                dragOffset.y

                                                            scaleX =
                                                                1.08f

                                                            scaleY =
                                                                1.08f

                                                            alpha =
                                                                0.9f
                                                        }
                                                    }
                                                    .pointerInput(
                                                        isDecorating,
                                                        decoration.id,
                                                        bookPositions,
                                                        decorations
                                                    ) {

                                                        if (
                                                            isDecorating
                                                        ) {

                                                            detectDragGesturesAfterLongPress(

                                                                onDragStart = {

                                                                    draggingDecorationId =
                                                                        decoration.id

                                                                    draggingBookId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        slotBounds[
                                                                            slotPosition
                                                                        ]?.center

                                                                    selectedPlacedDecorationId =
                                                                        null

                                                                    selectedDecoration =
                                                                        null
                                                                },

                                                                onDrag = {
                                                                        change,
                                                                        dragAmount ->

                                                                    change.consume()

                                                                    dragOffset +=
                                                                        dragAmount
                                                                },

                                                                onDragCancel = {

                                                                    draggingDecorationId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        null
                                                                },

                                                                onDragEnd = {

                                                                    val startCenter =
                                                                        dragStartCenter

                                                                    if (
                                                                        startCenter !=
                                                                        null
                                                                    ) {

                                                                        val dropPoint =
                                                                            startCenter +
                                                                                    dragOffset

                                                                        val targetSlot =
                                                                            slotBounds
                                                                                .entries
                                                                                .firstOrNull {
                                                                                        entry ->

                                                                                    entry
                                                                                        .value
                                                                                        .contains(
                                                                                            dropPoint
                                                                                        )
                                                                                }
                                                                                ?.key

                                                                        if (
                                                                            targetSlot !=
                                                                            null
                                                                        ) {

                                                                            val targetShelfIndex =
                                                                                targetSlot
                                                                                    .first

                                                                            val targetSlotIndex =
                                                                                targetSlot
                                                                                    .second

                                                                            val targetBook =
                                                                                books
                                                                                    .firstOrNull {
                                                                                            currentBook ->

                                                                                        effectiveBookPositions[
                                                                                            currentBook.id
                                                                                        ] ==
                                                                                                targetSlot
                                                                                    }

                                                                            val targetDecoration =
                                                                                decorations
                                                                                    .firstOrNull {
                                                                                            currentDecoration ->

                                                                                        currentDecoration
                                                                                            .shelfIndex ==
                                                                                                targetShelfIndex &&
                                                                                                currentDecoration
                                                                                                    .slotIndex ==
                                                                                                targetSlotIndex
                                                                                    }

                                                                            val isTargetFree =
                                                                                targetBook ==
                                                                                        null &&
                                                                                        (
                                                                                                targetDecoration ==
                                                                                                        null ||
                                                                                                        targetDecoration.id ==
                                                                                                        decoration.id
                                                                                                )

                                                                            if (
                                                                                isTargetFree
                                                                            ) {

                                                                                onMoveDecoration(
                                                                                    decoration,
                                                                                    targetShelfIndex,
                                                                                    targetSlotIndex
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    draggingDecorationId =
                                                                        null

                                                                    dragOffset =
                                                                        Offset.Zero

                                                                    dragStartCenter =
                                                                        null
                                                                }
                                                            )
                                                        }
                                                    },
                                            onClick = {

                                                if (
                                                    isDecorating
                                                ) {

                                                    selectedPlacedDecorationId =
                                                        if (
                                                            selectedPlacedDecorationId ==
                                                            decoration.id
                                                        ) {
                                                            null
                                                        } else {
                                                            decoration.id
                                                        }

                                                    selectedDecoration =
                                                        null
                                                }
                                            }
                                        )
                                    }

                                    /*
                                     * EMPTY DROP TARGET
                                     */
                                    isAvailableDropTarget -> {

                                        Text(
                                            text = "+",
                                            fontSize =
                                                22.sp,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .primary
                                                    .copy(
                                                        alpha =
                                                            0.7f
                                                    )
                                        )
                                    }

                                    /*
                                     * EMPTY SLOT WHILE
                                     * PLACING DECORATION
                                     */
                                    isDecorating &&
                                            selectedDecoration !=
                                            null -> {

                                        Text(
                                            text = "+",
                                            fontSize =
                                                22.sp,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DecorationItem(
    decoration: ShelfDecoration,
    isDecorating: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
            .graphicsLayer {
                scaleX =
                    decoration.scale

                scaleY =
                    decoration.scale
            }
            .clickable(
                enabled =
                    isDecorating,
                onClick =
                    onClick
            ),
        contentAlignment =
            Alignment.BottomCenter
    ) {

        val artworkModifier =
            when (decoration.type) {

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
            modifier =
                artworkModifier,
            decoration =
                decoration.type
        )
    }
}


@Composable
fun BookSpine(
    book: Book,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val heights =
        listOf(
            125.dp,
            140.dp,
            150.dp,
            135.dp
        )

    val spineHeight =
        heights[
            book.id %
                    heights.size
        ]

    val spineColor =
        BookSpineColors[
            book.id %
                    BookSpineColors.size
        ]

    val bookmarkShape =
        GenericShape { size, _ ->

            moveTo(
                0f,
                0f
            )

            lineTo(
                size.width,
                0f
            )

            lineTo(
                size.width,
                size.height
            )

            lineTo(
                size.width / 2f,
                size.height *
                        0.72f
            )

            lineTo(
                0f,
                size.height
            )

            close()
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                spineHeight
            )
            .background(
                color =
                    spineColor,
                shape =
                    RoundedCornerShape(
                        topStart =
                            8.dp,
                        topEnd =
                            8.dp
                    )
            )
            .clickable(
                onClick =
                    onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                book.title,
            modifier =
                Modifier
                    .width(
                        spineHeight -
                                16.dp
                    )
                    .rotate(
                        -90f
                    ),
            maxLines = 1,
            softWrap = false,
            overflow =
                TextOverflow
                    .Ellipsis,
            textAlign =
                TextAlign.Center,
            fontSize =
                14.sp,
            color =
                MaterialTheme
                    .colorScheme
                    .onPrimary
        )

        if (
            book.status ==
            ReadingStatus.WANT_TO_READ
        ) {

            Box(
                modifier =
                    Modifier
                        .align(
                            Alignment
                                .TopEnd
                        )
                        .padding(
                            end =
                                6.dp
                        )
                        .width(
                            11.dp
                        )
                        .height(
                            24.dp
                        )
                        .clip(
                            bookmarkShape
                        )
                        .background(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
            )
        }
    }
}


private fun DecorationType.drawableRes():
        Int? {

    return when (this) {

        DecorationType.PLANT ->
            R.drawable
                .decoration_plant_waterfall

        DecorationType.CANDLE ->
            R.drawable
                .decoration_candle

        DecorationType.LAMP ->
            R.drawable
                .decoration_lamp

        DecorationType.PUMPKIN ->
            R.drawable
                .decoration_pumpkin

        DecorationType.FRAME ->
            null

        DecorationType.TEA_CUP ->
            R.drawable
                .decoration_tea
    }
}


@Composable
private fun DecorationArtwork(
    decoration: DecorationType,
    modifier: Modifier = Modifier
) {

    val drawableRes =
        decoration.drawableRes()

    if (
        drawableRes != null
    ) {

        Image(
            painter =
                painterResource(
                    id =
                        drawableRes
                ),
            contentDescription =
                decoration
                    .displayName,
            modifier =
                modifier,
            contentScale =
                ContentScale.Fit
        )

    } else {

        Text(
            text =
                decoration.emoji,
            fontSize =
                30.sp
        )
    }
}