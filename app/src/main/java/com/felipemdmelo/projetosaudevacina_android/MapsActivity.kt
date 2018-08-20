package com.felipemdmelo.projetosaudevacina_android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.felipemdmelo.projetosaudevacina_android.models.PostoSaude
import com.felipemdmelo.projetosaudevacina_android.webservice.RetrofitInit
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var LOCATION_PERMISSION_CODE = 100

    /**
     * LIFE CYCLE METHODS
     * AND OVERRIDE BUILT IN METHODS
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Init..
        init()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
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
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do what you need to do.
                    //getUserLocation()
                    startLocationUpdates()

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    /**
     * PRIVATE METHODS
     */
    private fun init() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    var latlng = LatLng(location!!.latitude, location!!.longitude)
                    moveCameraToMe(latlng)
                }
            }
        }

        locationRequest = LocationRequest().apply {
            interval = 60000
            fastestInterval = 50000
            smallestDisplacement = 50F
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Recupera lista de postos de saude..
        val call = RetrofitInit().getPostoSaudeService().listPostoSaude()
        call.enqueue(object : Callback<List<PostoSaude>> {

            override fun onResponse(call: Call<List<PostoSaude>>, response: Response<List<PostoSaude>>) {
                val postosSaude = response.body()
                if (postosSaude != null) {
                    Toast.makeText(applicationContext, R.string.msg_request_success, Toast.LENGTH_SHORT).show()

                    postosSaude.forEach {
                        var latlng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                        addMarker(latlng, it.nome, "Hepatite B. 45 doses")
                    }
                } else {
                    Toast.makeText(applicationContext, R.string.msg_request_no_data, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PostoSaude>>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.msg_request_failed, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun startLocationUpdates() {
        val permissionCheck =
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(permissionCheck) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null /* Looper */)
        } else {
            askUserLocationPermission()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun askUserLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else { // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_CODE)
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String, snippet: String) {
        mMap.addMarker(MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(snippet))
    }

    private fun moveCameraToMe(latLng: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

}
