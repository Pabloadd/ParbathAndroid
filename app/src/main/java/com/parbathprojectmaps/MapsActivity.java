package com.parbathprojectmaps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.parbathprojectmaps.Models.ClusterMarker;
import com.parbathprojectmaps.Models.DialogModule;
import com.parbathprojectmaps.Models.PolylineData;
import com.parbathprojectmaps.Models.ServiceLocation;
import com.parbathprojectmaps.Models.ToiletPlaces;
import com.parbathprojectmaps.util.MyClusterManagerRender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnPolylineClickListener, TextToSpeech.OnInitListener {

    private static final String TAG = "INFO_LOG";
    private GoogleMap mMap;
    //    private ImageView mGps; este fue reemplazodo por floating button

    ImageButton altgps, altbath, altparking, infobtn;

    //VARIABLES DE SENSOR ACELEROMETRO, PARA FACILIDAD DE USO
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;

    private String welcome = "Bienvenido a PAR BATH, a continuación se presenta" +
            " un mapa y 4 botones, primero se encuentra en la parte inferior izquierda la opción para encontrar el sanitario más" +
            " cercano, arriba de esta opción se encuentra un botón para el reconocimiento de voz que te" +
            " podría ser de ayuda, en la parte inferior central se encuentra la opción" +
            " para determinar tu ubicación actual, y a la derecha se encuentra el botón para encontrar el estacionamiento más cercano.";
    private String getVoiceData = "";

    //Variable de Text to Speech y respuesta por voz (voz recognizer)
    private TextToSpeech textToSpeech;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    //marcador del primer Toilet
    private static final LatLng UTPToilet = new LatLng(8.48887, -80.328052);

    //marcadores de parking
    private static final LatLng UTPParking = new LatLng(8.488374, -80.328141);

    //me faltaba esot ?
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //    var for google directions api
    private GeoApiContext geoApiContext = null;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FirebaseFirestore mDb;
    private Location userLocation;
    private ClusterManager mClusterManager;
    private MyClusterManagerRender myClusterManagerRender;
    private ArrayList<ClusterMarker> clusterMarkers = new ArrayList<>();

    //Widget preview informacion BottomSheet
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView iconoBottomSheet;

    private ArrayList<PolylineData> mPlylinesData = new ArrayList<>();

    private ArrayList<ToiletPlaces> toiletPlacesArrayList = new ArrayList<>();
    private ArrayList<ServiceLocation> serviceLocationArrayList = new ArrayList<>();

    private String markerPlace;
    private TextView txtDistancia, txtDireccion;

    public String userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        altgps = (ImageButton) findViewById(R.id.imgbtn_gps);
        altbath = (ImageButton) findViewById(R.id.imgbtn_bath);
        altparking = (ImageButton) findViewById(R.id.imgbtn_parking);
        infobtn = (ImageButton) findViewById(R.id.info_btn);

        mDb = FirebaseFirestore.getInstance();
        txtDistancia = (TextView) findViewById(R.id.textView7);
        txtDireccion = (TextView) findViewById(R.id.textView6);
        iconoBottomSheet = (ImageView) findViewById(R.id.imageView5);
//        alt_gps_btn = (Button) findViewById(R.id.alt_gps_button);

        textToSpeech = new TextToSpeech(this, this);
        texto_Voz(welcome);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        getLocationPermission();

        // inicializacion de variables de sensor acelerometro
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorAcelerometroActive();

//        showQuestionAccebility();
    }


    private void showQuestionAccebility() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle(R.string.title_dialog);
        builder.setMessage(R.string.message_dialog)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Dialog LOG ","Respuesta si de dialogo");
                        if (userLocation != null){
                            textToSpeech.stop();
                            Intent al = new Intent(MapsActivity.this, AlternativaMapa.class);
                            al.putExtra("position_user",userLocation);
                            startActivity(al);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Dialog LOG ","Respuesta no de dialogo");
                    }
                }).show();
    }


    //Metodo creador de marcadores de la data obtenida de firebaseStore
    public void makingMarkerFromFireBaseStore() {

        Log.d(TAG, "Entro la metodo de creacion de marcadores");

        if (mMap != null) {
            Log.d(TAG, "Map es deferente de null");
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
                Log.d(TAG, "Cluster Manager es null");
            }
            if (myClusterManagerRender == null) {
                myClusterManagerRender = new MyClusterManagerRender(
                        getApplicationContext(),
                        mMap,
                        mClusterManager);
                mClusterManager.setRenderer(myClusterManagerRender);
                Log.d(TAG, "Cluster Manager Render es null");
            }
        }

        for (ServiceLocation servicesMarks : serviceLocationArrayList) {
            Log.d(TAG, "Entro en el for que toiletPlaces");
            try {
                String snippet = "";
                int iconoMark = R.drawable.ic_toilet;
                try {
//                    Integer.parseInt(servicesMarks.getLugares().getIcono());
                    iconoMark = Integer.decode(servicesMarks.getPlace_description().getIcon());
                    Log.d(TAG, "Si hay icono");
                } catch (NumberFormatException e) {
                    Log.d(TAG, "AddMarcadores: no Icono for " + servicesMarks.getPlace_description().getName_place());
                }// fin try para colocar icono a los marcadores
                Log.d(TAG, "Agregando Marcadores...");
                ClusterMarker newClusterMarker = new ClusterMarker(
                        new LatLng(servicesMarks.getPosition_lat_long().getLatitude(),
                                servicesMarks.getPosition_lat_long().getLongitude()),
                        servicesMarks.getPlace_description().getName_place(), "", iconoMark,
                        servicesMarks.getPlace_description());

                mClusterManager.addItem(newClusterMarker);
                clusterMarkers.add(newClusterMarker);
            } catch (NullPointerException e) {
                Log.e(TAG, "addMarcadores: NullPointer Exception: " + e.getMessage());
            }
        }//fin for
        Log.d(TAG, "No entro en le for...");
        mClusterManager.cluster();

    }//fin del metodo



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (mMap != null) {
                mMap.clear();

                if (mPlylinesData.size() > 0) {
                    mPlylinesData.clear();
                    mPlylinesData = new ArrayList<>();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //metodo que obtiene la data del FireBase relacionada a los servicios
    private void getInfoServices() {

        if (toiletPlacesArrayList.isEmpty()){
            toiletPlacesArrayList.clear();
            CollectionReference lugaresToilets = mDb.collection(getString(R.string.getLugares));

            Query lugaresQuery = lugaresToilets;

            lugaresQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ToiletPlaces toiletp = document.toObject(ToiletPlaces.class);
                            //la informacion se guarda en arraylist la cual es un objeto de
                            //la clase toiletPlaces
                            toiletPlacesArrayList.add(toiletp);
                        }
                    } else {
                        Log.d(TAG, "ERROR en consulta de info services" );
                    }
                }
            });
        }

        Log.d(TAG, "El tamano del array infoplaces " + toiletPlacesArrayList.size());
    }//fin del metodo getInfoServices

    private void getPosicionServices() {
        if (serviceLocationArrayList.isEmpty()){
            CollectionReference lugaresServices = mDb.collection(getString(R.string.getPosicionLugares));

            Query lugaresQuery = lugaresServices;
            lugaresQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen lugaresServices failed.", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        serviceLocationArrayList.clear();
                        serviceLocationArrayList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            ServiceLocation services = doc.toObject(ServiceLocation.class);
                            serviceLocationArrayList.add(services);
                        }
                        makingMarkerFromFireBaseStore();

                        Log.d(TAG, "El tamano del array Positionplaces " + serviceLocationArrayList.size());
                    }
                }
            });
        }

    }


    private void init() {
        Log.d(TAG, "init: initializing");

        altgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
                try {
                    moveCamera(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 15f, null);
                    textToSpeech.speak("Tu ubicación es la siguiente " + userAddress, TextToSpeech.QUEUE_FLUSH, null);
                }catch (Exception e){
                    Log.e("ERROR LOCATION","Todavia no se obtiene posicion de usuario, intenta unos segundos despues");
                }
            }
        });

        altbath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "BotonMoverCamaraBath: clicked bBath icon");
                moveCamera(new LatLng(UTPToilet.latitude, UTPToilet.longitude),
                        15f, null);
            }
        });

        altparking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "BotonMoverCamaraParking: clicked bPark icon");
                moveCamera(new LatLng(UTPParking.latitude, UTPParking.longitude),
                        15f, null);
            }
        });

        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                texto_Voz(getResources().getString(R.string.mensaje_bienvenida));
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        // mTextViewState.setText("Dragging...");
                        Intent i = new Intent(MapsActivity.this, ListService.class);
