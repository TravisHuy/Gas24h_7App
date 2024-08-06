package com.nhathuy.gas24h_7app.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.HomeViewpagerAdapter
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentHomeBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryFragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private var viewPager2Adapter: HomeViewpagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSlideImage()
        fetchCategoriesFromFireStore()
    }

    private fun fetchCategoriesFromFireStore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("categories")
            .get()
            .addOnSuccessListener { document ->
                val categories = document.map { doc ->
                    ProductCategory(
                        id = doc.getString("id") ?: "",
                        categoryName = doc.getString("categoryName") ?: ""
                    )
                }.sorted()
                setCategoriesFragment(categories)
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun setCategoriesFragment(categories: List<ProductCategory>) {
        val categoriesFragments = categories.map { category ->
            ProductListCategoryFragment.newInstance(category.id, category.categoryName)
        }

        viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            tab.text = categories[position].categoryName
        }.attach()

        binding.viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateViewPagerHeight(position)
            }
        })

        // Set initial height
        binding.viewPagerHome.post {
            updateViewPagerHeight(0)
        }
    }

    fun updateViewPagerHeight(position: Int = binding.viewPagerHome.currentItem) {
        viewPager2Adapter?.let { adapter ->
            val currentFragment = adapter.getFragment(position)
            currentFragment.view?.post {
                val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(binding.viewPagerHome.width, View.MeasureSpec.EXACTLY)
                val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                currentFragment.view?.measure(wMeasureSpec, hMeasureSpec)
                if (binding.viewPagerHome.layoutParams.height != currentFragment.view?.measuredHeight) {
                    binding.viewPagerHome.layoutParams = (binding.viewPagerHome.layoutParams).also { lp ->
                        lp.height = currentFragment.view?.measuredHeight ?: ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
            }
        }
    }

    private fun setSlideImage() {
        val listImage = listOf(
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(listImage)
    }

    override fun onResume() {
        super.onResume()
        fetchCategoriesFromFireStore()
    }
}