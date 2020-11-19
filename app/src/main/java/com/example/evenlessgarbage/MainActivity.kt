package com.example.evenlessgarbage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private val file = File("data/data/com.example.evenlessgarbage/cells.dat")
    private val cloneFile = File("data/data/com.example.evenlessgarbage/clone.dat")
    var autoEnabled = false
    private lateinit var newColorList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // create the file if non-existent
        if (!file.exists()) {
            file.createNewFile()
        }
        // create the file if non-existent
        if (!cloneFile.exists()) {
            cloneFile.createNewFile()
        }


        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CellListFragment.newInstance()

            next_gen_button.setOnClickListener { fragment.updateColonyPassthrough() }

            auto_button.setOnClickListener {
                autoEnabled = !autoEnabled
                continueWithDelay(fragment)
            }

            save_button.setOnClickListener {
                if (!file.exists()) {
                    file.createNewFile()
                }
                fragment.savePassthrough(file)
            }
            load_button.setOnClickListener { fragment.loadPassthrough(file) }
            save_activity.setOnClickListener {
                fragment.savePassthrough(cloneFile)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            load_activity.setOnClickListener {
                if (cloneFile.exists()) {
                    fragment.loadPassthrough(cloneFile)
                    cloneFile.delete()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.make_new_activity_first),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            color_button.setOnClickListener {
                if (color_button.text != getString(R.string.done)) { // if button hasn't been clicked before
                    color_button.text = getString(R.string.done)
                    light_pulse.isVisible = true
                    dark_pulse.isVisible = true
                    dead_color.isVisible = true
                } else { // if it has been clicked before
                    color_button.text = getString(R.string.change_colors)
                    light_pulse.isVisible = false
                    dark_pulse.isVisible = false
                    dead_color.isVisible = false
                    val lightColor = light_pulse.text.toString()
                    val darkColor = dark_pulse.text.toString()
                    val deadColor = dead_color.text.toString()


                    newColorList = listOf(lightColor, darkColor, deadColor)
                    fragment.newColors(newColorList)
                }
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
            }, 750)
        } else {
            auto_button.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.red_200))
            autoEnabled = false
        }
    }
}