package com.grezzoss.sistemaventascliente.modulo;

import android.app.Activity;
import android.os.StrictMode;
import android.widget.Toast;

import com.grezzoss.sistemaventascliente.ScannerActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by maest on 3/07/2018.
 */

public class LectorEstadoProductoEsperaCliente implements Runnable {

    private volatile Thread lectorEstadoProductoEsperaCliente;
    private final int idEspera;
    private final Activity actividadEscaner;

    public LectorEstadoProductoEsperaCliente(final int idEspera, final Activity actividadEscaner){
        this.idEspera = idEspera;
        this.actividadEscaner = actividadEscaner;
    }

    public void setLectorEstadoProductoEsperaCliente(Thread lectorEstadoProductoEsperaCliente){
        this.lectorEstadoProductoEsperaCliente = lectorEstadoProductoEsperaCliente;
    }

    @Override
    public void run(){
        while(lectorEstadoProductoEsperaCliente == Thread.currentThread()){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("idEspera", idEspera);
                    List l = new LinkedList();
                    l.addAll(Arrays.asList(jsonParam));
                    String jsonString = JSONValue.toJSONString(l);
                    jsonString = URLEncoder.encode(jsonString, "UTF-8");
                    String url = "http://192.168.0.6/SistemaVenta/obtenerespera.php";
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    String urlParameters = "json=" + jsonString;
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();
                    int respuesta = con.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            result.append(line);
                        }
                        JSONObject respuestaJSON = new JSONObject(result.toString());
                        if(!ScannerActivity.flagEsperaProductoCliente){
                            if(respuestaJSON.getString("estado").equals("Listo")){
                                ScannerActivity.flagEsperaProductoCliente = true;
                                Toast.makeText(this.actividadEscaner,"Su producto está en la caja: " + respuestaJSON.getString("nombre"),Toast.LENGTH_LONG).show();
                            }
                        }
                        br.close();
                    }

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Thread.sleep(2000);
            } catch (Exception e) {

            }
        }
    }
}
