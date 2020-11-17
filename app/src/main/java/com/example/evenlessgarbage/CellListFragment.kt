package com.example.evenlessgarbage

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CellListFragment"
private const val rows = 20
private const val columns = 20


class CellListFragment : Fragment() {

    private lateinit var cellRecyclerView: RecyclerView
    private var adapter: CellAdapter? = null

    private val cellListViewModel: CellListViewModel by lazy {
        ViewModelProviders.of(this).get(CellListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cell_list, container, false)
        fillItUp()
        cellRecyclerView =
            view.findViewById(R.id.cell_recycler_view) as RecyclerView
        cellRecyclerView.layoutManager = GridLayoutManager(context, rows)
        updateUI()


        return view
    }

    private fun updateUI() {
        val cells = cellListViewModel.cells
        adapter = CellAdapter(cells)
        cellRecyclerView.adapter = adapter
    }

    private inner class CellHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var cell: Cell

        val slotTextView: TextView = itemView.findViewById(R.id.cell_pos)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(cell: Cell) {
            this.cell = cell
            slotTextView.text = this.cell.row.toString()
            pulseUI(cell, slotTextView)
        }


        override fun onClick(p0: View?) {
            cellListViewModel.switchState(cell, slotTextView)
            Toast.makeText(context, "${cell.row}, ${cell.living}!", Toast.LENGTH_SHORT).show()
            pulseUI(cell, slotTextView)

        }
    }

    private inner class CellAdapter(var cells: MutableList<MutableList<Cell>>) :
        RecyclerView.Adapter<CellHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_cell,
                parent,
                false
            )
            return CellHolder(view)
        }

        override fun onBindViewHolder(holder: CellHolder, position: Int) {
            val row = position % cells.size
            val column = position / cells.size
            holder.bind(cells[row][column])
        }

        override fun getItemCount() = rows * columns
    }

    companion object {
        fun newInstance(): CellListFragment {
            return CellListFragment()
        }
    }

    // put filler data in the cells 2d array
    private fun fillItUp() {
        for (i in 0 until rows) {
            val something: MutableList<Cell> = mutableListOf()
            cellListViewModel.cells.add(something)
            for (j in 0 until columns) {
                something.add(Cell(i, j, false))
            }
        }

    }

    // next gen
    fun updateColony(cells: MutableList<MutableList<Cell>>) {
        val livingNeighborsCount = Array(rows) { IntArray(columns) }
        updateUI()
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

    // this could probably be a lot better
    // pulses on update
    fun pulseUI(cell: Cell, slotTextView: TextView) {
        val handler = Handler()
        fun changeToOtherColorPlease(colorString: String) {
            handler.postDelayed({
                slotTextView.setBackgroundColor(Color.parseColor(colorString))
            }, 1000)
        }
        if (cell.living) {
            slotTextView.setBackgroundColor(Color.parseColor("#A5D6A7")) // green light
            changeToOtherColorPlease("#FF66BB6A") // green dark
        } else {
            slotTextView.setBackgroundColor(Color.parseColor("#EF9A9A")) // red light
            changeToOtherColorPlease("#FFEF5350") // red dark
        }
    }


}
