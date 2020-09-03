package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;

public class ReportarErrorActivity extends AppCompatActivity {
    ProgressDialog progressDialog = null;
    EditText etMensaje;
    String codempresa, codubicacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_error);
        etMensaje = (EditText) findViewById(R.id.etMensaje);
        Button btnEnviarReporte = (Button) findViewById(R.id.btnEnviarReporte);
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        codempresa= mypreferences.getString("CODIGO_EMPRESA","unknown");
        codubicacion= mypreferences.getString("UBICACION","unknown");
        btnEnviarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMensaje.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(), "Ingresa la descripción de tu problema.", Toast.LENGTH_SHORT).show();
                    etMensaje.setFocusable(true);
                    return;
                }
                progressDialog = new ProgressDialog(ReportarErrorActivity.this);
                progressDialog.setMessage("Enviando...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                String mensaje = URLEncoder.encode(etMensaje.getText().toString());
                String consulta = "https://www.apifbempresas.fastbuych.com/Empresas/ReportarProblema?empresa="+codempresa+"&ubicacion=" + codubicacion + "&mensaje=" + mensaje;
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>0){
                            //try {
                                //list = new ArrayList<>();
                                //JSONArray lista = new JSONArray(response);
                                //String nombreportada = "", imagenproducto = "";
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Reporte enviado.", Toast.LENGTH_SHORT).show();
                                finish();
                            //}
                            //catch (JSONException e) {
                                //e.printStackTrace();
                                //progressDialog.dismiss();
                            //}
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                });

                queue.add(stringRequest);
            }
        });
    }
}
