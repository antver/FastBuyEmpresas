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
import com.fastbuy.fastbuyempresas.Entidades.Empresa;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class PerfilActivity extends AppCompatActivity {
    TextView txtNombrep, txtRazonp,txtnocoindiden,txtUsuario,txtcampoClave,txtcamponombre,txtrepe;
    ImageView ivFoto,ivcerrarsesion;
    Button btnAceptarcambio, btnCancelarcambio,ivcoinciden;
    EditText txtcontra,txtcontraN,txtcontraNR, etusuario;
    Typeface script;
    Switch swCierre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        txtNombrep= (TextView) findViewById(R.id.txtNombreEmpresap);
        txtRazonp= (TextView) findViewById(R.id.txtrazonsocialp);
        ivFoto=(ImageView) findViewById(R.id.ivempresap);
        btnAceptarcambio= (Button) findViewById(R.id.btnAceptarp);
        btnCancelarcambio= (Button) findViewById(R.id.btnCancelarp);
        txtcontra= (EditText) findViewById(R.id.txtContrasenactual);
        txtcontraN= (EditText) findViewById(R.id.txtContrasena);
        txtcontraNR= (EditText) findViewById(R.id.txtContrasenaRepet);
        txtnocoindiden= (TextView) findViewById(R.id.txtNoCoinciden);
        ivcoinciden= (Button) findViewById(R.id.ivCoinciden);
        etusuario=(EditText) findViewById(R.id.etUsuario);
        ivcerrarsesion=(ImageView) findViewById(R.id.btncerrarsesion);
        txtUsuario=(TextView) findViewById(R.id.txtUsuario);
        txtcampoClave=(TextView) findViewById(R.id.txtcampoClave);
        txtcamponombre=(TextView) findViewById(R.id.txtcamponombre);
        txtrepe=(TextView) findViewById(R.id.txtrepe);
        swCierre= (Switch) findViewById(R.id.swAbierta);

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        txtNombrep.setTypeface(script);
        txtRazonp.setTypeface(script);
        txtUsuario.setTypeface(script);
        etusuario.setTypeface(script);
        txtcampoClave.setTypeface(script);
        txtcontra.setTypeface(script);
        txtcamponombre.setTypeface(script);
        txtcontraN.setTypeface(script);
        txtrepe.setTypeface(script);
        txtcontraNR.setTypeface(script);
        btnAceptarcambio.setTypeface(script);
        btnCancelarcambio.setTypeface(script);
        swCierre.setTypeface(script);
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
        txtNombrep.setText(nombreempresa);
        txtRazonp.setText(razonsocial);
        etusuario.setText(login);
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        Log.v("RUTA_IMAGEN",url);
        Picasso.with(PerfilActivity.this)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivFoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) ivFoto.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        ivFoto.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        ivFoto.setImageResource(R.mipmap.ic_launcher);
                    }
                });
        //Toast.makeText(PerfilActivity.this, "e "+cierre,Toast.LENGTH_SHORT).show();
        if(cierre.equals("Abierto")){
            swCierre.setText("Empresa Abierta");
            swCierre.setChecked(true);
            //Toast.makeText(PerfilActivity.this,"Error: "+Globales.EstadoEmpresa, Toast.LENGTH_SHORT).show();
        }else{
            swCierre.setChecked(false);
            swCierre.setText("Empresa Cerrada");
           // Toast.makeText(PerfilActivity.this,"Error: "+Globales.EstadoEmpresa, Toast.LENGTH_SHORT).show();
        }
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
                        String url="vacia";
                        String tipo="Desea Cancelar Cambio de Contraseña?";
                        String opcion="C";
                        //Toast.makeText(PerfilActivity.this,"asd",Toast.LENGTH_SHORT).show();
                        createCustomDialog(url,tipo,nombreempresa,opcion).show();
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
        ivcerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                String tipo="Desea Cerrar Sesión?";
                String opcion="Cerrar";
                //Toast.makeText(PerfilActivity.this,"asd",Toast.LENGTH_SHORT).show();
                createCustomDialog(url,tipo,nombreempresa,opcion).show();
            }
        });
        swCierre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swCierre.setChecked(false);
                    String url=Globales.servidor + "/Empresas/AbrirForzado?auth=" + Globales.token+"&codigoe="+codigoe+"&codigou="+codigou;
                    String tipo="Desea Abrir La Tienda?";
                    String opcion="A";
                    createCustomDialog(url,tipo,nombreempresa,opcion).show();
                }
                else{
                    swCierre.setChecked(true);
                    String url=Globales.servidor + "/Empresas/CierreForzado?auth=" + Globales.token+"&codigoe="+codigoe+"&codigou="+codigou;
                    String tipo="Desea Cerrar La Tienda?";
                    String opcion="A";
                    createCustomDialog(url,tipo,nombreempresa,opcion).show();
                }
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
                                            if(respuesta.equals("Cerrada") || respuesta.equals("Abierta")){
                                                if(respuesta.equals("Cerrada")){
                                                    editor.putString("CIERRE", "Cerrado");
                                                    editor.commit();
                                                    //Globales.EstadoEmpresa="Cerrado";
                                                    swCierre.setText("Empresa Cerrada");
                                                }
                                                else{
                                                    editor.putString("CIERRE", "Abierto");
                                                    editor.commit();
                                                    //Globales.EstadoEmpresa="Abierto";
                                                    swCierre.setText("Empresa Abierta");
                                                }

                                                Toast toast = Toast.makeText(PerfilActivity.this, "Empresa "+respuesta, Toast.LENGTH_SHORT);
                                                View vistaToast = toast.getView();
                                                vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                                toast.show();
                                                alertDialog.dismiss();
                                                killActivity();
                                                overridePendingTransition(0, 0);
                                                startActivity(getIntent());
                                                overridePendingTransition(0, 0);
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
                        }if(opcion=="C"){
                            Intent intent = new Intent(PerfilActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                        if(opcion=="Cerrar"){

                            editor.clear();
                            editor.commit();

                            Intent myService = new Intent(PerfilActivity.this, BakgroundPedidos.class);
                            stopService(myService);

                            Intent intent= new Intent(PerfilActivity.this,LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(PerfilActivity.this,"Sesion Cerrada con Exito", Toast.LENGTH_SHORT).show();
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
