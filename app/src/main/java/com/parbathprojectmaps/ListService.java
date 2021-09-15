package com.parbathprojectmaps;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.parbathprojectmaps.Models.ToiletPlaces;
import com.parbathprojectmaps.util.CustomAdapter;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nullable;

public class ListService extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "INFO LIST > ";
    ArrayList<ToiletPlaces> services = new ArrayList<>();
    ListView listView;

    public FirebaseFirestore serviceDB = FirebaseFirestore.getInstance();

    private static CustomAdapter adapter;
    private String placeToFind;

//    Variable de Text to Speech y respuesta por voz (voz recognizer)
    TextToSpeech textoAvoz;
    private static final int REQ_CODE_SPEECH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_service);

        Bundle bundle = new Bundle(getIntent().getExtras());
        placeToFind = bundle.getString("lugarService");

        newQueryforPlaceSelected(placeToFind);

        listView = (ListView) findViewById(R.id.list);

        textoAvoz = new TextToSpeech(this,this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        texto_a_voz("Has vuelto a la lista de servicios de "+ placeToFind);
//        textoAvoz.speak("en Resumen",TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        textoAvoz.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void newQueryforPlaceSelected(String placeToFind) {
        CollectionReference referenciLista = serviceDB.collection(getString(R.string.getLugares));
        Log.d(TAG,"Lugar a consultar " + placeToFind);
        Query query = referenciLista.
              whereEqualTo("name_place",placeToFind);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: Listen lugaresServices failed.", e);
                    Toast.makeText(ListService.this, "Error Lista: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    services.clear();
                    services = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        ToiletPlaces toiletInfo = doc.toObject(ToiletPlaces.class);
                        services.add(toiletInfo);
                        Log.d(TAG,"Info List " + toiletInfo.getName_place() + ", " +
                                toiletInfo.getServiceType() + ", "+toiletInfo.getUbicationReference());
                    }
                    makingTheList();
                }
            }
        });
        Log.d(TAG,"Que! ya! ");
    }// fin del metodo

    public void makingTheList(){
        adapter = new CustomAdapter(services,getApplicationContext());
        texto_a_voz("La cantidad de servicios en "+ placeToFind +" es de " + Integer.toString(services.size()));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToiletPlaces servs = services.get(i);

                String idservicio = servs.getIdService();
                String lugar = servs.getName_place();
                String ubicacion = servs.getUbicationReference();
                String horario = servs.getSchedules();

                String descripcion = servs.getDescription();
                String tipo = servs.getServiceType();
                ArrayList<String> fotos = servs.getImages();
                Log.d(TAG," INFORMACION FOTO: " + fotos);
                informacionServicioSelecionado(lugar, ubicacion, horario, descripcion, tipo, idservicio,fotos);
            }
        });
    }

    private void informacionServicioSelecionado(String lugar, String ubicacion, String horario
            , String descripcion, String tipo, String idservicio, ArrayList<String> fotos) {
        Intent intent = new Intent(this, InformacionToilet.class);
        intent.putExtra("keyLugar",lugar);
        intent.putExtra("keyTipo", tipo);
        intent.putExtra("keyUbicacion",ubicacion);
        intent.putExtra("keyHorario",horario);
        intent.putExtra("keyDesc", descripcion);
        intent.putExtra("keyid",idservicio);
        intent.putExtra("keyFotos", fotos);
        texto_a_voz("Presentando información");
        textoAvoz.stop();
        startActivity(intent);
    }

    public void texto_a_voz(String cadena){
        textoAvoz.speak(cadena,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            int idioma = textoAvoz.setLanguage(Locale.getDefault());
            if (idioma == TextToSpeech.LANG_NOT_SUPPORTED || idioma == TextToSpeech.LANG_MISSING_DATA){
//                code idioma no soportado
                Toast.makeText(this,"Lenguaje para TextToSpeech no soportado",Toast.LENGTH_LONG).show();
            }else {
//                Exito
            }
        }
    } // fin onInit

    public void voiceRecognition(){
        Intent intento = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intento.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intento.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intento.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hola, ¿Cómo puedo ayudarte?");

        try{
            startActivityForResult(intento,REQ_CODE_SPEECH);
        }catch (ActivityNotFoundException e ){
            //code for error
        }
    } // fin voiceRecognition

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH: {
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    texto_a_voz(resultado.get(0));
                }
            }
        }
    }
}
