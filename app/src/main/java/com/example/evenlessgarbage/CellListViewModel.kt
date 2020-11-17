package com.example.evenlessgarbage

import android.widget.TextView
import androidx.lifecycle.ViewModel

class CellListViewModel : ViewModel() {
    var cells: MutableList<MutableList<Cell>> = mutableListOf()

    fun switchState(cell: Cell, slotTextView: TextView) {
        if (cell.living) { // switch cell to dead
            cell.living = false
        } else if (!cell.living) {
            cell.living = true
        }
    }
}