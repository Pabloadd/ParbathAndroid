package com.parbathprojectmaps;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.parbathprojectmaps.Models.Reporte;
import com.parbathprojectmaps.util.CustomAdapterComentarios;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nullable;

public class  InformacionToilet extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "";
    TextView textubiacion, texthorario, textestado;
    ImageView altbtn_reporte;// altbtn_speaker;
    Button reporte;
    TextView  txtmoreInfo;
    ListView listComentarios;

    /* esto pa imagenes */
    private ViewPager slider;
    private SliderAdapter sliderAdapter;
    public int[] list_img;
    public ArrayList<String> list_foto;
    public String[] fotos_string ;
    int lengFotos = 0;

    //Variable de Text to Speech y respuesta por voz (voz recognizer)
    TextToSpeech textToSpeech;
    private static final int REQ_CODE_SPEECH = 100;

    ArrayList<Reporte> arrayListReport = new ArrayList<>();
    private static CustomAdapterComentarios adapterComentarios;

    FirebaseFirestore reporteInfo = FirebaseFirestore.getInstance();

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = fd.getReference("UTP Cocle");

    //widget galeria
    private ImageView imageG1,imageG2,imageG3,imageG4;
    private String iconSelectedForImages, lugarService, idService,
                    ubicacion, horario, descripcion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_toilet);

        //imagenes
        slider = findViewById(R.id.slider);
        sliderAdapter = new SliderAdapter(this);
        slider.setAdapter(sliderAdapter);

        altbtn_reporte = (ImageView) findViewById(R.id.imgbtn_reporte);

        Bundle bundle = new Bundle(getIntent().getExtras());
        iconSelectedForImages = bundle.getString("keyTipo");
        lugarService = bundle.getString("keyLugar");
        idService = bundle.getString("keyid");
        ubicacion = bundle.getString("keyUbicacion");
        horario = bundle.getString("keyHorario");
        descripcion = bundle.getString("keyDesc");
//        list_foto = bundle.getStringArrayList("keyFotos");
//        lengFotos = list_foto.size();
//        pasarData();
//        SliderAdapter a = new SliderAdapter(fotos_string);
        listComentarios = (ListView) findViewById(R.id.listaComentarios);

        textubiacion = (TextView) findViewById(R.id.txtUbicacion);
        texthorario = (TextView) findViewById(R.id.txtHorario);
        textestado = (TextView) findViewById(R.id.txtEstado);
//        reporte = (Button) findViewById(R.id.button2);
        txtmoreInfo = (TextView) findViewById(R.id.txtMoreInfo);

        textubiacion.setText(ubicacion);
        texthorario.setText(horario);
        txtmoreInfo.setText(descripcion);
        textestado.setText("Información no disponible.");
        textToSpeech = new TextToSpeech(this,this);
        textA_Voz("Welcome");
        consultaReporteServicio(lugarService);

//        local.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_toilet, 0, 0, 0);

        altbtn_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent report = new Intent(InformacionToilet.this,formulario.class);
                report.putExtra("ubicacion",lugarService);
                startActivity(report);

            }
        });

        setDataLayout();

    }// fin oncreate

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeech.speak("en resumen",TextToSpeech.QUEUE_FLUSH,null);
    }

    private void pasarData() {
        for(int i=0;i<list_foto.size();i++){
            fotos_string[i] = list_foto.get(i);
        }
        Log.d(TAG,"ARREGLO FOTOS STRING " + fotos_string);
    }


    private void consultaReporteServicio(String lugarService) {
        final CollectionReference refReportes = reporteInfo.collection(getString(R.string.getReportes));

        Query query = refReportes.whereEqualTo("ubicacionReporte",lugarService);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.e(TAG,"onEvent: Listen ReporetesInfo failed ", e);
                    return;
                }
                if (queryDocumentSnapshots != null){
                    arrayListReport.clear();
                    arrayListReport = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Reporte reporte = doc.toObject(Reporte.class);
                        arrayListReport.add(reporte);
                        Log.d(TAG," Reportes Info: " + reporte.getNombre() + " "+
                        reporte.getOpinion());

                    }
