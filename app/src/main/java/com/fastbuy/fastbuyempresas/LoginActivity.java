package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Config.Globales;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    EditText etNombre,etClave;
    ProgressDialog proDialog= null;
    Typeface script,script2;
    TextView txtTitulo, txtNombretema, txtclavet, txtusuariot,txtOlvidotema;
    CheckBox chkRecordar;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etNombre= (EditText) findViewById(R.id.etNombre);
        etClave= (EditText) findViewById(R.id.etClave);
        Button btnIngresar= (Button) findViewById((R.id.btnIngresar));
        txtusuariot=(TextView) findViewById(R.id.txtUsuariot);
        txtclavet=(TextView) findViewById(R.id.txtclavet);
        txtNombretema= (TextView) findViewById(R.id.txtEmpresaTema);
        txtTitulo=(TextView) findViewById(R.id.txtTitulo);
        //txtOlvidotema=(TextView) findViewById(R.id.txtOlvidotema);
        chkRecordar = (CheckBox) findViewById(R.id.chkRecordar);

        String fuente="fonts/Riffic.ttf";
        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente);
        this.script2= Typeface.createFromAsset(getAssets(),fuente2);
        txtTitulo.setTypeface(script);
        txtNombretema.setTypeface(script2);
        txtclavet.setTypeface(script2);
        txtusuariot.setTypeface(script2);
        etNombre.setTypeface(script2);
        etClave.setTypeface(script2);
        //txtOlvidotema.setTypeface(script2);
        btnIngresar.setTypeface(script2);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        myEditor = myPreferences.edit();
        etNombre.setText(myPreferences.getString("USUARIO_GUARDADO", ""));
        etClave.setText(myPreferences.getString("CLAVE_GUARDADA", ""));
        chkRecordar.setChecked(myPreferences.getBoolean("RECORDAR_CLAVE", false));
        if(!isNetDisponible()){
            Intent intent=new Intent(LoginActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        /*chkRecordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                myEditor.putBoolean("RECORDAR_CLAVE", isChecked);
                myEditor.commit();
            }
        });*/
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNombre.getText().length() == 0) {
                    Toast toast = Toast.makeText(LoginActivity.this, "Ingrese Nombre de Empresa", Toast.LENGTH_SHORT);
                    View vistatoast = toast.getView();
                    vistatoast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    etNombre.requestFocus();
                    return;
                }
                if (etClave.getText().length() == 0) {
                    Toast toast = Toast.makeText(LoginActivity.this, "Ingrese su clave de acceso.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    etClave.requestFocus();
                    return;
                }
                String user = URLEncoder.encode(etNombre.getText().toString());
                String clave = URLEncoder.encode(etClave.getText().toString());
                String consulta = Globales.servidor + "/Empresas/Verificar?auth=" + Globales.token + "&usuario=" + user + "&clave=" + clave;
                proDialog = new ProgressDialog(LoginActivity.this);
                proDialog.setMessage("Verificando acceso ...");
                proDialog.setIndeterminate(true);
                proDialog.setCancelable(false);
                proDialog.show();

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length() > 0) {
                            try {
                                JSONObject objeto = new JSONObject(response);
                                String respuesta = objeto.getString("mensaje");
                                if (respuesta.equals("usuario")) {
                                    Toast toast = Toast.makeText(LoginActivity.this, "Usuario incorrecto.", Toast.LENGTH_SHORT);
                                    View vistaToast = toast.getView();
                                    vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                                    toast.show();
                                    Toast.makeText(LoginActivity.this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                                    etNombre.requestFocus();
                                }
                                if (respuesta.equals("clave")) {
                                    Toast toast = Toast.makeText(LoginActivity.this, "Clave incorrecta.", Toast.LENGTH_SHORT);
                                    View vistaToast = toast.getView();
                                    vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                                    toast.show();
                                    etClave.requestFocus();
                                }
                                if (respuesta.equals("ok")) {
                                    myEditor.putBoolean("RECORDAR_CLAVE", false);
                                    if(chkRecordar.isChecked()){
                                        myEditor.putString("USUARIO_GUARDADO", etNombre.getText().toString());
                                        myEditor.putString("CLAVE_GUARDADA", etClave.getText().toString());
                                        myEditor.putBoolean("RECORDAR_CLAVE", true);
                                    }

                                    myEditor.putString("CODIGO_EMPRESA", objeto.getString("codigo"));
                                    myEditor.putString("NOMBRE_EMPRESA", objeto.getString("nombre"));
                                    myEditor.putString("UBICACION", objeto.getString("ubicacion"));
                                    myEditor.putString("RAZON_SOCIAL", objeto.getString("razonsocial"));
                                    myEditor.putString("FOTO_EMPRESA", objeto.getString("imagen"));
                                    myEditor.putString("LOGIN", objeto.getString("login"));
                                    myEditor.putString("SERVICIO", "0");
                                    myEditor.putString("CIERRE", objeto.getString("cierre"));
                                    myEditor.commit();
                                    finish();

                                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                    startActivity(intent);
                                }
                                proDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                proDialog.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        proDialog.dismiss();
                        Intent intent=new Intent(LoginActivity.this, DesconectadoActivity.class);
                        startActivity(intent);
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);
            }
        });




    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }
}
