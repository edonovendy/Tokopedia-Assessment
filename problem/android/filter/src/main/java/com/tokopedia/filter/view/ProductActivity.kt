package com.tokopedia.filter.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tokopedia.filter.R
import com.tokopedia.filter.adapter.ProductAdapter
import com.tokopedia.filter.model.ProductModel
import com.tokopedia.filter.model.ShopModel
import com.tokopedia.filter.utils.Config.Companion.CITY
import com.tokopedia.filter.utils.Config.Companion.DATA
import com.tokopedia.filter.utils.Config.Companion.DISCOUNT_PERCENTAGE
import com.tokopedia.filter.utils.Config.Companion.F_MAX_PRICE
import com.tokopedia.filter.utils.Config.Companion.F_MIN_PRICE
import com.tokopedia.filter.utils.Config.Companion.F_SELECTED_LOCATIONS
import com.tokopedia.filter.utils.Config.Companion.ID
import com.tokopedia.filter.utils.Config.Companion.IMAGE_URL
import com.tokopedia.filter.utils.Config.Companion.LOCATIONS
import com.tokopedia.filter.utils.Config.Companion.NAME
import com.tokopedia.filter.utils.Config.Companion.PRICES
import com.tokopedia.filter.utils.Config.Companion.PRICE_INT
import com.tokopedia.filter.utils.Config.Companion.PRODUCTS
import com.tokopedia.filter.utils.Config.Companion.SHOP
import com.tokopedia.filter.utils.Config.Companion.SLASHED_PRICE_INT
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ProductActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var productAdapter: ProductAdapter
    lateinit var fabFilter: FloatingActionButton
    var listProducts = ArrayList<ProductModel>()
    var filterPrice = IntArray(2) { 0 }
    var listCity = hashMapOf<String, Int?>()
    val REQUEST_FILTER:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        fabFilter = findViewById(R.id.fabFilter)

        processData()

        recyclerView = findViewById(R.id.product_list)
        productAdapter = ProductAdapter(listProducts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = productAdapter

        fabFilter.setOnClickListener{ filterPage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            val minPrice = data?.getIntExtra(F_MIN_PRICE, 0)
            val maxPrice = data?.getIntExtra(F_MAX_PRICE, 0)
            val selectedLocations = data?.getStringArrayExtra(F_SELECTED_LOCATIONS)

            if (maxPrice != null) {
                if (minPrice != null) {
                    filterProduct(minPrice, maxPrice, selectedLocations)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            listProducts.clear()
            processData()
            productAdapter = ProductAdapter(listProducts)
            recyclerView.adapter = productAdapter
        }
    }

    private fun filterProduct(minPrice:Int, maxPrice:Int, cities: Array<String>?){
        listProducts = if (cities != null && !cities.isEmpty()) {
            listProducts.filter { product -> cities.contains(product.shopModel.city) &&  product.priceInt!! in (minPrice + 1) until maxPrice + 1} as ArrayList<ProductModel>
        } else {
            listProducts.filter { it.priceInt!! in (minPrice + 1) until maxPrice + 1 } as ArrayList<ProductModel>
        }
        productAdapter = ProductAdapter(listProducts)
        recyclerView.adapter = productAdapter
    }

    private fun filterPage() {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra(PRICES, filterPrice)
        intent.putExtra(LOCATIONS, listCity)
        startActivityForResult(intent, REQUEST_FILTER)
    }

    private fun getData(): String {
        val stringBuffer = StringBuffer()
        val bufferReader: BufferedReader
        try{
            bufferReader = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.products)))
            bufferReader.readLines().map { line -> stringBuffer.append(line) }
        } catch (ioException: IOException){
            ioException.printStackTrace()
            return ""
        }

        return stringBuffer.toString()
    }

    private fun processData(){
        val jsonString: String = getData()
        val jsonObject = JSONObject(jsonString)
        val jsonData = jsonObject.getJSONObject(DATA)
        val jsonArrayProduct = jsonData.getJSONArray(PRODUCTS)

        for (i in 0 until jsonArrayProduct.length()){
            val productDetail = jsonArrayProduct.getJSONObject(i);
            val shopJson = productDetail[SHOP] as JSONObject

            val shopID = shopJson[ID].toString().toInt()
            val shopName = shopJson[NAME].toString()
            val shopCity = shopJson[CITY].toString()
            val shopData = ShopModel(shopID, shopName, shopCity)

            val productID = productDetail[ID].toString().toInt()
            val productName = productDetail[NAME].toString()
            val productImageUrl = productDetail[IMAGE_URL].toString()
            val productPriceInt = productDetail[PRICE_INT].toString().toInt()
            val productDiscountPercentage = productDetail[DISCOUNT_PERCENTAGE].toString().toInt()
            val productSlashedPriceInt = productDetail[SLASHED_PRICE_INT].toString().toInt()

            val newProduct = ProductModel(productID, productName, productImageUrl, productPriceInt, productDiscountPercentage, productSlashedPriceInt, shopData)

            setMinMaxPrice(productPriceInt)
            shopData.city?.let { groupByCity(it) }

            listProducts.add(newProduct)
        }
    }

    private fun setMinMaxPrice(price: Int){
        if(filterPrice[0] == 0){
            filterPrice[0] = price
            return
        }
        if(filterPrice[1] == 0){
            filterPrice[1] = price
            return
        }

        if(price < filterPrice[0]) filterPrice[0] = price
        else if(price > filterPrice[1]) filterPrice[1] = price
    }

    private fun groupByCity(city:String){
        if (!listCity.containsKey(city)) listCity[city] = 1
        else {
            var tempCity:Int? = listCity[city]
            if (tempCity != null) {
                tempCity += 1
            }
            listCity[city] = tempCity
        }
    }
}