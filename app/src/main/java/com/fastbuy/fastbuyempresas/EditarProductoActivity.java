package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class EditarProductoActivity extends AppCompatActivity {

    TextInputEditText txtNombreProducto;
    TextInputEditText txtDescripcionProducto;
    ImageView ivImagenProducto;
    ProgressDialog proDialog;
    SharedPreferences mypreferences;
    String codigo, presentacion, ubicacion;
    TextView txtPresentacion, txtPrecio, txtPrecioVenta;
    Button btnGuardar;
    Switch swEstado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);
        mypreferences = PreferenceManager.getDefaultSharedPreferences(EditarProductoActivity.this);

        txtNombreProducto = (TextInputEditText) findViewById(R.id.txtNombreProducto);
        txtDescripcionProducto = (TextInputEditText) findViewById(R.id.txtDescripcionProducto);
        txtPrecio = (TextView) findViewById(R.id.txtPrecio);
        txtPresentacion = (TextView) findViewById(R.id.txtPresentacion);
        ivImagenProducto = (ImageView) findViewById(R.id.ivImagenProducto);
        swEstado = (Switch) findViewById(R.id.swEstado);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        codigo = mypreferences.getString("producto_seleccionado", "0");
        presentacion = mypreferences.getString("presentacion_seleccionada", "0");
        ubicacion = mypreferences.getString("UBICACION", "0");
        CargarProducto();
        btnGuardar.setEnabled(false);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarCambios();
            }
        });
    }

    void CargarProducto(){

        proDialog = new ProgressDialog(EditarProductoActivity.this);
        proDialog.setMessage("Cargando...");
        proDialog.setIndeterminate(true);
        proDialog.setCancelable(false);
        proDialog.show();
        String consulta= "http://apifbempresas.fastbuych.com/Empresas/MostrarProductoPorEmpresa?codigo=" + codigo + "&ubicacion=" + ubicacion + "&presentacion=" + presentacion;
        RequestQueue queue = Volley.newRequestQueue(EditarProductoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject object = new JSONObject(response);
                        String nombre = object.getString("nombre");
                        String descripcion = object.getString("descripcion");
                        String precio = object.getString("precio");
                        String presentacion = object.getString("presentacion");
                        String precioventa = object.getString("precioventa");
                        String imagen = object.getString("imagen");
                        String tiempo = object.getString("tiempo");
                        String estado = object.getString("estado");
                        String url= Globales.servidorfotosproductos+"/fotos/"+imagen;
                        if(estado.equals("0")){
                            swEstado.setChecked(true);
                        }else{
                            swEstado.setChecked(false);
                        }
                        btnGuardar.setEnabled(true);
                        txtNombreProducto.setText(nombre);
                        txtDescripcionProducto.setText(descripcion);
                        txtPresentacion.setText(presentacion);
                        txtPrecio.setText("S/ " + precio);
                        Glide.with(EditarProductoActivity.this)
                                .load(url)
                                .placeholder ( R.mipmap.ic_launcher)
                                .centerCrop()
                                .error(R.mipmap.ic_launcher)
                                .into(ivImagenProducto);
                        proDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        proDialog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                proDialog.dismiss();
                Intent intent=new Intent(EditarProductoActivity.this, DesconectadoActivity.class);
                startActivity(intent);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    void GuardarCambios(){
        proDialog = new ProgressDialog(EditarProductoActivity.this);
        proDialog.setMessage("Aplicando cambios...");
        proDialog.setIndeterminate(true);
        proDialog.setCancelable(false);
        proDialog.show();
        String nombre = URLEncoder.encode(txtNombreProducto.getText().toString());
        String descripcion = URLEncoder.encode(txtDescripcionProducto.getText().toString());
        String estado = "0";
        if(swEstado.isChecked()){
            estado = "0";
        }else{
            estado = "1";
        }
        String consulta= "http://apifbempresas.fastbuych.com/Empresas/GuardarProductoPorEmpresa?codigo=" + codigo + "&ubicacion=" + ubicacion + "&presentacion=" + presentacion  + "&nombre="+ nombre+ "&descripcion="+ descripcion+ "&estado=" + estado ;
        RequestQueue queue = Volley.newRequestQueue(EditarProductoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject object = new JSONObject(response);
                        String respuesta = object.getString("respuesta");
                        if(respuesta.equals("actualizado")){
                            Toast.makeText(EditarProductoActivity.this, "Tu producto ha sido actualizado.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EditarProductoActivity.this, "Ocurrió un error, inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                        proDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        proDialog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                proDialog.dismiss();
                Intent intent=new Intent(EditarProductoActivity.this, DesconectadoActivity.class);
                startActivity(intent);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}
