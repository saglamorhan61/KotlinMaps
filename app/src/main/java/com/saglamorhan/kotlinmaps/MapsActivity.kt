package com.saglamorhan.kotlinmaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        /*
        // Add a marker in Sydney and move the camera
        val kahta = LatLng(37.790578, 38.615157)
        mMap.addMarker(MarkerOptions().position(kahta).title("Marker in Kahta"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kahta,15f))

         */

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location?) {

                if (location != null){
                    mMap.clear()
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))

                    // Bu konumun adresini almak
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                    try {

                        val addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        if (addressList!=null && addressList.size>0){
                            println(addressList.get(0).toString())
                        }


                    }catch (e: Exception){
                        e.printStackTrace()
                    }


                }


            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }

        }
        //izin kontrolu yapiliyor
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //izin verilmemisse yapilacaklar
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            // Izin verilmisse ve location degismemisse var olan son location i haritada gostermek
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null){
                val lastKnownLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Your Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,15f))
            }
        }


    }
    // Kullanicidan bi izin istedikten sonra kullanicinin cevabini verir.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        if (requestCode == 1){
            if (grantResults.isNotEmpty()){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //izin verilmisse yapilacaklar
                    //Kullanicinin konumunu almak
                    //burasi kullanici izni ilk verdiginde calisir
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

                }

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }




}