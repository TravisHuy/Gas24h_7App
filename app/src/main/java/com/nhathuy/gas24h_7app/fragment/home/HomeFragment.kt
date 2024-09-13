package com.nhathuy.gas24h_7app.fragment.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.HomeViewpagerAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.FragmentHomeBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductListCategoryFragment
import com.nhathuy.gas24h_7app.viewmodel.HomeSharedViewModel
import com.nhathuy.gas24h_7app.viewmodel.ViewModelFactory
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home), HomeFragmentContract.View {

    private lateinit var binding: FragmentHomeBinding
    private var viewPager2Adapter: HomeViewpagerAdapter? = null

    @Inject
    lateinit var presenter: HomeFragmentPresenter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var homeSharedViewModel: HomeSharedViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeSharedViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[HomeSharedViewModel::class.java]
        presenter.attachView(this)
        setupObserves()
        setSlideImage()

        homeSharedViewModel.refreshData()

    }

    private fun setupObserves() {
        homeSharedViewModel.categories.observe(viewLifecycleOwner) { categories ->
            setupTabLayout(categories)
        }
        homeSharedViewModel.productCategory.observe(viewLifecycleOwner) { productsMap ->
            updateProductsInViewPager(productsMap)
        }
        homeSharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }
        homeSharedViewModel.error.observe(viewLifecycleOwner) { error ->
            showError(error)
        }
    }

    private fun setupTabLayout(categories: List<ProductCategory>) {
        val fragments = categories.map { category ->
            ProductListCategoryFragment.newInstance(category.id, category.categoryName)
        }
        viewPager2Adapter = HomeViewpagerAdapter(fragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            tab.text = categories[position].categoryName
        }.attach()

        binding.viewPagerHome.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val categoryId = categories[position].id
                homeSharedViewModel.fetchProductForCategory(categoryId)
                updateViewPagerHeight(position)
            }
        })


        if (categories.isNotEmpty()) {
            homeSharedViewModel.fetchProductForCategory(categories[0].id)
        }
    }

    private fun updateProductsInViewPager(productsMap: Map<String, List<Product>>) {
        viewPager2Adapter?.let { adapter ->
            for (i in 0 until adapter.itemCount) {
                val fragment = adapter.getFragment(i) as? ProductListCategoryFragment
                fragment?.let {
                    val categoryId = (binding.viewPagerHome.adapter as HomeViewpagerAdapter)
                        .getItemId(i).toString()
                    val products = productsMap[categoryId] ?: emptyList()
                    it.updateProducts(products)
                }
            }
        }
        updateViewPagerHeight(binding.viewPagerHome.currentItem)
    }

    override fun onResume() {
        super.onResume()
        if (homeSharedViewModel.categories.value.isNullOrEmpty()) {
            homeSharedViewModel.refreshData()
        }
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showBanners(banners: List<String>) {
        val slideModels = banners.map {
            SlideModel(it, ScaleTypes.FIT)
        }
        binding.imageSlider.setImageList(slideModels)
    }

    override fun showCategories(categories: List<ProductCategory>) {
//        setCategoriesFragment(categories)
        setupTabLayout(categories)
    }

//    private fun setCategoriesFragment(categories: List<ProductCategory>) {
//        val categoriesFragments = categories.map { category ->
//            ProductListCategoryFragment.newInstance(category.id, category.categoryName)
//        }
//
//        viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
//        binding.viewPagerHome.adapter = viewPager2Adapter
//
//        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
//            tab.text = categories[position].categoryName
//        }.attach()
//
//        binding.viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                updateViewPagerHeight(position)
//            }
//        })
//
//        // Set initial height
//        binding.viewPagerHome.post {
//            updateViewPagerHeight(0)
//        }
//    }

    fun updateViewPagerHeight(position: Int = binding.viewPagerHome.currentItem) {
        viewPager2Adapter?.let { adapter ->
            val currentFragment = adapter.getFragment(position)
            currentFragment.view?.post {
                val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    binding.viewPagerHome.width,
                    View.MeasureSpec.EXACTLY
                )
                val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                currentFragment.view?.measure(wMeasureSpec, hMeasureSpec)
                if (binding.viewPagerHome.layoutParams.height != currentFragment.view?.measuredHeight) {
                    binding.viewPagerHome.layoutParams =
                        (binding.viewPagerHome.layoutParams).also { lp ->
                            lp.height = currentFragment.view?.measuredHeight
                                ?: ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
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