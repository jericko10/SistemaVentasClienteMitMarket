package com.grezzoss.sistemaventascliente;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
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

import cz.msebera.android.httpclient.Header;

public class DialogCantidad extends AppCompatDialogFragment {

    public String codigoBarraProducto = "";
    String nombreProducto;
    String stockProducto;
    String detalleProducto;
    EditText cantidad;
    boolean flagProductoObtenido = false;
    public String dniCliente = "";
    boolean flagBaseDatos = false;
    private int stockactual = 0;
    private int idProducto = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.cantidad_producto,null);

        builder.setView(view)
                .setTitle("PRODUCTO: AGUA MINERAL - DETALLE: 180ml - STOCK: 25")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i){
                        Toast.makeText(getActivity(),"Producto Agregado",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });



        return builder.create();

    }

    public void obtenerProductoEscaneado(){
        if(flagBaseDatos) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                com.loopj.android.http.AsyncHttpClient client = new com.loopj.android.http.AsyncHttpClient();
                client.get("http://192.168.0.6/SistemaVent08a/listar_productos.php?codigoBarraProducto=5901234123457", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int success, Header[] headers, byte[] bytes) {
                        if (success == 200) {
                            try {
                                //recibiendo los valores del JSON
                                JSONArray jsonArray=new JSONArray(new String(bytes));
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    nombreProducto = jsonArray.getJSONObject(i).getString("nombre");
                                    detalleProducto = jsonArray.getJSONObject(i).getString("detalle");
                                    stockProducto = jsonArray.getJSONObject(i).getString("stock");
                                    stockactual = jsonArray.getJSONObject(i).getInt("stock");
                                    idProducto = jsonArray.getJSONObject(i).getInt("idProducto");
                                    flagProductoObtenido = true;
                                }
                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(getActivity(),"Ocurrió un problema...",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getActivity(),"Ocurrió un problema...",Toast.LENGTH_SHORT).show();
                    }
                });

            if(!flagProductoObtenido){
                Toast.makeText(getActivity(),"Ocurrió un problema...",Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){

        }
        }}


    public void guardarProductoEsperaCliente(){
        if(flagBaseDatos) {
            stockactual = stockactual - Integer.parseInt(String.valueOf(cantidad.getText()));
            if(!ScannerActivity.flagProductoEsperaClienteGuardado) {
                if (stockactual >= 0) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("dniCliente", codigoBarraProducto);
                        List l = new LinkedList();
                        l.addAll(Arrays.asList(jsonParam));
                        String jsonString = JSONValue.toJSONString(l);
                        jsonString = URLEncoder.encode(jsonString, "UTF-8");
                        String url = "http://192.168.0.6/SistemaVenta/registrarproductoesperacliente.php";
                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("User-Agent", "Mozilla/5.0");
                        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        String urlParameters = "json=" + jsonString;
                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(urlParameters);
                        wr.flush();
                        wr.close();

                        JSONObject jsonParam1 = new JSONObject();
                        jsonParam1.put("dniCliente", codigoBarraProducto);
                        List l1 = new LinkedList();
                        l.addAll(Arrays.asList(jsonParam1));
                        String jsonString1 = JSONValue.toJSONString(1l);
                        jsonString1 = URLEncoder.encode(jsonString1, "UTF-8");
                        String url1 = "http://192.168.0.6/SistemaVenta/obtenerultimoid.php";
                        URL obj1 = new URL(url1);
                        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
                        con1.setRequestMethod("GET");
                        con1.setRequestProperty("User-Agent", "Mozilla/5.0");
                        con1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        String urlParameters1 = "json=" + jsonString1;
                        con1.setDoOutput(true);
                        DataOutputStream wr1 = new DataOutputStream(con.getOutputStream());
                        wr1.writeBytes(urlParameters1);
                        wr1.flush();
                        wr1.close();
                        int respuesta1 = con.getResponseCode();
                        StringBuilder result1 = new StringBuilder();
                        if (respuesta1 == HttpURLConnection.HTTP_OK) {
                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                result1.append(line);
                            }
                            JSONObject respuestaJSON = new JSONObject(result1.toString());
                            ScannerActivity.idEspera = respuestaJSON.getInt("id");
                            br.close();
                        }

                        JSONObject jsonParam2 = new JSONObject();
                        jsonParam2.put("idEspera", ScannerActivity.idEspera);
                        jsonParam2.put("codigo_barra", codigoBarraProducto);
                        jsonParam2.put("dniCliente", dniCliente);
                        jsonParam2.put("cantidad", cantidad.getText());
                        List l2 = new LinkedList();
                        l.addAll(Arrays.asList(jsonParam2));
                        String jsonString2 = JSONValue.toJSONString(l2);
                        jsonString2 = URLEncoder.encode(jsonString2, "UTF-8");
                        String url2 = "http://192.168.0.6/SistemaVenta/registrarproductoesperaclientedetalle.php";
                        URL obj2 = new URL(url2);
                        HttpURLConnection c = (HttpURLConnection) obj2.openConnection();
                        c.setRequestMethod("POST");
                        c.setRequestProperty("User-Agent", "Mozilla/5.0");
                        c.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        String urlParameters2 = "json=" + jsonString2;
                        c.setDoOutput(true);
                        DataOutputStream wr2 = new DataOutputStream(con.getOutputStream());
                        wr2.writeBytes(urlParameters2);
                        wr2.flush();
                        wr2.close();

                        JSONObject jsonParam3 = new JSONObject();
                        jsonParam3.put("idProducto", idProducto);
                        jsonParam3.put("stock", stockactual);
                        List l3 = new LinkedList();
                        l.addAll(Arrays.asList(jsonParam3));
                        String jsonString3 = JSONValue.toJSONString(l3);
                        jsonString3 = URLEncoder.encode(jsonString3, "UTF-8");
                        String url3 = "http://192.168.0.6/SistemaVenta/actualizarstockproducto.php";
                        URL obj3 = new URL(url3);
                        HttpURLConnection c3 = (HttpURLConnection) obj3.openConnection();
                        c3.setRequestMethod("POST");
                        c3.setRequestProperty("User-Agent", "Mozilla/5.0");
                        c3.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        String urlParameters3 = "json=" + jsonString3;
                        c3.setDoOutput(true);
                        DataOutputStream wr3 = new DataOutputStream(con.getOutputStream());
                        wr3.writeBytes(urlParameters3);
                        wr3.flush();
                        wr3.close();

                        ScannerActivity.flagProductoEsperaClienteGuardado = true;
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "La cantidad es mayor al stock.", Toast.LENGTH_SHORT).show();
                    return;
                }
                } else {
                    if (stockactual >= 0) {
                        try {
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("idEspera", ScannerActivity.idEspera);
                            jsonParam.put("codigo_barra", codigoBarraProducto);
                            jsonParam.put("dniCliente", dniCliente);
                            jsonParam.put("cantidad", cantidad.getText());
                            List l = new LinkedList();
                            l.addAll(Arrays.asList(jsonParam));
                            String jsonString = JSONValue.toJSONString(l);
                            jsonString = URLEncoder.encode(jsonString, "UTF-8");
                            String url = "http://192.168.0.6/SistemaVenta/registrarproductoesperaclientedetalle.php";
                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("User-Agent", "Mozilla/5.0");
                            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                            String urlParameters = "json=" + jsonString;
                            con.setDoOutput(true);
                            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                            wr.writeBytes(urlParameters);
                            wr.flush();
                            wr.close();

                            JSONObject jsonParam3 = new JSONObject();
                            jsonParam3.put("idProducto", idProducto);
                            jsonParam3.put("stock", stockactual);
                            List l3 = new LinkedList();
                            l.addAll(Arrays.asList(jsonParam3));
                            String jsonString3 = JSONValue.toJSONString(l3);
                            jsonString3 = URLEncoder.encode(jsonString3, "UTF-8");
                            String url3 = "http://192.168.0.6/SistemaVenta/actualizarstockproducto.php";
                            URL obj3 = new URL(url3);
                            HttpURLConnection c3 = (HttpURLConnection) obj3.openConnection();
                            c3.setRequestMethod("POST");
                            c3.setRequestProperty("User-Agent", "Mozilla/5.0");
                            c3.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                            String urlParameters3 = "json=" + jsonString3;
                            c3.setDoOutput(true);
                            DataOutputStream wr3 = new DataOutputStream(con.getOutputStream());
                            wr3.writeBytes(urlParameters3);
                            wr3.flush();
                            wr3.close();

                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "La cantidad es mayor al stock.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
    }
}
