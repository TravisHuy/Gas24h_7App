//package com.nhathuy.gas24h_7app.fragment.categories
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.GridLayoutManager
//import com.nhathuy.gas24h_7app.R
//import com.nhathuy.gas24h_7app.adapter.ProductAdapter
//import com.nhathuy.gas24h_7app.data.model.Product
//import com.nhathuy.gas24h_7app.data.model.ProductCategory
//import com.nhathuy.gas24h_7app.databinding.FragmentProductListCategoryBinding
//import com.nhathuy.gas24h_7app.util.Constants.ARG_CATEGORY
//
//
//class ProductListCategoryFragment : Fragment(R.layout.fragment_product_list_category) {
//
//    private lateinit var binding: FragmentProductListCategoryBinding
//    private lateinit var category:ProductCategory
//
//    companion object{
//        fun newInstance(categoryName: String) = ProductListCategoryFragment().apply {
//            arguments = Bundle().apply {
//                putString(ARG_CATEGORY, categoryName)
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            val categoryName=it.getString(ARG_CATEGORY)
//            category=ProductCategory.fromDisplayName(categoryName!!)?:ProductCategory.GAS_VIP
//        }
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        binding= FragmentProductListCategoryBinding.inflate(inflater,container,false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupRecyclerView()
//    }
//
//    private fun setupRecyclerView() {
//        val products=getProductCategory(category)
//        val adapter= ProductAdapter(products)
//        binding.recyclerViewProducts.apply {
//            layoutManager=GridLayoutManager(requireContext(),2)
//            this.adapter=adapter
//            isNestedScrollingEnabled=false
//        }
//    }
//
//    private fun getProductCategory(category: ProductCategory): List<Product> {
//        return if (category == ProductCategory.GAS_VIP) {
//            listOf(
//                Product("1", "Gas Stove 1", ProductCategory.GAS_VIP, "Description 1", 100.0, null, listOf(
//                    "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
//                Product("2", "Gas Stove 2", ProductCategory.GAS_VIP, "Description 2", 120.0, null, listOf(
//                    "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
//                Product("3", "Gas Stove 3", ProductCategory.GAS_VIP, "Description 3", 140.0, null, listOf(
//                    "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
//                Product("4", "Gas Stove 4", ProductCategory.GAS_VIP, "Description 4", 160.0, null, listOf(
//                    "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c"))
//            )
//        } else {
//            emptyList()
//        }
//    }
//}