package com.fastbuy.fastbuyempresas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.DetallePedidoListAdapter;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.DetallePedido;
import com.fastbuy.fastbuyempresas.Entidades.Empresa;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.Entidades.Producto;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetallesActivity extends AppCompatActivity {
    TextView txtMontoSubtotal,txtitems,txthora,txtestado,txtfpago,txttotaltema,txtProducto,txtPrecio,txtOpciones;
    ProgressDialog progDailog = null;
    ArrayList<DetallePedido> list;
    DetallePedidoListAdapter adapter = null;
    GridView gridView;
    ImageButton btnCancelarPed,btnAceptarPed,btnCancelarD, btnlistoped,btnpreparadotodo,btnQr;
    String codigoe,nombreempresa,url,alerta,tipo, item, colorestadop;
    Button btnCancelarDeta,btnAceptarDeta;
    LinearLayout lymostrar;
    public String items="";
    Typeface script;
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos2 = nDatabaseReference.child("cancelado/");

    EditText cajaCambio ;
    DetallesActivity.AsyncTask_load ast;

    TextView txtView_nombre_cliente, txtView_codigo_cliente, txtView_direccion_cliente, txtView_telefono_cliente;
    LinearLayout linearLayout_info_client, linearLayout_3_buttons;

    Button btnCollapseInfoClient;
    Button btnIniciarDelivery, btnFinalizarDelivery;

    TextView txtView_TipoRecojo;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        cajaCambio = (EditText) findViewById(R.id.cajaCambio);
        txtestado= (TextView) findViewById(R.id.txtestado2);
        txthora= (TextView) findViewById(R.id.txthora2);
        txtitems= (TextView) findViewById(R.id.txtitem2);
        gridView = (GridView) findViewById(R.id.gvDetalles);
        txtMontoSubtotal= (TextView) findViewById((R.id.txtMontoSubtotal));
        btnAceptarPed= (ImageButton) findViewById(R.id.btnAceptarPed);
        btnCancelarPed= (ImageButton) findViewById(R.id.btnCancelarPed);
        btnCancelarD= (ImageButton) findViewById(R.id.ivcancelard);
        btnlistoped= (ImageButton) findViewById(R.id.btnListoPed);
        btnpreparadotodo= (ImageButton) findViewById(R.id.btnListoPed);
        btnAceptarDeta=(Button) findViewById(R.id.btnAceptarDeta);
        btnCancelarDeta=(Button)findViewById(R.id.btnCancelarDeta);
        lymostrar= (LinearLayout) findViewById(R.id.lyCanceladoDet);
        txtfpago=(TextView) findViewById(R.id.txtfpago);
        txttotaltema=(TextView) findViewById(R.id.txttotaltema);
        txtProducto=(TextView) findViewById(R.id.txtProducto);
        txtPrecio=(TextView) findViewById(R.id.txtPrecio);
        txtOpciones=(TextView) findViewById(R.id.txtOpciones);
        btnQr=(ImageButton) findViewById(R.id.btnQr);
        txtView_TipoRecojo = (TextView) findViewById(R.id.text_view_tipo_recojo);

        // Layouts cuando Recojo en tienda es = 1
        linearLayout_info_client =  findViewById(R.id.layout_info_client);
        linearLayout_3_buttons =    findViewById(R.id.layout_3_buttons);

        // Txt views para información del Cliente
        txtView_nombre_cliente =    findViewById(R.id.text_view_client);
        txtView_codigo_cliente =    findViewById(R.id.text_view_code_pedido_client);
        txtView_direccion_cliente = findViewById(R.id.text_view_address_client);
        txtView_telefono_cliente =  findViewById(R.id.text_view_telf_client);

        // Buttons para iniciar y finalizar Delivery y mostrar datos del Cliente (Incluidos layout_3_buttons)
        btnIniciarDelivery =        findViewById(R.id.btnIniciarDelivery);
        btnFinalizarDelivery =      findViewById(R.id.btnFinalizarDelivery);
        btnCollapseInfoClient =     findViewById(R.id.btnCollapseInfoClient);


        // Ocultando Layouts por defecto (se activarán cuando se verifique que PED_RecogerEnTienda sea igual a 1)
        linearLayout_info_client.setVisibility(View.GONE);
        linearLayout_3_buttons.setVisibility(View.GONE);
        txtView_TipoRecojo.setVisibility(View.GONE);




        // Click event para hacer visible la información del usuario (Solo funciona cuando PED_RecogerEnTienda es igual a 1)
        btnCollapseInfoClient.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( View.GONE == linearLayout_info_client.getVisibility()) {
                    linearLayout_info_client.setVisibility(View.VISIBLE);
                }
                else{
                    linearLayout_info_client.setVisibility(View.GONE);
                }

            }
        });

        btnIniciarDelivery.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                iniciarDelivery();


            }
        });

        btnFinalizarDelivery.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarDelivery();
            }
        });

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        txtestado.setTypeface(script);
        txthora.setTypeface(script);
        txtitems.setTypeface(script);
        txtfpago.setTypeface(script);
        btnAceptarDeta.setTypeface(script);
        btnCancelarDeta.setTypeface(script);
        txttotaltema.setTypeface(script);
        txtMontoSubtotal.setTypeface(script);
        txtProducto.setTypeface(script);
        txtPrecio.setTypeface(script);
        txtOpciones.setTypeface(script);

        ast = new AsyncTask_load();
        ast.execute();

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(DetallesActivity.this);
        nombreempresa = (myPreferences.getString("NOMBRE_EMPRESA", "unknown"));
        codigoe = (myPreferences.getString("CODIGO_EMPRESA", "unknown"));
        colorEstado(Globales.pedidoestado); //centry gothic
        txtestado.setText(Globales.pedidoestado);
        txthora.setText(Globales.pedidohora);
        txtitems.setText(Globales.pedidoitems);
        txtfpago.setText("Pago Con "+Globales.fpago);
        progDailog = new ProgressDialog(DetallesActivity.this);
        progDailog.setMessage("Cargando Detalles...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        if(isNetDisponible()){
            //Toast.makeText(DetallesActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(DetallesActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        listaDetalles();
        btnAceptarPed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url=Globales.servidor + "/Empresas/aceptarTodo?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado;
                alerta= "Desea Aceptar Pedido?";
                tipo="Aceptado";
                colorestadop=" PREPARANDO";
                createCustomDialog(url,alerta,tipo,colorestadop).show();
            }
        });
        btnCancelarPed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(Globales.fpago.equals("Efectivo")){
                    url=Globales.servidor + "/Empresas/cancelarTodo?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado+"&empresa="+codigoe;
                    alerta= "Desea Cancelar Pedido?";
                    tipo="Cancelado";
                    colorestadop=" ANULADO";
                    createCustomDialog(url,alerta,tipo,colorestadop).show();
                }else{
                    url=Globales.servidor + "/Empresas/cancelarTodoTarjeta?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado+"&empresa="+codigoe;
                    alerta= "Desea Cancelar Pedido Por Tarjeta?";
                    tipo="Cancelado";
                    colorestadop=" ANULADO";
                    createCustomDialog(url,alerta,tipo,colorestadop).show();
                }

            }
        });
        btnpreparadotodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url=Globales.servidor + "/Empresas/PreparadoTodo?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado;
                alerta= "Pedido Preparado?";
                tipo="Preparado";
                colorestadop=" RECOGER";
                createCustomDialog(url,alerta,tipo,colorestadop).show();
            }
        });
        btnCancelarDeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*url=Globales.servidor + "/Empresas/PendienteDetalle?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado;
                alerta= "Desea Cancelar?";
                tipo="Cambios Cancelados";
                colorestadop=" PREPARADO";
                createCustomDialog(url,alerta,tipo,colorestadop).show();*/
                //Globales.cancelarDetalle=0;
                Globales.Pedidositem.clear();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Globales.pedidoestado=" PENDIENTE";
                items="";
                //colorEstado(Globales.pedidoestado);
            }
        });
        btnAceptarDeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Globales.Pedidositem.size()==0){
                    Toast.makeText(DetallesActivity.this,"No Existe Ningún cambio",Toast.LENGTH_SHORT).show();
                }else{
                    for(int i=0;i<Globales.Pedidositem.size();i++){
                        if(i==Globales.Pedidositem.size()-1){
                            items+=Globales.Pedidositem.get(i);
                            url=Globales.servidor + "/Empresas/CambiarTodo?auth=" + Globales.token+"&codigo="+Globales.pedidoSeleccionado+"&item="+items+"&codigoe="+codigoe;
                            alerta= "Desea Aceptar Cambios?";
                            tipo="Cambios Aceptados";
                            colorestadop=" ESPERA";
                            createCustomDialog(url,alerta,tipo,colorestadop).show();
                            items="";
                        }else{
                            items+= Globales.Pedidositem.get(i)+"-";
                        }
                    }
                }
            }
        });
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesActivity.this, QrActivity.class);
                startActivity(intent);
            }
        });

        cajaCambio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cajaCambio.getText().toString().length() > 0)
                    lymostrar.setVisibility(View.VISIBLE);
                else
                    lymostrar.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void finalizarDelivery() {
        progDailog = new ProgressDialog(DetallesActivity.this);
        progDailog.setMessage("Finalizando Delivery");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();


        String consulta = Globales.servidorPedido+"/Pedido/finalizarDelivery?auth="+Globales.token+"&pedido="+Globales.pedidoSeleccionado;
        RequestQueue queue = Volley.newRequestQueue(DetallesActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);

                        Log.i("SQL UPDATE", lista.toString() );

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

    private void iniciarDelivery() {
        progDailog = new ProgressDialog(DetallesActivity.this);
        progDailog.setMessage("Iniciando Delivery");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        String consulta = Globales.servidorPedido+"/Pedido/iniciarDelivery?auth="+Globales.token+"&pedido="+Globales.pedidoSeleccionado;
        RequestQueue queue = Volley.newRequestQueue(DetallesActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        Log.i("SQL UPDATE", lista.toString() );

                        progDailog.dismiss();
                        btnIniciarDelivery.setVisibility(View.GONE);

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

    public void listaDetalles(){
        String consulta= Globales.servidorPedido+"/Pedido/AndroidDetallePedido?auth="+Globales.token+"&pedido="+Globales.pedidoSeleccionado;
        RequestQueue queue= Volley.newRequestQueue(DetallesActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                if(response.length()>0){
                    try{
                        list= new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        Log.i("recoger en tienda",lista.toString());

                        int recogerEnTienda =lista.getJSONObject(0).getInt("PED_RecogerEnTienda");

                        // Set info of Client

                        String PED_codigo=      lista.getJSONObject(0).getString("PED_Codigo");
                        String PED_direccion=   lista.getJSONObject(0).getString("PED_Direccion");
                        String PED_telefono=    lista.getJSONObject(0).getString("PED_Telefono");
                        String PED_nombre=      lista.getJSONObject(0).getString("PED_Nombre");
                        int PED_atendido=      lista.getJSONObject(0).getInt("PED_Atendido");



                        txtView_codigo_cliente.setText(PED_codigo);
                        txtView_direccion_cliente.setText(PED_direccion);
                        txtView_nombre_cliente.setText(PED_nombre);
                        txtView_telefono_cliente.setText(PED_telefono);
                        linearLayout_3_buttons.setVisibility(View.VISIBLE);
                        btnIniciarDelivery.setVisibility(View.GONE);
                        btnFinalizarDelivery.setVisibility(View.GONE);

                        if(recogerEnTienda==1) {
                            txtView_TipoRecojo.setText("Recojo en Tienda");
                            txtView_TipoRecojo.setVisibility(View.VISIBLE);
                        }
                        if(recogerEnTienda==0){
                            txtView_TipoRecojo.setText("Llevar a Domicilio");
                            txtView_TipoRecojo.setVisibility(View.VISIBLE);

                        }

                        if(PED_atendido == 0)
                        {
                            btnIniciarDelivery.setVisibility(View.VISIBLE);
                        }
                        if(PED_atendido == 3)
                        {
                            btnFinalizarDelivery.setVisibility(View.VISIBLE);
                        }



                        Log.i("HELLO", lista.toString());
                        int cont = 0;
                        double suma = 0;
                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);

                            cont++;
                            DetallePedido detallePedido = new DetallePedido();
                            Producto producto = new Producto();
                            Empresa empresa = new Empresa();
                            detallePedido.setCantidad(pendiente.getInt("PD_Cantidad"));
                            detallePedido.setTotal(pendiente.getDouble("PD_Total"));
                            detallePedido.setNumero(pendiente.getInt("PD_Item"));
                            producto.setDescripcion(pendiente.getString("PRO_Descripcion"));
                            empresa.setNombreComercial(pendiente.getString("EMP_NombreComercial"));
                            detallePedido.setPresentacion(pendiente.getString("presentacion"));

                            producto.setEmpresa(empresa);
                            detallePedido.setProducto(producto);
                            detallePedido.setPersonalizacion(pendiente.getString("PD_Personalizado"));
                            detallePedido.setEstado(pendiente.getInt("PD_Entregado"));
                            detallePedido.setAtendido(Integer.parseInt(pendiente.getString("PED_Atendido")));
                            Globales.pedidoestado=String.valueOf(cargarPedido(Integer.parseInt(pendiente.getString("PED_Atendido"))));
                            //botonesDetalle(pendiente.getInt("PD_Entregado"));

                            suma += pendiente.getDouble("PD_Total");
                            list.add(detallePedido);


                        }
                        txtMontoSubtotal.setText(String.valueOf(suma) + "0");
                        adapter = new DetallePedidoListAdapter(DetallesActivity.this, R.layout.item_detalles, list, DetallesActivity.this);
                        gridView.setAdapter(adapter);

                        //adapter.notifyDataSetChanged();

                        progDailog.dismiss();
                    }catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        }
        );
        queue.add(stringRequest);
    }

    public void colorEstado(String pedido){
        if(pedido==" PENDIENTE"){
            txtestado.setBackgroundResource(R.color.pendiente);//INVISIBLE
            btnAceptarPed.setVisibility(View.VISIBLE);
            btnCancelarPed.setVisibility(View.VISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            //lymostrar.setVisibility(View.VISIBLE);
        }if(pedido==" FINALIZADO"){
            txtestado.setBackgroundResource(R.color.atendido);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }if(pedido==" ANULADO"){
            txtestado.setBackgroundResource(R.color.rojo);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }if(pedido==" EN PROCESO"){
            txtestado.setBackgroundResource(R.color.proceso);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.VISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }if(pedido==" PREPARANDO"){
            txtestado.setBackgroundResource(R.color.preparando);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.VISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }if(pedido==" PREPARADO"){
            txtestado.setBackgroundResource(R.color.recoger);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }
        if(pedido==" ESPERA"){
            txtestado.setBackgroundResource(R.color.alert);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }
        if(pedido==" RECOGER"){
            txtestado.setBackgroundResource(R.color.proceso);
            btnAceptarPed.setVisibility(View.INVISIBLE);
            btnCancelarPed.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.VISIBLE);

            btnlistoped.setVisibility(View.INVISIBLE);
            lymostrar.setVisibility(View.INVISIBLE);
        }
    }

    public AlertDialog createCustomDialog(final String urls, String alertas,final String tipos,final String colorestadops) {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflar y establecer el layout para el dialogo
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        View v = inflater.inflate(R.layout.dialog_aceptar, null);
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        TextView txtNombreRepartidor = (TextView) v.findViewById(R.id.txtEmpresa);
        TextView txtAlerta= (TextView) v.findViewById(R.id.txtDialog);
        txtAlerta.setText(alertas);
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
                        // Aceptar
                        String consulta= urls;
                        RequestQueue queue = Volley.newRequestQueue(DetallesActivity.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.length()>0){
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        String respuesta = object.getString("mensaje");
                                        if(respuesta.equals("Listo")){
                                            Globales.pedidoestado=colorestadops;
                                            Toast toast = Toast.makeText(DetallesActivity.this, "Pedido "+ tipos+" con éxito.", Toast.LENGTH_SHORT);
                                            View vistaToast = toast.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                            toast.show();
                                            alertDialog.dismiss();
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                        }
                                        if(!respuesta.equals("Listo") && !respuesta.equals("CAMBIADO")){
                                            Toast toast = Toast.makeText(DetallesActivity.this, respuesta, Toast.LENGTH_SHORT);
                                            View vistaToast = toast.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                                            toast.show();
                                            alertDialog.dismiss();
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                        }
                                        if(respuesta.equals("CAMBIADO")){
                                            Globales.pedidoestado=colorestadops;
                                            Toast toast = Toast.makeText(DetallesActivity.this, "Cambios Aceptados", Toast.LENGTH_SHORT);
                                            View vistaToast = toast.getView();
                                            vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                            alertDialog.dismiss();
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                            Globales.Pedidositem.clear();

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

    public String cargarPedido(int atendido){

        if(atendido==0){
            return " PENDIENTE";
        }if(atendido==1){
            return " ATENDIDO";
        }if(atendido==2){
            return " ANULADO";
        }if(atendido==3){
            return " EN PROCESO";
        }if(atendido==4){
            return " PREPARANDO";
        }if(atendido==5){
            return " RECOGER";
        }
        return "";
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
    public class AsyncTask_load extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            cambioPedidos2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(DetallesActivity.this);
                    String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
                    String codigou= mypreferences.getString("UBICACION","unknown");
                    RDPedidos2 data = dataSnapshot.getValue(RDPedidos2.class);
                    if(codigoe.equals(data.getEmpresa()) && Globales.pedidoSeleccionado.equals(data.getId())){
                        Intent intent=new Intent(DetallesActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }
    }
}
