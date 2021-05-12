package com.tokopedia.maps.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tokopedia.maps.model.CountryResponse
import com.tokopedia.maps.model.CountryModelDetail
import com.tokopedia.maps.NetworkConfig
import com.tokopedia.maps.R
import com.tokopedia.maps.utils.Config.Companion.STRING_INPUT_INFO
import com.tokopedia.maps.utils.Config.Companion.STRING_NETWORK_INFO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class MapsActivity : AppCompatActivity() {

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var searchText: EditText? = null
    private var buttonSearch: View? = null
    private lateinit var textCountryName: TextView
    private lateinit var textCountryCapital: TextView
    private lateinit var textCountryPopulation: TextView
    private lateinit var textCountryCallCode: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        bindViews()

        buttonSearch!!.setOnClickListener {
            searchCity()
        }

        mapFragment!!.getMapAsync { googleMap -> this@MapsActivity.googleMap = googleMap }
    }

    private fun bindViews() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        textCountryName = findViewById(R.id.textCountryName)
        textCountryCapital = findViewById(R.id.textCountryCapital)
        textCountryPopulation = findViewById(R.id.textCountryPopulation)
        textCountryCallCode = findViewById(R.id.textCountryCallCode)
        searchText = findViewById(R.id.editSearch)
        buttonSearch = findViewById(R.id.btnSearch)
    }

    private fun searchCity() {

        googleMap?.clear()

        val idCountry: String? = Locale.getISOCountries().find {
            Locale("", it).displayCountry == searchText?.text.toString() }

        if (idCountry != null) {
            NetworkConfig().getService().getCountryDetail(idCountry).enqueue(object: Callback<CountryResponse> {
                override fun onResponse(call: Call<CountryResponse>, response: Response<CountryResponse>) {
                    val countryObject: CountryResponse? = response.body()
                    val lat = countryObject?.data?.get(0)?.latitude?.toDouble()
                    val lng = countryObject?.data?.get(0)?.longitude?.toDouble()
                    if (lat != null && lng != null){
                        val latLng = LatLng(lat, lng)
                        mapFragment!!.getMapAsync {
                            googleMap?.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position)))
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3f))
                        }
                    }

                    if (countryObject != null) {
                        countryObject.data?.get(0)?.let { it1 -> showCountryData(it1) }
                    }
                }

                override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, STRING_NETWORK_INFO, Toast.LENGTH_LONG).show()
                }

            })
        } else {
            Toast.makeText(this, STRING_INPUT_INFO, Toast.LENGTH_LONG).show()
        }

    }

    private fun showCountryData(countryData: CountryModelDetail) {
        textCountryName.text = "Nama negara: ${countryData.name}"
        textCountryCapital.text = "Ibukota: ${countryData.capital}"
        textCountryPopulation.text = "Jumlah penduduk: ${countryData.population}"
        textCountryCallCode.text = "Kode telepon: ${countryData.phone}"
    }

    private fun getCountryCode(countryName: String): String? =
            Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

}
