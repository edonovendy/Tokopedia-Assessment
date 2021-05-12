package com.tokopedia.filter.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tokopedia.filter.R
import com.tokopedia.filter.utils.Config.Companion.F_MAX_PRICE
import com.tokopedia.filter.utils.Config.Companion.F_MIN_PRICE
import com.tokopedia.filter.utils.Config.Companion.F_SELECTED_LOCATIONS
import com.tokopedia.filter.utils.Config.Companion.LOCATIONS
import com.tokopedia.filter.utils.Config.Companion.PRICES
import com.tokopedia.filter.utils.Config.Companion.TITLE
import java.text.DecimalFormat
import java.text.NumberFormat

class FilterActivity : AppCompatActivity() {

    lateinit var chipCity: ChipGroup
    lateinit var textMinPrice: TextView
    lateinit var textMaxPrice: TextView
    lateinit var seekbarMinPrice: SeekBar
    lateinit var seekbarMaxPrice: SeekBar
    lateinit var btnFilter: Button
    lateinit var btnResetFilter: Button
    var selectedCity = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        title = TITLE
        bindViews()
        val numberFormat: NumberFormat = DecimalFormat("#,###")

        val minMaxPrice: IntArray? = intent.getIntArrayExtra(PRICES)
        val minPrice = minMaxPrice?.get(0)
        val maxPrice = minMaxPrice?.get(1)
        textMinPrice.text = "Rp ${numberFormat.format(minPrice)}"
        textMaxPrice.text = "Rp ${numberFormat.format(maxPrice)}"
        seekbarMinPrice.max = maxPrice ?: 0
        seekbarMinPrice.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p1 < minPrice!!) textMinPrice.text = "Rp ${numberFormat.format(minPrice)}"
                else textMinPrice.text = "Rp ${numberFormat.format(p1)}"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        seekbarMaxPrice.max = maxPrice!!
        seekbarMaxPrice.progress = maxPrice
        seekbarMaxPrice.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p1 < minPrice!!) textMaxPrice.text = "Rp ${numberFormat.format(minPrice)}"
                else textMaxPrice.text = "Rp ${numberFormat.format(p1)}"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        btnFilter.setOnClickListener{ setFilter() }
        btnResetFilter.setOnClickListener{ resetFilter() }

        val locations = intent.getSerializableExtra(LOCATIONS) as Map<String, Int>
        val sortedLocations = locations.toList().sortedBy { (_, value) -> value }.reversed().toMap()

        setChipsData(sortedLocations)



    }

    private fun bindViews(){
        chipCity = findViewById(R.id.cpLocation)
        textMinPrice = findViewById(R.id.tvMinPrice)
        textMaxPrice = findViewById(R.id.tvMaxPrice)
        seekbarMinPrice = findViewById(R.id.seekbarMinPrice)
        seekbarMaxPrice = findViewById(R.id.seekbarMaxPrice)
        btnFilter = findViewById(R.id.btnFilter)
        btnResetFilter = findViewById(R.id.btnResetFilter)
    }

    private fun setFilter(){
        val intentFilter = Intent(this, ProductActivity::class.java)
        intentFilter.putExtra(F_MIN_PRICE, seekbarMinPrice.progress)
        intentFilter.putExtra(F_MAX_PRICE, seekbarMaxPrice.progress)
        intentFilter.putExtra(F_SELECTED_LOCATIONS, selectedCity.toTypedArray())
        setResult(Activity.RESULT_OK, intentFilter)
        finish()
    }

    private fun resetFilter() {
        val intentFilter = Intent(this, ProductActivity::class.java)
        setResult(Activity.RESULT_CANCELED, intentFilter)
        finish()
    }

    private fun setChipsData(cities:Map<String, Int>){
        for (city in cities){
            val newChip = Chip(this)
            newChip.text = city.key
            newChip.isClickable = true
            newChip.isCloseIconVisible = false
            newChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey))

            newChip.setOnClickListener{
                if (selectedCity.isEmpty() || !selectedCity.contains(city.key)) {
                    selectedCity.add(city.key)
                    newChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                }
                else {
                    selectedCity.remove(city.key)
                    newChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey))
                }
            }

            chipCity.addView(newChip)
        }
    }



}