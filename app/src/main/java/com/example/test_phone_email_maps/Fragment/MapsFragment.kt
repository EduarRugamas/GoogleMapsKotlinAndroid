package com.example.test_phone_email_maps.Fragment


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.test_phone_email_maps.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import java.io.IOException


class MapsFragment : Fragment() {


    private val permisionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var callback: LocationCallback? = null
    private val Code_Permision = 100
    private var marcadorBellaVista: Marker? = null
    private var pocisionActual: LatLng? = null
    private var Bellavista: LatLng? = null
    private val client = OkHttpClient()

    private lateinit var Map: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_maps, container, false)

            fusedLocationClient = FusedLocationProviderClient(requireActivity())
            initLocationUser()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync{

                googleMap -> Map = googleMap

                Map.uiSettings.isZoomControlsEnabled = true
                Map.isMyLocationEnabled = true
                Map.uiSettings.isMyLocationButtonEnabled = true

            if(validationPermissionUbication()){
                     obtenerUbicacion()
                        Marcadores()
            }else{
                GetPermission()
            }

            Bellavista = LatLng(13.7071116, -89.1474706)

            //agregar marcador en direcion iglesia bella vista Latitud = 13.7071116, Longitud = -89.1474706
            marcadorBellaVista = Map.addMarker(MarkerOptions()
                .position(Bellavista!!)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet("Iglesia Bella vista Ubicada en Soyapango")
                .title("Iglesia Bella Vista"))
            marcadorBellaVista?.tag = 0


            
        }


        return view
    }



    override fun onStart() {
        super.onStart()
        if(validationPermissionUbication()){
                obtenerUbicacion()
        }else{
            GetPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }

    //Metodos de validacion de permisos de ubicacion
    private fun validationPermissionUbication(): Boolean {

            val hayUbicacion = ActivityCompat.checkSelfPermission(requireActivity(), permisionFineLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacion
    }
    private fun GetPermission() {

        val racional = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permisionFineLocation)

        if(racional){
            //Mensaje de explicacion de porque se necesita el mensaje....
            solicitudPermission()
        }else{
            solicitudPermission()
        }
    }

    private fun solicitudPermission() {
        requestPermissions(arrayOf(permisionFineLocation), Code_Permision)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            Code_Permision ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Ya se puede obtener la ubicacion
                }else{
                    Toast.makeText( requireContext(),"No se aceptaron los permisos ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {

             callback = object: LocationCallback() {
                override fun onLocationResult(ubicacionResult: LocationResult?) {
                    super.onLocationResult(ubicacionResult)

                        for(ubicacion in ubicacionResult?.locations!!){
                            Toast.makeText(requireContext(), ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(), Toast.LENGTH_SHORT).show()
                             pocisionActual = LatLng(ubicacion.latitude, ubicacion.longitude)
                            Map.addMarker(MarkerOptions()
                                .position(pocisionActual!!)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title("Mi Ubicacion Actual...")
                                .snippet("Esta es tu ubicacion actual en el mapa"))
                            Map.animateCamera(CameraUpdateFactory.newLatLngZoom(pocisionActual,10f))
                            }

                }
            }
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun initLocationUser() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 900000
        locationRequest?.fastestInterval = 900000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private fun detenerActualizacionUbicacion(){
        fusedLocationClient?.removeLocationUpdates(callback)
    }


    //metodo de dibujado de ruta por medio de peticion http con volley
    private fun cargarURL(url: String) {

        /*val queue = Volley.newRequestQueue(requireContext())
        val solicitud = StringRequest(Request.Method.GET, url,
            com.android.volley.Response.Listener<String> { response ->
                Log.d("HTTP", response.toString())

            },
            com.android.volley.Response.ErrorListener { Error ->
                // Handle error
                Log.d("HTTP Error", Error.toString())
            })
            queue.add(solicitud)*/
        //Log.d("solicitud", solicitud.toString())
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Log.e("error", e.toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
               //Log.d("http", call.toString())

                val input = response.body?.byteStream()
                //val bitMap = BitmapFactory.decodeStream(input)

                Log.v("httpp response", input.toString())
            }


        })

    }
    private fun Marcadores(){
        Map.setOnMapLongClickListener {

            val coordenadas = LatLng(Bellavista!!.longitude, Bellavista!!.latitude)
            val origen = "origin=" + pocisionActual.toString() + "&"
            val destino = "destination=" + coordenadas.toString() + "&"
            //val api_key = "AIzaSyAsUb8N5LS_tdXTchN4PHoKJba8ln0mh_I" + "&"
            val parametros = origen + destino + "sensor=false&mode=driving"

            cargarURL("https://maps.googleapis.com/maps/api/directions/json?" + parametros)
        }
    }


}