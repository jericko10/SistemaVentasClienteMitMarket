package com.grezzoss.sistemaventascliente.conexiones;

import android.os.StrictMode;
import android.preference.PreferenceActivity;

import com.grezzoss.sistemaventascliente.RegistroActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class Conexion {

    public static void comprobarConexion()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        com.loopj.android.http.AsyncHttpClient client=new com.loopj.android.http.AsyncHttpClient();
        client.get("http://192.168.44.33/SistemaVenta/comprobar_conexion.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String comprobar_conexion;
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            comprobar_conexion = jsonArray.getJSONObject(i).getString("estado");
                            if (comprobar_conexion.equals("1")) {
                                RegistroActivity.flagBaseDatos = true;
                            } else if (comprobar_conexion.equals("2")) {
                                RegistroActivity.flagBaseDatos = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
