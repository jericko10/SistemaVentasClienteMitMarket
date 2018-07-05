package com.grezzoss.sistemaventascliente;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.grezzoss.sistemaventascliente.conexiones.Conexion;

import java.util.ArrayList;

public class RegistroActivity extends AppCompatActivity {
    public static boolean flagBaseDatos=false;
    private EditText numeroDNI;
    private Button buttonIngresar;
    private int dniValor = 0;
    private Conexion conexion;

    RelativeLayout rellay1;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        numeroDNI = findViewById(R.id.numeroDNI);
        buttonIngresar = findViewById(R.id.buttonIngresar);
        rellay1 = findViewById(R.id.rellay1);
        handler.postDelayed(runnable, 2000); //2000 tiempo de efecto splash
buttonIngresar.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick (View v){



    dniValor=numeroDNI.length();

    if(dniValor==8){
        conexion = new Conexion();
        Intent i = new Intent(RegistroActivity.this,ScannerActivity.class);
        i.putExtra("dni",numeroDNI.getText().toString());
        flagBaseDatos = true;
        i.putExtra("flagBaseDatos",flagBaseDatos);
        startActivity(i);
    }
    else {
        Toast.makeText(getApplicationContext(), "DNI debe contener 8 dígitos" , Toast.LENGTH_LONG).show();
    }

}} );


    }



}
