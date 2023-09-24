package com.example.listopia

import android.media.Image
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This forces app into light mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // This makes it so the input for price is locked to 2 places after the decimal.
        findViewById<EditText>(R.id.itemPriceET).setFilters(
            arrayOf<InputFilter>(
                DigitsInputFilter(
                    Integer.MAX_VALUE,
                    2,
                    Double.POSITIVE_INFINITY
                )
            )
        )
        var items: ArrayList<Item>
        var keyOIS: String = ""
        var key: String = ""
        val sharedPrefs = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val options = sharedPrefs.all.keys.toTypedArray()
        val arrayAdp =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, options)
        findViewById<Spinner>(R.id.item_lists).adapter = arrayAdp
        findViewById<Spinner>(R.id.item_lists).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Toast.makeText(
                        this@MainActivity,
                        "The chosen list is: ${options[p2]}",
                        Toast.LENGTH_SHORT
                    ).show()
                    keyOIS = options[p2]
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        fun toggleVisibility() {
            findViewById<ConstraintLayout>(R.id.start_screen).visibility = View.INVISIBLE
            findViewById<RecyclerView>(R.id.items).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.info_layout).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.listopia_logo).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.listopia_logo).visibility = View.VISIBLE
        }
        fun loadList() {
            // Loads previous list data
            var gson = Gson()
            var json = sharedPrefs.getString(key, "")
            val type: Type = object : TypeToken<ArrayList<Item>>() {}.type
            try {
                items = gson.fromJson(json, type)
            } catch (e: NullPointerException) {
                items = ArrayList<Item>()
            }

            val itemsRV = findViewById<RecyclerView>(R.id.items)
            val adapter = ItemAdapter(items, key)
            itemsRV.adapter = adapter
            itemsRV.layoutManager = LinearLayoutManager(this)
            findViewById<Button>(R.id.addBtn).setOnClickListener {
                val name = findViewById<EditText>(R.id.itemNameET).getText().toString()
                val price = findViewById<EditText>(R.id.itemPriceET).getText().toString()
                val url = findViewById<EditText>(R.id.itemURLET).getText().toString()
                val item = Item(name, price, url)
                items.add(item)
                adapter.notifyDataSetChanged()

                findViewById<EditText>(R.id.itemNameET).getText().clear()
                findViewById<EditText>(R.id.itemPriceET).getText().clear()
                findViewById<EditText>(R.id.itemURLET).getText().clear()

                // Saves List Data
                val sharedPrefs = getSharedPreferences("shared preferences", MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                val gson = Gson()
                val json = gson.toJson(items)
                editor.putString(key, json)
                editor.apply()
                Log.d("key in saved:", key)
            }
        }
        findViewById<Button>(R.id.newListBtn).setOnClickListener {
            key = findViewById<EditText>(R.id.newListET).text.toString()
            toggleVisibility()
            loadList()
        }
        findViewById<Button>(R.id.loadListBtn).setOnClickListener {
            key = keyOIS
            toggleVisibility()
            loadList()
        }
    }
}