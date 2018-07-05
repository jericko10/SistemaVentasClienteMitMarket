package com.grezzoss.sistemaventascliente;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import java.net.*;
import java.io.*;
import org.json.simple.JSONValue;
import android.os.*;

import com.google.zxing.Result;
import com.grezzoss.sistemaventascliente.modulo.LectorEstadoProductoEsperaCliente;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public boolean flagBaseDatos = false;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static String dni;
    static boolean flagProductoEsperaClienteGuardado = false;
    static int idEspera = 0;
    public static boolean flagEsperaProductoCliente = false;
    private volatile Thread hiloEstadoProducto;
    private LectorEstadoProductoEsperaCliente productoClienteEspera;
    public String nombreCaja = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;
        Bundle datoDNI=getIntent().getExtras();
        dni=datoDNI.getString("dni");
        Bundle datoFlagBaseDatos = getIntent().getExtras();
        flagBaseDatos = datoFlagBaseDatos.getBoolean("flagBaseDatos");

        if(currentApiVersion >=  Build.VERSION_CODES.KITKAT)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permiso concedido", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }

    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permiso concedido, puedes acceder a la cámara", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permiso denegado, no tienes acceso a la cámara", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("Debes conceder permiso para utilizar la aplicación",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("CANCELAR", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String codigoBarraProducto = result.getText();
        Log.d("SistemaVentasCliente", result.getText());
        Log.d("SistemaVentasCliente", result.getBarcodeFormat().toString());
        abrirOpenDialogCantidad(codigoBarraProducto);
        onResume();
        if(idEspera != 0){
            productoClienteEspera = new LectorEstadoProductoEsperaCliente(idEspera,this);
            hiloEstadoProducto = new Thread(productoClienteEspera);
            productoClienteEspera.setLectorEstadoProductoEsperaCliente(hiloEstadoProducto);
            hiloEstadoProducto.start();
        }
    }

    public void abrirOpenDialogCantidad(String codigoBarraProducto){
        DialogCantidad cantidad = new DialogCantidad();
        cantidad.codigoBarraProducto = codigoBarraProducto;
        cantidad.dniCliente = dni;
        cantidad.flagBaseDatos = flagBaseDatos;
        cantidad.show(getSupportFragmentManager(),"cantidad");
    }
}

