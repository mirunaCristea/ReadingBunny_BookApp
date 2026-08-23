package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readingbunny.data.repository.ShelfBookPositionRepository
import com.example.readingbunny.data.repository.ShelfDecorationRepository
import com.example.readingbunny.model.DecorationType
import com.example.readingbunny.model.ShelfBookPosition
import com.example.readingbunny.model.ShelfDecoration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BookshelfViewModel(
    private val decorationRepository: ShelfDecorationRepository,
    private val positionRepository: ShelfBookPositionRepository
) : ViewModel() {

    val decorations: Flow<List<ShelfDecoration>> =
        decorationRepository.getAllDecorations()

    val bookPositions: Flow<List<ShelfBookPosition>> =
        positionRepository.getAllPositions()

    fun addDecoration(
        type: DecorationType,
        shelfIndex: Int,
        slotIndex: Int
    ) {
        viewModelScope.launch {
            decorationRepository.addDecoration(
                ShelfDecoration(
                    type = type,
                    shelfIndex = shelfIndex,
                    slotIndex = slotIndex
                )
            )
        }
    }

    fun moveBook(
        bookId: Int,
        shelfIndex: Int,
        slotIndex: Int
    ) {
        viewModelScope.launch {
            positionRepository.savePosition(
                ShelfBookPosition(
                    bookId = bookId,
                    shelfIndex = shelfIndex,
                    slotIndex = slotIndex
                )
            )
        }
    }

    fun deleteDecoration(decoration: ShelfDecoration) {
        viewModelScope.launch {
            decorationRepository.deleteDecoration(decoration)
        }
    }

    fun moveDecoration(
        decoration: ShelfDecoration,
        shelfIndex: Int,
        slotIndex: Int
    ) {
        viewModelScope.launch {

            val updatedDecoration = decoration.copy(
                shelfIndex = shelfIndex,
                slotIndex = slotIndex
            )

            decorationRepository.updateDecoration(
                updatedDecoration
            )
        }
    }

    fun updateDecorationTransform(
        decoration: ShelfDecoration,
        scale: Float = decoration.scale,
        rotation: Float = decoration.rotation,
        offsetX: Float = decoration.offsetX,
        offsetY: Float = decoration.offsetY
    ) {
        viewModelScope.launch {
            decorationRepository.updateDecoration(
                decoration.copy(
                    scale = scale,
                    rotation = rotation,
                    offsetX = offsetX,
                    offsetY = offsetY
                )
            )
        }
    }

}

