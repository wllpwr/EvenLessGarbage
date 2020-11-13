package com.example.evenlessgarbage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "CellListFragment"

class CellListFragment : Fragment() {

    private lateinit var cellRecyclerView: RecyclerView
    private var adapter: CellAdapter? = null

    private val cellListViewModel: CellListViewModel by lazy {
        ViewModelProviders.of(this).get(CellListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total cells: ${cellListViewModel.cells.size}")


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cell_list, container, false)
        cellRecyclerView =
            view.findViewById(R.id.cell_recycler_view) as RecyclerView
        cellRecyclerView.layoutManager = GridLayoutManager(context, 20)
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
            slotTextView.text = this.cell.slot.toString()
        }

        override fun onClick(p0: View?) {
            switchState(cell, slotTextView)
            Toast.makeText(context, "${cell.slot}, ${cell.living}!", Toast.LENGTH_SHORT).show()
            updateColony(cellListViewModel.cells)
        }
    }

    private inner class CellAdapter(var cells: List<Cell>) : RecyclerView.Adapter<CellHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_cell,
                parent,
                false
            )
            return CellHolder(view)
        }

        override fun onBindViewHolder(holder: CellHolder, position: Int) {
            val cell = cells[position]
            holder.bind(cell)
        }

        override fun getItemCount() = 400
    }

    companion object {
        fun newInstance(): CellListFragment {
            return CellListFragment()
        }
    }

    private fun switchState(cell: Cell, slotTextView: TextView) {
        if (cell.living) {
            slotTextView.setBackgroundColor(Color.parseColor("#FFEF5350"))
            cell.living = false
        } else {
            slotTextView.setBackgroundColor(Color.parseColor("#FF66BB6A"))
            cell.living = true
        }
    }

    private fun updateColony(cells: List<Cell>) {
        val livingNeighborsCount = IntArray(400) { 0 }

        for (i in 0 until 400) {
            val left = i - 1
            val right = i + 1
            val up = i - 20
            val down = i + 20
            val upright = up + 1
            val upleft = up - 1
            val downleft = down - 1
            val downright = down + 1
            //handles out of bounds issues
            /*for (j in livingNeighborsCount){

                if (left){
                    livingNeighborsCount[j - 400]
                }
                else if (livingNeighborsCount[j] < 0){
                    livingNeighborsCount[j + 400]
                }
            }
            */
            val allDirections = arrayOf(livingNeighborsCount[left], livingNeighborsCount[upleft],
                livingNeighborsCount[up], livingNeighborsCount[upright], livingNeighborsCount[right],
                livingNeighborsCount[downleft], livingNeighborsCount[down], livingNeighborsCount[downright])

            // TODO: Goes out of bounds. Need to fix.
            if (cells[i].living) {
                for (a in allDirections){
                    allDirections[a]++
                }
            }
        }
        for (i in 0 until 400) {
            // If the cell has 4 or more living neighbors, it dies
            // by overcrowding.
            if (livingNeighborsCount[i] >= 4) {
                cells[i].living = false
            }
            // A cell dies by exposure if it has 0 or 1 living neighbors.
            if (livingNeighborsCount[i] < 2) {
                cells[i].living = false
            }
            // A cell is born if it has 3 living neighbors.
            if (livingNeighborsCount[i] == 3) {
                cells[i].living = true
            }

        }
    }
}