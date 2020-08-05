package com.fastbuy.fastbuyempresas;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class BakgroundPedidos extends Service {

    private static final String TAG = "BackgroundSoundService";
    String servicio="";
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos = nDatabaseReference.child("pedidos/");
    final WorkManager mWorkManager = WorkManager.getInstance();
    final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
    int notificador=0;
    BakgroundPedidos.AsyncTask_load ast;
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind()" );
        return null;
    }

    private AsyncTask_load createAsyncTask(){
            if (ast == null) {
                return ast = new AsyncTask_load();
            }else {
                ast.cancel(true);
                return ast = new AsyncTask_load();
            }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        /*
        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(BakgroundPedidos.this);
        servicio= mypreferences.getString("SERVICIO","unknown");*/
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //player.start();

        mWorkManager.enqueue(mRequest);
        //SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(BakgroundPedidos.this);
        // String servicio= mypreferences.getString("SERVICIO","unknown");
       //if(servicio.equals("0")){
           //createAsyncTask().execute();
           /*SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(BakgroundPedidos.this);
           SharedPreferences.Editor myEditor = myPreferences.edit();
           myEditor.putString("SERVICIO", "1");*/
        //}
        //createAsyncTask().execute();
        //createAsyncTask().execute();
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        Log.i(TAG, "onUnBind()");
        return null;
    }

    public void onStop() {
        Log.i(TAG, "onStop()");
    }
    public void onPause() {
        Log.i(TAG, "onPause()");
    }
    @Override
    public void onDestroy() {

        //createAsyncTask().execute();
        //mWorkManager.cancelWorkById(mRequest.getId());
        mWorkManager.enqueue(mRequest);
        //ast.execute();
        /*SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(BakgroundPedidos.this);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("SERVICIO", "1");*/
        //Globales.servicio="0";
        //Toast.makeText(this, "Service stopped...", Toast.LENGTH_SHORT).show();
        /*
        Log.i(TAG, "onCreate() , service stopped...");*/
    }
    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory()");
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
            cambioPedidos.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(BakgroundPedidos.this);
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
                            Intent intent = new Intent(BakgroundPedidos.this, MenuActivity.class);
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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //createAsyncTask().execute();
        }
    }

}
