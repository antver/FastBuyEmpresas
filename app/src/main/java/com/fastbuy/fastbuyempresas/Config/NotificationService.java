package com.fastbuy.fastbuyempresas.Config;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

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
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
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

public class NotificationService extends Service {

    private Thread workerThread = null;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    Notification notification;
    SharedPreferences mypreferences;
    String codigoe, codigou;
    DatabaseReference nDatabaseReference;
    DatabaseReference cambioPedidos;

    @Override
    public void onCreate() {
        super.onCreate();
        mypreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        codigou= mypreferences.getString("UBICACION","unknown");
        nDatabaseReference = FirebaseDatabase.getInstance().getReference();
        cambioPedidos = nDatabaseReference.child("pedidos/");
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

    @TargetApi(Build.VERSION_CODES.O)
    void mostrarNotificacion(String message){
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.app_name);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, PrincipalActivity.class);
        Resources res = this.getResources();
        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int NOTIFY_ID = 0; // ID of notification
            String id = NOTIFICATION_CHANNEL_ID; // default_channel_id
            String title = NOTIFICATION_CHANNEL_ID; // Default Channel
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notifManager == null) {
                notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(getString(R.string.app_name)).setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.ic_tienda)   // required
                    .setContentText(message)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_tienda))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

            startForeground(1, notification);

        } else {
            pendingIntent = PendingIntent.getActivity(context, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_tienda)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_tienda))
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setContentTitle(getString(R.string.app_name)).setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText(message).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("SERVICIO", "consultando");
                consultarPendientes();//llamamos nuestro metodo
                handler.postDelayed(this,60000);//se ejecutara cada 1 minuto
            }
        },5000);//empezara a ejecutarse después de 5 milisegundos

        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    Log.i( "MISERVICIO","Intent recivido");
                    NotificacionesStart();
                }
            });
            workerThread.start();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //App.getInstance().cancelPendingRequests(getClass().getSimpleName());
        super.onDestroy();

        Intent broadcastIntent = new Intent(getApplicationContext(), BootBroadcast.class);
        sendBroadcast(broadcastIntent);
    }
    int notificador = 0;
    void NotificacionesStart(){
        cambioPedidos.addValueEventListener(new ValueEventListener() {
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
}
