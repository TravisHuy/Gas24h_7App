package com.nhathuy.gas24h_7app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.tabs.TabLayoutMediator
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.HomeViewpagerAdapter
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentHomeBinding
import com.nhathuy.gas24h_7app.fragment.categories.AccessoryFragment
import com.nhathuy.gas24h_7app.fragment.categories.BuiltInGasStoveFragment
import com.nhathuy.gas24h_7app.fragment.categories.ElectricCookTopFragment
import com.nhathuy.gas24h_7app.fragment.categories.FurnitureFragment
import com.nhathuy.gas24h_7app.fragment.categories.GasStoveFragment
import com.nhathuy.gas24h_7app.fragment.categories.GasStoveWithOvenFragment
import com.nhathuy.gas24h_7app.fragment.categories.HomeCategoryFragment
import com.nhathuy.gas24h_7app.fragment.categories.InFraredCookTopFragment
import com.nhathuy.gas24h_7app.fragment.categories.InductionCookerFragment
import com.nhathuy.gas24h_7app.fragment.categories.KitchenSinkFragment
import com.nhathuy.gas24h_7app.fragment.categories.MicrowaveFragment
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryFragment
import com.nhathuy.gas24h_7app.fragment.categories.RangeHoodFragment
import com.nhathuy.gas24h_7app.fragment.categories.RestaurantGasFragment
import com.nhathuy.gas24h_7app.fragment.promotion.PromotionFragment

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
        setCategoriesFragment()
    }

    private fun setCategoriesFragment() {
//        val categoriesFragments= arrayListOf<Fragment>(
//            HomeCategoryFragment(),
//            RestaurantGasFragment(),
//            AccessoryFragment(),
//            GasStoveFragment(),
//            BuiltInGasStoveFragment(),
//            InductionCookerFragment(),
//            ElectricCookTopFragment(),
//            InFraredCookTopFragment(),
//            MicrowaveFragment(),
//            RangeHoodFragment(),
//            KitchenSinkFragment(),
//            GasStoveWithOvenFragment(),
//            FurnitureFragment(),
//        )

        val categories=ProductCategory.values()
        val categoriesFragments=categories.map {category->
            ProductListCategoryFragment.newInstance(category.categoryName)
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

//            when(position){
//                0 -> tab.text="Gas VIP Khuyến mãi"
//                1 -> tab.text="Bọ bình-Gas Quán Ăn"
//                2 -> tab.text="Phụ Kiện"
//                3 -> tab.text="Bếp Gas"
//                4 -> tab.text="Bếp Gas Âm"
//                5 -> tab.text="Bếp Từ"
//                6 -> tab.text="Bếp Điện"
//                7 -> tab.text="Bếp Hồng Ngoại"
//                8 -> tab.text="Lò Nướng-Lò Vi Sóng"
//                9 -> tab.text="Máy Hút Mùi"
//                10 -> tab.text="Bồn Rửa"
//                11 -> tab.text="Bếp Gas Lò Nướng"
//                else -> tab.text="Thiết bị Nội Thất"
//            }
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
