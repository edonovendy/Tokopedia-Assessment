package com.tokopedia.filter.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tokopedia.filter.R
import com.tokopedia.filter.model.ProductModel
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.text.NumberFormat


class ProductAdapter(private var products: List<ProductModel>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: ProductModel = products[position]
        val nf:NumberFormat = DecimalFormat("#,###")

        DownloadImageTask(holder.imgProduct, holder.progress).execute(product.imageUrl)

        holder.textProductName.text = product.name
        holder.textProductPrice.text = "Rp ${nf.format(product.priceInt)}"

        if(product.discountPercentage != 0) {
            holder.textProductSlashedPrice.text = "Rp ${nf.format(product.slashPriceInt)}"
            holder.textProductDiscount.text = "${product.discountPercentage.toString()}%"
            holder.textProductSlashedPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.layoutDiscount.visibility = View.VISIBLE
        } else {
            holder.layoutDiscount.visibility = View.GONE
        }

        holder.textProductCity.text = product.shopModel.city
    }

    private class DownloadImageTask(var bmImage: ImageView, var progress: ProgressBar) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val input: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
            progress.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layoutDiscount: LinearLayout = view.findViewById(R.id.layoutDiscount)
        var textProductName: TextView = view.findViewById(R.id.textProductName)
        var textProductPrice: TextView = view.findViewById(R.id.textProductPrice)
        var textProductSlashedPrice: TextView = view.findViewById(R.id.textProductSlashedPrice)
        var textProductDiscount: TextView = view.findViewById(R.id.textProductDiscount)
        var textProductCity: TextView = view.findViewById(R.id.textProductCity)
        var imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        var progress: ProgressBar = view.findViewById(R.id.progress)

    }
}