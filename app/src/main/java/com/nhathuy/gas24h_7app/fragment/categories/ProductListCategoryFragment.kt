package com.nhathuy.gas24h_7app.fragment.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentProductListCategoryBinding
import com.nhathuy.gas24h_7app.util.Constants.ARG_CATEGORY
import com.nhathuy.gas24h_7app.util.Constants.ARG_CATEGORY_ID


class ProductListCategoryFragment : Fragment(R.layout.fragment_product_list_category) {

    private lateinit var binding: FragmentProductListCategoryBinding
    private lateinit var productAdapter: ProductAdapter
    private var categoryName: String? =null
    private var categoryId: String? =null
    companion object{
        fun newInstance(categoryId: String, categoryName: String) = ProductListCategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY_ID, categoryId)
                putString(ARG_CATEGORY, categoryName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId=it.getString(ARG_CATEGORY_ID)
            categoryName=it.getString(ARG_CATEGORY)

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentProductListCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchProducts()
    }


    private fun setupRecyclerView() {
        productAdapter= ProductAdapter(emptyList())
        binding.recyclerViewProducts.apply {
            layoutManager=GridLayoutManager(requireContext(),2)
            this.adapter=productAdapter
            isNestedScrollingEnabled=false
        }
    }
    private fun fetchProducts() {
//        binding.progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        Log.d("ProductListCategory", "Fetching products for category ID: $categoryId")

        val query = db.collection("products")
            .whereEqualTo("categoryId", categoryId)

        Log.d("ProductListCategory","Query: ${query.toString()}")

        query.get()
            .addOnSuccessListener { documents ->
                val productList = documents.mapNotNull { document ->
                    Log.d("ProductListCategory", "Document ID: ${document.id}, data: ${document.data}")
                    document.toObject(Product::class.java)
                }
                Log.d("ProductListCategory", "Retrieved ${productList.size} products")
                updateRecyclerView(productList)
//                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
//                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProductListCategory", "Error fetching products", exception)
            }
    }

    private fun updateRecyclerView(products: List<Product>) {
         productAdapter.updateData(products)
    }

}