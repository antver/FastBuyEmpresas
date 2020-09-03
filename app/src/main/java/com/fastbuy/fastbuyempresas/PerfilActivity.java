package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Config.ServicioPartner;
import com.fastbuy.fastbuyempresas.Entidades.Empresa;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class PerfilActivity extends AppCompatActivity {
    Button btnAceptarcambio, btnCancelarcambio,ivcoinciden;
    EditText txtcontra,txtcontraN,txtcontraNR, etusuario;
    Typeface script;
    TextView txtnocoindiden;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnAceptarcambio= (Button) findViewById(R.id.btnAceptarp);
        btnCancelarcambio= (Button) findViewById(R.id.btnCancelarp);
        txtcontra= (EditText) findViewById(R.id.txtContrasenactual);
        txtcontraN= (EditText) findViewById(R.id.txtContrasena);
        txtcontraNR= (EditText) findViewById(R.id.txtContrasenaRepet);
        txtnocoindiden= (TextView) findViewById(R.id.txtNoCoinciden);
        ivcoinciden= (Button) findViewById(R.id.ivCoinciden);
        etusuario=(EditText) findViewById(R.id.etUsuario);

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        etusuario.setTypeface(script);
        txtcontra.setTypeface(script);
        txtcontraN.setTypeface(script);
        txtcontraNR.setTypeface(script);
        btnAceptarcambio.setTypeface(script);
        btnCancelarcambio.setTypeface(script);
        if(isNetDisponible()){
            //Toast.makeText(PerfilActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(PerfilActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        limpiar();
        //cierraTeclado();
        final SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(PerfilActivity.this);
        final String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String login= mypreferences.getString("LOGIN","unknown");

        final String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        final String codigou= mypreferences.getString("UBICACION","unknown");
        final String cierre= mypreferences.getString("CIERRE","unknown");
        etusuario.setText(login);
        btnAceptarcambio.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                String clave= URLEncoder.encode(txtcontra.getText().toString());
                String claven= URLEncoder.encode(txtcontraN.getText().toString());
                String clavenr= URLEncoder.encode(txtcontraNR.getText().toString());
                String usuario= URLEncoder.encode(etusuario.getText().toString());
                if(txtcontraNR.getText().length()==0){
                    Toast.makeText(PerfilActivity.this,"Ingrese Contraseña Nueva A Comprobar",Toast.LENGTH_SHORT).show();
                }
                if(txtcontraN.getText().length()==0){
                    Toast.makeText(PerfilActivity.this,"Ingrese Contraseña Nueva",Toast.LENGTH_SHORT).show();
                }
                if(txtcontra.length()==0){
                    Toast.makeText(PerfilActivity.this,"Ingrese Contraseña Actual",Toast.LENGTH_SHORT).show();
                }
                if(usuario.length()==0){
                    Toast.makeText(PerfilActivity.this,"Ingrese Usuario",Toast.LENGTH_SHORT).show();
                }
                if(txtcontra.getText().length()!=0 && txtcontraN.getText().length()!=0 && txtcontraNR.length()!=0){
                    if(claven.equals(clavenr)){
                        String url=Globales.servidor + "/Empresas/CambioContra?auth=" + Globales.token+"&codigo="+codigoe+"&codigou="+codigou+"&clave="+clave+"&claven="+clavenr+"&usuario="+usuario;
                        String tipo="Desea Cambiar de Contraseña?";
                        String opcion="A";
                        //Toast.makeText(PerfilActivity.this,"asd",Toast.LENGTH_SHORT).show();
                        createCustomDialog(url,tipo,nombreempresa,opcion).show();
                    }else{
                        Toast.makeText(PerfilActivity.this,"La Nueva Contraseña No Coincide"+claven+"-"+clavenr,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnCancelarcambio.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                        finish();
                }
        });
        txtcontraNR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String claven= txtcontraN.getText().toString();
                String clavenr= txtcontraNR.getText().toString();
                if(claven.equals(clavenr)){
                    txtnocoindiden.setVisibility(View.GONE);
                    ivcoinciden.setVisibility(View.VISIBLE);
                }
                else{
                    txtnocoindiden.setVisibility(View.VISIBLE);
                    ivcoinciden.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public AlertDialog createCustomDialog(final String url,String tipo, String nombreempresa, final String opcion) {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
        final SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(PerfilActivity.this);
        final SharedPreferences.Editor editor = mypreferences.edit();
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflar y establecer el layout para el dialogo
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        View v = inflater.inflate(R.layout.dialog_aceptar, null);
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        TextView txtNombreRepartidor = (TextView) v.findViewById(R.id.txtEmpresa);
        TextView txtAlerta= (TextView) v.findViewById(R.id.txtDialog);
        txtAlerta.setText(tipo);
        txtNombreRepartidor.setText(nombreempresa);
        Button btnSi = (Button) v.findViewById(R.id.btnSi);
        Button btnNo = (Button) v.findViewById(R.id.btnNo);

        btnSi.setTypeface(script);
        btnNo.setTypeface(script);
        txtNombreRepartidor.setTypeface(script);
        txtAlerta.setTypeface(script);

        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        btnSi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(opcion=="A"){
                            String consulta= url;
                            RequestQueue queue = Volley.newRequestQueue(PerfilActivity.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.length()>0){
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            String respuesta = object.getString("mensaje");
                                            if(respuesta.equals("clave")){
                                                Toast toast = Toast.makeText(PerfilActivity.this, "Contraseña Invalida", Toast.LENGTH_SHORT);
                                                View vistaToast = toast.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                                                toast.show();
                                                txtcontra.setText("");
                                                txtcontra.requestFocus();
                                                alertDialog.dismiss();
                                            }
                                            if(respuesta.equals("usuario")){
                                                Toast toast = Toast.makeText(PerfilActivity.this, "Usuario ya Existe", Toast.LENGTH_SHORT);
                                                View vistaToast = toast.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                                                toast.show();
                                                txtcontra.setText("");
                                                txtcontra.requestFocus();
                                                alertDialog.dismiss();
                                            }
                                            if(respuesta.equals("ok")){
                                                Toast toast = Toast.makeText(PerfilActivity.this, "Exito al cambiar la Contraseña", Toast.LENGTH_SHORT);
                                                View vistaToast = toast.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                                toast.show();
                                                Intent intent= new Intent(PerfilActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                                alertDialog.dismiss();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            alertDialog.dismiss();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alertDialog.dismiss();
                                }
                            });
                            queue.add(stringRequest);
                        }

                    }
                }
        );
        btnNo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                }
        );
        return alertDialog;
    }
    public void limpiar(){
        txtcontra.setText("");
        txtcontraN.setText("");
        txtcontraNR.setText("");
    }
    private void killActivity() {
        finish();
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
    /*public void cierraTeclado(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }*/
}
