package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.TextView;

import com.fastbuy.fastbuyempresas.Config.Globales;
import com.fastbuy.fastbuyempresas.Config.ValidacionDatos;

public class DesconectadoActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeLayout;
    public Typeface script,script2;
    ValidacionDatos validacion= new ValidacionDatos();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desconectado);

        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente2);
        String fuente="fonts/NEXABOLD.otf";
        this.script2= Typeface.createFromAsset(getAssets(),fuente);

        TextView txtLoSentimos = (TextView) findViewById(R.id.txtLoSentimos);
        TextView txtComprueba = (TextView) findViewById(R.id.txtComprueba);

        txtLoSentimos.setTypeface(script2);
        txtComprueba.setTypeface(script);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Aqui ejecutamos el codigo necesario para refrescar nuestra interfaz grafica.
                //Antes de ejecutarlo, indicamos al swipe layout que muestre la barra indeterminada de progreso.
                swipeLayout.setRefreshing(true);
                //Vamos a simular un refresco con un handle.

                if(validacion.hayConexiónRed(DesconectadoActivity.this)){
                    SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(DesconectadoActivity.this);
                    String empresa= myPreferences.getString("NOMBRE_EMPRESA","unknown");

                    if(empresa.equals("unknown")){
                        Intent intent=new Intent(DesconectadoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent=new Intent(DesconectadoActivity.this, PrincipalActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }

        });
    }
    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && validacion.hayConexiónRed(DesconectadoActivity.this)) {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(DesconectadoActivity.this);
            String empresa= myPreferences.getString("NOMBRE_EMPRESA","unknown");
            if(empresa.equals("unknown")){
                Intent intent=new Intent(DesconectadoActivity.this, LoginActivity.class);
                startActivity(intent);
            }else{
                Intent intent=new Intent(DesconectadoActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
            //moveTaskToBack(false);
            //return false;
        }else{
            Intent intent=new Intent(DesconectadoActivity.this, MainActivity.class);
            startActivity(intent);
            moveTaskToBack(false);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
