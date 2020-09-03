package com.fastbuy.fastbuyempresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuy.fastbuyempresas.Config.ServicioPartner;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {
    private Typeface script, script2;
    TextView txtLogo,txtFondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnComenzar=(Button) findViewById(R.id.btnComenzar);
        txtLogo= (TextView) findViewById(R.id.txtFondoLogo);
        txtFondo= (TextView) findViewById(R.id.txtFondo);
        String fuente="fonts/Riffic.ttf";
        String fuente2="fonts/GOTHIC.ttf";
        this.script= Typeface.createFromAsset(getAssets(),fuente);
        this.script2= Typeface.createFromAsset(getAssets(),fuente2);
        txtLogo.setTypeface(script);
        txtFondo.setTypeface(script2);
        btnComenzar.setTypeface(script2);
        final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final String empresa= myPreferences.getString("NOMBRE_EMPRESA","unknown");

        //if(!empresa.equals("unknown")){
           //serv de tipo Intent
            //startService(myService); //ctx de tipo Context
        //}

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String servicio= myPreferences.getString("SERVICIO","unknown");
                if(empresa.equals("unknown")){
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent= new Intent(MainActivity.this, PrincipalActivity.class);
                    startActivity(intent);
                }
            }


        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
