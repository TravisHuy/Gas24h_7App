package com.nhathuy.gas24h_7app.ui.google_map

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivityGoogleMapBinding
import com.nhathuy.gas24h_7app.ui.shipping_address.ShippingAddressActivity
import java.io.IOException
import java.util.Locale

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityGoogleMapBinding
    private lateinit var mMap : GoogleMap
    private lateinit var geocoder: Geocoder
    private var currentMarker : Marker ? = null
    private var currentAddress: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geocoder  = Geocoder(this, Locale.getDefault())

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupSearchView()
        setupSaveButton()

        //get the current address from the intent
        currentAddress = intent.getStringExtra("currentAddress")
    }
    private fun setupSearchView() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener{_, _, _ ->
            val query = binding.searchView.text.toString()
            searchAddress(query)
            binding.searchView.hide()
            true

        }
    }

    private fun searchAddress(address: String) {
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                updateMap(latLng, address)
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Lỗi khi tìm kiếm địa chỉ", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateMap(latLng: LatLng, title: String) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        currentMarker?.remove()  // Remove the old marker
        currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(title))

        binding.searchBar.text = title

        currentAddress = title
    }
    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            currentAddress?.let {
                address ->
                val intent = Intent()
                intent.putExtra("address", address)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }?: run {
                Toast.makeText(this, "Please choose an address before saving", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //dùng ví trí hiện tại để hiển thị trên bản đồ
        currentAddress?.let {
            address ->
            searchAddress(address)
        } ?: run {
            val defaultLatLng = LatLng(10.8231, 106.6297)
            updateMap(defaultLatLng, "Hồ Chí Minh")
        }

        // Thiết lập listener cho sự kiện click trên bản đồ
        mMap.setOnMapClickListener { latLng ->
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0) ?: "Vị trí không xác định"
                    updateMap(latLng, addressText)
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Lỗi khi lấy địa chỉ cho vị trí này", Toast.LENGTH_SHORT).show()
            }
        }
    }
}