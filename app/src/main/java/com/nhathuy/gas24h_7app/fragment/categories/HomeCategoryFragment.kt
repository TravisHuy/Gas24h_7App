package com.nhathuy.gas24h_7app.fragment.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.adapter.ProductGridAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentHomeCategoryBinding


class HomeCategoryFragment : Fragment() {
    private lateinit var binding:FragmentHomeCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val products = listOf(
            Product("1", "Gas Stove 1", ProductCategory.GAS_VIP, "Description 1", 100.0, null, listOf(
                "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
            Product("2", "Gas Stove 2", ProductCategory.GAS_VIP, "Description 2", 120.0, null, listOf(
                "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
            Product("3", "Gas Stove 3", ProductCategory.GAS_VIP, "Description 3", 140.0, null, listOf(
                "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c")),
            Product("4", "Gas Stove 4", ProductCategory.GAS_VIP, "Description 4", 160.0, null, listOf(
                "https://firebasestorage.googleapis.com/v0/b/mvpfirebase-e2b2f.appspot.com/o/bep_gas.jpg?alt=media&token=f29f624f-66d4-46ec-af39-6cc62a61df1c"))
        )
//
//        val adapter = ProductGridAdapter(requireContext(), products)
//        binding.gridviewProducts.adapter = adapter

        Log.d("PromotionFragment", "Number of products: ${products.size}")

        val adapter = ProductAdapter(products)
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }

        Log.d("PromotionFragment", "RecyclerView setup completed")
    }
}