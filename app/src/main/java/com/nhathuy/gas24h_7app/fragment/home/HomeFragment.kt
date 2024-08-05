package com.nhathuy.gas24h_7app.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSlideImage()
        fetchCategoriesFromFireStore()
    }

    private fun fetchCategoriesFromFireStore() {
        val db=FirebaseFirestore.getInstance()
        db.collection("categories")
            .get()
            .addOnSuccessListener {document->
                val categories =document.map {document->
                    ProductCategory(id = document.getString("id")?:"",
                    categoryName = document.getString("categoryName")?:"")
                }.sorted()
                setCategoriesFragment(categories)
            }
            .addOnFailureListener {

            }
    }

    private fun setCategoriesFragment(categories: List<ProductCategory>) {

        val categoriesFragments=categories.map {category->
            ProductListCategoryFragment.newInstance(category.id,category.categoryName)
        }

        val viewPager2Adapter= HomeViewpagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter=viewPager2Adapter

        binding.viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentFragment = categoriesFragments[position].view
                currentFragment?.post {
                    val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(binding.viewPagerHome.width, View.MeasureSpec.EXACTLY)
                    val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    currentFragment.measure(wMeasureSpec, hMeasureSpec)
                    if (binding.viewPagerHome.layoutParams.height != currentFragment.measuredHeight) {
                        binding.viewPagerHome.layoutParams = (binding.viewPagerHome.layoutParams).also { lp ->
                            lp.height = currentFragment.measuredHeight
                        }
                    }
                }
            }
        })

        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){
            tab,position ->
            tab.text=categories[position].categoryName
        }.attach()
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
}
