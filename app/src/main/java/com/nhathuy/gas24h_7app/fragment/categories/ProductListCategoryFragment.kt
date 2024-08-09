package com.nhathuy.gas24h_7app.fragment.categories

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentProductListCategoryBinding
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.util.Constants.ARG_CATEGORY
import com.nhathuy.gas24h_7app.util.Constants.ARG_CATEGORY_ID
import javax.inject.Inject


class ProductListCategoryFragment : Fragment(R.layout.fragment_product_list_category),ProductListCategoryContract.View {

    private var _binding: FragmentProductListCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private var categoryName: String? =null
    private var categoryId: String? =null
    private var isDataFetched = false
    @Inject
    lateinit var presenter: ProductListCategoryPresenter
    companion object{
        fun newInstance(categoryId: String, categoryName: String) = ProductListCategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY_ID, categoryId)
                putString(ARG_CATEGORY, categoryName)
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
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

        _binding= FragmentProductListCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        presenter.attachView(this)

        categoryId?.let {
                presenter.fetchProduct(it)
            }

    }

    override fun onResume() {
        super.onResume()

            categoryId?.let {
                presenter.fetchProduct(it)
            }

    }
    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showProducts(products: List<Product>) {
        productAdapter.updateData(products)
        view?.post {
            (parentFragment as? HomeFragment)?.updateViewPagerHeight()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
    private fun setupRecyclerView() {
        productAdapter= ProductAdapter(emptyList())
        binding.recyclerViewProducts.apply {
            layoutManager=GridLayoutManager(requireContext(),2)
            this.adapter=productAdapter
            isNestedScrollingEnabled=true
        }
    }
//    private fun fetchProducts() {
//        binding.progressBar.visibility = View.VISIBLE
//        val db = FirebaseFirestore.getInstance()
//        Log.d("ProductListCategory", "Fetching products for category ID: $categoryId")
//
//        val query = db.collection("products")
//            .whereEqualTo("categoryId", categoryId)
//
//        Log.d("ProductListCategory","Query: ${query.toString()}")
//
//        query.get()
//            .addOnSuccessListener { documents ->
//                val productList = documents.mapNotNull { document ->
//                    Log.d("ProductListCategory", "Document ID: ${document.id}, data: ${document.data}")
//                    document.toObject(Product::class.java)
//                }
//                Log.d("ProductListCategory", "Retrieved ${productList.size} products")
//                updateRecyclerView(productList)
//                binding.progressBar.visibility = View.GONE
//                isDataFetched = true
//            }
//            .addOnFailureListener { exception ->
//                binding.progressBar.visibility = View.GONE
//                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
//                Log.e("ProductListCategory", "Error fetching products", exception)
//            }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }


}