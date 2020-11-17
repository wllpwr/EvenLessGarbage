package com.example.evenlessgarbage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CellListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
        val cellListViewModel: CellListViewModel by lazy {
            ViewModelProviders.of(this).get(CellListViewModel::class.java)
        }
        next_gen_button.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.next_gen_completed), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            cellListViewModel.updateColony(cellListViewModel.cells)
        }
    }
}