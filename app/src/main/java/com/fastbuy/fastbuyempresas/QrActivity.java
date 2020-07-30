package com.fastbuy.fastbuyempresas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.ImageViewCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Entidades.RDPedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.android.QRCode;

public class QrActivity extends AppCompatActivity {

    ImageView ivempresaqr, btnperfilqr, btnmenuqr, btnproductosqr,imagenCodigo;
    ProgressDialog progDailog = null;
    TextView txtNombre,txtrazonsocialqr,txtaunta;
    Typeface script2;
    DatabaseReference nDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cambioPedidos = nDatabaseReference.child("qr/").child("codigop");
    QrActivity.AsyncTask_load ast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        txtNombre= (TextView) findViewById(R.id.txtNombreEmpresaqr);
        txtrazonsocialqr= (TextView) findViewById(R.id.txtrazonsocialqr);
        btnperfilqr= (ImageView) findViewById(R.id.btnperfilqr);
        btnmenuqr=(ImageView) findViewById(R.id.btnmenuqr);
        btnproductosqr=(ImageView) findViewById(R.id.btnproductosqr);
        txtaunta= (TextView) findViewById(R.id.txtapunta);
        if(isNetDisponible()){
            //Toast.makeText(QrActivity.this,"Conexión a internet", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent=new Intent(QrActivity.this, DesconectadoActivity.class);
            startActivity(intent);
        }
        String fuente2="fonts/GOTHIC.ttf";
        this.script2= Typeface.createFromAsset(getAssets(),fuente2);


        //txtTitulo.setTypeface(script);
        txtNombre.setTypeface(script2);
        txtrazonsocialqr.setTypeface(script2);
        txtaunta.setTypeface(script2);

        String texto = Globales.pedidoSeleccionado;
        Bitmap bitmap = QRCode.from(texto).withSize(100, 100).bitmap();

        imagenCodigo = findViewById(R.id.ivqr);
        imagenCodigo.setImageBitmap(bitmap);

        SharedPreferences mypreferences= PreferenceManager.getDefaultSharedPreferences(QrActivity.this);
        String nombreempresa= mypreferences.getString("NOMBRE_EMPRESA","unknown");
        String razonsocial= mypreferences.getString("RAZON_SOCIAL","unknown");
        String fotoempresa= mypreferences.getString("FOTO_EMPRESA","unknown");
        String codigoe= mypreferences.getString("CODIGO_EMPRESA","unknown");
        String codigou= mypreferences.getString("UBICACION","unknown");
        //QRCode.from(texto).bitmap();
        progDailog = new ProgressDialog(QrActivity.this);
        progDailog.setMessage("Cargando Codigo QR...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        txtNombre.setText(nombreempresa);
        txtrazonsocialqr.setText(razonsocial);
        ivempresaqr= (ImageView) findViewById(R.id.ivempresaqr);
        String url= Globales.servidorfotos+"/logos/"+fotoempresa;
        Log.v("RUTA_IMAGEN",url);
        Picasso.with(QrActivity.this)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivempresaqr, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imagebitmap= ((BitmapDrawable) ivempresaqr.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable= RoundedBitmapDrawableFactory.create(getResources(),imagebitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imagebitmap.getWidth(),imagebitmap.getHeight())/ 2.0f);
                        ivempresaqr.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        ivempresaqr.setImageResource(R.mipmap.ic_launcher);
                    }
                });
        btnmenuqr.setBackgroundResource(R.color.fastbuy);
        int color= ContextCompat.getColor(getApplicationContext(), R.color.blanco);
        ImageViewCompat.setImageTintList(btnmenuqr, ColorStateList.valueOf(color));

        btnproductosqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });
        btnperfilqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
        ast = new AsyncTask_load();
        ast.execute();
        progDailog.dismiss();
    }
    public class AsyncTask_load extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            cambioPedidos.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String pedidop=dataSnapshot.getValue().toString();
                    if(Globales.pedidoSeleccionado.equals(pedidop)) {
                        Intent intent = new Intent(QrActivity.this, MenuActivity.class);
                        startActivity(intent);
                        Toast toast = Toast.makeText(QrActivity.this,"Pedido entregado al repartidor",Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_exito);
                        toast.show();
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
