package com.fastbuy.fastbuyempresas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos = nDatabaseReference.child("pedidos/");
    int notificador=0;
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        Data taskData = getInputData();
        String taskDataString = taskData.getString(MenuActivity.MESSAGE_STATUS);
        showNotification();
        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();
        return Result.success(outputData);
    }
    private void showNotification() {
        cambioPedidos.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
                String codigou= mypreferences.getString("UBICACION","unknown");
                notificador++;
                if(notificador>1){
                    RDPedidos data = dataSnapshot.getValue(RDPedidos.class);
                    //String empresa = dataSnapshot.getValue().
                    if(codigoe.equals(data.getEmpresa()) && codigou.equals(data.getUbicacion())){
                        String channelId  = getApplicationContext().getString(R.string.default_notification_channel_id);
                        String channelName = getApplicationContext().getString(R.string.default_notification_channel_name);
                        NotificationChannel androidChannel = null;

                        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notify);
                        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        int icono = R.drawable.ic_tienda;
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
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
                            NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                            mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(icono)
                                    .setContentTitle("FastBuy Delívery")
                                    .setContentText("Nuevo Pedido")
                                    .setShowWhen(true)
                                    .setVibrate(new long[]{100, 250, 100, 500})
                                    .setAutoCancel(true)
                                    .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notify));

                            mNotifyMgr.notify(0, mBuilder.build());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
