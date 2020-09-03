package com.fastbuy.fastbuyempresas.Config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuy.fastbuyempresas.Adapters.PedidoListAdapter;
import com.fastbuy.fastbuyempresas.DesconectadoActivity;
import com.fastbuy.fastbuyempresas.Entidades.Pedido;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.fastbuy.fastbuyempresas.MenuActivity;
import com.fastbuy.fastbuyempresas.PrincipalActivity;
import com.fastbuy.fastbuyempresas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServicioPartner extends Service {
    private Thread workerThread = null;
    //private Handler workerThreadPendientes = null;
    DatabaseReference nDatabaseReference;
    DatabaseReference cambioPedidos;
    int notificador = 0;
    NotificationCompat.Builder mBuilder;
    SharedPreferences mypreferences;
    String codigoe, codigou;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i( "MISERVICIO","Servicio Creado");
        mypreferences= PreferenceManager.getDefaultSharedPreferences(ServicioPartner.this);
        codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        codigou= mypreferences.getString("UBICACION","unknown");
        //notificationManager = new AdministradorNotificaciones();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    Log.i( "MISERVICIO","Intent recivido");
                    NotificacionesStart();
                }
            });
            workerThread.start();
        }
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                consultarPendientes();//llamamos nuestro metodo
                handler.postDelayed(this,60000);//se ejecutara cada 1 minuto
            }
        },10000);//empezara a ejecutarse después de 5 milisegundos

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(ServicioPartner.this, BootBroadcast.class);
        sendBroadcast(broadcastIntent);
    }

    void NotificacionesStart(){
        nDatabaseReference = FirebaseDatabase.getInstance().getReference();
        cambioPedidos = nDatabaseReference.child("pedidos/");
        cambioPedidos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificador++;
                if(notificador > 1){
                    RDPedidos data = dataSnapshot.getValue(RDPedidos.class);
                    //String empresa = dataSnapshot.getValue().
                    if(codigoe.equals(data.getEmpresa()) && codigou.equals(data.getUbicacion())){
                        mostrarNotificacion("Tienes un nuevo pedido por entregar.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void consultarPendientes(){
        String consulta = "https://www.apifbempresas.fastbuych.com/Empresas/PedidosxEmpresaUbicacion?auth=Xid20200101e34CorpFastBuySAC2020comfastbuyfastbuyempresas"+ "&codigoe=" + codigoe + "&codigou=" + codigou;;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        //list = new ArrayList<>();
                        JSONArray lista = new JSONArray(response);
                        Log.i("Consultas",lista.toString());
                        int cont = 0;
                        for (int i = 0; i < lista.length(); i++){
                            JSONObject pendiente = lista.getJSONObject(i);
                            if(Integer.parseInt(pendiente.getString("atendido"))==0){
                                cont++;
                            }
                        }
                        if (cont > 0){
                            mostrarNotificacion("Tienes pedidos pendientes que no haz visto.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    void mostrarNotificacion(String mensaje){
        String channelId  = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);

        //Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notificacion);Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notificacion);
        NotificationManager nm = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), null);

        int icono = R.drawable.ic_tienda;
        Intent intent = new Intent(ServicioPartner.this, PrincipalActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importancia = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel androidChannel = new NotificationChannel(channelId, channelName, importancia);
            androidChannel.setDescription("Comunicación de pedidos entrantes a Partners");
            androidChannel.enableLights(true);
            androidChannel.setLightColor(Color.RED);
            androidChannel.enableVibration(true);
            androidChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            //androidChannel.setSound(soundUri,audioAttributes);
            nm.createNotificationChannel(androidChannel);
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        }
        mBuilder.setSmallIcon(icono)
                .setContentIntent(pendingIntent)
                .setContentTitle("FastBuy Partner")
                .setContentText(mensaje);
        //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notificacion));

        nm.notify(5055154, mBuilder.build());
        MediaPlayer mediaPlayer = MediaPlayer.create(ServicioPartner.this, R.raw.notify);
        mediaPlayer.start();
    }
}
