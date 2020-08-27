package com.fastbuy.fastbuyempresas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.Config.ServicioPartner;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos2;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.fastbuy.fastbuyempresas.Config.Globales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    ImageView ivimagen, btnperfil, btnmenu, btnproductos,btnreportes;
    ProgressDialog progDailog = null;
    TextView txtNombre;
    Intent myService;
    TextView txtrazonsocial;
    PedidoListAdapter adapter = null;
    TextView txtPedidos;
    GridView gridView2;
    ArrayList<Pedido> list;
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos = nDatabaseReference.child("pedidos/");
    DatabaseReference cambioPedidos2 = nDatabaseReference.child("cancelado/");
    public static final String MESSAGE_STATUS = "message_status";
    MenuActivity.AsyncTask_load ast;
    Typeface script2;
    //DatabaseReference cambioPedidos = nDatabaseReference.child("pedidos").child("empresa");
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //
        //
        txtNombre= (TextView) findViewById(R.id.txtNombreEmpresa);
        txtrazonsocial= (TextView) findViewById(R.id.txtrazonsocial);
        txtPedidos=(TextView) findViewById(R.id.txtPedidos);
        gridView2= (GridView) findViewById(R.id.gvPedidos2);
        btnperfil= (ImageView) findViewById(R.id.btnperfil);
        btnmenu=(ImageView) findViewById(R.id.btnmenu);
        btnproductos=(ImageView) findViewById(R.id.btnproductos);
        btnreportes=(ImageButton) findViewById(R.id.btnreportes);

        String fuente2="fonts/GOTHIC.ttf";
        this.script2= Typeface.createFromAsset(getAssets(),fuente2);


        //txtTitulo.setTypeface(script);
        txtNombre.setTypeface(script2);
        txtrazonsocial.setTypeface(script2);
        txtPedidos.setTypeface(script2);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");

        progDailog = new ProgressDialog(MenuActivity.this);
        progDailog.setMessage("Cargando pendientes...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        cargarPedidos(codigoe,codigou);

        txtNombre.setText(nombreempresa);
        txtrazonsocial.setText(razonsocial);
        ivimagen= (ImageView) findViewById(R.id.ivempresa);
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        Log.v("RUTA_IMAGEN",url);
        Picasso.with(MenuActivity.this)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivimagen, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) ivimagen.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        ivimagen.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        ivimagen.setImageResource(R.mipmap.ic_launcher);
                    }
                });
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MenuActivity", "Key: " + key + " Value: " + value);
            }
        }
        /*final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
        mWorkManager.enqueue(mRequest);
        mWorkManager.getWorkInfoByIdLiveData(mRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    //tvStatus.append(state.toString() + "\n");
                }
            }
        });*/

        if (!isMyServiceRunning(ServicioPartner.class)){ //método que determina si el servicio ya está corriendo o no
            myService = new Intent(this, ServicioPartner.class); //serv de tipo Intent
            startService(myService); //ctx de tipo Context
        }
        //ast = new AsyncTask_load();
        //ast.execute();

        btnperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PerfilActivity.class);
                startActivity(intent);
                /*Intent intent = new Intent(MenuActivity.this, ReporteActivity.class);
                startActivity(intent);*/
            }
        });
        btnmenu.setBackgroundResource(R.color.fastbuy);
        int color= ContextCompat.getColor(getApplicationContext(), R.color.blanco);
        ImageViewCompat.setImageTintList(btnmenu, ColorStateList.valueOf(color));

        btnproductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });
        btnreportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ReporteActivity.class);
                startActivity(intent);
            }
        });
        if(isNetDisponible()){
            //Toast.makeText(MenuActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(MenuActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        /*if(isNetDisponible()){
            Toast.makeText(MenuActivity.this,"Datos", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MenuActivity.this,"No Hay Conexión a internet", Toast.LENGTH_SHORT).show();
        }*/
    }
    public void cargarPedidos(String codigoe,String codigou){

        String consulta = Globales.servidor +"/Empresas/PedidosxEmpresaUbicacion?auth="+Globales.token+ "&codigoe=" + codigoe + "&codigou=" + codigou;;
        RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        Log.i("Consultas",lista.toString());
                        int cont = 0;
                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);