//                        i.putExtra("lugarService","Universidad Tecnologica de Panama - Cocle");
                        i.putExtra("lugarService", markerPlace);
                        startActivity(i);
                        textToSpeech.stop();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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
    @Override
    public void onMapReady(final GoogleMap googleMap) {
//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setOnPolylineClickListener(this);
        if (mLocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);//deshabilitando tools de google

            init();

            getInfoServices();
            getPosicionServices();

            // se crea listener para den clic en los iconos del bano, para mostrar informacion
            mMap.setOnMarkerClickListener(this);

        } // fin de condicion de los permisos otorgados

    }//fin onMapReady

    // metodo de que escucha los clic en los marcadares utilizado para mostrar informacion
    // o lanzar un evento diferente
    public boolean onMarkerClick(final Marker marker) {
        markerPlace = marker.getTitle();

        calculateDirections(marker);

        texto_Voz("Has seleccionado la siguiente ubicación, " + markerPlace);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;

    } // fin onMarkerClick

    private void SensorAcelerometroActive() {
        //AGREGAR CODIGO DE TEXTO A VOZ...
        if (sensor == null) {
            // funcion de voz diciendo que no posee sensor acelerometro
            Toast.makeText(this, "algo esta fallando", Toast.LENGTH_LONG).show();
        } else {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float x = sensorEvent.values[0];
                    if (x < -5) {
                        textToSpeech.stop();
                        Intent i = new Intent(MapsActivity.this, AlternativaMapa.class);
                        i.putExtra("position_user",userLocation);
                        startActivity(i);
                        stop();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }
    }

    //metodo relacionado al sensor acelerometro
    private void start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //metodo relacionado al sensor acelerometro
    private void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
        textToSpeech.speak("Has vuelto al mapa de navegación", TextToSpeech.QUEUE_FLUSH, null);
        getDeviceLocation();
        focusCameraCurrentLocation(userLocation);
        textToSpeech.speak("Tu ubicación es la siguiente " + userAddress,TextToSpeech.QUEUE_ADD,null);
    }

    protected void onStart() {

        super.onStart();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }// fin moveCamera

    //metodo para el calculo de direcciones
    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        userLocation.getLatitude(),
                        userLocation.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                // add las lienas polylines al mapa
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage());

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                //condicion para evitar doble almacenamiento de polylines
                if (mPlylinesData.size() > 0) {
                    for (PolylineData polylineData : mPlylinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPlylinesData.clear();
                    mPlylinesData = new ArrayList<>();
                }
                double duration = 9999999;
                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                    polyline.setClickable(true);
                    mPlylinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }
                }
            }
        });
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);

        // instancia de geoapi de directions api
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void focusCameraCurrentLocation(Location location){
        try{
            moveCamera(new LatLng(location.getLatitude(),location.getLongitude()),15f,"");
        }catch (Exception e){
            Log.e("ERROR ","userlocation es igual a null "+e.getMessage());
        }
    }

    //    me faltaba este metoo?
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMapsActivity(this);
        final boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnable) {
            //settings posee interesantes opciones de configuracion del sistema
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        getLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,(LocationListener) Local);


    }//fin getDeviceLocation

    boolean rstlocation = false;
    private void setLocation(Location location) {
        if( location.getLatitude() != 0.0 && location.getLongitude() != 0.0 ){

            if (rstlocation == false) {
                try{
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if(!list.isEmpty()){
                        Address DirCalle = list.get(0);
                        userAddress = DirCalle.getLocality() + ", " + DirCalle.getSubLocality() + ", " + DirCalle.getAdminArea();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                } // fin try catch
                rstlocation = true;
            }else{
                if (location.getLatitude() != userLocation.getLatitude() && location.getLongitude() != userLocation.getLongitude()){
                    try{
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> list = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        if(!list.isEmpty()){
                            Address DirCalle = list.get(0);
                            userAddress = DirCalle.getLocality() + ", " + DirCalle.getSubLocality() + ", " + DirCalle.getAdminArea();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    } // fin try catch

                } // fin condicion de desigualdad de datos
            } // fin condicion de flag boolean

            userLocation = location;
        } // find condicion if
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    } //fin onRequestPermissionsResult

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        for(PolylineData polylineData: mPlylinesData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(MapsActivity.this, R.color.blue));
                polylineData.getPolyline().setZIndex(1);
                /*
                * polylineData.getLeg().distance;
                * polylineData.getLeg().duration;
                * */
//                Se coloca en bottomSheet la previewInformation de el lugar destino
                txtDistancia.setText(polylineData.getLeg().distance.toString());
                txtDireccion.setText(markerPlace);
                //TEXTO A VOZ
                String addres_from = getFirstAddres(polylineData.getLeg().startAddress);
                String addres_to = getFirstAddres(polylineData.getLeg().endAddress);
                String message = "Desde tu ubicación " + addres_from + " a " + markerPlace;
                textToSpeech.speak(message +
                                ", son " + polylineData.getLeg().distance.toString() + " kilometros de distancia",
                        TextToSpeech.QUEUE_FLUSH,null);

            }else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(MapsActivity.this, R.color.gray));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    } //fin del metodo onPolylineClick

    private String getFirstAddres(String addres){
        try{
            String[] arrOfstr = addres.split(",");
            String from = arrOfstr[0] + ", "+ arrOfstr[1];
            return from;
        }catch (Exception e){
            Log.e("Error split string","Message: " + e.getMessage());
        }finally {
            String[] arrOfstr = addres.split(",");
            return arrOfstr[0];
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    //metodos de Text_to_Speech
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            int lenguaje = textToSpeech.setLanguage(Locale.getDefault());
            if (lenguaje == TextToSpeech.LANG_NOT_SUPPORTED || lenguaje == TextToSpeech.LANG_MISSING_DATA){
                //code de lenguaje no soportado
            }else{
                texto_Voz(welcome);
            }
        }else{
            //code para indicar que TextToSpeech no pudo inicializarce
        }
    }

    private void texto_Voz(String cadena) {
//        int acumulador=0;
//        double rlatitud=0, rlongitud=0;
//        ArrayList<String> nombreLugares = new ArrayList<>();
//        if (cadena.contains("Coclé") || cadena.contains("penonomé") || cadena.contains("coclé")){
//            textToSpeech.speak("Iniciando busqueda",TextToSpeech.QUEUE_FLUSH,null);
//            for (ServiceLocation objServicelocat : serviceLocationArrayList){
//                if (objServicelocat.getPlace_description().getName_place().contains("Cocle")){
//                    acumulador += 1;
//                    nombreLugares.add(objServicelocat.getPlace_description().getName_place());
//                    rlatitud = objServicelocat.getPosition_lat_long().getLatitude();
//                    rlongitud = objServicelocat.getPosition_lat_long().getLongitude();
//
//                }else{
//                    break;
//                }
//            }
//            if (acumulador != 0){
//                moveCamera(new LatLng(rlatitud,rlongitud),13f,"");
//                String number = Integer.toString(acumulador);
//
//                textToSpeech.speak("Existen " + number + " cantida de baños y estacionamiento en los suiguientes lugares",
//                        TextToSpeech.QUEUE_FLUSH,null);
//                for (int i=0;i<nombreLugares.size();i++){
//                    textToSpeech.speak(nombreLugares.get(i),TextToSpeech.QUEUE_FLUSH,null);
//                }
//            }else{
//                textToSpeech.speak("Lo sentimos, aun no tenemos cobertura sobre este lugar",TextToSpeech.QUEUE_FLUSH,null);
//            }
//        }else if (cadena.contains("Anton")){
//            textToSpeech.speak("Existe servicio, pero no esta cubierto.",TextToSpeech.QUEUE_FLUSH,null);
//        }
//
//        if (cadena.contains("Dónde") || cadena.contains("Cuál") || cadena.contains("dónde") || cadena.contains("cuál")){
//            if (cadena.contains("ubicación") || cadena.contains("encuentro") || cadena.contains("estoy")){
//                String auxUserAddress = "";
//                if (userAddress != null){
//                    if (rstlocation == false){
//                        textToSpeech.speak("Tu ubicación es la siguiente " + userAddress,TextToSpeech.QUEUE_FLUSH,null);
//                        moveCamera(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()),15f,null);
//                        auxUserAddress = userAddress;
//
//                    }else{
//                        if (!auxUserAddress.equals(userAddress)){
//                            textToSpeech.speak("Tu ubicación es la siguiente " + userAddress,TextToSpeech.QUEUE_FLUSH,null);
//                            moveCamera(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()),15f,null);
//                            auxUserAddress = userAddress;
//                        }
//                    }
//                }
//            }
//        }
        if (textToSpeech.isSpeaking()){
            textToSpeech.speak(cadena,TextToSpeech.QUEUE_ADD,null);
        }else if (!textToSpeech.isSpeaking()){
            textToSpeech.speak(cadena,TextToSpeech.QUEUE_FLUSH,null);
        }
        //textToSpeech.speak("",TextToSpeech.QUEUE_FLUSH,null);
    }

    private void setupVoiceRecognicion(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, ¿Cómo puedo ayudarte?");

        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException e){
            //code error
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    texto_Voz(result.get(0));
//                    getVoiceData = result.get(0);
                }
            }
        }
    }

    /*CLASE LOCALIZACION*/
    public class Localizacion implements LocationListener{
        MapsActivity mapsActivity;

        public MapsActivity getMapsActivity() {
            return mapsActivity;
        }

        public void setMapsActivity(MapsActivity mapsActivity){
            this.mapsActivity = mapsActivity;
        }

        @Override
        public void onLocationChanged(Location location) {
            location.getLatitude();
            location.getLongitude();
            this.mapsActivity.setLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            switch (i){
                case LocationProvider.AVAILABLE:
                    Log.d("debug","LocationProvider.AVAILABLE");
                break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug","LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug","LocationProvider.TEMPORALY_UNAVAILABLE");
                break;

            }
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(mapsActivity.getApplicationContext(),"GPS Activado",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(mapsActivity.getApplicationContext(),"GPS Desactivado",Toast.LENGTH_LONG).show();
            textToSpeech.speak("El GPS ha sido desactivado, el funcionamiento del sistema se vera afectado.",TextToSpeech.QUEUE_FLUSH,null);
        }
    }


}
