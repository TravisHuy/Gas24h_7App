package com.nhathuy.gas24h_7app.ui.detail_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityDetailProductBinding
import javax.inject.Inject

class DetailProductActivity : AppCompatActivity() ,DetailProductContract.View{

    private lateinit var binding:ActivityDetailProductBinding

    @Inject
    lateinit var presenter: DetailProductPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupImageSlider()
//        setupSuggestProducts()
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val productId = intent.getStringExtra("PRODUCT_ID")?:""
        val categoryId = intent.getStringExtra("CATEGORY_ID")?:""

        presenter.loadProductDetails(productId)
        presenter.loadSuggestProducts(categoryId)

    }

    private fun setupSuggestProducts() {
//        // Thiết lập phần sản phẩm gợi ý
//        val suggestLayout = binding.layoutSuggestProduct.root
//        val suggestRecyclerView = suggestLayout.findViewById<RecyclerView>(R.id.suggest_rec)
//
//        // Tạo danh sách sản phẩm gợi ý
//        val suggestProducts = listOf(
//            SuggestProduct(R.drawable.test_image, "Serum red peel da So Natural", "115.000đ", "Đã bán 1,9k"),
//            SuggestProduct(R.drawable.test_image, "Khô Cá Chỉ Vàng Tẩm Gia Vị", "68.000đ", "Đã bán 2k"),
//            SuggestProduct(R.drawable.test_image, "CAMERA IP YOOSEE", "172.000đ", "Đã bán 152,5k"),
//            SuggestProduct(R.drawable.test_image, "Tai nghe HiFi Audio", "7.989đ", "Đã bán 3,9k")
//        )
//
//        // Thiết lập RecyclerView với GridLayoutManager 2 cột
//        val layoutManager = GridLayoutManager(this, 2)
//        suggestRecyclerView.layoutManager = layoutManager
//        suggestRecyclerView.adapter = SuggestProductAdapter(suggestProducts)


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
//            holder.priceText.text = product.price
            holder.soldText.text = product.sold
        }

        override fun getItemCount() = products.size
    }
    data class SuggestProduct(
        val image: Int,
        val name: String,
        val price: Double,
        val sold: String
    )

    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showError(message:String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showProductDetails(product: Product) {
        binding.detailProductName.text=product.name
        binding.layoutDescriptionProduct.shortDescription.text=product.description
    }

    override fun setupImageSlider(detailImages: List<String>) {
        val imageList= detailImages.map {
            imageUrl->
            SlideModel(imageUrl,ScaleTypes.FIT)
        }
        binding.imageSlider.setImageList(imageList)
    }

    override fun setupSuggestProduct(products: List<Product>) {
//        val layoutManager = GridLayoutManager(this, 2)
//        binding.layoutSuggestProduct.root.findViewById<RecyclerView>(R.id.suggest_rec).apply {
//            this.layoutManager = layoutManager
//            adapter = SuggestProductAdapter(products.map { product ->
//                SuggestProduct(
//                    image = R.drawable.placeholder_image, // Sử dụng hình placeholder nếu không có hình
//                    name = product.name,
//                    price = product.price,
//                    sold = product.soldCount // Giả sử `soldCount` là một thuộc tính của `Product`
//                )
//            })
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}