//981293432
                            Pedido pedido = new Pedido();
                            pedido.setCodigo(Integer.parseInt(pendiente.getString("codigop")));
                            pedido.setVendido(Float.parseFloat(pendiente.getString("vendido")));
                            pedido.setTiempopreparacion(pendiente.getString("tiempo"));
                            pedido.setHoraPedido(pendiente.getString("horaped").toString());

                            pedido.setItem(Integer.parseInt(pendiente.getString("items")));
                            if(Integer.parseInt(pendiente.getString("atendido"))==0){
                                cont++;
                            }
                            pedido.setAtendido(cargarPedido(Integer.parseInt(pendiente.getString("atendido"))));
                            pedido.setFpago(pendiente.getString("fpago"));
                            list.add(pedido);
                        }
                        txtPedidos.setText(String.valueOf(cont) + " Pedidos Pendientes");

                        adapter = new PedidoListAdapter(MenuActivity.this, R.layout.item_pedidos, list);
                        gridView2.setAdapter(adapter);
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
    public String cargarPedido(int atendido){

        if(atendido==0){
            return " PENDIENTE";
        }if(atendido==1){
            return " FINALIZADO";
        }if(atendido==2){
            return " ANULADO";
        }if(atendido==3){
            return " PREPARANDO";
        }if(atendido==4){
            return " PREPARANDO";
        }if(atendido==5){
            return " RECOGER";
        }if(atendido==7){
            return " ESPERA";
        }
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        cargarPedidos(codigoe,codigou);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        final String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        final String codigou= mypreferences.getString("UBICACION","unknown");
        cargarPedidos(codigoe,codigou);
        cambioPedidos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Comment rdpedidos= dataSnapshot.getValue(Comment.class);
                RDPedidos data = dataSnapshot.getValue(RDPedidos.class);
                //String empresa = dataSnapshot.getValue().
                if(codigoe.equals(data.getEmpresa()) && codigou.equals(data.getUbicacion())){
                    cargarPedidos(codigoe,codigou);
                    //Toast.makeText(MenuActivity.this,"Pedido Entrante", Toast.LENGTH_SHORT).show();
                }
               // Toast.makeText(MenuActivity.this,"Pedido Entrante - " + data.getEmpresa()+" - "+ data.getId()+" - "+data.getUbicacion(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(MenuActivity.this,MainActivity.class);
            startActivity((intent));
            moveTaskToBack(false);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    int notificador=0;

    public class AsyncTask_load extends AsyncTask<Void,Integer,Boolean>{

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
            cambioPedidos.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
                    String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
                    String codigou= mypreferences.getString("UBICACION","unknown");
                    notificador++;
                    if(notificador>1){
                        RDPedidos data = dataSnapshot.getValue(RDPedidos.class);
                        //String empresa = dataSnapshot.getValue().
                        if(codigoe.equals(data.getEmpresa()) && codigou.equals(data.getUbicacion())){
                            String channelId  = getString(R.string.default_notification_channel_id);
                            String channelName = getString(R.string.default_notification_channel_name);
                            NotificationChannel androidChannel = null;

                            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            int icono = R.drawable.ic_tienda;
                            Intent intent = new Intent(MenuActivity.this, MenuActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                androidChannel= new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                androidChannel.enableLights(true);
                                androidChannel.enableVibration(true);
                                androidChannel.setLightColor(Color.GREEN);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                        .build();

                                androidChannel.setVibrationPattern((new long[]{Notification.DEFAULT_VIBRATE}));
                                androidChannel.setSound(soundUri,audioAttributes);

                                if (nm != null) {
                                    nm.createNotificationChannel( androidChannel );
                                    Notification.Builder builder = new Notification.Builder(getApplicationContext(), "default")
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(icono)
                                            .setContentTitle("FastBuy Delívery")
                                            .setContentText("Nuevo Pedido")
                                            .setAutoCancel(true)
                                            .setShowWhen(true);

                                    nm.notify(1, builder.build());//notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageame() + "/" + R.raw.audio));
                                }
                            } else {
                                NotificationCompat.Builder mBuilder;
                                NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(icono)
                                        .setContentTitle("FastBuy Delívery")
                                        .setContentText("Nuevo Pedido")
                                        .setShowWhen(true)
                                        .setVibrate(new long[]{100, 250, 100, 500})
                                        .setAutoCancel(true)
                                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify));

                                mNotifyMgr.notify(0, mBuilder.build());
                            }

                            cargarPedidos(codigoe,codigou);
                            Toast.makeText(MenuActivity.this,"Pedido Entrante", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            cambioPedidos2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
                    String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
                    String codigou= mypreferences.getString("UBICACION","unknown");
                    RDPedidos2 data = dataSnapshot.getValue(RDPedidos2.class);
                    if(codigoe.equals(data.getEmpresa())){
                        cargarPedidos(codigoe,codigou);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }
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
