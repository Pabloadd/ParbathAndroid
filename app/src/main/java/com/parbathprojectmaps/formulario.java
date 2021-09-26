package com.parbathprojectmaps;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parbathprojectmaps.Models.Reporte;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class formulario extends AppCompatActivity implements TextToSpeech.OnInitListener {

//    ToiletPlace toiletPlace;
    RatingBar valo;
    Button btn4;
    String ubicaionReporte = "";
    TextToSpeech tts;
    //Elementos del formulario
    EditText eNombre, eApellido, eOpinion;
    String infointerface = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        Bundle bundle = new Bundle(getIntent().getExtras());
        ubicaionReporte = bundle.getString("ubicacion");
        tts = new TextToSpeech(this,this);
        eNombre = (EditText) findViewById(R.id.edtNombre);
        eApellido = (EditText) findViewById(R.id.edtApellido);
        eOpinion = (EditText) findViewById(R.id.edtOpinion);
        valo = (RatingBar) findViewById(R.id.ratingBar);

        btn4 = (Button) findViewById(R.id.button4);
        infointerface = String.valueOf(R.string.interfazreporte);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = String.valueOf(valo.getRating());

                saveReporte(v);

            }
        });

    }// fin onCreate

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveReporte(View ve){


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newReporte = db.collection("Reportes")
                .document();
        final View v = ve;
        String fecha_actual = getLocalDate();
        Reporte reporte = new Reporte();
        reporte.setNombre(eNombre.getText().toString());
        reporte.setApellido(eApellido.getText().toString());
        reporte.setOpinion(eOpinion.getText().toString());
        reporte.setRatestars(valo.getRating());
        reporte.setTimestamp(fecha_actual);
        reporte.setUbicacionReporte(ubicaionReporte);
        reporte.setReport_id(newReporte.getId());


        newReporte.set(reporte).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Snackbar .make(v,"Reporte Enviado",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                    clearFormulario();

                }else{
                    Snackbar .make(v,"Fallo Envio de Reporte",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            }
        });
    }//fin saveReporte

    public void clearFormulario(){
        eNombre.setText("");
        eApellido.setText("");
        eOpinion.setText("");
        valo.setRating(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getLocalDate(){
        LocalDate localDate = LocalDate.now();
        return localDate.toString();
    }

    @Override
    public void onInit(int i) {
        if(i == TextToSpeech.SUCCESS){
            int lang = tts.setLanguage(Locale.getDefault());
            if (lang == TextToSpeech.LANG_NOT_SUPPORTED || lang == TextToSpeech.LANG_MISSING_DATA){
                Toast.makeText(this, "Lenguaje no soportado", Toast.LENGTH_SHORT).show();

            }else{
                textoavoz(infointerface);
            }
        }
    }

    public void textoavoz(String s){
        tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
    }
}
