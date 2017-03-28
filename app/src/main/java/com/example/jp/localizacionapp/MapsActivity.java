package com.example.jp.localizacionapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,GoogleMap.OnMapLongClickListener {


    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    public static String result="";
    public static String distancia;
    public static double latpos, lngpos;
    public static double latTele=42.237020;
    public static double lngTele=-8.712628;
    CircleOptions circle;
    private static final String LOGTAG = "android-localizacion";
    public static Marker marcaTelepizza;
    LatLng telp = new LatLng(42.236954, -8.712717);
    LatLng abn = new LatLng(42.237667, -8.720249);
    int radius = 100;

    public static double latAbn=42.237782;
    public static double lngAbn=-8.720155;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        instrucciones();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        LatLng telepizza = new LatLng(latTele, lngTele);
        marcaTelepizza = mMap.addMarker(new MarkerOptions().position(telepizza).title("Telepizza").snippet("Marca Telepizza"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(telepizza));
        marcaTelepizza.setVisible(false);


        // Controles UI
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        CircleOptions circuloTelepizza = new CircleOptions()
                .center(telp)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        // Añadir círculo Telepizza
        Circle areaTelepizza = mMap.addCircle(circuloTelepizza);

        CircleOptions circuloAbanca = new CircleOptions()
                .center(abn)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        // Añadir círculo Abanca
        Circle areaAbanca = mMap.addCircle(circuloAbanca);





    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        updateUI(lastLocation);
        calcularDistancia();


        Toast.makeText(this, distancia+" metros ", Toast.LENGTH_LONG).show();

    }
    public void calcularDistancia() {


        float earthRadius = (float) 6378.137;

        float dLat = (float) Math.toRadians(latpos - latTele);
        float dLng = (float) Math.toRadians(lngpos - lngTele);
        float a = (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(latTele)) * Math.cos(Math.toRadians(latpos)) *
                                Math.sin(dLng / 2) * Math.sin(dLng / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        float dist = earthRadius * c;
        float distMet = dist * 1000;
        distancia = String.valueOf(distMet);




            if (distMet <= 20.00) {
                marcaTelepizza.setVisible(true);
            }

            Toast.makeText(this, "Quedan " + distancia + "metros hasta la marca", Toast.LENGTH_SHORT).show();

        }

    private void updateUI(Location loc) {

        if (loc != null) {
            latpos=loc.getLatitude();
            lngpos=loc.getLongitude();
            //Tosat para saber longitud y latitud de mi posicion
             //Toast.makeText(this, String.valueOf(lat1)+" "+String.valueOf(lng1), Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "Latitud y Longitud desconocidas", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    public void instrucciones() {

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Busca la marca");
        build.setMessage(instrucciones);
        build.setPositiveButton("Aceptar", null);
        build.create();
        build.show();
    }

    public String instrucciones = "Bienvenido,estas listo para buscar la marca escondida??\n" +
            ".- Activa tu localizacion.\n" +
            ".- Para que no sea tan dificil,hemos limitado el area en un circulo,tu mision? Encontrar la marca y escanear el codigo QR.\n" +
            ".- Pulsando una vez en la pantalla se mostrará la distancia hasta la marca.\n" +
            ".- Cuando estes a menos de 20 metros aparecerá un circulo menor que mostrará el area concreto de la marca.\n" +
            "Buena suerte suerte.";

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}




