package com.rmanzur.ec2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rmanzur.ec2.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var mCurrentLocation= MutableLiveData<LocationModel>()
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val locationCallbackRequest = LocationRequest.create().apply {
        interval=10000
        fastestInterval=5000
        priority=LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val logTAG = MapsActivity::class.java.simpleName
    lateinit var locationCallback:LocationCallback

    fun startLocationTracking(context:Context){
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locations: LocationResult) {
                super.onLocationResult(locations)
                for (location in locations.locations){
                    mCurrentLocation.postValue(LocationModel(location, Calendar.getInstance().timeInMillis))
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionHelper.askForLocationPermission(this)
            return
        }
        mFusedLocationClient.requestLocationUpdates(locationCallbackRequest,locationCallback,null)
    }

    fun stopLocationTracking(context: Context){
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
        stopLocationTracking(this)
    }

    override fun onResume() {
        super.onResume()
        checkPermissionAndSetView()
    }

    private fun checkPermissionAndSetView(){
        if (PermissionHelper.arePermissionsGranted(this)){
            val mapFragment= supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            startLocationTracking(this)
        }else{
            PermissionHelper.askForLocationPermission(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==PermissionHelper.REQUEST_CODE_FOR_PERMISSION){
            checkPermissionAndSetView()
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mCurrentLocation.observe(this, androidx.lifecycle.Observer {
            Log.e(logTAG, "${it.location.latitude}, ${it.location.longitude}, ${it.timestamp}")
            val location = LatLng(it.location.latitude, it.location.longitude)
            mMap.addMarker(MarkerOptions().position(location).title("${it.timestamp}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        })
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) !=PackageManager.PERMISSION_GRANTED
        ){
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.setMinZoomPreference(4f)
        mMap.setMaxZoomPreference(20f)
    }
}