package com.parbathprojectmaps;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.parbathprojectmaps.Models.ServiceLocation;
import com.parbathprojectmaps.util.CustomAdpaterMapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class AlternativaMapa extends AppCompatActivity implements TextToSpeech.OnInitListener{

    ArrayList<ServiceLocation> serviceLocations = new ArrayList<>();
    ListView listView;
    private static CustomAdpaterMapa customAdpaterMapa;
    private FirebaseFirestore mDb;
    private TextToSpeech textToSpeech;
    private Location location_user;
    ImageButton gps_btn;
    String address_user;

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeech.speak("Has vuelto a la lista de lugares de interes.",TextToSpeech.QUEUE_FLUSH,null);
    }

    protected void onPause() {
        textToSpeech.stop();
        super.onPause();
    }

    protected void onDestroy() {
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternativa_mapa);

        listView = (ListView) findViewById(R.id.list_servicelocation);
        textToSpeech = new TextToSpeech(this,this);
        mDb = FirebaseFirestore.getInstance();
        gps_btn = (ImageButton) findViewById(R.id.gps_imgbtn);
        Bundle bundle = new Bundle(getIntent().getExtras());
        location_user = (Location) bundle.get("position_user");
        consultaPositionServices();
        sentitizador("Welcome");
        inicializadorBotones();
    }

    private void inicializadorBotones() {
        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String auxAddresUser = getAddressUser();
                textToSpeech.speak("Tu ubicación es la siguiente " + auxAddresUser, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private String getAddressUser(){
        try{
            Geocoder geocoder = new Geocoder(this,Locale.getDefault());
            List<Address> listAddress = geocoder.getFromLocation(location_user.getLatitude(),location_user.getLongitude(),1);
            if(!listAddress.isEmpty()){
                Address direccion = listAddress.get(0);
                address_user = direccion.getLocality() + ", " + direccion.getSubLocality() + ", " + direccion.getAdminArea();
            }else {
                address_user = "No es posible determinar su ubicacion, intente más tarde";
            }
        }catch (Exception e){
            Log.e("ERROR", "while getting user address: " + e.getMessage());
            address_user = "No es posible determinar su ubicacion, intente más tarde";
        }
        return address_user;
    }

    private void sentitizador(String welcome) {
        if (welcome.contains("Welcome")){
            textToSpeech.speak("Alternativa al mapa de navegación, a continuación se presenta" +
                    " una lista de lugares a los que podría ser tu destino, al seleccionar uno " +
                    "podrás saber sobre la cantidad de sanitarios y estacionamientos que posee dicho lugar.",TextToSpeech.QUEUE_FLUSH,null);

        }
    }

    private void consultaPositionServices(){
        final CollectionReference servicesLocation = mDb.collection(getString(R.string.getPosicionLugares));

        Query serviesQuery = servicesLocation;
        serviesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.e("","onEvent: Listen lugaresServies from AlternativeMap failed", e);
                    return;
                }

                if (queryDocumentSnapshots != null){
                    serviceLocations.clear();
                    serviceLocations = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Log.i("LOG AL > ","doc query  "+ doc.getData());
                        ServiceLocation servs = doc.toObject(ServiceLocation.class);
                        Log.i("LOG AL > ","Data object "+ servs);
                        serviceLocations.add(servs);
                    }

                    ListaMarcadores();
                }
            }
        });
    }

    public void ListaMarcadores(){
        customAdpaterMapa = new CustomAdpaterMapa(serviceLocations,getApplicationContext(),location_user);
        listView.setAdapter(customAdpaterMapa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServiceLocation servs = serviceLocations.get(i);
                String nombreLugar = servs.getPlace_description().getName_place();
//                String ubicacion = servs.getLocation_path();
                itemFromListSelected(nombreLugar);
            }
        });
    }

    private void itemFromListSelected(String nombreLugar) {
        Intent intent = new Intent(AlternativaMapa.this,ListService.class);
        intent.putExtra("lugarService",nombreLugar);
        startActivity(intent);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            int language = textToSpeech.setLanguage(Locale.getDefault());
            if (language == TextToSpeech.LANG_NOT_SUPPORTED || language == TextToSpeech.LANG_MISSING_DATA){
                Toast.makeText(getApplicationContext(),"Algo anda mal, TextoSeech",Toast.LENGTH_LONG);
            }else{
                sentitizador("Welcome");
            }
        }
    }
}
