package com.nhathuy.gas24h_7app.ui.detail_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivityDetailProductBinding

class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImageSlider()
        setupSuggestProducts()
    }

    private fun setupSuggestProducts() {
        // Thiết lập phần sản phẩm gợi ý
        val suggestLayout = binding.layoutSuggestProduct.root
        val suggestRecyclerView = suggestLayout.findViewById<RecyclerView>(R.id.suggest_rec)

        // Tạo danh sách sản phẩm gợi ý
        val suggestProducts = listOf(
            SuggestProduct(R.drawable.test_image, "Serum red peel da So Natural", "115.000đ", "Đã bán 1,9k"),
            SuggestProduct(R.drawable.test_image, "Khô Cá Chỉ Vàng Tẩm Gia Vị", "68.000đ", "Đã bán 2k"),
            SuggestProduct(R.drawable.test_image, "CAMERA IP YOOSEE", "172.000đ", "Đã bán 152,5k"),
            SuggestProduct(R.drawable.test_image, "Tai nghe HiFi Audio", "7.989đ", "Đã bán 3,9k")
        )

        // Thiết lập RecyclerView với GridLayoutManager 2 cột
        val layoutManager = GridLayoutManager(this, 2)
        suggestRecyclerView.layoutManager = layoutManager
        suggestRecyclerView.adapter = SuggestProductAdapter(suggestProducts)


    }

    private fun setupImageSlider() {
        val listImage = listOf(
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(listImage)
    }
    class SuggestProductAdapter(private val products: List<SuggestProduct>) :
        RecyclerView.Adapter<SuggestProductAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.productImage)
            val nameText: TextView = view.findViewById(R.id.productName)
            val priceText: TextView = view.findViewById(R.id.productPrice)
            val soldText: TextView = view.findViewById(R.id.productSold)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.suggest_product_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.imageView.setImageResource(product.image)
            holder.nameText.text = product.name
            holder.priceText.text = product.price
            holder.soldText.text = product.sold
        }

        override fun getItemCount() = products.size
    }
    data class SuggestProduct(
        val image: Int,
        val name: String,
        val price: String,
        val sold: String
    )
}