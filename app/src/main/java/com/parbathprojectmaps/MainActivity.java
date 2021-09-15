package com.parbathprojectmaps;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button ubi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ubi = (Button) findViewById(R.id.button);

    }

    public void vamonos(View v){
        Intent i = new Intent(MainActivity.this,MapsActivity.class);
        startActivity(i);
    }


}
