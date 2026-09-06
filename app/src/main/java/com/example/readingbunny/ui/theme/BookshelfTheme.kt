package com.example.readingbunny.ui.theme

import androidx.compose.ui.graphics.Color

data class BookshelfColors(
    val background: Color,
    val shelfBackground: Color,

    val wood: Color,
    val woodHighlight: Color,
    val woodShadow: Color,
    val woodOuter: Color,
    val woodInner: Color,

    val innerShelfShadow: Color
)
val CozyBookshelfColors = BookshelfColors(
    background = BookshelfCozyBackground,
    shelfBackground = BookshelfCozyShelfBackground,

    wood = BookshelfCozyWood,
    woodHighlight = BookshelfCozyWoodHighlight,
    woodShadow = BookshelfCozyWoodShadow,
    woodOuter = BookshelfCozyWoodOuter,
    woodInner = BookshelfCozyWoodInner,

    innerShelfShadow = BookshelfCozyInnerShadow
)

val ForestBookshelfColors = BookshelfColors(
    background = BookshelfForestBackground,
    shelfBackground = BookshelfForestShelfBackground,

    wood = BookshelfForestWood,
    woodHighlight = BookshelfForestWoodHighlight,
    woodShadow = BookshelfForestWoodShadow,
    woodOuter = BookshelfForestWoodOuter,
    woodInner = BookshelfForestWoodInner,

    innerShelfShadow = BookshelfForestInnerShadow
)

val NightBookshelfColors = BookshelfColors(
    background = BookshelfNightBackground,
    shelfBackground = BookshelfNightShelfBackground,

    wood = BookshelfNightWood,
    woodHighlight = BookshelfNightWoodHighlight,
    woodShadow = BookshelfNightWoodShadow,
    woodOuter = BookshelfNightWoodOuter,
    woodInner = BookshelfNightWoodInner,

    innerShelfShadow = BookshelfNightInnerShadow
)