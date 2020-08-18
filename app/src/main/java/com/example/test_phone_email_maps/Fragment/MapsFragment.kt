package com.example.test_phone_email_maps.Fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.test_phone_email_maps.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*



class MapsFragment : Fragment() {


    private val permisionFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var callback: LocationCallback? = null
    private val Code_Permision = 100
    private var marcadorBellaVista: Marker? = null
    private lateinit var pocisionActual: LatLng
    private var Bellavista: LatLng? = null

    //variable que maneja el mapa es tipo google map
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
        mapFragment?.getMapAsync {

                googleMap ->
            Map = googleMap

            Map.uiSettings.isZoomControlsEnabled = true
            Map.isMyLocationEnabled = true
            Map.uiSettings.isMyLocationButtonEnabled = true

            if (validationPermissionUbication()) {
                obtenerUbicacion()

            } else {
                GetPermission()
            }

            Bellavista = LatLng(13.7071116, -89.1474706)

            //agregar marcador en direcion iglesia bella vista Latitud = 13.7071116, Longitud = -89.1474706
            marcadorBellaVista = Map.addMarker(
                MarkerOptions()
                    .position(Bellavista!!)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .snippet("Iglesia Bella vista Ubicada en Soyapango")
                    .title("Iglesia Bella Vista")
            )
            marcadorBellaVista?.tag = 0
           // apiDirections()
        }


        return view
    }


    override fun onStart() {
        super.onStart()
        if (validationPermissionUbication()) {
            obtenerUbicacion()

        } else {
            GetPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }

    //Metodos de validacion de permisos de ubicacion
    private fun validationPermissionUbication(): Boolean {

        val hayUbicacion = ActivityCompat.checkSelfPermission(
            requireActivity(),
            permisionFineLocation
        ) == PackageManager.PERMISSION_GRANTED

        return hayUbicacion
    }

    private fun GetPermission() {

        val racional = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            permisionFineLocation
        )

        if (racional) {
            //Mensaje de explicacion de porque se necesita el mensaje....
            solicitudPermission()
        } else {
            solicitudPermission()
        }
    }

    private fun solicitudPermission() {
        requestPermissions(arrayOf(permisionFineLocation), Code_Permision)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Code_Permision -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Ya se puede obtener la ubicacion
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se aceptaron los permisos ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {

        callback = object : LocationCallback() {
            override fun onLocationResult(ubicacionResult: LocationResult?) {
                super.onLocationResult(ubicacionResult)

                for (ubicacion in ubicacionResult?.locations!!) {
                    Toast.makeText(
                        requireContext(),
                        ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    pocisionActual = LatLng(ubicacion.latitude, ubicacion.longitude)
                    Map.addMarker(
                             MarkerOptions()
                            .position(pocisionActual)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .title("Mi Ubicacion Actual...")
                            .snippet("Esta es tu ubicacion actual en el mapa")
                    )
                    Map.animateCamera(CameraUpdateFactory.newLatLngZoom(pocisionActual, 11.6f))
                    //Log.d("position1", ubicacion.toString())
                }
                    //Log.d("posicion actual", pocisionActual.toString())
                /*
                * instanciamiento de la funcion en el metodo de obtencion y agregado de marcador en la pocision actual del usuario
                * caso contrario en la obtencion de la latitud y longitud retornara un null
                *
                */
                apiDirections(UbicacionActual = LatLng(pocisionActual.latitude, pocisionActual.longitude))

            }
        }
        //Log.d("posicion",pocisionActual?.latitude.toString())
        //Log.d("posicion2",pocisionActual?.longitude.toString())
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun initLocationUser() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 30000
        locationRequest?.fastestInterval = 30000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun detenerActualizacionUbicacion() {
        fusedLocationClient?.removeLocationUpdates(callback)
    }

    //recibe parametro de tipo LatLng o latitud y longitud la funcion caso contrario la posicion del usuario retorna null
    private fun apiDirections(UbicacionActual: LatLng){
        //val coordenadas = LatLng(Bellavista!!.latitude, Bellavista!!.longitude).toString()
        //val origen: String = "origin="+ pocisionActual!!.latitude.toString() +","+pocisionActual!!.longitude.toString()
        //val origin: String = pocisionActual?.latitude.toString()+","+pocisionActual?.longitude.toString()

        //coordenada de destino en este caso iglesia en soyapango (se modificara mas adelante)
        val destino: String = Bellavista?.latitude.toString()+","+Bellavista?.longitude.toString()
        /*
        coordenada estaticas de test de ruta en solicitud http

        val destination1 = LatLng(13.8151128,-89.0591834)
        val origin2 = destination1.latitude.toString()+","+ destination1.longitude.toString()
         */
        // constante con la pocision actual del usuario
        val origin = UbicacionActual.latitude.toString() +","+UbicacionActual.longitude.toString()
        val destination = destino
        //comprobacion del retorno de la variable, si realmente esta obteniendo los valores de latitud y longitud
        Log.d("probando pocision", UbicacionActual.toString())

        //url de api directions para retornar json con los valores de distancia y tiempo del origen hacia el destino
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin}&destination=${destination}&sensor=false&mode=driving&key=AIzaSyCixligL5HFYUrDQewAJzuWPewHC7hTwYE"

        //solicitud http usando la libreria Volley para retornar el json
        val queue = Volley.newRequestQueue(requireContext())
        val solicitud = StringRequest(Request.Method.GET, url,Response.Listener<String>{
            response ->  Log.d("respuesta", response)

        }, Response.ErrorListener{
            error: VolleyError? -> Log.e("error", error.toString())
        })
        queue.add(solicitud)

    }

}




