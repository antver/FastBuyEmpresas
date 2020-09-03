package com.fastbuy.fastbuyempresas.ui.notifications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Config.ServicioPartner;
import com.fastbuy.fastbuyempresas.ContactanosActivity;
import com.fastbuy.fastbuyempresas.LoginActivity;
import com.fastbuy.fastbuyempresas.PerfilActivity;
import com.fastbuy.fastbuyempresas.R;
import com.fastbuy.fastbuyempresas.ReportarErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsFragment extends Fragment {
    ProgressDialog progDailog = null;
   // private NotificationsViewModel notificationsViewModel;
    String cierre = "Cerrado";
    String codempresa, codubicacion;
    TextView tvNombre, tvDescripcion, tvCiudad, tvDireccion, tvEstado;
    Switch swCierre;
    LinearLayout btnCerrarSesion, btnCambiarClave, btnContactanos, btnReportarProblema;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        tvNombre  = (TextView) root.findViewById(R.id.tvNombreEmpresa);
        tvDescripcion = (TextView) root.findViewById(R.id.tvDescripcion);
        tvCiudad = (TextView) root.findViewById(R.id.tvCiudad);
        tvDireccion = (TextView) root.findViewById(R.id.tvDireccion);
        tvDireccion = (TextView) root.findViewById(R.id.tvDireccion);
        tvEstado = (TextView) root.findViewById(R.id.tvEstado);
        swCierre = (Switch) root.findViewById(R.id.swAbierta);
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        btnCerrarSesion = (LinearLayout) root.findViewById(R.id.btnCerrarSesion);
        btnCambiarClave = (LinearLayout) root.findViewById(R.id.btnCambiarClave);
        btnContactanos = (LinearLayout) root.findViewById(R.id.btnContactanos);
        btnReportarProblema = (LinearLayout) root.findViewById(R.id.btnReportarProblema);

        codempresa= mypreferences.getString("CODIGO_EMPRESA","unknown");
        codubicacion= mypreferences.getString("UBICACION","unknown");
        progDailog = new ProgressDialog(getContext());
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        cargardatos();

        swCierre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String url=Globales.servidor + "/Empresas/CierreForzado?auth=" + Globales.token+"&codigoe="+codempresa+"&codigou="+codubicacion;
                    Log.v("CIERRE abierto", url);
                    cargarCierreForzado(url);
                }
                else{
                    String url= Globales.servidor + "/Empresas/AbrirForzado?auth=" + Globales.token+"&codigoe="+ codempresa+"&codigou="+codubicacion;
                    Log.v("CIERRE", url);
                    cargarCierreForzado(url);
                }
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomDialogCerrarSesion().show();
            }
        });

        btnCambiarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClave = new Intent(getContext(), PerfilActivity.class);
                startActivity(intentClave);
            }
        });

        btnContactanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClave = new Intent(getContext(), ContactanosActivity.class);
                startActivity(intentClave);
            }
        });

        btnReportarProblema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClave = new Intent(getContext(), ReportarErrorActivity.class);
                startActivity(intentClave);
            }
        });


        /*final TextView textView = root.findViewById(R.id.text_notifications);
        textView.setText("MI FRAGMENTITO");
       /*notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    void cargardatos(){
        String consulta = "https://www.apifbempresas.fastbuych.com/Empresas/DatosEmpresaPerfil?empresa="+codempresa+"&ubicacion=" + codubicacion;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        //list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        String nombreportada = "", imagenproducto = "";
                        for (int i = 0; i < lista.length(); i++){
                            String nombre = lista.getJSONObject(i).getString("EMP_NombreComercial");
                            String descripcion = lista.getJSONObject(i).getString("EMP_RazonSocial");
                            cierre = lista.getJSONObject(i).getString("EU_CierreForzado");
                            String ciudad = lista.getJSONObject(i).getString("UBI_Nombre");
                            String direccion = lista.getJSONObject(i).getString("EU_Direccion");
                            String estado = lista.getJSONObject(i).getString("EstadoAbierto");
                            tvNombre.setText(nombre);
                            tvDescripcion.setText(descripcion);
                            tvCiudad.setText(ciudad);
                            tvDireccion.setText(direccion);
                            tvEstado.setText(estado);
                            if(cierre.equals("0")){
                                swCierre.setChecked(false);
                            }else{
                                swCierre.setChecked(true);
                            }
                            if (estado.equals("Cerrado")){
                                tvEstado.setBackgroundResource(R.drawable.shadow_rojo);
                            }else{
                                if(cierre.equals("0")){
                                    tvEstado.setText("Abierto");
                                    tvEstado.setBackgroundResource(R.drawable.shadow_verde);
                                }else{
                                    tvEstado.setText("Cerrado");
                                    tvEstado.setBackgroundResource(R.drawable.shadow_rojo);
                                }
                            }

                        }
                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        });

        queue.add(stringRequest);

    }

    public void cargarCierreForzado(String url){
        final ProgressDialog progDailog1 = new ProgressDialog(getContext());
        progDailog1.setMessage("Cargando...");
        progDailog1.setIndeterminate(true);
        progDailog1.setCancelable(false);
        progDailog1.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject object = new JSONObject(response);
                        String respuesta = object.getString("mensaje");
                        if(respuesta.equals("Cerrada")){
                            swCierre.setChecked(true);
                        }
                        else{
                            swCierre.setChecked(false);
                        }
                        progDailog1.dismiss();
                        cargardatos();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog1.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog1.dismiss();
            }
        });
        queue.add(stringRequest);
    }

    public AlertDialog createCustomDialog(final String url) {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflar y establecer el layout para el dialogo
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        View v = inflater.inflate(R.layout.dialog_aceptar, null);
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        TextView txtNombreRepartidor = (TextView) v.findViewById(R.id.txtEmpresa);
        TextView txtAlerta= (TextView) v.findViewById(R.id.txtDialog);
        txtAlerta.setText("¿Desea continuar?");
        txtNombreRepartidor.setText("Cierre de Emergencia");
        Button btnSi = (Button) v.findViewById(R.id.btnSi);
        Button btnNo = (Button) v.findViewById(R.id.btnNo);

        //btnSi.setTypeface(script);
        //btnNo.setTypeface(script);
        //txtNombreRepartidor.setTypeface(script);
        //txtAlerta.setTypeface(script);

        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        btnSi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String consulta= url;
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.length()>0){
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        String respuesta = object.getString("mensaje");
                                        if(respuesta.equals("Cerrada")){
                                            swCierre.setChecked(true);
                                        }
                                        else{
                                            swCierre.setChecked(false);
                                        }
                                        Toast toast = Toast.makeText(getContext(), "Empresa Cerrada", Toast.LENGTH_SHORT);
                                        View vistaToast = toast.getView();
                                        vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                        toast.show();
                                        alertDialog.dismiss();
                                        cargardatos();

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

    public AlertDialog createCustomDialogCerrarSesion() {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_aceptar, null);
        TextView txtNombreRepartidor = (TextView) v.findViewById(R.id.txtEmpresa);
        TextView txtAlerta= (TextView) v.findViewById(R.id.txtDialog);
        txtAlerta.setText("¿Desea continuar?");
        txtNombreRepartidor.setText("Cerrar Sesión");
        Button btnSi = (Button) v.findViewById(R.id.btnSi);
        Button btnNo = (Button) v.findViewById(R.id.btnNo);

        //btnSi.setTypeface(script);
        //btnNo.setTypeface(script);
        //txtNombreRepartidor.setTypeface(script);
        //txtAlerta.setTypeface(script);

        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        btnSi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
                        final SharedPreferences.Editor editor = mypreferences.edit();
                        String usuario = mypreferences.getString("USUARIO_GUARDADO", "");
                        String clave = mypreferences.getString("CLAVE_GUARDADA", "");
                        boolean recordar = mypreferences.getBoolean("RECORDAR_CLAVE", false);
                        editor.clear();
                        editor.putBoolean("RECORDAR_CLAVE", recordar);
                        editor.putString("CLAVE_GUARDADA", clave);
                        editor.putString("USUARIO_GUARDADO", usuario);
                        editor.commit();

                        Intent myService = new Intent(getContext(), ServicioPartner.class);
                        getActivity().stopService(myService);

                        Intent intent= new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(),"Sesion Cerrada con Exito", Toast.LENGTH_SHORT).show();
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
}
