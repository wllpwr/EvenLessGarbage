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
import java.io.File

private const val rows = 20
private const val columns = 20


class CellListFragment : Fragment() {

    private lateinit var cellRecyclerView: RecyclerView
    private var adapter: CellAdapter? = null

    var livingLight = "#A5D6A7"
    var livingDark = "#FF66BB6A"
    var deadDark = "#FFEF5350"

    private val cellListViewModel: CellListViewModel by lazy {
        ViewModelProviders.of(this).get(CellListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cell_list, container, false)
        cellListViewModel.fillItUp()
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
            cellListViewModel.switchState(cell)
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

    // pulses on update, rewrite
    // this seems really inefficient
    // an animation would be better, but I've gotten this far already
    fun pulseUI(cell: Cell, slotTextView: TextView) {
        fun repeatPulse() {
            Handler().postDelayed({
                pulseUI(cell, slotTextView)
            }, 100)
        }

        fun changeToOtherColorPlease(colorString: String) {
            Handler().postDelayed({
                slotTextView.setBackgroundColor(Color.parseColor(colorString))
                repeatPulse()
            }, 250)
        }
        if (cell.living) {
            slotTextView.setBackgroundColor(Color.parseColor(livingLight)) // green light
            changeToOtherColorPlease(livingDark) // green dark
        } else {
            slotTextView.setBackgroundColor(Color.parseColor(deadDark)) // red dark
        }
    }


    // passthrough functions, allows MainActivity to communicate with CellListViewModel
    fun updateColonyPassthrough() {
        cellListViewModel.updateColony()
        cellRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun savePassthrough(file: File) {
        cellListViewModel.save(file)
        Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show()
    }

    fun loadPassthrough(file: File) {
        cellListViewModel.load(file)
        Toast.makeText(context, getString(R.string.loaded), Toast.LENGTH_SHORT).show()
        cellRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun newColors(colorList: List<String>) {
        // corrections if user does not input the pound sign at the beginning
        if (colorList[0].isNotEmpty()) {
            livingLight = if (colorList[0][0].toString() != "#") {
                "#${colorList[0]}"
            } else {
                colorList[0]
            }
        }
        if (colorList[1].isNotEmpty()) {
            livingDark = if (colorList[1][0].toString() != "#") {
                "#${colorList[1]}"
            } else {
                colorList[1]
            }
        }
        if (colorList[2].isNotEmpty()) {
            deadDark = if (colorList[2][0].toString() != "#") {
                "#${colorList[2]}"
            } else {
                colorList[2]
            }
        }
    }
}
