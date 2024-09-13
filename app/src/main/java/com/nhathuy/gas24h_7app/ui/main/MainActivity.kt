package com.nhathuy.gas24h_7app.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivityMainBinding
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.viewmodel.HomeSharedViewModel
import com.nhathuy.gas24h_7app.viewmodel.ViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var homeSharedViewModel: HomeSharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Log để kiểm tra
            Log.d("MainActivity","MainActivity onCreate executed successfully")
        } catch (e: Exception) {
            // Log lỗi nếu có
            e.printStackTrace()
            Log.d("MainActivity","Error in MainActivity: ${e.message}")
        }
        (application as Gas24h_7Application).getGasComponent().inject(this)
        homeSharedViewModel = ViewModelProvider(this,viewModelFactory)[HomeSharedViewModel::class.java]

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val bottomNavView: BottomNavigationView = binding.appBarMain.contentMain.bottomNavView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_order, R.id.nav_hotline, R.id.nav_notification, R.id.nav_profile),
            drawerLayout
        )

        navController.addOnDestinationChangedListener{
                _, destination, _ ->
            if(destination.id == R.id.nav_home){
                homeSharedViewModel.refreshData()
            }
        }

        if(intent.hasExtra("navigate_to")){
            when(intent.getStringExtra("navigate_to")){
                "hotline" -> navController.navigate(R.id.nav_hotline)
            }

        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}