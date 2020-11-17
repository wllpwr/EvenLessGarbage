package com.example.evenlessgarbage

import android.widget.TextView
import androidx.lifecycle.ViewModel

private const val rows = 20
private const val columns = 20

class CellListViewModel : ViewModel() {
    var cells: MutableList<MutableList<Cell>> = mutableListOf()

    // put filler data in the cells 2d array
    fun fillItUp() {
        for (i in 0 until rows) {
            val something: MutableList<Cell> = mutableListOf()
            cells.add(something)
            for (j in 0 until columns) {
                something.add(Cell(i, j, false))
            }
        }
    }

    fun switchState(cell: Cell, slotTextView: TextView) {
        if (cell.living) { // switch cell to dead
            cell.living = false
        } else if (!cell.living) {
            cell.living = true
        }
    }

    // next gen
    fun updateColony(cells: MutableList<MutableList<Cell>>) {
        val livingNeighborsCount = Array(rows) { IntArray(columns) }
        for (i in 0 until rows) {
            for (j in 0 until columns) {

                val leftOfRow = i + rows - 1
                val rightOfRow = i + 1
                val leftOfColumn = j + columns - 1
                val rightOfColumn = j + 1
                if (cells[i][j].living) {
                    livingNeighborsCount[leftOfRow % rows][leftOfColumn % columns]++
                    livingNeighborsCount[leftOfRow % rows][j % columns]++
                    livingNeighborsCount[(i + rows - 1) % rows][rightOfColumn % columns]++
                    livingNeighborsCount[i % rows][leftOfColumn % columns]++
                    livingNeighborsCount[i % rows][rightOfColumn % columns]++
                    livingNeighborsCount[rightOfRow % rows][leftOfColumn % columns]++
                    livingNeighborsCount[rightOfRow % rows][j % columns]++
                    livingNeighborsCount[rightOfRow % rows][rightOfColumn % columns]++
                }

            }
        }
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                // If the cell has 4 or more living neighbors, it dies
                // by overcrowding.
                if (livingNeighborsCount[i][j] >= 4) {
                    cells[i][j].living = false
                }

                // A cell dies by exposure if it has 0 or 1 living neighbors.
                if (livingNeighborsCount[i][j] < 2) {
                    cells[i][j].living = false
                }

                // A cell is born if it has 3 living neighbors.
                if (livingNeighborsCount[i][j] == 3) {
                    cells[i][j].living = true
                }
            }
        }
    }

}