//                    Log.d(TAG," FOTOS InfoView: Size  " + list_foto.size());
                    makingListReporte();
                }
            }
        });
    }

    private void makingListReporte() {
        adapterComentarios = new CustomAdapterComentarios(arrayListReport,getApplicationContext());

        listComentarios.setAdapter(adapterComentarios);


    }


    public void setDataLayout(){

        textToSpeech.speak("Hola 3",TextToSpeech.QUEUE_FLUSH,null);
        if (iconSelectedForImages.equals("Estacionamiento")){
//            list_img = new int[]{R.drawable.croquis_utp, R.drawable.parking_utp1, R.drawable.parking_utp2};
//            imageG1.setImageResource(R.drawable.parking_utp1);
//            imageG2.setImageResource(R.drawable.parking_utp2);
//            imageG3.setImageResource(R.drawable.parking_utp3);
//            imageG4.setImageResource(R.drawable.parking_utp1);
//            moreInfo.setText("Estacionamiento espacioso para \nautos grandes");
        }else if(iconSelectedForImages.equals("Baño")){
//            list_img = new int[]{R.drawable.bano1, R.drawable.bano2, R.drawable.bano3};
//            imageG1.setImageResource(R.drawable.bano1);
//            imageG2.setImageResource(R.drawable.bano2);
//            imageG3.setImageResource(R.drawable.bano3);
//            imageG4.setImageResource(R.drawable.bano1);
//            moreInfo.setText("Lavado con borde delantero a 85cm del\n suelo \n\n" +
//                    "Inodoro con altura 47cm ubicado a 40cm\n de la pared \n\n" +
//                    "lateral con asideros horizontales\n" +
//                    "La puerta abre hacia afuera");
        }

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String serviceStatus = "";
                serviceStatus = (dataSnapshot.getValue(String.class));
                if (serviceStatus.isEmpty()){
                    textestado.setText("Información no disponible.");
                }else{
                    textestado.setText(serviceStatus);
                }
                Log.e(TAG,"consulta RTDB: " + serviceStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Fallo en lectura RTDB: "+ databaseError);
            }
        });
    }

//    METODOS DE TEXTO A VOZ Y ASISTENTE DE VOZ

    public void voiceRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hola, ¿Cómo puedo ayudarte?");

        try{
            startActivityForResult(intent, REQ_CODE_SPEECH);
        }catch (ActivityNotFoundException e){
           Toast.makeText(this,"Error voice recogn",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH:{
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textA_Voz(result.get(0));
                }
            }
        }
    }

    public void textA_Voz(String cadena){
        textToSpeech.speak(cadena,TextToSpeech.QUEUE_FLUSH,null);
        if (cadena.contains("Welcome")){
            String string_content = "La informacion del servicio que seleccionaste es la siguiente: "+
                    "Ubicacion" + textubiacion.getText().toString() +
                    ". Horario " + texthorario.getText().toString() +
                    ". El servicio se encuentra: " + textestado.getText().toString() +
                    ". Informacion extra es "+ txtmoreInfo.getText().toString();
            textToSpeech.speak(string_content,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            int lang = textToSpeech.setLanguage(Locale.getDefault());
            if (lang == TextToSpeech.LANG_NOT_SUPPORTED || lang == TextToSpeech.LANG_MISSING_DATA){
                Toast.makeText(this,"Lenguaje soportado",Toast.LENGTH_LONG).show();
            }else{

                textA_Voz("Welcome");
            }
        }
    }





    //  ESTO ES UNA CLASE SOBRE LAS IMAGENES
    private class SliderAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater inflater;
//        private String[] arrayfotos;
//        SliderAdapter(String[] fotos_string){
//            arrayfotos = fotos_string;
//        }


        public SliderAdapter(Context context) {
            this.context = context;
        }

        // list



       int[] list_img = new int[]{R.drawable.croquis_utp, R.drawable.parking_utp1, R.drawable.parking_utp2, R.drawable.bano1, R.drawable.bano2, R.drawable.bano3};


        @Override
        public int getCount() {
            return list_img.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_slider, container, false );
            ImageView imageView = view.findViewById(R.id.img_item);

            imageView.setImageResource(list_img[position]);

//            String uri = "@drawable/"+arrayfotos[position];
//            Log.d(TAG," FOTOS InfoView: Size  " + list_foto.size() +" Nombre"+ list_foto.get(0));
//            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
//            Drawable imagen = ContextCompat.getDrawable(getApplicationContext(), imageResource);
//            imageView.setImageDrawable(imagen);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FullScreen.class);
                    intent.putExtra("posisi", position);
                    startActivity(intent);

                }
            });


            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);

        }


    }
}
