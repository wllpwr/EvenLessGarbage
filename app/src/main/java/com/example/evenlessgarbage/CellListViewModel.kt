package com.example.evenlessgarbage

import androidx.lifecycle.ViewModel

class CellListViewModel : ViewModel() {

    var cells = mutableListOf<Cell>()

    init {
        for (i in 0 until 400) {
            val cell = Cell()
            cell.slot = i
            cells = (cells + cell) as MutableList<Cell>
        }
    }
}