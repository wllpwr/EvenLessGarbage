package com.example.evenlessgarbage

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {


    private val file = File("data/data/com.example.evenlessgarbage/cells.dat")
    var autoEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!file.exists()) { // create the file if non-existent
            file.createNewFile()
        }
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CellListFragment.newInstance()
            next_gen_button.setOnClickListener {
                fragment.updateColonyPassthrough()
            }

            auto_button.setOnClickListener {
                autoEnabled = !autoEnabled
                continueWithDelay(fragment)
            }

            saveButton.setOnClickListener {
                if (!file.exists()) {
                    file.createNewFile()
                }
                fragment.savePassthrough(file)
            }
            loadButton.setOnClickListener {
                fragment.loadPassthrough(file)
            }
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }

    }

    private fun continueWithDelay(fragment: CellListFragment) {
        if (autoEnabled) {
            auto_button.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.green_200))
            Handler().postDelayed({
                fragment.updateColonyPassthrough()
                continueWithDelay(fragment)
            }, 500)
        } else {
            auto_button.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.red_200))
            autoEnabled = false
        }
    }
}